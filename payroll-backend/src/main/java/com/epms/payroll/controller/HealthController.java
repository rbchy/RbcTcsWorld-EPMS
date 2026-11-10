package com.epms.payroll.controller;
import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api") public class HealthController {@GetMapping("/health") public Map<String,String> health(){return Map.of("status","UP","application","EPMS Payroll");}}
