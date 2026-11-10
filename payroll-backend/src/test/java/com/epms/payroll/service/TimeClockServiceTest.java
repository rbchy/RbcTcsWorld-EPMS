package com.epms.payroll.service;

import com.epms.payroll.entity.Employee;
import com.epms.payroll.entity.TimeEntry;
import com.epms.payroll.repository.EmployeeRepository;
import com.epms.payroll.repository.TimeEntryRepository;
import io.qameta.allure.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Positive and negative coverage for TimeClockService: clocking in/out and the
 * regular-vs-overtime split that clockOut() computes from the elapsed shift.
 */
@ExtendWith(MockitoExtension.class)
@Epic("EPMS Payroll Backend")
@Feature("Time Clock")
class TimeClockServiceTest {

    @Mock private EmployeeRepository employeeRepo;
    @Mock private TimeEntryRepository timeEntryRepo;

    private TimeClockService service() {
        return new TimeClockService(employeeRepo, timeEntryRepo);
    }

    private Employee employee(long id) {
        Employee e = new Employee();
        e.setId(id);
        e.setFirstName("Donna");
        e.setLastName("Lewis");
        return e;
    }

    @Test
    @Tag("positive")
    @Story("Clock in")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("clockIn() opens a new entry when the employee has no open entry")
    @Description("An employee with no currently-open time entry should get a fresh OPEN entry stamped with now().")
    void clockInOpensNewEntry() {
        when(timeEntryRepo.findFirstByEmployeeIdAndStatus(11L, "OPEN")).thenReturn(Optional.empty());
        when(employeeRepo.findById(11L)).thenReturn(Optional.of(employee(11L)));
        when(timeEntryRepo.save(any(TimeEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        TimeEntry entry = service().clockIn(11L);

        assertEquals("OPEN", entry.getStatus());
        assertNotNull(entry.getClockIn());
        assertEquals(11L, entry.getEmployee().getId());
    }

    @Test
    @Tag("negative")
    @Story("Clock in")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("clockIn() refuses a second clock-in while one is already open")
    @Description("An employee cannot have two open time entries at once — this would corrupt hours worked.")
    void clockInRefusedWhenAlreadyOpen() {
        when(timeEntryRepo.findFirstByEmployeeIdAndStatus(11L, "OPEN")).thenReturn(Optional.of(new TimeEntry()));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service().clockIn(11L));
        assertEquals("This employee is already clocked in.", ex.getMessage());
        verify(employeeRepo, never()).findById(any());
    }

    @Test
    @Tag("positive")
    @Story("Clock out")
    @Severity(SeverityLevel.CRITICAL)
    @DisplayName("clockOut() splits a >8-hour shift into 8 regular hours plus overtime")
    @Description("clockOut() stamps clockOut with the current time, so the fixture clocks in ~9 hours ago (minus a "
            + "30-minute break, ~8.5 worked hours) rather than pinning an exact past clock-out: regular hours must "
            + "cap at exactly 8.00 and the remainder must land in overtime, regardless of test-execution jitter.")
    void clockOutSplitsRegularAndOvertime() {
        TimeEntry open = new TimeEntry();
        open.setStatus("OPEN");
        open.setClockIn(LocalDateTime.now().minusHours(9));
        open.setBreakMinutes(30);
        when(timeEntryRepo.findById(77L)).thenReturn(Optional.of(open));
        when(timeEntryRepo.save(any(TimeEntry.class))).thenAnswer(inv -> inv.getArgument(0));

        TimeEntry closed = service().clockOut(77L);

        assertEquals("CLOSED", closed.getStatus());
        assertNotNull(closed.getClockOut());
        assertEquals(new BigDecimal("8.00"), closed.getRegularHours(), "a shift over 8 worked hours always caps regular hours at 8.00");
        assertTrue(closed.getOvertimeHours().compareTo(BigDecimal.ZERO) > 0, "the remaining ~0.5 worked hours must show up as overtime");
    }

    @Test
    @Tag("negative")
    @Story("Clock out")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("clockOut() refuses an entry that is already CLOSED")
    @Description("Clocking out twice on the same entry must be rejected, not silently overwrite the recorded hours.")
    void clockOutRefusedWhenAlreadyClosed() {
        TimeEntry closed = new TimeEntry();
        closed.setStatus("CLOSED");
        when(timeEntryRepo.findById(77L)).thenReturn(Optional.of(closed));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service().clockOut(77L));
        assertEquals("This time entry is already closed.", ex.getMessage());
        verify(timeEntryRepo, never()).save(any());
    }

    @Test
    @Tag("negative")
    @Story("Clock out")
    @Severity(SeverityLevel.NORMAL)
    @DisplayName("clockOut() throws for an unknown time-entry id")
    @Description("An id that does not correspond to any time entry must fail rather than clocking out an arbitrary row.")
    void clockOutThrowsForUnknownEntry() {
        when(timeEntryRepo.findById(404L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> service().clockOut(404L));
    }
}
