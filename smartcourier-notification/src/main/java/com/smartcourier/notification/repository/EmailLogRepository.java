package com.smartcourier.notification.repository;

import com.smartcourier.notification.entity.EmailLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmailLogRepository extends JpaRepository<EmailLog, UUID> {
    Page<EmailLog> findByRecipientEmailContainingIgnoreCase(String email, Pageable pageable);
}
