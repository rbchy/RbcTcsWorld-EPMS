package com.epms.payroll.service;
import com.epms.payroll.dto.AccountBalance;
import com.epms.payroll.entity.*;
import com.epms.payroll.repository.*;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.*;

/**
 * Posts a balanced double-entry journal entry every time a payroll run is saved,
 * and exposes the resulting general-ledger journal and account balances.
 *
 * The entry always balances against PayrollCalculator's net-pay formula:
 *   DEBIT  gross pay + employer FICA match + 401k employer match
 *   CREDIT federal/FICA/state/local/LST/SUI/retirement/health payables + net pay
 */
@Service
public class PayrollAccountingService {
    private final AccountRepository accountRepo;
    private final JournalEntryRepository journalRepo;
    private final JournalLineRepository lineRepo;

    public PayrollAccountingService(AccountRepository a, JournalEntryRepository j, JournalLineRepository l) {
        accountRepo = a; journalRepo = j; lineRepo = l;
    }

    @PostConstruct
    public void seedChartOfAccounts() {
        seed("1000", "Cash", "ASSET", "DEBIT");
        seed("5000", "Salaries and Wages Expense", "EXPENSE", "DEBIT");
        seed("5010", "Employer Payroll Tax Expense (FICA Match)", "EXPENSE", "DEBIT");
        seed("5020", "401(k) Employer Match Expense", "EXPENSE", "DEBIT");
        seed("2000", "Federal Income Tax Payable", "LIABILITY", "CREDIT");
        seed("2010", "FICA Tax Payable", "LIABILITY", "CREDIT");
        seed("2020", "PA State Tax Payable", "LIABILITY", "CREDIT");
        seed("2030", "Local Income Tax Payable", "LIABILITY", "CREDIT");
        seed("2040", "LST Payable", "LIABILITY", "CREDIT");
        seed("2050", "PA SUI Payable", "LIABILITY", "CREDIT");
        seed("2060", "Retirement Plan Payable", "LIABILITY", "CREDIT");
        seed("2070", "Health Insurance Payable", "LIABILITY", "CREDIT");
        seed("2080", "Net Pay Payable", "LIABILITY", "CREDIT");
    }

    private void seed(String code, String name, String type, String side) {
        if (accountRepo.findByCode(code).isEmpty()) {
            Account a = new Account();
            a.setCode(code); a.setName(name); a.setType(type); a.setNormalSide(side);
            accountRepo.save(a);
        }
    }

    private Account acc(String code) { return accountRepo.findByCode(code).orElseThrow(); }

    private void debit(JournalEntry e, String code, BigDecimal amount) {
        if (amount == null || amount.signum() == 0) return;
        JournalLine l = new JournalLine();
        l.setJournalEntry(e); l.setAccount(acc(code)); l.setDebit(amount); l.setCredit(BigDecimal.ZERO);
        e.getLines().add(l);
    }

    private void credit(JournalEntry e, String code, BigDecimal amount) {
        if (amount == null || amount.signum() == 0) return;
        JournalLine l = new JournalLine();
        l.setJournalEntry(e); l.setAccount(acc(code)); l.setDebit(BigDecimal.ZERO); l.setCredit(amount);
        e.getLines().add(l);
    }

    public JournalEntry postForPayroll(Payroll p) {
        JournalEntry e = new JournalEntry();
        e.setPayroll(p);
        e.setEmployee(p.getEmployee());
        e.setEntryDate(p.getPayDate());
        e.setDescription("Payroll run for " + p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName()
                + " (" + p.getPeriodBeginning() + " to " + p.getPeriodEnding() + ")");

        BigDecimal ficaMatch = p.getSocialSecurityTax().add(p.getMedicareTax());
        debit(e, "5000", p.getGrossPay());
        debit(e, "5010", ficaMatch);
        debit(e, "5020", p.getEmployerMatch());

        BigDecimal ficaEmployee = ficaMatch.multiply(BigDecimal.valueOf(2));
        credit(e, "2000", p.getFederalTax());
        credit(e, "2010", ficaEmployee);
        credit(e, "2020", p.getPaTax());
        credit(e, "2030", p.getLocalTax());
        credit(e, "2040", p.getLst());
        credit(e, "2050", p.getPaSui());
        BigDecimal retirement = p.getTraditional401k().add(p.getRoth()).add(p.getTraditionalCatchUp())
                .add(p.getRothCatchUp()).add(p.getEmployerMatch());
        credit(e, "2060", retirement);
        credit(e, "2070", p.getHealth());
        credit(e, "2080", p.getNetPay());

        return journalRepo.save(e);
    }

    /**
     * Posts a mirror-image journal entry (every debit becomes a credit and vice versa) for a
     * PROCESSED payroll that is being reversed. This zeroes out its ledger impact without ever
     * touching the original entry or using negative amounts — the textbook-correct way to reverse
     * a posted journal entry.
     */
    public JournalEntry reverseForPayroll(Payroll p) {
        JournalEntry e = new JournalEntry();
        e.setPayroll(p);
        e.setEmployee(p.getEmployee());
        e.setEntryDate(java.time.LocalDate.now());
        e.setDescription("REVERSAL of payroll run for " + p.getEmployee().getFirstName() + " " + p.getEmployee().getLastName()
                + " (" + p.getPeriodBeginning() + " to " + p.getPeriodEnding() + ")");

        BigDecimal ficaMatch = p.getSocialSecurityTax().add(p.getMedicareTax());
        credit(e, "5000", p.getGrossPay());
        credit(e, "5010", ficaMatch);
        credit(e, "5020", p.getEmployerMatch());

        BigDecimal ficaEmployee = ficaMatch.multiply(BigDecimal.valueOf(2));
        debit(e, "2000", p.getFederalTax());
        debit(e, "2010", ficaEmployee);
        debit(e, "2020", p.getPaTax());
        debit(e, "2030", p.getLocalTax());
        debit(e, "2040", p.getLst());
        debit(e, "2050", p.getPaSui());
        BigDecimal retirement = p.getTraditional401k().add(p.getRoth()).add(p.getTraditionalCatchUp())
                .add(p.getRothCatchUp()).add(p.getEmployerMatch());
        debit(e, "2060", retirement);
        debit(e, "2070", p.getHealth());
        debit(e, "2080", p.getNetPay());

        return journalRepo.save(e);
    }

    public List<JournalEntry> allEntries() { return journalRepo.findAllByOrderByEntryDateDescIdDesc(); }
    public List<JournalEntry> byEmployee(Long employeeId) { return journalRepo.findByEmployeeIdOrderByEntryDateDescIdDesc(employeeId); }

    public List<AccountBalance> balances() {
        List<Account> accounts = accountRepo.findAll();
        List<AccountBalance> out = new ArrayList<>();
        for (Account a : accounts) {
            List<JournalLine> lines = lineRepo.findByAccountId(a.getId());
            BigDecimal debitTotal = BigDecimal.ZERO, creditTotal = BigDecimal.ZERO;
            for (JournalLine l : lines) {
                debitTotal = debitTotal.add(l.getDebit());
                creditTotal = creditTotal.add(l.getCredit());
            }
            BigDecimal balance = "DEBIT".equals(a.getNormalSide())
                    ? debitTotal.subtract(creditTotal)
                    : creditTotal.subtract(debitTotal);
            out.add(new AccountBalance(a.getCode(), a.getName(), a.getType(), a.getNormalSide(), debitTotal, creditTotal, balance));
        }
        out.sort(Comparator.comparing(AccountBalance::code));
        return out;
    }
}
