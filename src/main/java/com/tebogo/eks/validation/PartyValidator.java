package com.tebogo.eks.validation;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.error.ValidationException;

public class PartyValidator implements TransactionValidator {

    @Override
    public void validate(TransactionRequest request) {

        if (request.initiator() == null) {
            throw new ValidationException(
                "validation.initiator.required"
            );
        }

        if (request.receiver() == null) {
            throw new ValidationException(
                "validation.receiver.required"
            );
        }
    }
}