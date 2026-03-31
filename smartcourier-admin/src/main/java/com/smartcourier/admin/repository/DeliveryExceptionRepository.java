package com.smartcourier.admin.repository;

import com.smartcourier.admin.entity.DeliveryException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryExceptionRepository extends JpaRepository<DeliveryException, UUID> {

    List<DeliveryException> findByStatus(DeliveryException.ExceptionStatus status);

    List<DeliveryException> findByExceptionType(DeliveryException.ExceptionType exceptionType);

    List<DeliveryException> findByDeliveryId(UUID deliveryId);

    @Query("SELECT COUNT(e) FROM DeliveryException e WHERE e.status = :status")
    Long countByStatus(DeliveryException.ExceptionStatus status);

    @Query("SELECT COUNT(e) FROM DeliveryException e WHERE e.exceptionType = :type")
    Long countByExceptionType(DeliveryException.ExceptionType type);
}
