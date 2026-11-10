package com.epms.payroll.service;
import com.epms.payroll.entity.AuditLog;
import com.epms.payroll.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;

/** Writes and reads the audit trail. Every write here is additive — nothing in this table is ever edited or deleted. */
@Service
public class AuditLogService {
    private final AuditLogRepository repo;
    public AuditLogService(AuditLogRepository r) { repo = r; }

    public AuditLog record(String entityType, Long entityId, String action, String fieldName,
                            Object oldValue, Object newValue, String performedBy, String reason) {
        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setFieldName(fieldName);
        log.setOldValue(oldValue == null ? null : String.valueOf(oldValue));
        log.setNewValue(newValue == null ? null : String.valueOf(newValue));
        log.setPerformedBy(performedBy == null ? "system" : performedBy);
        log.setReason(reason);
        return repo.save(log);
    }

    public List<AuditLog> forEntity(String entityType, Long entityId) {
        return repo.findByEntityTypeAndEntityIdOrderByPerformedAtDesc(entityType, entityId);
    }

    public List<AuditLog> all() {
        return repo.findAllByOrderByPerformedAtDesc();
    }
}
