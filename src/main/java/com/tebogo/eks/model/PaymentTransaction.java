package com.tebogo.eks.model;

import java.math.BigDecimal;
import java.time.Instant;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_transaction")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
        name = "correlation_id",
        nullable = false,
        unique = true,
        length = 36
    )
    private String correlationId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    private String status;

    @Column(name = "rule_decision")
    private String ruleDecision;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(
        cascade = CascadeType.PERSIST,
        optional = false
    )
    @JoinColumn(name = "initiator_id")
    private InitiatorParty initiator;

    @ManyToOne(
        cascade = CascadeType.PERSIST,
        optional = false
    )
    @JoinColumn(name = "receiver_id")
    private ReceiverParty receiver;

    @Column(name = "weather_json", columnDefinition = "text")
    private String weatherJson;

    @PrePersist
    public void beforePersist() {
        createdAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRuleDecision() {
        return ruleDecision;
    }

    public void setRuleDecision(String ruleDecision) {
        this.ruleDecision = ruleDecision;
    }

    public InitiatorParty getInitiator() {
        return initiator;
    }

    public void setInitiator(InitiatorParty initiator) {
        this.initiator = initiator;
    }

    public ReceiverParty getReceiver() {
        return receiver;
    }

    public void setReceiver(ReceiverParty receiver) {
        this.receiver = receiver;
    }

    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherJson) {
        this.weatherJson = weatherJson;
    }
}