package com.epms.payroll.config;

import com.epms.payroll.dto.PayrollRequest;
import com.epms.payroll.entity.Employee;
import com.epms.payroll.entity.TimeEntry;
import com.epms.payroll.entity.User;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.PayrollRepository;
import com.epms.payroll.repository.TimeEntryRepository;
import com.epms.payroll.repository.UserRepository;
import com.epms.payroll.service.PayrollService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Bootstraps the admin account (existing behaviour, unchanged) and, only when a
 * table is empty, seeds 30 realistic demo rows into it. Payroll rows are created
 * through the real PayrollService.save(...) so JournalEntry/JournalLine/AuditLog
 * records cascade-populate exactly the way a real payroll run would produce them.
 * Every seed step is guarded independently, so re-running the app never duplicates
 * data and never disturbs rows a user has already created or edited.
 */
@Configuration
public class DataInitializer {

    private static final int DEMO_ROW_COUNT = 30;

    @Bean
    CommandLineRunner init(UserRepository userRepo, PasswordEncoder enc, EmployeeRepository employeeRepo,
                            TimeEntryRepository timeEntryRepo, PayrollRepository payrollRepo, PayrollService payrollService) {
        return args -> {
            seedAdmin(userRepo, enc);
            List<Employee> employees = seedEmployees(employeeRepo);
            seedTimeEntries(timeEntryRepo, employees);
            seedPayrolls(payrollRepo, payrollService, employees);
            seedDemoUsers(userRepo, enc);
        };
    }

    private void seedAdmin(UserRepository repo, PasswordEncoder enc) {
        if (repo.findByUsername("admin").isEmpty()) {
            User u = new User();
            u.setUsername("admin");
            u.setPasswordHash(enc.encode("admin123"));
            u.setRole("ADMIN");
            repo.save(u);
        }
    }

    // These arrays are also the source of truth mirrored by the dropdown lists in
    // the JavaFX EmployeeView form, so seeded data always matches a legal dropdown choice.
    private static final String[] DEPARTMENTS = {"Packaging", "Production", "Quality Assurance", "Warehouse", "Maintenance", "Administration"};
    private static final String[] JOB_TITLES = {"Packaging Operator", "Line Leader", "QA Inspector", "Production Supervisor", "Machine Operator", "Warehouse Associate", "Maintenance Technician", "Shift Supervisor", "Material Handler", "Quality Technician"};
    private static final String[] STATES = {"PA", "NJ", "NY", "OH", "MD", "DE"};
    private static final String[] EMPLOYMENT_STATUSES = {"ACTIVE", "ACTIVE", "ACTIVE", "ACTIVE", "ON_LEAVE", "INACTIVE", "TERMINATED"};
    private static final String[] PAY_TYPES = {"HOURLY", "HOURLY", "HOURLY", "SALARY"};
    private static final String[] PAY_FREQUENCIES = {"WEEKLY", "BIWEEKLY", "SEMIMONTHLY", "MONTHLY"};
    private static final String[] FILING_STATUSES = {"SINGLE", "MARRIED_FILING_JOINTLY", "MARRIED_FILING_SEPARATELY", "HEAD_OF_HOUSEHOLD"};
    // 30 distinct first/last names -> 30 distinct demo employees (indexed directly, not modulo).
    private static final String[] FIRST_NAMES = {"James", "Maria", "Robert", "Linda", "Michael", "Patricia", "David", "Barbara", "John", "Jennifer",
            "William", "Susan", "Richard", "Jessica", "Joseph", "Sarah", "Thomas", "Karen", "Charles", "Nancy",
            "Daniel", "Lisa", "Matthew", "Betty", "Anthony", "Mark", "Donna", "Paul", "Carol", "Steven"};
    private static final String[] LAST_NAMES = {"Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez",
            "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin",
            "Lee", "Perez", "Thompson", "White", "Harris", "Clark", "Lewis", "Robinson", "Walker", "Young"};

