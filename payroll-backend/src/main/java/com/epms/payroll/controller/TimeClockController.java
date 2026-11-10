package com.epms.payroll.controller;

import com.epms.payroll.entity.TimeEntry;
import com.epms.payroll.service.TimeClockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/time-entries")
public class TimeClockController {

    private final TimeClockService service;

    public TimeClockController(TimeClockService service) {
        this.service = service;
    }

    @PostMapping("/clock-in")
    public TimeEntry clockIn(@RequestBody Map<String, Object> body) {
        Long employeeId = Long.valueOf(String.valueOf(body.get("employeeId")));
        return service.clockIn(employeeId);
    }

    @PostMapping("/{id}/clock-out")
    public TimeEntry clockOut(@PathVariable Long id) {
        return service.clockOut(id);
    }

    @GetMapping("/employee/{id}")
    public List<TimeEntry> byEmployee(@PathVariable Long id) {
        return service.byEmployee(id);
    }

    @GetMapping
    public List<TimeEntry> all() {
        return service.all();
    }
}
