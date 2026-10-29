package ua.pnu.autolanded.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.pnu.autolanded.entity.VehicleAuditReport;
import java.util.List;

@Repository
public interface VehicleAuditReportRepository extends JpaRepository<VehicleAuditReport, Long> {
    List<VehicleAuditReport> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<VehicleAuditReport> findByVinOrderByCreatedAtDesc(String vin);
    List<VehicleAuditReport> findTop10ByOrderByCreatedAtDesc();
}
