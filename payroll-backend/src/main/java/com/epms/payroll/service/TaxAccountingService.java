package com.epms.payroll.service;
import com.epms.payroll.dto.YtdTaxSummary;
import com.epms.payroll.entity.Payroll;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Tracks year-to-date taxable wages per employee so the Social Security wage-base
 * cap can be applied across multiple payroll runs in the same calendar year.
 */
@Service
public class TaxAccountingService {
    public static final BigDecimal SOCIAL_SECURITY_WAGE_BASE = new BigDecimal("168600.00");

    private final PayrollRepository payrollRepo;
    private final EmployeeRepository employeeRepo;

    public TaxAccountingService(PayrollRepository p, EmployeeRepository e) { payrollRepo = p; employeeRepo = e; }

    private BigDecimal z(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private BigDecimal ficaWagesOf(Payroll p) { return z(p.getGrossPay()).subtract(z(p.getHealth())); }

    public List<Payroll> yearToDate(Long employeeId, int year) {
        return payrollRepo.findByEmployeeId(employeeId).stream()
                .filter(p -> p.getPayDate() != null && p.getPayDate().getYear() == year)
                .toList();
    }

    public BigDecimal ytdSocialSecurityWagesBefore(Long employeeId, int year) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Payroll p : yearToDate(employeeId, year)) sum = sum.add(ficaWagesOf(p));
        return sum;
    }

    /** Caps the FICA wages for a NEW payroll run against what has already been earned this year. */
    public BigDecimal capSocialSecurityWages(Long employeeId, int year, BigDecimal proposedFicaWages) {
        BigDecimal already = ytdSocialSecurityWagesBefore(employeeId, year);
        BigDecimal room = SOCIAL_SECURITY_WAGE_BASE.subtract(already);
        if (room.signum() <= 0) return BigDecimal.ZERO;
        return proposedFicaWages.min(room).setScale(2, RoundingMode.HALF_UP);
    }

    public YtdTaxSummary summary(Long employeeId, int year) {
        List<Payroll> rows = yearToDate(employeeId, year);
        BigDecimal ssWages = BigDecimal.ZERO, ssTax = BigDecimal.ZERO, medWages = BigDecimal.ZERO, medTax = BigDecimal.ZERO,
                fedWages = BigDecimal.ZERO, fedTax = BigDecimal.ZERO, gross = BigDecimal.ZERO, net = BigDecimal.ZERO;
        for (Payroll p : rows) {
            BigDecimal ficaWages = ficaWagesOf(p);
            ssWages = ssWages.add(ficaWages);
            medWages = medWages.add(ficaWages);
            ssTax = ssTax.add(z(p.getSocialSecurityTax()));
            medTax = medTax.add(z(p.getMedicareTax()));
            fedWages = fedWages.add(z(p.getFederalTaxableWages()));
            fedTax = fedTax.add(z(p.getFederalTax()));
            gross = gross.add(z(p.getGrossPay()));
            net = net.add(z(p.getNetPay()));
        }
        BigDecimal remaining = SOCIAL_SECURITY_WAGE_BASE.subtract(ssWages);
        if (remaining.signum() < 0) remaining = BigDecimal.ZERO;
        return new YtdTaxSummary(employeeId, year, ssWages, ssTax, SOCIAL_SECURITY_WAGE_BASE, remaining,
                medWages, medTax, fedWages, fedTax, gross, net);
    }

    public List<YtdTaxSummary> summaryAll(int year) {
        List<YtdTaxSummary> out = new java.util.ArrayList<>();
        for (com.epms.payroll.entity.Employee e : employeeRepo.findAll()) {
            YtdTaxSummary s = summary(e.getId(), year);
            if (!yearToDate(e.getId(), year).isEmpty()) out.add(s);
        }
        return out;
    }
}
