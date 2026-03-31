package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.Hub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface HubRepository extends JpaRepository<Hub, UUID> {
    Optional<Hub> findByHubCode(String hubCode);
    Optional<Hub> findByCity(String city);
}
