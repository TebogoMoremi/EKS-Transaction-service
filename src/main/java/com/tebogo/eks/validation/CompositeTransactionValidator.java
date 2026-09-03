package com.tebogo.eks.validation;

import java.util.List;

import com.tebogo.eks.dto.TransactionRequest;

public class CompositeTransactionValidator {

    private final List<TransactionValidator> validators;

    public CompositeTransactionValidator() {

        validators = List.of(
            new TransactionTypeValidator(),
            new AmountValidator(),
            new PartyValidator()
        );
    }

    public void validate(TransactionRequest request) {

        for (TransactionValidator validator : validators) {
            validator.validate(request);
        }
    }
}