package com.epms.payroll.repository;
import com.epms.payroll.entity.JournalLine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface JournalLineRepository extends JpaRepository<JournalLine,Long>{
    List<JournalLine> findByAccountId(Long accountId);
}
