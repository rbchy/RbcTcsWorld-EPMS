package com.epms.payroll.controller;
import com.epms.payroll.dto.YtdTaxSummary;
import com.epms.payroll.service.TaxAccountingService;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController @RequestMapping("/api/tax")
public class TaxController {
    private final TaxAccountingService s;
    public TaxController(TaxAccountingService x) { s = x; }

    @GetMapping("/ytd/{employeeId}")
    public YtdTaxSummary ytd(@PathVariable Long employeeId, @RequestParam(required = false) Integer year) {
        int y = year != null ? year : LocalDate.now().getYear();
        return s.summary(employeeId, y);
    }

    @GetMapping("/ytd-all")
    public List<YtdTaxSummary> ytdAll(@RequestParam(required = false) Integer year) {
        int y = year != null ? year : LocalDate.now().getYear();
        return s.summaryAll(y);
    }
}
