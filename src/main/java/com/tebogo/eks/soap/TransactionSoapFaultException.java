package com.tebogo.eks.soap;

import jakarta.xml.ws.WebFault;

@WebFault(
    name = "TransactionFault",
    targetNamespace = TransactionSoapService.NAMESPACE
)
public class TransactionSoapFaultException extends Exception {

    private static final long serialVersionUID = 1L;

    private final TransactionSoapFault faultInfo;

    public TransactionSoapFaultException(
            String message,
            TransactionSoapFault faultInfo) {

        super(message);
        this.faultInfo = faultInfo;
    }

    public TransactionSoapFault getFaultInfo() {
        return faultInfo;
    }
}