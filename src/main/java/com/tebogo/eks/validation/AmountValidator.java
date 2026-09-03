package com.tebogo.eks.validation;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.error.ValidationException;

public class AmountValidator implements TransactionValidator {

    @Override
    public void validate(TransactionRequest request) {

        if (request.amount() == null) {
            throw new ValidationException(
                "validation.amount.required"
            );
        }

        if (request.amount().signum() <= 0) {
            throw new ValidationException(
                "validation.amount.positive"
            );
        }
    }
}