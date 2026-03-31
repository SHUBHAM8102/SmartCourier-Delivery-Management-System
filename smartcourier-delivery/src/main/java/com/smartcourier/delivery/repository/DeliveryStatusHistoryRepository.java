package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.DeliveryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, Long> {
    List<DeliveryStatusHistory> findByDeliveryIdOrderByChangedAtDesc(UUID deliveryId);
}
