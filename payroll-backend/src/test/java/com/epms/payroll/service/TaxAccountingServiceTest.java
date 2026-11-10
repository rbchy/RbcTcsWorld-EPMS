package com.epms.payroll.service;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/** Regression coverage for the Social Security wage-base cap that spans multiple payroll runs in a year. */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Tax Accounting")
class TaxAccountingServiceTest {

    @Mock private PayrollRepository payrollRepo;
    @Mock private EmployeeRepository employeeRepo;

    private Payroll payrollRow(int year, String gross, String health, String ssTax, String medTax) {
        Payroll p = new Payroll();
        Employee emp = new Employee();
        p.setEmployee(emp);
        p.setPayDate(LocalDate.of(year, 6, 15));
        p.setGrossPay(new BigDecimal(gross));
        p.setHealth(new BigDecimal(health));
        p.setSocialSecurityTax(new BigDecimal(ssTax));
        p.setMedicareTax(new BigDecimal(medTax));
        p.setFederalTaxableWages(new BigDecimal(gross));
        p.setFederalTax(BigDecimal.ZERO);
        p.setNetPay(new BigDecimal(gross));
        return p;
    }

    @Test
    @Tag("positive")
    @Story("Wage-base cap")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("A run well under the wage base is allowed at its full proposed wages")
    void wellUnderCapAllowsFullProposedWages() {
        when(payrollRepo.findByEmployeeId(1L)).thenReturn(List.of(
                payrollRow(2026, "2000.00", "0", "124.00", "29.00")));
        TaxAccountingService s = new TaxAccountingService(payrollRepo, employeeRepo);

        BigDecimal capped = s.capSocialSecurityWages(1L, 2026, new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("2000.00"), capped);
    }

    @Test
    @Tag("negative")
    @Story("Wage-base cap")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("A run that would cross the wage base is capped to the remaining room only")
    void wageBaseCapLimitsANewRunThatWouldCrossTheThreshold() {
        // Already earned $168,000 of SS-taxable wages this year; a new $2,000 run would cross the $168,600 base.
        when(payrollRepo.findByEmployeeId(1L)).thenReturn(List.of(
                payrollRow(2026, "168000.00", "0", "10416.00", "2436.00")));
        TaxAccountingService s = new TaxAccountingService(payrollRepo, employeeRepo);

        BigDecimal capped = s.capSocialSecurityWages(1L, 2026, new BigDecimal("2000.00"));
        assertEquals(new BigDecimal("600.00"), capped, "only $600 of room remains before the $168,600 wage base");
    }

    @Test
    @Tag("negative")
    @Story("Wage-base cap")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("Wages already at or over the cap stop any further Social Security tax")
    void wagesAlreadyAtOrOverCapStopsAnyFurtherSocialSecurityTax() {
        when(payrollRepo.findByEmployeeId(1L)).thenReturn(List.of(
                payrollRow(2026, "168600.00", "0", "10453.20", "2444.70")));
        TaxAccountingService s = new TaxAccountingService(payrollRepo, employeeRepo);

        BigDecimal capped = s.capSocialSecurityWages(1L, 2026, new BigDecimal("2000.00"));
        assertEquals(BigDecimal.ZERO, capped);
    }

    @Test
    @Tag("positive")
    @Story("YTD summary")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("YTD summary aggregates only the payroll rows from the requested year")
    void ytdSummaryAggregatesEveryPayrollInTheYear() {
        when(payrollRepo.findByEmployeeId(1L)).thenReturn(List.of(
                payrollRow(2026, "1000.00", "0", "62.00", "14.50"),
                payrollRow(2026, "1000.00", "0", "62.00", "14.50"),
                payrollRow(2025, "1000.00", "0", "62.00", "14.50")));
        TaxAccountingService s = new TaxAccountingService(payrollRepo, employeeRepo);

        var summary = s.summary(1L, 2026);
        assertEquals(new BigDecimal("2000.00"), summary.ytdGrossPay(), "only the two 2026 rows should count");
        assertEquals(new BigDecimal("124.00"), summary.ytdSocialSecurityTax());
        assertEquals(new BigDecimal("2000.00"), summary.ytdSocialSecurityWages());
    }
}
