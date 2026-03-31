package com.smartcourier.admin.repository;

import com.smartcourier.admin.entity.ServiceLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceLocationRepository extends JpaRepository<ServiceLocation, UUID> {

    Optional<ServiceLocation> findByLocationCode(String locationCode);

    List<ServiceLocation> findByIsActive(Boolean isActive);

    List<ServiceLocation> findByLocationType(ServiceLocation.LocationType locationType);

    List<ServiceLocation> findByCity(String city);

    boolean existsByLocationCode(String locationCode);

    @Query("SELECT COUNT(l) FROM ServiceLocation l WHERE l.locationType = :type AND l.isActive = true")
    Long countByLocationTypeAndIsActive(ServiceLocation.LocationType type);
}
