package com.tebogo.eks.validation;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.error.ValidationException;

public class LocationValidator
        implements TransactionValidator {

    @Override
    public void validate(
            TransactionRequest request) {

        if (request.location() == null) {

            throw new ValidationException(
                    "validation.location.required"
            );
        }

        if (request.location().latitude() == null
                || request.location().longitude() == null) {

            throw new ValidationException(
                    "validation.location.required"
            );
        }
    }
}