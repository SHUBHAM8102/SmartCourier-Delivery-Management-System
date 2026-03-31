package com.smartcourier.tracking.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "delivery_proofs", schema = "tracking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryProof {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "proof_type", nullable = false, length = 30)
    private ProofType proofType;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(name = "receiver_contact", length = 20)
    private String receiverContact;

    @Column(name = "proof_file_url", length = 500)
    private String proofFileUrl;

    @Column(name = "otp_verified", nullable = false)
    @Builder.Default
    private Boolean otpVerified = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "captured_by")
    private UUID capturedBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum ProofType {
        SIGNATURE,
        PHOTO,
        OTP_ONLY,
        SIGNATURE_AND_PHOTO
    }
}
