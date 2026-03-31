package com.smartcourier.admin.repository;

import com.smartcourier.admin.entity.ReportRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReportRunRepository extends JpaRepository<ReportRun, UUID> {

    List<ReportRun> findByReportDefinitionId(UUID reportDefinitionId);

    List<ReportRun> findByRequestedBy(UUID requestedBy);

    List<ReportRun> findByStatus(ReportRun.ReportStatus status);
}
