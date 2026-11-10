package com.epms.payroll.dto;
import java.math.BigDecimal;
public record PayrollResult(BigDecimal grossPay,BigDecimal federalTaxableWages,BigDecimal federalTax,BigDecimal socialSecurityTax,BigDecimal medicareTax,BigDecimal paTax,BigDecimal localTax,BigDecimal lst,BigDecimal paSui,BigDecimal health,BigDecimal traditional401k,BigDecimal roth,BigDecimal traditionalCatchUp,BigDecimal rothCatchUp,BigDecimal employerMatch,BigDecimal netPay){}