    private List<Employee> seedEmployees(EmployeeRepository repo) {
        if (repo.count() > 0) return repo.findAll();
        List<Employee> saved = new ArrayList<>();
        for (int i = 0; i < DEMO_ROW_COUNT; i++) {
            Employee e = new Employee();
            e.setEmployeeNumber(String.format("EMP%04d", 1000 + i));
            e.setFirstName(FIRST_NAMES[i]);
            e.setLastName(LAST_NAMES[i]);
            e.setEmail((FIRST_NAMES[i] + "." + LAST_NAMES[i] + "@epms-demo.com").toLowerCase());
            e.setPhone(String.format("(717) 555-%04d", 1000 + i));
            e.setAddress((100 + i) + " Main Street");
            e.setCity("Harrisburg");
            e.setState(STATES[i % STATES.length]);
            e.setZipCode(String.format("17%03d", 100 + i));
            e.setDepartment(DEPARTMENTS[i % DEPARTMENTS.length]);
            e.setJobTitle(JOB_TITLES[i % JOB_TITLES.length]);
            e.setHireDate(LocalDate.of(2018 + (i % 7), 1 + (i % 12), 1 + (i % 28)));
            e.setEmploymentStatus(EMPLOYMENT_STATUSES[i % EMPLOYMENT_STATUSES.length]);
            e.setPayType(PAY_TYPES[i % PAY_TYPES.length]);
            e.setHourlyRate(BigDecimal.valueOf(15.50 + (i % 10) * 1.25).setScale(2, RoundingMode.HALF_UP));
            e.setPayFrequency(PAY_FREQUENCIES[i % PAY_FREQUENCIES.length]);
            e.setFilingStatus(FILING_STATUSES[i % FILING_STATUSES.length]);
            e.setTraditional401kPct(BigDecimal.valueOf(i % 6));
            e.setRothPct(BigDecimal.valueOf(i % 3));
            e.setTraditionalCatchUpPct(i >= 15 ? BigDecimal.ONE : BigDecimal.ZERO);
            e.setRothCatchUpPct(i >= 20 ? BigDecimal.ONE : BigDecimal.ZERO);
            e.setHealthDeduction(BigDecimal.valueOf(40 + (i % 5) * 10));
            e.setActive(!"TERMINATED".equals(e.getEmploymentStatus()) && !"INACTIVE".equals(e.getEmploymentStatus()));
            saved.add(repo.save(e));
        }
        return saved;
    }

    private void seedTimeEntries(TimeEntryRepository repo, List<Employee> employees) {
        if (repo.count() > 0 || employees.isEmpty()) return;
        LocalDate weekStart = LocalDate.now().minusDays(7);
        for (int i = 0; i < DEMO_ROW_COUNT; i++) {
            Employee emp = employees.get(i % employees.size());
            LocalDate day = weekStart.plusDays(i % 5);
            TimeEntry t = new TimeEntry();
            t.setEmployee(emp);
            t.setWorkDate(day);
            t.setClockIn(day.atTime(8, 0));
            t.setClockOut(day.atTime(16, 30));
            t.setBreakMinutes(30);
            t.setRegularHours(BigDecimal.valueOf(8));
            t.setOvertimeHours(i % 7 == 0 ? BigDecimal.valueOf(2) : BigDecimal.ZERO);
            t.setDoubletimeHours(BigDecimal.ZERO);
            t.setStatus("CLOSED");
            repo.save(t);
        }
    }

    private void seedPayrolls(PayrollRepository repo, PayrollService service, List<Employee> employees) {
        if (repo.count() > 0 || employees.isEmpty()) return;
        LocalDate periodBegin = LocalDate.now().minusDays(13);
        LocalDate periodEnd = LocalDate.now().minusDays(7);
        LocalDate payDate = LocalDate.now().minusDays(2);
        for (int i = 0; i < DEMO_ROW_COUNT; i++) {
            Employee emp = employees.get(i % employees.size());
            PayrollRequest r = new PayrollRequest(
                    emp.getId(), periodBegin, periodEnd, payDate,
                    BigDecimal.valueOf(40),
                    i % 6 == 0 ? BigDecimal.valueOf(4) : BigDecimal.ZERO,
                    i % 5 == 0 ? BigDecimal.valueOf(4) : BigDecimal.ZERO,
                    i % 9 == 0 ? BigDecimal.valueOf(2) : BigDecimal.ZERO,
                    BigDecimal.valueOf(3), BigDecimal.valueOf(6),
                    new BigDecimal("4.1143"), new BigDecimal("6.2"), new BigDecimal("1.45"),
                    new BigDecimal("3.07"), new BigDecimal("1.10"), BigDecimal.ONE,
                    new BigDecimal("0.0747"), new BigDecimal("60"));
            try {
                service.save(r, "system-seed");
            } catch (Exception ignored) {
                // A single bad demo row (e.g. an inactive employee) should never block startup.
            }
        }
    }

    private static final String[] ROLES = {"ADMIN", "PAYROLL_ADMIN", "HR", "MANAGER", "EMPLOYEE", "VIEWER"};

    private void seedDemoUsers(UserRepository repo, PasswordEncoder enc) {
        if (repo.count() > 1) return; // only "admin" should exist at this point on a fresh run
        for (int i = 1; i <= DEMO_ROW_COUNT; i++) {
            String username = "demo" + i;
            if (repo.findByUsername(username).isPresent()) continue;
            User u = new User();
            u.setUsername(username);
            u.setPasswordHash(enc.encode("demo123"));
            u.setRole(ROLES[i % ROLES.length]);
            repo.save(u);
        }
    }
}
