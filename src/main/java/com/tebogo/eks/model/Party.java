package com.tebogo.eks.model;

import jakarta.persistence.*;

@Entity
@Table(name = "party")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "account_reference", nullable = false)
    private String accountReference;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAccountReference() {
        return accountReference;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccountReference(String accountReference) {
        this.accountReference = accountReference;
    }
}