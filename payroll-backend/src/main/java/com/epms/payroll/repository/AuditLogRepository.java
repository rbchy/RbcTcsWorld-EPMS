package com.epms.payroll.repository;
import com.epms.payroll.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog,Long>{
    List<AuditLog> findByEntityTypeAndEntityIdOrderByPerformedAtDesc(String entityType, Long entityId);
    List<AuditLog> findAllByOrderByPerformedAtDesc();
}
