package com.epms.payroll.service;

import com.epms.payroll.dto.W2Data;
import com.epms.payroll.entity.Employee;
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
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/** Positive and negative coverage for W2Service: aggregating a calendar year of payrolls into W-2 box totals. */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("W-2 Generation")
class W2ServiceTest {

    @Mock private EmployeeRepository employeeRepo;
    @Mock private PayrollRepository payrollRepo;

    private W2Service service() {
        return new W2Service(employeeRepo, payrollRepo);
    }

    private Employee employee(long id) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName("Carol");
        e.setLastName("Walker");
        return e;
    }

    private Payroll payroll(LocalDate payDate, String gross, String fed, String ss, String med, String pa, String local, String k401, String roth) {
        Payroll p = new Payroll();
        p.setPayDate(payDate);
        p.setGrossPay(new BigDecimal(gross));
        p.setHealth(BigDecimal.ZERO);
        p.setFederalTaxableWages(new BigDecimal(gross));
        p.setFederalTax(new BigDecimal(fed));
        p.setSocialSecurityTax(new BigDecimal(ss));
        p.setMedicareTax(new BigDecimal(med));
        p.setPaTax(new BigDecimal(pa));
        p.setLocalTax(new BigDecimal(local));
        p.setTraditional401k(new BigDecimal(k401));
        p.setTraditionalCatchUp(BigDecimal.ZERO);
        p.setRoth(new BigDecimal(roth));
        p.setRothCatchUp(BigDecimal.ZERO);
        return p;
    }

    @Test
    @Tag("positive")
    @Story("Annual aggregation")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("generate() sums every box across all payrolls in the requested year, and only that year")
    void generateSumsOnlyTheRequestedYear() {
        when(employeeRepo.findById(5L)).thenReturn(Optional.of(employee(5L)));
        when(payrollRepo.findByEmployeeId(5L)).thenReturn(List.of(
                payroll(LocalDate.of(2026, 3, 15), "1000.00", "100.00", "62.00", "14.50", "30.70", "10.00", "50.00", "20.00"),
                payroll(LocalDate.of(2026, 6, 15), "2000.00", "200.00", "124.00", "29.00", "61.40", "20.00", "100.00", "40.00"),
                payroll(LocalDate.of(2025, 6, 15), "5000.00", "500.00", "310.00", "72.50", "153.50", "50.00", "250.00", "100.00")));

        W2Data w2 = service().generate(5L, 2026);

        assertEquals(new BigDecimal("3000.00"), w2.box1Wages(), "box1: only the two 2026 rows");
        assertEquals(new BigDecimal("300.00"), w2.box2FederalTax());
        assertEquals(new BigDecimal("3000.00"), w2.box3SsWages());
        assertEquals(new BigDecimal("186.00"), w2.box4SsTax());
        assertEquals(new BigDecimal("43.50"), w2.box6MedicareTax());
        assertEquals(new BigDecimal("92.10"), w2.box17StateTax());
        assertEquals(new BigDecimal("30.00"), w2.box19LocalTax());
        assertEquals(new BigDecimal("150.00"), w2.box12dTraditional401k(), "401(k) + catch-up across both 2026 runs");
        assertEquals(new BigDecimal("60.00"), w2.box12aaRoth());
        assertEquals(new BigDecimal(2), w2.payrollCount());
    }

    @Test
    @Tag("negative")
    @Story("Annual aggregation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("generate() for a year with no payroll rows returns an all-zero W-2, not an error")
    void generateWithNoRowsInYearReturnsZeroedW2() {
        when(employeeRepo.findById(5L)).thenReturn(Optional.of(employee(5L)));
        when(payrollRepo.findByEmployeeId(5L)).thenReturn(List.of(
                payroll(LocalDate.of(2025, 6, 15), "5000.00", "500.00", "310.00", "72.50", "153.50", "50.00", "250.00", "100.00")));

        W2Data w2 = service().generate(5L, 2026);

        assertEquals(BigDecimal.ZERO, w2.box1Wages());
        assertEquals(BigDecimal.ZERO, w2.payrollCount());
    }

    @Test
    @Tag("negative")
    @Story("Annual aggregation")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("generate() throws for an unknown employee id")
    void generateThrowsForUnknownEmployee() {
        when(employeeRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service().generate(999L, 2026));
    }

    @Test
    @Tag("positive")
    @Story("Company-wide W-2 run")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("generateAll() only includes employees who actually had a payroll that year")
    void generateAllSkipsEmployeesWithNoPayrollsThatYear() {
        Employee withPay = employee(5L);
        Employee withoutPay = employee(6L);
        withoutPay.setFirstName("Steven");
        withoutPay.setLastName("Young");
        when(employeeRepo.findAll()).thenReturn(List.of(withPay, withoutPay));
        when(employeeRepo.findById(5L)).thenReturn(Optional.of(withPay));
        when(employeeRepo.findById(6L)).thenReturn(Optional.of(withoutPay));
        when(payrollRepo.findByEmployeeId(5L)).thenReturn(List.of(
                payroll(LocalDate.of(2026, 3, 15), "1000.00", "100.00", "62.00", "14.50", "30.70", "10.00", "50.00", "20.00")));
        when(payrollRepo.findByEmployeeId(6L)).thenReturn(List.of());

        List<W2Data> all = service().generateAll(2026);

        assertEquals(1, all.size(), "employee #6 had zero 2026 payrolls and must be excluded");
        assertEquals(5L, all.get(0).employeeId());
    }
}
