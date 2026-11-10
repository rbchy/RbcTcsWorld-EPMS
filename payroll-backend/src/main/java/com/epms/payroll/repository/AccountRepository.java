package com.epms.payroll.repository;
import com.epms.payroll.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface AccountRepository extends JpaRepository<Account,Long>{ Optional<Account> findByCode(String code); }
