package com.tebogo.eks.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "cashout_transaction")
public class CashOutTransaction extends PaymentTransaction {

    private String destinationType;

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }
}