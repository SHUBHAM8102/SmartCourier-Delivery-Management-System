package com.smartcourier.admin.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "report_definitions", schema = "admin")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "report_code", nullable = false, unique = true, length = 50)
    private String reportCode;

    @Column(name = "report_name", nullable = false, length = 100)
    private String reportName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "query_template", columnDefinition = "TEXT")
    private String queryTemplate;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
