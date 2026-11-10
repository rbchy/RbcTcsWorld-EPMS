package com.epms.desktop.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Employee {
    public Long id;
    public String employeeNumber, firstName, middleName, lastName, email, phone, address, city, state, zipCode,
            department, jobTitle, employmentStatus, payType, payFrequency, filingStatus;
    public LocalDate hireDate;
    public BigDecimal hourlyRate = BigDecimal.ZERO, traditional401kPct = BigDecimal.ZERO, rothPct = BigDecimal.ZERO,
            traditionalCatchUpPct = BigDecimal.ZERO, rothCatchUpPct = BigDecimal.ZERO, healthDeduction = BigDecimal.ZERO;
    public boolean active = true;

    public String displayName() { return employeeNumber + " - " + firstName + " " + lastName; }
    public String toString() { return displayName(); }
}
