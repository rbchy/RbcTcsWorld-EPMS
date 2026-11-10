package com.epms.payroll.controller;
import com.epms.payroll.dto.*; import com.epms.payroll.service.AuthService; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/auth") public class AuthController {private final AuthService s;public AuthController(AuthService x){s=x;}@PostMapping("/login") public LoginResponse login(@RequestBody LoginRequest r){return s.login(r);} }
