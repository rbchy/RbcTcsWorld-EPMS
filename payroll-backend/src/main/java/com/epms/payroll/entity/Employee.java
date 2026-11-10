package com.epms.payroll.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name="employees")
public class Employee {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String employeeNumber;
    @Column(nullable=false) private String firstName;
    private String middleName;
    @Column(nullable=false) private String lastName;
    private String email, phone, address, city, state, zipCode, department, jobTitle;
    private LocalDate hireDate;
    private String employmentStatus="ACTIVE";
    private String payType="HOURLY";
    @Column(precision=12,scale=2) private BigDecimal hourlyRate=BigDecimal.ZERO;
    private String payFrequency="WEEKLY";
    private String filingStatus="MARRIED_FILING_JOINTLY";
    @Column(precision=6,scale=3) private BigDecimal traditional401kPct=BigDecimal.ZERO;
    @Column(precision=6,scale=3) private BigDecimal rothPct=BigDecimal.ZERO;
    @Column(precision=6,scale=3) private BigDecimal traditionalCatchUpPct=BigDecimal.ZERO;
    @Column(precision=6,scale=3) private BigDecimal rothCatchUpPct=BigDecimal.ZERO;
    @Column(precision=12,scale=2) private BigDecimal healthDeduction=BigDecimal.ZERO;
    @Column(nullable=false) private boolean active=true;
    public Long getId(){return id;} public void setId(Long v){id=v;} public String getEmployeeNumber(){return employeeNumber;} public void setEmployeeNumber(String v){employeeNumber=v;}
    public String getFirstName(){return firstName;} public void setFirstName(String v){firstName=v;} public String getMiddleName(){return middleName;} public void setMiddleName(String v){middleName=v;}
    public String getLastName(){return lastName;} public void setLastName(String v){lastName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public String getCity(){return city;} public void setCity(String v){city=v;} public String getState(){return state;} public void setState(String v){state=v;} public String getZipCode(){return zipCode;} public void setZipCode(String v){zipCode=v;}
    public String getDepartment(){return department;} public void setDepartment(String v){department=v;} public String getJobTitle(){return jobTitle;} public void setJobTitle(String v){jobTitle=v;}
    public LocalDate getHireDate(){return hireDate;} public void setHireDate(LocalDate v){hireDate=v;} public String getEmploymentStatus(){return employmentStatus;} public void setEmploymentStatus(String v){employmentStatus=v;}
    public String getPayType(){return payType;} public void setPayType(String v){payType=v;} public BigDecimal getHourlyRate(){return hourlyRate;} public void setHourlyRate(BigDecimal v){hourlyRate=v;}
    public String getPayFrequency(){return payFrequency;} public void setPayFrequency(String v){payFrequency=v;} public String getFilingStatus(){return filingStatus;} public void setFilingStatus(String v){filingStatus=v;}
    public BigDecimal getTraditional401kPct(){return traditional401kPct;} public void setTraditional401kPct(BigDecimal v){traditional401kPct=v;}
    public BigDecimal getRothPct(){return rothPct;} public void setRothPct(BigDecimal v){rothPct=v;} public BigDecimal getTraditionalCatchUpPct(){return traditionalCatchUpPct;} public void setTraditionalCatchUpPct(BigDecimal v){traditionalCatchUpPct=v;}
    public BigDecimal getRothCatchUpPct(){return rothCatchUpPct;} public void setRothCatchUpPct(BigDecimal v){rothCatchUpPct=v;} public BigDecimal getHealthDeduction(){return healthDeduction;} public void setHealthDeduction(BigDecimal v){healthDeduction=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
}
