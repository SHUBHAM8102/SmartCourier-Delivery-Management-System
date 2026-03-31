package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {

    Optional<Delivery> findByTrackingNumber(String trackingNumber);

    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);

    List<Delivery> findByCustomerId(UUID customerId);

    List<Delivery> findByStatus(Delivery.DeliveryStatus status);

    List<Delivery> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    @Query("SELECT d FROM Delivery d WHERE d.deletedAt IS NULL ORDER BY d.createdAt DESC")
    List<Delivery> findAllActive();

    boolean existsByTrackingNumber(String trackingNumber);
}
