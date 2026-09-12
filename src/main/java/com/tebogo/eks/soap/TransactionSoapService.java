package com.tebogo.eks.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(
    name = "TransactionSoapPort",
    targetNamespace = TransactionSoapService.NAMESPACE
)
public interface TransactionSoapService {

    String NAMESPACE =
            "http://soap.eks.tebogo.com/transactions";

    @WebMethod(operationName = "CreateTransaction")
    @WebResult(
        name = "transactionResponse",
        targetNamespace = NAMESPACE
    )
    TransactionSoapResponse createTransaction(
        @WebParam(
            name = "transactionRequest",
            targetNamespace = NAMESPACE
        )
        TransactionSoapRequest request
    ) throws TransactionSoapFaultException;
}