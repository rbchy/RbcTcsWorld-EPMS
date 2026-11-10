package com.epms.payroll.entity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity @Table(name="journal_entries")
public class JournalEntry {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne private Payroll payroll;
    @ManyToOne(optional=false) private Employee employee;
    private LocalDate entryDate;
    private String description;
    @OneToMany(mappedBy="journalEntry", cascade=CascadeType.ALL, orphanRemoval=true, fetch=FetchType.EAGER)
    private List<JournalLine> lines = new ArrayList<>();
    public Long getId(){return id;}
    public Payroll getPayroll(){return payroll;} public void setPayroll(Payroll v){payroll=v;}
    public Employee getEmployee(){return employee;} public void setEmployee(Employee v){employee=v;}
    public LocalDate getEntryDate(){return entryDate;} public void setEntryDate(LocalDate v){entryDate=v;}
    public String getDescription(){return description;} public void setDescription(String v){description=v;}
    public List<JournalLine> getLines(){return lines;} public void setLines(List<JournalLine> v){lines=v;}
}
