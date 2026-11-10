package com.epms.payroll;
import com.epms.payroll.dto.*; import com.epms.payroll.entity.Employee; import com.epms.payroll.service.PayrollCalculator;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName; import org.junit.jupiter.api.Tag; import org.junit.jupiter.api.Test; import java.math.BigDecimal; import java.time.LocalDate; import static org.junit.jupiter.api.Assertions.*;

/**
 * Golden regression test: reproduces the user's real uploaded paystub line-for-line.
 * Hourly rate $18.55, 40 regular hrs, 8 doubletime hrs, two extra-pay lines, standard
 * reference tax/deduction rates. If PayrollCalculator's math ever drifts, this fails.
 */
@Epic("EPMS Payroll Backend")
@Feature("Payroll Calculation")
class PayrollCalculatorTest {

    private Employee referenceEmployee() {
        Employee e = new Employee();
        e.setHourlyRate(new BigDecimal("18.55"));
        e.setHealthDeduction(new BigDecimal("82.34"));
        e.setTraditional401kPct(new BigDecimal("5"));
        e.setRothPct(new BigDecimal("1"));
        e.setTraditionalCatchUpPct(new BigDecimal("1"));
        e.setRothCatchUpPct(new BigDecimal("1"));
        return e;
    }

    private PayrollRequest referenceRequest() {
        return new PayrollRequest(null, LocalDate.of(2026, 8, 24), LocalDate.of(2026, 8, 30), LocalDate.of(2026, 9, 4),
                new BigDecimal("40"), new BigDecimal("8"), new BigDecimal("40"), new BigDecimal("8"),
                new BigDecimal("3"), new BigDecimal("6"), new BigDecimal("4.1143"), new BigDecimal("6.2"),
                new BigDecimal("1.45"), new BigDecimal("3.07"), new BigDecimal("1.10"), new BigDecimal("1"),
                new BigDecimal("0.0747"), new BigDecimal("60"));
    }

    @Test
    @Tag("positive")
    @Story("Golden regression")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Reference paystub: gross and net pay match the real uploaded paystub")
    void referencePaystubGrossAndNet() {
        var x = new PayrollCalculator().calculate(referenceEmployee(), referenceRequest());
        assertEquals(new BigDecimal("1206.80"), x.grossPay());
        assertEquals(new BigDecimal("1052.05"), x.federalTaxableWages());
        assertEquals(new BigDecimal("849.88"), x.netPay());
    }

    @Test
    @Tag("positive")
    @Story("Golden regression")
    @Severity(SeverityLevel.BLOCKER)
    @DisplayName("Reference paystub: every individual line item matches the real uploaded paystub")
    void referencePaystubEveryLineItem() {
        var x = new PayrollCalculator().calculate(referenceEmployee(), referenceRequest());
        assertEquals(new BigDecimal("1206.80"), x.grossPay(), "gross pay");
        assertEquals(new BigDecimal("1052.05"), x.federalTaxableWages(), "federal taxable wages");
        assertEquals(new BigDecimal("43.28"), x.federalTax(), "federal tax");
        assertEquals(new BigDecimal("69.72"), x.socialSecurityTax(), "social security tax");
        assertEquals(new BigDecimal("16.30"), x.medicareTax(), "medicare tax");
        assertEquals(new BigDecimal("34.52"), x.paTax(), "PA state tax");
        assertEquals(new BigDecimal("12.37"), x.localTax(), "local tax");
        assertEquals(new BigDecimal("1.00"), x.lst(), "LST");
        assertEquals(new BigDecimal("0.84"), x.paSui(), "PA SUI");
        assertEquals(new BigDecimal("82.34"), x.health(), "health deduction");
        assertEquals(new BigDecimal("60.34"), x.traditional401k(), "401(k)");
        assertEquals(new BigDecimal("12.07"), x.roth(), "Roth");
        assertEquals(new BigDecimal("12.07"), x.traditionalCatchUp(), "401(k) catch-up");
        assertEquals(new BigDecimal("12.07"), x.rothCatchUp(), "Roth catch-up");
        assertEquals(new BigDecimal("36.20"), x.employerMatch(), "employer 401(k) match");
        assertEquals(new BigDecimal("849.88"), x.netPay(), "net pay");
    }

    @Test
    @Tag("positive")
    @Story("Internal consistency")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Gross pay equals the sum of every earnings line")
    void grossPayIsSumOfEarningsLines() {
        var x = new PayrollCalculator().calculate(referenceEmployee(), referenceRequest());
        BigDecimal regular = new BigDecimal("18.55").multiply(new BigDecimal("40"));
        BigDecimal doubletime = new BigDecimal("37.10").multiply(new BigDecimal("8"));
        BigDecimal extra1 = new BigDecimal("3").multiply(new BigDecimal("40"));
        BigDecimal extra2 = new BigDecimal("6").multiply(new BigDecimal("8"));
        assertEquals(regular.add(doubletime).add(extra1).add(extra2), x.grossPay());
    }

    @Test
    @Tag("positive")
    @Story("Internal consistency")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("Net pay equals gross pay minus every deduction, with none omitted")
    void netPayEqualsGrossMinusEveryDeduction() {
        var x = new PayrollCalculator().calculate(referenceEmployee(), referenceRequest());
        BigDecimal totalDeductions = x.federalTax().add(x.socialSecurityTax()).add(x.medicareTax())
                .add(x.paTax()).add(x.localTax()).add(x.lst()).add(x.paSui()).add(x.health())
                .add(x.traditional401k()).add(x.roth()).add(x.traditionalCatchUp()).add(x.rothCatchUp());
        assertEquals(x.grossPay().subtract(totalDeductions), x.netPay());
    }
}
