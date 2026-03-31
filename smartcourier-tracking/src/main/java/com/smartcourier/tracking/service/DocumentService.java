package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.DocumentResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponse uploadDocument(UUID deliveryId, String documentType, MultipartFile file, UUID uploadedBy);
    List<DocumentResponse> getDocumentsByDeliveryId(UUID deliveryId);
    DocumentResponse getDocumentById(UUID documentId);
}
