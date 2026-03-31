package com.smartcourier.tracking.repository;

import com.smartcourier.tracking.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findByDeliveryId(UUID deliveryId);
    List<Document> findByDeliveryIdAndDocumentType(UUID deliveryId, Document.DocumentType documentType);
}
