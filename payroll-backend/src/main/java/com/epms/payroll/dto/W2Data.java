package com.epms.payroll.dto;
import java.math.BigDecimal;
public record W2Data(Long employeeId, String employeeName, int year,
 BigDecimal box1Wages, BigDecimal box2FederalTax,
 BigDecimal box3SsWages, BigDecimal box4SsTax,
 BigDecimal box5MedicareWages, BigDecimal box6MedicareTax,
 BigDecimal box16StateWages, BigDecimal box17StateTax,
 BigDecimal box18LocalWages, BigDecimal box19LocalTax,
 BigDecimal box12dTraditional401k, BigDecimal box12aaRoth,
 BigDecimal payrollCount){}
