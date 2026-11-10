package com.epms.payroll.controller;
import com.epms.payroll.dto.W2Data;
import com.epms.payroll.service.W2Service;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/w2")
public class W2Controller {
    private final W2Service s;
    public W2Controller(W2Service x) { s = x; }

    @GetMapping("/{employeeId}/{year}")
    public W2Data one(@PathVariable Long employeeId, @PathVariable int year) { return s.generate(employeeId, year); }

    @GetMapping("/year/{year}")
    public List<W2Data> byYear(@PathVariable int year) { return s.generateAll(year); }
}
