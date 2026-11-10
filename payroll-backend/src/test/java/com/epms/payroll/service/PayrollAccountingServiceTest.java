package com.epms.payroll.service;

import com.epms.payroll.entity.*;
import com.epms.payroll.repository.*;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Every posted journal entry must balance — total debits must equal total credits, always.
 * This is the accounting-side regression companion to PayrollCalculatorTest's golden paystub.
 */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Payroll Accounting")
class PayrollAccountingServiceTest {

    @Mock private AccountRepository accountRepo;
    @Mock private JournalEntryRepository journalRepo;
    @Mock private JournalLineRepository lineRepo;

    private PayrollAccountingService service;

    private Payroll referencePayroll() {
        Employee emp = new Employee();
        emp.setFirstName("John");
        emp.setLastName("Smith");
        Payroll p = new Payroll();
        p.setEmployee(emp);
        p.setPeriodBeginning(LocalDate.of(2026, 8, 24));
        p.setPeriodEnding(LocalDate.of(2026, 8, 30));
        p.setPayDate(LocalDate.of(2026, 9, 4));
        p.setGrossPay(new BigDecimal("1206.80"));
        p.setFederalTax(new BigDecimal("43.28"));
        p.setSocialSecurityTax(new BigDecimal("69.72"));
        p.setMedicareTax(new BigDecimal("16.30"));
        p.setPaTax(new BigDecimal("34.52"));
        p.setLocalTax(new BigDecimal("12.37"));
        p.setLst(new BigDecimal("1.00"));
        p.setPaSui(new BigDecimal("0.84"));
        p.setHealth(new BigDecimal("82.34"));
        p.setTraditional401k(new BigDecimal("60.34"));
        p.setRoth(new BigDecimal("12.07"));
        p.setTraditionalCatchUp(new BigDecimal("12.07"));
        p.setRothCatchUp(new BigDecimal("12.07"));
        p.setEmployerMatch(new BigDecimal("36.20"));
        p.setNetPay(new BigDecimal("849.88"));
        return p;
    }

    private void stubAccountsAndSave() {
        when(accountRepo.findByCode(anyString())).thenAnswer(inv -> {
            String code = inv.getArgument(0);
            Account a = new Account();
            a.setCode(code);
            a.setName(code);
            a.setNormalSide(code.startsWith("5") || code.startsWith("1") ? "DEBIT" : "CREDIT");
            a.setType(code.startsWith("5") ? "EXPENSE" : code.startsWith("1") ? "ASSET" : "LIABILITY");
            return Optional.of(a);
        });
        when(journalRepo.save(any(JournalEntry.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new PayrollAccountingService(accountRepo, journalRepo, lineRepo);
    }

    private BigDecimal sumDebits(JournalEntry e) {
        return e.getLines().stream().map(JournalLine::getDebit).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumCredits(JournalEntry e) {
        return e.getLines().stream().map(JournalLine::getCredit).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Test
    @Tag("positive")
    @Story("Posting")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("A posted payroll journal entry always balances (debits == credits)")
    void postedEntryBalancesDebitsAgainstCredits() {
        stubAccountsAndSave();
        JournalEntry e = service.postForPayroll(referencePayroll());

        BigDecimal debits = sumDebits(e);
        BigDecimal credits = sumCredits(e);

        assertEquals(debits, credits, "a payroll journal entry must always balance");
        assertEquals(new BigDecimal("1329.02"), debits, "gross + employer FICA match + employer 401k match");
    }

    @Test
    @Tag("positive")
    @Story("Reversal")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A reversal entry mirrors and balances against the original posting")
    void reversalEntryAlsoBalancesAndMirrorsTheOriginal() {
        stubAccountsAndSave();
        JournalEntry original = service.postForPayroll(referencePayroll());
        JournalEntry reversal = service.reverseForPayroll(referencePayroll());

        assertEquals(sumDebits(original), sumCredits(reversal), "the reversal must credit exactly what the original debited");
        assertEquals(sumCredits(original), sumDebits(reversal), "the reversal must debit exactly what the original credited");
        assertEquals(sumDebits(reversal), sumCredits(reversal), "the reversal entry itself must also balance");
    }
}
