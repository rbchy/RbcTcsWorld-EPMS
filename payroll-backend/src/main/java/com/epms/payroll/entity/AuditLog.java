package com.epms.payroll.entity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Immutable record of every sensitive change made in the system — who changed what, from what, to what, and why. */
@Entity @Table(name="audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false) private String entityType;
    @Column(nullable=false) private Long entityId;
    @Column(nullable=false) private String action;
    private String fieldName;
    @Column(length=1000) private String oldValue;
    @Column(length=1000) private String newValue;
    private String performedBy;
    @Column(nullable=false) private LocalDateTime performedAt = LocalDateTime.now();
    @Column(length=1000) private String reason;

    public Long getId(){return id;}
    public String getEntityType(){return entityType;} public void setEntityType(String v){entityType=v;}
    public Long getEntityId(){return entityId;} public void setEntityId(Long v){entityId=v;}
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getFieldName(){return fieldName;} public void setFieldName(String v){fieldName=v;}
    public String getOldValue(){return oldValue;} public void setOldValue(String v){oldValue=v;}
    public String getNewValue(){return newValue;} public void setNewValue(String v){newValue=v;}
    public String getPerformedBy(){return performedBy;} public void setPerformedBy(String v){performedBy=v;}
    public LocalDateTime getPerformedAt(){return performedAt;} public void setPerformedAt(LocalDateTime v){performedAt=v;}
    public String getReason(){return reason;} public void setReason(String v){reason=v;}
}
