package com.smartcourier.admin.config;

import com.smartcourier.admin.entity.ReportDefinition;
import com.smartcourier.admin.repository.ReportDefinitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ReportDefinitionRepository reportDefinitionRepository;

    @Override
    public void run(String... args) {
        if (reportDefinitionRepository.count() == 0) {
            log.info("Initializing default report definitions...");

            List<ReportDefinition> defaultReports = List.of(
                    ReportDefinition.builder()
                            .reportCode("DELIVERY_SUMMARY")
                            .reportName("Delivery Summary Report")
                            .description("Summary of all deliveries with status breakdown")
                            .queryTemplate("SELECT status, COUNT(*) FROM deliveries GROUP BY status")
                            .isActive(true)
                            .build(),
                    ReportDefinition.builder()
                            .reportCode("EXCEPTION_REPORT")
                            .reportName("Exception Report")
                            .description("Report of all delivery exceptions by type and severity")
                            .queryTemplate("SELECT exception_type, severity, COUNT(*) FROM delivery_exceptions GROUP BY exception_type, severity")
                            .isActive(true)
                            .build(),
                    ReportDefinition.builder()
                            .reportCode("HUB_PERFORMANCE")
                            .reportName("Hub Performance Report")
                            .description("Performance metrics for all hubs")
                            .queryTemplate("SELECT hub_id, COUNT(*) as total_deliveries FROM deliveries GROUP BY hub_id")
                            .isActive(true)
                            .build(),
                    ReportDefinition.builder()
                            .reportCode("DELAYED_DELIVERIES")
                            .reportName("Delayed Deliveries Report")
                            .description("List of all delayed deliveries")
                            .queryTemplate("SELECT * FROM deliveries WHERE exception_status = 'DELAYED'")
                            .isActive(true)
                            .build(),
                    ReportDefinition.builder()
                            .reportCode("FAILED_DELIVERIES")
                            .reportName("Failed Deliveries Report")
                            .description("List of all failed deliveries")
                            .queryTemplate("SELECT * FROM deliveries WHERE exception_status = 'FAILED'")
                            .isActive(true)
                            .build()
            );

            reportDefinitionRepository.saveAll(defaultReports);
            log.info("Default report definitions initialized successfully");
        }
    }
}
