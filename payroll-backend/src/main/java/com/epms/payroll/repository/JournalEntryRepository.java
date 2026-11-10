package com.epms.payroll.repository;
import com.epms.payroll.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JournalEntryRepository extends JpaRepository<JournalEntry,Long>{
    List<JournalEntry> findByEmployeeIdOrderByEntryDateDescIdDesc(Long employeeId);
    List<JournalEntry> findAllByOrderByEntryDateDescIdDesc();
}
