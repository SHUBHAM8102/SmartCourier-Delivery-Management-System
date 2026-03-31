package com.smartcourier.tracking.repository;

import com.smartcourier.tracking.entity.DeliveryProof;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryProofRepository extends JpaRepository<DeliveryProof, UUID> {
    List<DeliveryProof> findByDeliveryId(UUID deliveryId);
}
