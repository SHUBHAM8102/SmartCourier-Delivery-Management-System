package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.DeliveryAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryAddressRepository extends JpaRepository<DeliveryAddress, UUID> {
    List<DeliveryAddress> findByDeliveryId(UUID deliveryId);
}
