package com.epms.payroll.controller;
import com.epms.payroll.entity.Employee; import com.epms.payroll.service.EmployeeService; import jakarta.servlet.http.HttpServletRequest; import org.springframework.web.bind.annotation.*; import java.util.List; import java.util.Map;
@RestController @RequestMapping("/api/employees") public class EmployeeController {private final EmployeeService s;public EmployeeController(EmployeeService x){s=x;}
 @GetMapping public List<Employee> list(@RequestParam(required=false) String q){return s.list(q);}
 @GetMapping("/{id}") public Employee get(@PathVariable Long id){return s.get(id);}
 @PostMapping public Employee add(@RequestBody Employee e){return s.save(e);}
 @PutMapping("/{id}") public Employee update(@PathVariable Long id,@RequestBody Employee e){e.setId(id);return s.save(e);}
 @PatchMapping("/{id}/status") public Employee setActive(@PathVariable Long id,@RequestBody Map<String,Object> body,HttpServletRequest req){return s.setActive(id, Boolean.parseBoolean(String.valueOf(body.get("active"))), performedBy(req));}
 @DeleteMapping("/{id}") public void delete(@PathVariable Long id){s.delete(id);}
 private String performedBy(HttpServletRequest req){Object u=req.getAttribute("username");return u==null?"system":String.valueOf(u);}
}
