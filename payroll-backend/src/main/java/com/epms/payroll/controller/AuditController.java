package com.epms.payroll.controller;
import com.epms.payroll.entity.AuditLog;
import com.epms.payroll.service.AuditLogService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/audit")
public class AuditController {
    private final AuditLogService s;
    public AuditController(AuditLogService x) { s = x; }

    @GetMapping
    public List<AuditLog> all() { return s.all(); }

    @GetMapping("/entity")
    public List<AuditLog> forEntity(@RequestParam String type, @RequestParam Long id) {
        return s.forEntity(type, id);
    }
}
