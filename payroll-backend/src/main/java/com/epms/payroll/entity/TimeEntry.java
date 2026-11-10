package com.epms.payroll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity @Table(name="time_entries")
public class TimeEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) private Employee employee;
    private LocalDate workDate;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Integer breakMinutes = 0;
    @Column(precision=10,scale=2) private BigDecimal regularHours = BigDecimal.ZERO;
    @Column(precision=10,scale=2) private BigDecimal overtimeHours = BigDecimal.ZERO;
    @Column(precision=10,scale=2) private BigDecimal doubletimeHours = BigDecimal.ZERO;
    private String status = "OPEN";

    public Long getId(){return id;}
    public Employee getEmployee(){return employee;}
    public void setEmployee(Employee v){employee=v;}
    public LocalDate getWorkDate(){return workDate;}
    public void setWorkDate(LocalDate v){workDate=v;}
    public LocalDateTime getClockIn(){return clockIn;}
    public void setClockIn(LocalDateTime v){clockIn=v;}
    public LocalDateTime getClockOut(){return clockOut;}
    public void setClockOut(LocalDateTime v){clockOut=v;}
    public Integer getBreakMinutes(){return breakMinutes;}
    public void setBreakMinutes(Integer v){breakMinutes=v;}
    public BigDecimal getRegularHours(){return regularHours;}
    public void setRegularHours(BigDecimal v){regularHours=v;}
    public BigDecimal getOvertimeHours(){return overtimeHours;}
    public void setOvertimeHours(BigDecimal v){overtimeHours=v;}
    public BigDecimal getDoubletimeHours(){return doubletimeHours;}
    public void setDoubletimeHours(BigDecimal v){doubletimeHours=v;}
    public String getStatus(){return status;}
    public void setStatus(String v){status=v;}
}
