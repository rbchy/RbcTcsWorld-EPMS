package com.epms.payroll.dto;
import java.math.BigDecimal;
public record YtdTaxSummary(Long employeeId, int year, BigDecimal ytdSocialSecurityWages, BigDecimal ytdSocialSecurityTax,
 BigDecimal socialSecurityWageBase, BigDecimal remainingSsWageRoom, BigDecimal ytdMedicareWages, BigDecimal ytdMedicareTax,
 BigDecimal ytdFederalTaxableWages, BigDecimal ytdFederalTax, BigDecimal ytdGrossPay, BigDecimal ytdNetPay){}
