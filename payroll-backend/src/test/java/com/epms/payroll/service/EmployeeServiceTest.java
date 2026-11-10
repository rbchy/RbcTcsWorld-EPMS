package com.epms.payroll.service;

import com.epms.payroll.entity.Employee;
import com.epms.payroll.repository.EmployeeRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Positive and negative coverage for EmployeeService: lookup, search, save,
 * and the active/inactive status toggle (which must also write an audit record).
 */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Employee Management")
class EmployeeServiceTest {

    @Mock private EmployeeRepository repo;
    @Mock private AuditLogService audit;

    private EmployeeService service() {
        return new EmployeeService(repo, audit);
    }

    private Employee employee(long id, String first, String last) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName(first);
        e.setLastName(last);
        e.setEmployeeNumber("EMP" + id);
        return e;
    }

    @Test
    @Tag("positive")
    @Story("Lookup")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("get() returns the employee when the id exists")
    @Description("A known employee id should resolve to the matching Employee row.")
    void getReturnsEmployeeWhenFound() {
        when(repo.findById(7L)).thenReturn(Optional.of(employee(7L, "Maria", "Garcia")));

        Employee result = service().get(7L);

        assertEquals("Maria", result.getFirstName());
        assertEquals("Garcia", result.getLastName());
    }

    @Test
    @Tag("negative")
    @Story("Lookup")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("get() throws when the id does not exist")
    @Description("Looking up a non-existent employee id must fail loudly instead of returning null.")
    void getThrowsWhenEmployeeMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service().get(999L));
    }

    @Test
    @Tag("positive")
    @Story("Search")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("list() with a blank query returns every employee")
    @Description("An empty or null search term should be treated as \"no filter\" and return the full roster.")
    void listWithBlankQueryReturnsEverything() {
        when(repo.findAll()).thenReturn(List.of(employee(1L, "A", "B"), employee(2L, "C", "D")));

        assertEquals(2, service().list("").size());
        assertEquals(2, service().list(null).size());
        verify(repo, never()).findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmployeeNumberContainingIgnoreCase(any(), any(), any());
    }

    @Test
    @Tag("positive")
    @Story("Search")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("list() with a search term filters by name or employee number")
    @Description("A non-blank query must be routed to the name/employee-number search, not findAll().")
    void listWithQueryDelegatesToSearch() {
        when(repo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmployeeNumberContainingIgnoreCase("garcia", "garcia", "garcia"))
                .thenReturn(List.of(employee(7L, "Maria", "Garcia")));

        List<Employee> result = service().list("garcia");

        assertEquals(1, result.size());
        verify(repo, never()).findAll();
    }

    @Test
    @Tag("positive")
    @Story("Status toggle")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("setActive() flips the flag and records an audit entry")
    @Description("Deactivating an employee must persist the new value and leave an audit trail of who changed it and when.")
    void setActiveTogglesStatusAndWritesAudit() {
        Employee existing = employee(7L, "Maria", "Garcia");
        existing.setActive(true);
        when(repo.findById(7L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Employee.class))).thenAnswer(inv -> inv.getArgument(0));

        Employee result = service().setActive(7L, false, "qa-user");

        assertFalse(result.isActive());
        verify(audit).record(eq("EMPLOYEE"), eq(7L), eq("STATUS_CHANGE"), eq("active"), eq(true), eq(false), eq("qa-user"), isNull());
    }

    @Test
    @Tag("negative")
    @Story("Status toggle")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("setActive() throws for an unknown employee and never touches the audit log")
    @Description("Toggling status on a non-existent employee id must fail before any audit record is written.")
    void setActiveThrowsWhenEmployeeMissing() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service().setActive(999L, false, "qa-user"));
        verify(audit, never()).record(any(), any(), any(), any(), any(), any(), any(), any());
    }
}
