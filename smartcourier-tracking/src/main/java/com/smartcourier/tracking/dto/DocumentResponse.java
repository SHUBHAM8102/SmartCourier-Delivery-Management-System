package com.smartcourier.tracking.dto;

import com.smartcourier.tracking.entity.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private UUID id;
    private UUID deliveryId;
    private Document.DocumentType documentType;
    private String fileName;
    private String fileUrl;
    private String fileMimeType;
    private Long fileSizeBytes;
    private UUID uploadedBy;
    private String checksumSha256;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
}
