package com.epms.payroll.service;

import com.epms.payroll.dto.PayrollRequest;
import com.epms.payroll.dto.PayrollResult;
import com.epms.payroll.entity.AuditLog;
import com.epms.payroll.entity.Employee;
import com.epms.payroll.entity.JournalEntry;
import com.epms.payroll.entity.Payroll;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.PayrollRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Positive and negative coverage for PayrollService: calculate/save, and the
 * status-transition guards on void/reverse that protect posted accounting entries.
 * Uses the real PayrollCalculator so the math is exercised end to end; every other
 * collaborator is mocked, matching this project's existing unit-test style.
 */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Payroll Processing")
class PayrollServiceTest {

    @Mock private EmployeeRepository employeeRepo;
    @Mock private PayrollRepository payrollRepo;
    @Mock private TaxAccountingService taxService;
    @Mock private PayrollAccountingService accountingService;
    @Mock private AuditLogService auditService;

    private PayrollService service() {
        return new PayrollService(employeeRepo, payrollRepo, new PayrollCalculator(), taxService, accountingService, auditService);
    }

    private Employee employee() {
        Employee e = new Employee();
        e.setId(42L);
        e.setFirstName("Steven");
        e.setLastName("Young");
        e.setHourlyRate(new BigDecimal("20.00"));
        return e;
    }

    private PayrollRequest request() {
        return new PayrollRequest(42L, LocalDate.of(2026, 8, 24), LocalDate.of(2026, 8, 30), LocalDate.of(2026, 9, 4),
                new BigDecimal("40"), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("10"), new BigDecimal("6.2"),
                new BigDecimal("1.45"), new BigDecimal("3.07"), new BigDecimal("1"), new BigDecimal("1"),
                new BigDecimal("0.07"), new BigDecimal("50"));
    }

    /** No cap in effect: hand back exactly the FICA base offered, so the wage-base branch is a no-op. */
    private void stubNoSocialSecurityCap() {
        when(taxService.capSocialSecurityWages(anyLong(), anyInt(), any(BigDecimal.class)))
                .thenAnswer(inv -> inv.getArgument(2));
    }

    @Test
    @Tag("positive")
    @Story("Calculate")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("calculate() returns gross/net pay for a known employee")
    @Description("A plain 40-regular-hour week at $20/hr with no benefits should produce the expected gross and net pay.")
    void calculateReturnsExpectedPay() {
        when(employeeRepo.findById(42L)).thenReturn(Optional.of(employee()));
        stubNoSocialSecurityCap();

        PayrollResult result = service().calculate(request());

        assertEquals(new BigDecimal("800.00"), result.grossPay());
        assertEquals(new BigDecimal("624.68"), result.netPay());
    }

    @Test
    @Tag("negative")
    @Story("Calculate")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("calculate() throws for an unknown employee id")
    @Description("A PayrollRequest referencing a non-existent employee must fail before any math is attempted.")
    void calculateThrowsForUnknownEmployee() {
        when(employeeRepo.findById(999L)).thenReturn(Optional.empty());
        PayrollRequest badRequest = new PayrollRequest(999L, LocalDate.now(), LocalDate.now(), LocalDate.now(),
                BigDecimal.TEN, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO);

        assertThrows(NoSuchElementException.class, () -> service().calculate(badRequest));
    }

    @Test
    @Tag("positive")
    @Story("Save")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("save() persists the payroll, posts a journal entry, and writes an audit record")
    @Description("Saving a calculated payroll must cascade into the accounting ledger and the audit trail exactly once each.")
    void saveCascadesToAccountingAndAudit() {
        when(employeeRepo.findById(42L)).thenReturn(Optional.of(employee()));
        stubNoSocialSecurityCap();
        when(payrollRepo.save(any(Payroll.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountingService.postForPayroll(any(Payroll.class))).thenReturn(new JournalEntry());
        when(auditService.record(any(), any(), any(), any(), any(), any(), any(), any())).thenReturn(new AuditLog());

        Payroll saved = service().save(request(), "qa-user");

        assertEquals(new BigDecimal("800.00"), saved.getGrossPay());
        verify(accountingService).postForPayroll(saved);
        verify(auditService).record(eq("PAYROLL"), any(), eq("CREATE"), isNull(), isNull(), any(), eq("qa-user"), isNull());
    }

    @Test
    @Tag("negative")
    @Story("Void")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("voidPayroll() refuses to void an already-PROCESSED payroll")
    @Description("Once a payroll has been processed its accounting entries are live; voiding must be blocked in favor of Reverse.")
    void voidRefusesProcessedPayroll() {
        Payroll processed = new Payroll();
        processed.setStatus("PROCESSED");
        when(payrollRepo.findById(5L)).thenReturn(Optional.of(processed));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service().voidPayroll(5L, "typo", "qa-user"));
        assertEquals("A processed or already-reversed payroll cannot be voided — use Reverse instead.", ex.getMessage());
        verify(payrollRepo, never()).save(any());
    }

    @Test
    @Tag("negative")
    @Story("Reverse")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("reversePayroll() refuses a payroll that was never PROCESSED")
    @Description("Only a payroll that actually posted to the ledger (status PROCESSED) may be reversed.")
    void reverseRefusesNonProcessedPayroll() {
        Payroll calculated = new Payroll();
        calculated.setStatus("CALCULATED");
        when(payrollRepo.findById(9L)).thenReturn(Optional.of(calculated));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service().reversePayroll(9L, "typo", "qa-user"));
        assertEquals("Only a PROCESSED payroll can be reversed.", ex.getMessage());
        verify(accountingService, never()).reverseForPayroll(any());
    }
}
