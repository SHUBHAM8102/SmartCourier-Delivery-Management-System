package com.smartcourier.admin.repository;

import com.smartcourier.admin.entity.ReportDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportDefinitionRepository extends JpaRepository<ReportDefinition, UUID> {

    Optional<ReportDefinition> findByReportCode(String reportCode);

    List<ReportDefinition> findByIsActive(Boolean isActive);

    boolean existsByReportCode(String reportCode);
}
