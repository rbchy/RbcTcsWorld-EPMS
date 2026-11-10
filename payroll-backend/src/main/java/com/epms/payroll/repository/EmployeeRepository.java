package com.epms.payroll.repository;
import com.epms.payroll.entity.Employee; import org.springframework.data.jpa.repository.JpaRepository; import java.util.List;
public interface EmployeeRepository extends JpaRepository<Employee,Long>{List<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmployeeNumberContainingIgnoreCase(String a,String b,String c);}
