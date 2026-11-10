package com.epms.payroll.controller;
import com.epms.payroll.dto.AccountBalance;
import com.epms.payroll.entity.JournalEntry;
import com.epms.payroll.service.PayrollAccountingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/accounting")
public class AccountingController {
    private final PayrollAccountingService s;
    public AccountingController(PayrollAccountingService x) { s = x; }

    @GetMapping("/journal")
    public List<JournalEntry> journal(@RequestParam(required = false) Long employeeId) {
        return employeeId != null ? s.byEmployee(employeeId) : s.allEntries();
    }

    @GetMapping("/balances")
    public List<AccountBalance> balances() { return s.balances(); }
}
