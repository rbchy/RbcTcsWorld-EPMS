package com.epms.payroll.service;

import com.epms.payroll.entity.Employee;
import com.epms.payroll.entity.TimeEntry;
import com.epms.payroll.exception.BusinessRuleException;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.TimeEntryRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TimeClockService {

    private final EmployeeRepository employeeRepository;
    private final TimeEntryRepository timeEntryRepository;

    public TimeClockService(EmployeeRepository employeeRepository, TimeEntryRepository timeEntryRepository) {
        this.employeeRepository = employeeRepository;
        this.timeEntryRepository = timeEntryRepository;
    }

    public TimeEntry clockIn(Long employeeId) {
        timeEntryRepository.findFirstByEmployeeIdAndStatus(employeeId, "OPEN").ifPresent(x -> {
            throw new BusinessRuleException("This employee is already clocked in.");
        });
        Employee employee = employeeRepository.findById(employeeId).orElseThrow();
        TimeEntry entry = new TimeEntry();
        entry.setEmployee(employee);
        entry.setWorkDate(LocalDate.now());
        entry.setClockIn(LocalDateTime.now());
        entry.setStatus("OPEN");
        return timeEntryRepository.save(entry);
    }

    public TimeEntry clockOut(Long entryId) {
        TimeEntry entry = timeEntryRepository.findById(entryId).orElseThrow();
        if (!"OPEN".equals(entry.getStatus())) {
            throw new BusinessRuleException("This time entry is already closed.");
        }
        entry.setClockOut(LocalDateTime.now());
        long minutes = Duration.between(entry.getClockIn(), entry.getClockOut()).toMinutes();
        int breakMinutes = entry.getBreakMinutes() == null ? 0 : entry.getBreakMinutes();
        double workedHours = Math.max(0, minutes - breakMinutes) / 60.0;
        double regular = Math.min(workedHours, 8.0);
        double overtime = Math.max(0, workedHours - 8.0);
        entry.setRegularHours(BigDecimal.valueOf(regular).setScale(2, RoundingMode.HALF_UP));
        entry.setOvertimeHours(BigDecimal.valueOf(overtime).setScale(2, RoundingMode.HALF_UP));
        entry.setStatus("CLOSED");
        return timeEntryRepository.save(entry);
    }

    public List<TimeEntry> byEmployee(Long employeeId) {
        return timeEntryRepository.findByEmployeeIdOrderByWorkDateDescIdDesc(employeeId);
    }

    public List<TimeEntry> all() {
        return timeEntryRepository.findAll();
    }
}
