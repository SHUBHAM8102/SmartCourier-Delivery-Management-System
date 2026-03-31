package com.smartcourier.delivery.repository;

import com.smartcourier.delivery.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query("SELECT e FROM OutboxEvent e WHERE e.published = false ORDER BY e.createdAt ASC")
    List<OutboxEvent> findUnpublishedEvents();

    @Modifying
    @Query("UPDATE OutboxEvent e SET e.published = true, e.publishedAt = :publishedAt WHERE e.id = :id")
    void markAsPublished(UUID id, LocalDateTime publishedAt);
}
