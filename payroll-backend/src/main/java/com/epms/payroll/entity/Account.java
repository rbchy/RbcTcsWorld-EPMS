package com.epms.payroll.entity;
import jakarta.persistence.*;

@Entity @Table(name="accounts")
public class Account {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String code;
    @Column(nullable=false) private String name;
    @Column(nullable=false) private String type;
    @Column(nullable=false) private String normalSide="DEBIT";
    public Long getId(){return id;}
    public String getCode(){return code;} public void setCode(String v){code=v;}
    public String getName(){return name;} public void setName(String v){name=v;}
    public String getType(){return type;} public void setType(String v){type=v;}
    public String getNormalSide(){return normalSide;} public void setNormalSide(String v){normalSide=v;}
}
