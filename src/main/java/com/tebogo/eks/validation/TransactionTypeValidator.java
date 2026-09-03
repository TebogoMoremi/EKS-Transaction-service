package com.tebogo.eks.validation;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.error.ValidationException;

public class TransactionTypeValidator implements TransactionValidator {

    @Override
    public void validate(TransactionRequest request) {

        if (request.type() == null) {
            throw new ValidationException(
                "validation.type.required"
            );
        }
    }
}