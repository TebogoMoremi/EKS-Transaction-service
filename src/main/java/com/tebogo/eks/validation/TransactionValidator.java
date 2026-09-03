package com.tebogo.eks.validation;

import com.tebogo.eks.dto.TransactionRequest;

public interface TransactionValidator {

    void validate(TransactionRequest request);
}