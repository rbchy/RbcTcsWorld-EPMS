package com.epms.payroll.dto;
import java.math.BigDecimal;
public record AccountBalance(String code, String name, String type, String normalSide, BigDecimal debitTotal, BigDecimal creditTotal, BigDecimal balance){}
