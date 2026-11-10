package com.epms.payroll.service;
import com.epms.payroll.dto.W2Data;
import com.epms.payroll.entity.Employee;
import com.epms.payroll.entity.Payroll;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.PayrollRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Aggregates a full calendar year of saved Payroll rows per employee into W-2 box values. */
@Service
public class W2Service {
    private final EmployeeRepository employeeRepo;
    private final PayrollRepository payrollRepo;

    public W2Service(EmployeeRepository e, PayrollRepository p) { employeeRepo = e; payrollRepo = p; }

    private BigDecimal z(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    public W2Data generate(Long employeeId, int year) {
        Employee emp = employeeRepo.findById(employeeId).orElseThrow();
        List<Payroll> rows = payrollRepo.findByEmployeeId(employeeId).stream()
                .filter(p -> p.getPayDate() != null && p.getPayDate().getYear() == year)
                .toList();

        BigDecimal box1 = BigDecimal.ZERO, box2 = BigDecimal.ZERO, box3 = BigDecimal.ZERO, box4 = BigDecimal.ZERO,
                box5 = BigDecimal.ZERO, box6 = BigDecimal.ZERO, box16 = BigDecimal.ZERO, box17 = BigDecimal.ZERO,
                box18 = BigDecimal.ZERO, box19 = BigDecimal.ZERO, box12d = BigDecimal.ZERO, box12aa = BigDecimal.ZERO;

        for (Payroll p : rows) {
            BigDecimal ficaWages = z(p.getGrossPay()).subtract(z(p.getHealth()));
            box1 = box1.add(z(p.getFederalTaxableWages()));
            box2 = box2.add(z(p.getFederalTax()));
            box3 = box3.add(ficaWages);
            box4 = box4.add(z(p.getSocialSecurityTax()));
            box5 = box5.add(ficaWages);
            box6 = box6.add(z(p.getMedicareTax()));
            box16 = box16.add(ficaWages);
            box17 = box17.add(z(p.getPaTax()));
            box18 = box18.add(ficaWages);
            box19 = box19.add(z(p.getLocalTax()));
            box12d = box12d.add(z(p.getTraditional401k())).add(z(p.getTraditionalCatchUp()));
            box12aa = box12aa.add(z(p.getRoth())).add(z(p.getRothCatchUp()));
        }

        String name = emp.getFirstName() + " " + emp.getLastName();
        return new W2Data(employeeId, name, year, box1, box2, box3, box4, box5, box6, box16, box17, box18, box19,
                box12d, box12aa, BigDecimal.valueOf(rows.size()));
    }

    public List<W2Data> generateAll(int year) {
        List<W2Data> out = new ArrayList<>();
        for (Employee e : employeeRepo.findAll()) {
            W2Data d = generate(e.getId(), year);
            if (d.payrollCount().signum() > 0) out.add(d);
        }
        return out;
    }
}
