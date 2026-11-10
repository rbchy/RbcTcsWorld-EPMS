package com.epms.payroll.repository;
import com.epms.payroll.entity.Payroll; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface PayrollRepository extends JpaRepository<Payroll,Long>{List<Payroll> findByEmployeeId(Long employeeId);}
