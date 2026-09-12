package com.tebogo.eks.soap;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
public class TransactionSoapFault {

    private String correlationId;
    private String code;
    private String message;
    private int httpStatus;

    public TransactionSoapFault() {
    }

    public TransactionSoapFault(
            String correlationId,
            String code,
            String message,
            int httpStatus) {

        this.correlationId = correlationId;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}