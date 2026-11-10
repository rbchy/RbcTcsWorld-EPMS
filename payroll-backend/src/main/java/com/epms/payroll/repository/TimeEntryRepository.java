package com.epms.payroll.repository;

import com.epms.payroll.entity.TimeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
    List<TimeEntry> findByEmployeeIdOrderByWorkDateDescIdDesc(Long employeeId);
    Optional<TimeEntry> findFirstByEmployeeIdAndStatus(Long employeeId, String status);
}
