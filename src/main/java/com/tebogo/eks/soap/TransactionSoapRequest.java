package com.tebogo.eks.soap;

import java.math.BigDecimal;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionSoapRequest")
public class TransactionSoapRequest {

    private String correlationId;

    private String type;
    private BigDecimal amount;
    private String currency;

    private String initiatorName;
    private String initiatorAccountReference;
    private String initiatorChannel;

    private String receiverName;
    private String receiverAccountReference;
    private String receiverDestination;

    private Double latitude;
    private Double longitude;

    private String sourceSystem;
    private String destinationType;

    public TransactionSoapRequest() {
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getInitiatorName() {
        return initiatorName;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public String getInitiatorAccountReference() {
        return initiatorAccountReference;
    }

    public void setInitiatorAccountReference(String initiatorAccountReference) {
        this.initiatorAccountReference = initiatorAccountReference;
    }

    public String getInitiatorChannel() {
        return initiatorChannel;
    }

    public void setInitiatorChannel(String initiatorChannel) {
        this.initiatorChannel = initiatorChannel;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverAccountReference() {
        return receiverAccountReference;
    }

    public void setReceiverAccountReference(String receiverAccountReference) {
        this.receiverAccountReference = receiverAccountReference;
    }

    public String getReceiverDestination() {
        return receiverDestination;
    }

    public void setReceiverDestination(String receiverDestination) {
        this.receiverDestination = receiverDestination;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public String getDestinationType() {
        return destinationType;
    }

    public void setDestinationType(String destinationType) {
        this.destinationType = destinationType;
    }
}