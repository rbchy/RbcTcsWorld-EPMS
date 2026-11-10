package com.epms.payroll.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="users")
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String username;
    @Column(nullable=false) private String passwordHash;
    @Column(nullable=false) private String role;
    private boolean active=true;
    private LocalDateTime createdAt=LocalDateTime.now();
    public Long getId(){return id;} public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public String getRole(){return role;} public void setRole(String v){role=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public LocalDateTime getCreatedAt(){return createdAt;}
}
