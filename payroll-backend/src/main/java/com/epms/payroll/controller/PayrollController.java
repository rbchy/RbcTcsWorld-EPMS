package com.epms.payroll.controller;
import com.epms.payroll.dto.*; import com.epms.payroll.entity.Payroll; import com.epms.payroll.service.PayrollService; import jakarta.servlet.http.HttpServletRequest; import org.springframework.web.bind.annotation.*; import java.util.List; import java.util.Map;
@RestController @RequestMapping("/api/payroll") public class PayrollController {private final PayrollService s;public PayrollController(PayrollService x){s=x;}
 @PostMapping("/calculate") public PayrollResult calculate(@RequestBody PayrollRequest r){return s.calculate(r);}
 @PostMapping("/save") public Payroll save(@RequestBody PayrollRequest r,HttpServletRequest req){return s.save(r, performedBy(req));}
 @GetMapping public List<Payroll> all(){return s.all();}
 @GetMapping("/employee/{id}") public List<Payroll> byEmployee(@PathVariable Long id){return s.byEmployee(id);}
 @PatchMapping("/{id}/status") public Payroll status(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest req){return s.updateStatus(id, String.valueOf(body.get("status")), performedBy(req));}
 @PatchMapping("/{id}/void") public Payroll voidPayroll(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest req){return s.voidPayroll(id, String.valueOf(body.get("reason")), performedBy(req));}
 @PatchMapping("/{id}/reverse") public Payroll reversePayroll(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest req){return s.reversePayroll(id, String.valueOf(body.get("reason")), performedBy(req));}
 private String performedBy(HttpServletRequest req){Object u=req.getAttribute("username");return u==null?"system":String.valueOf(u);}
}
