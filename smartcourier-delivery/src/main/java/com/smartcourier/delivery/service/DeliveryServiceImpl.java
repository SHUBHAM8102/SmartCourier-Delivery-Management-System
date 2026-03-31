package com.smartcourier.delivery.service;

import com.smartcourier.delivery.client.AuthServiceClient;
import com.smartcourier.delivery.config.RabbitMQConfig;
import com.smartcourier.delivery.dto.*;
import com.smartcourier.delivery.entity.*;
import com.smartcourier.delivery.exception.DeliveryException;
import com.smartcourier.delivery.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryServiceImpl implements DeliveryService {

    private static final String DELIVERY_NOT_FOUND = "Delivery not found";
    private static final String EMAIL_KEY = "email";
    private static final String CUSTOMER_ROLE = "CUSTOMER";

    private final Random random = new Random();

    private final DeliveryRepository deliveryRepository;
    private final DeliveryAddressRepository addressRepository;
    private final PackageRepository packageRepository;
    private final DeliveryStatusHistoryRepository statusHistoryRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final AuthServiceClient authServiceClient;

    @Override
    @Transactional
    public DeliveryResponse createDelivery(CreateDeliveryRequest request, UUID customerId) {
        Delivery delivery = Delivery.builder()
                .deliveryNumber(generateDeliveryNumber())
                .trackingNumber(generateTrackingNumber())
                .customerId(customerId)
                .serviceType(Delivery.ServiceType.valueOf(request.getServiceType()))
                .status(Delivery.DeliveryStatus.BOOKED)
                .paymentStatus(Delivery.PaymentStatus.PENDING)
                .notes(request.getNotes())
                .bookedAt(LocalDateTime.now())
                .build();

        final Delivery savedDelivery = deliveryRepository.save(delivery);

        DeliveryAddress senderAddress = createAddress(savedDelivery, request.getSenderAddress(), DeliveryAddress.AddressType.SENDER);
        DeliveryAddress receiverAddress = createAddress(savedDelivery, request.getReceiverAddress(), DeliveryAddress.AddressType.RECEIVER);
        addressRepository.saveAll(List.of(senderAddress, receiverAddress));

        List<com.smartcourier.delivery.entity.Package> packages = request.getPackages().stream()
                .map(pkgDto -> createPackage(savedDelivery, pkgDto))
                .toList();
        packageRepository.saveAll(packages);

        BigDecimal totalCharge = calculateCharges(packages, savedDelivery.getServiceType());
        savedDelivery.setQuotedAmount(totalCharge);
        final Delivery finalDelivery = deliveryRepository.save(savedDelivery);

        createStatusHistory(finalDelivery, null, Delivery.DeliveryStatus.BOOKED.name(), customerId, CUSTOMER_ROLE, "Delivery created");

        publishDeliveryCreatedEvent(finalDelivery);

        return mapToResponse(finalDelivery, senderAddress, receiverAddress, packages);
    }

    @Override
    public DeliveryResponse getDeliveryById(UUID id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryException("Delivery not found"));
        return mapToResponseFull(delivery);
    }

    @Override
    public DeliveryResponse getDeliveryByTrackingNumber(String trackingNumber) {
        Delivery delivery = deliveryRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new DeliveryException(DELIVERY_NOT_FOUND));
        return mapToResponseFull(delivery);
    }

    @Override
    public List<DeliveryResponse> getCustomerDeliveries(UUID customerId) {
        return deliveryRepository.findByCustomerIdOrderByCreatedAtDesc(customerId)
                .stream()
                .map(this::mapToResponseFull)
                .toList();
    }

    @Override
    public List<DeliveryResponse> getAllDeliveries() {
        return deliveryRepository.findAllActive()
                .stream()
                .map(this::mapToResponseFull)
                .toList();
    }

    @Override
    public List<DeliveryResponse> getDeliveriesByStatus(String status) {
        Delivery.DeliveryStatus deliveryStatus = Delivery.DeliveryStatus.valueOf(status.toUpperCase());
        return deliveryRepository.findByStatus(deliveryStatus)
                .stream()
                .map(this::mapToResponseFull)
                .toList();
    }

    @Override
    @Transactional
    public DeliveryResponse updateStatus(UUID id, UpdateStatusRequest request, UUID changedBy, String changeSource) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new DeliveryException("Delivery not found"));

        Delivery.DeliveryStatus oldStatus = delivery.getStatus();
        Delivery.DeliveryStatus newStatus = Delivery.DeliveryStatus.valueOf(request.getStatus().toUpperCase());

        delivery.setStatus(newStatus);
        
        if (newStatus == Delivery.DeliveryStatus.DELIVERED) {
            delivery.setDeliveredAt(LocalDateTime.now());
        }

        delivery = deliveryRepository.save(delivery);

        createStatusHistory(delivery, oldStatus.name(), newStatus.name(), changedBy, changeSource, request.getRemarks());

        publishStatusChangedEvent(delivery, oldStatus.name(), newStatus.name());

        return mapToResponseFull(delivery);
    }

    private DeliveryAddress createAddress(Delivery delivery, AddressDto dto, DeliveryAddress.AddressType type) {
        return DeliveryAddress.builder()
                .delivery(delivery)
                .addressType(type)
                .contactName(dto.getContactName())
                .contactPhone(dto.getContactPhone())
                .email(dto.getEmail())
                .line1(dto.getLine1())
                .line2(dto.getLine2())
                .landmark(dto.getLandmark())
                .city(dto.getCity())
                .state(dto.getState())
                .postalCode(dto.getPostalCode())
                .countryCode(dto.getCountryCode() != null ? dto.getCountryCode() : "IN")
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
    }

    private com.smartcourier.delivery.entity.Package createPackage(Delivery delivery, PackageDto dto) {
        return com.smartcourier.delivery.entity.Package.builder()
                .delivery(delivery)
                .packageType(dto.getPackageType())
                .description(dto.getDescription())
                .quantity(dto.getQuantity() != null ? dto.getQuantity() : 1)
                .weightKg(dto.getWeightKg())
                .lengthCm(dto.getLengthCm())
                .widthCm(dto.getWidthCm())
                .heightCm(dto.getHeightCm())
                .declaredValue(dto.getDeclaredValue())
                .fragile(Boolean.TRUE.equals(dto.getFragile()))
                .hazardous(Boolean.TRUE.equals(dto.getHazardous()))
                .build();
    }

    private BigDecimal calculateCharges(List<com.smartcourier.delivery.entity.Package> packages, Delivery.ServiceType serviceType) {
        BigDecimal baseRate = switch (serviceType) {
            case EXPRESS -> new BigDecimal("80.00");
            case INTERNATIONAL -> new BigDecimal("500.00");
            default -> new BigDecimal("40.00");
        };

        BigDecimal totalWeight = packages.stream()
                .map(pkg -> {
                    BigDecimal actualWeight = pkg.getWeightKg() != null ? pkg.getWeightKg() : BigDecimal.ZERO;
                    BigDecimal volumetricWeight = BigDecimal.ZERO;
                    if (pkg.getLengthCm() != null && pkg.getWidthCm() != null && pkg.getHeightCm() != null) {
                        volumetricWeight = pkg.getLengthCm().multiply(pkg.getWidthCm())
                                .multiply(pkg.getHeightCm())
                                .divide(new BigDecimal("5000"), 3, java.math.RoundingMode.HALF_UP);
                    }
                    return actualWeight.max(volumetricWeight);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return baseRate.multiply(totalWeight);
    }

    private void createStatusHistory(Delivery delivery, String oldStatus, String newStatus, UUID changedBy, String changeSource, String remarks) {
        DeliveryStatusHistory history = DeliveryStatusHistory.builder()
                .delivery(delivery)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .changedBy(changedBy)
                .changeSource(changeSource)
                .remarks(remarks)
                .changedAt(LocalDateTime.now())
                .build();
        statusHistoryRepository.save(history);
    }

    private void publishDeliveryCreatedEvent(Delivery delivery) {
        String email = getUserEmail(delivery.getCustomerId());
        
        Map<String, Object> event = new HashMap<>();
        event.put("deliveryId", delivery.getId().toString());
        event.put("trackingNumber", delivery.getTrackingNumber());
        event.put("customerId", delivery.getCustomerId().toString());
        event.put("status", delivery.getStatus().name());
        event.put("serviceType", delivery.getServiceType().name());
        if (email != null) {
            event.put(EMAIL_KEY, email);
        }
        
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.DELIVERY_CREATED_ROUTING_KEY, event);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.NOTIFICATION_DELIVERY_ROUTING_KEY, event);
    }

    private void publishStatusChangedEvent(Delivery delivery, String oldStatus, String newStatus) {
        String email = getUserEmail(delivery.getCustomerId());
        
        Map<String, Object> event = new HashMap<>();
        event.put("deliveryId", delivery.getId().toString());
        event.put("trackingNumber", delivery.getTrackingNumber());
        event.put("customerId", delivery.getCustomerId().toString());
        event.put("oldStatus", oldStatus);
        event.put("newStatus", newStatus);
        if (email != null) {
            event.put(EMAIL_KEY, email);
        }
        
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.STATUS_CHANGED_ROUTING_KEY, event);
    }

    private String getUserEmail(UUID customerId) {
        try {
            Map<String, String> response = authServiceClient.getUserEmail(customerId.toString());
            return response.get("email");
        } catch (Exception e) {
            log.warn("Failed to get user email for customer {}: {}", customerId, e.getMessage());
            return null;
        }
    }

    private String generateDeliveryNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "DN" + timestamp + String.format("%04d", random.nextInt(10000));
    }

    private String generateTrackingNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        return "SC" + timestamp + String.format("%04d", random.nextInt(10000));
    }

    private DeliveryResponse mapToResponse(Delivery delivery, DeliveryAddress sender, DeliveryAddress receiver, List<com.smartcourier.delivery.entity.Package> packages) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .deliveryNumber(delivery.getDeliveryNumber())
                .trackingNumber(delivery.getTrackingNumber())
                .customerId(delivery.getCustomerId())
                .serviceType(delivery.getServiceType().name())
                .status(delivery.getStatus().name())
                .paymentStatus(delivery.getPaymentStatus().name())
                .quotedAmount(delivery.getQuotedAmount())
                .currencyCode(delivery.getCurrencyCode())
                .senderAddress(mapAddressDto(sender))
                .receiverAddress(mapAddressDto(receiver))
                .packages(packages.stream().map(this::mapPackageDto).toList())
                .notes(delivery.getNotes())
                .bookedAt(delivery.getBookedAt())
                .createdAt(delivery.getCreatedAt())
                .build();
    }

    private DeliveryResponse mapToResponseFull(Delivery delivery) {
        List<DeliveryAddress> addresses = addressRepository.findByDeliveryId(delivery.getId());
        DeliveryAddress sender = addresses.stream().filter(a -> a.getAddressType() == DeliveryAddress.AddressType.SENDER).findFirst().orElse(null);
        DeliveryAddress receiver = addresses.stream().filter(a -> a.getAddressType() == DeliveryAddress.AddressType.RECEIVER).findFirst().orElse(null);
        List<com.smartcourier.delivery.entity.Package> packages = packageRepository.findByDeliveryId(delivery.getId());

        return mapToResponse(delivery, sender, receiver, packages);
    }

    private AddressDto mapAddressDto(DeliveryAddress address) {
        if (address == null) return null;
        return AddressDto.builder()
                .contactName(address.getContactName())
                .contactPhone(address.getContactPhone())
                .email(address.getEmail())
                .line1(address.getLine1())
                .line2(address.getLine2())
                .landmark(address.getLandmark())
                .city(address.getCity())
                .state(address.getState())
                .postalCode(address.getPostalCode())
                .countryCode(address.getCountryCode())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();
    }

    private PackageDto mapPackageDto(com.smartcourier.delivery.entity.Package pkg) {
        return PackageDto.builder()
                .packageType(pkg.getPackageType())
                .description(pkg.getDescription())
                .quantity(pkg.getQuantity())
                .weightKg(pkg.getWeightKg())
                .lengthCm(pkg.getLengthCm())
                .widthCm(pkg.getWidthCm())
                .heightCm(pkg.getHeightCm())
                .declaredValue(pkg.getDeclaredValue())
                .fragile(pkg.getFragile())
                .hazardous(pkg.getHazardous())
                .build();
    }
}
