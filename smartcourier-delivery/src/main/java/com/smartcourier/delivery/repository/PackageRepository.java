package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PackageRepository extends JpaRepository<Package, UUID> {
    List<Package> findByDeliveryId(UUID deliveryId);
}
