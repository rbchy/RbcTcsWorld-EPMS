package com.epms.payroll.dto;
import java.math.BigDecimal; import java.time.LocalDate;
public record PayrollRequest(Long employeeId, LocalDate periodBeginning, LocalDate periodEnding, LocalDate payDate,
 BigDecimal regularHours, BigDecimal doubletimeHours, BigDecimal extraHours1, BigDecimal extraHours2,
 BigDecimal extraRate1, BigDecimal extraRate2, BigDecimal federalRate, BigDecimal socialSecurityRate,
 BigDecimal medicareRate, BigDecimal paRate, BigDecimal localRate, BigDecimal lst, BigDecimal paSuiRate, BigDecimal employerMatchRate){}
