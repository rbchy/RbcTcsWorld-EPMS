package com.epms.payroll.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity @Table(name="journal_lines")
public class JournalLine {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(optional=false) @JoinColumn(name="journal_entry_id") private JournalEntry journalEntry;
    @ManyToOne(optional=false) private Account account;
    @Column(precision=14,scale=2) private BigDecimal debit=BigDecimal.ZERO;
    @Column(precision=14,scale=2) private BigDecimal credit=BigDecimal.ZERO;
    public Long getId(){return id;}
    @JsonIgnore
    public JournalEntry getJournalEntry(){return journalEntry;} public void setJournalEntry(JournalEntry v){journalEntry=v;}
    public Account getAccount(){return account;} public void setAccount(Account v){account=v;}
    public BigDecimal getDebit(){return debit;} public void setDebit(BigDecimal v){debit=v;}
    public BigDecimal getCredit(){return credit;} public void setCredit(BigDecimal v){credit=v;}
}
