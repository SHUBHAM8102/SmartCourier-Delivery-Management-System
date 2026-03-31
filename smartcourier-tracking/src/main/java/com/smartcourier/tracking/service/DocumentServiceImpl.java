package com.smartcourier.tracking.service;

import com.smartcourier.tracking.dto.DocumentResponse;
import com.smartcourier.tracking.entity.Document;
import com.smartcourier.tracking.exception.TrackingException;
import com.smartcourier.tracking.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;

    private String uploadPath = "./uploads/documents";

    @Override
    @Transactional
    public DocumentResponse uploadDocument(UUID deliveryId, String documentType, MultipartFile file, UUID uploadedBy) {
        try {
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(fileName);
            Files.write(filePath, file.getBytes());

            String checksum = calculateChecksum(file.getBytes());

            Document document = Document.builder()
                    .deliveryId(deliveryId)
                    .documentType(Document.DocumentType.valueOf(documentType))
                    .fileName(file.getOriginalFilename())
                    .fileUrl("/uploads/documents/" + fileName)
                    .fileMimeType(file.getContentType())
                    .fileSizeBytes(file.getSize())
                    .uploadedBy(uploadedBy)
                    .checksumSha256(checksum)
                    .build();

            Document saved = documentRepository.save(document);
            return mapToDocumentResponse(saved);

        } catch (IOException e) {
            throw new TrackingException("Failed to upload document: " + e.getMessage());
        }
    }

    @Override
    public List<DocumentResponse> getDocumentsByDeliveryId(UUID deliveryId) {
        return documentRepository.findByDeliveryId(deliveryId)
                .stream()
                .map(this::mapToDocumentResponse)
                .toList();
    }

    @Override
    public DocumentResponse getDocumentById(UUID documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new TrackingException("Document not found with id: " + documentId));
        return mapToDocumentResponse(document);
    }

    private String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private DocumentResponse mapToDocumentResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .deliveryId(document.getDeliveryId())
                .documentType(document.getDocumentType())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileMimeType(document.getFileMimeType())
                .fileSizeBytes(document.getFileSizeBytes())
                .uploadedBy(document.getUploadedBy())
                .checksumSha256(document.getChecksumSha256())
                .metadata(document.getMetadata())
                .createdAt(document.getCreatedAt())
                .build();
    }
}
