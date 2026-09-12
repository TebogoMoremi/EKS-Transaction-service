package com.tebogo.eks.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.dto.TransactionType;
import com.tebogo.eks.error.ValidationException;

class AmountValidatorTest {

    private AmountValidator validator;

    @BeforeEach
    void setUp() {
        validator = new AmountValidator();
    }

    @Test
    void shouldAcceptPositiveAmount() {

        TransactionRequest request =
                createRequest(new BigDecimal("250.00"));

        assertDoesNotThrow(
                () -> validator.validate(request)
        );
    }

    @Test
    void shouldRejectNegativeAmount() {

        TransactionRequest request =
                createRequest(new BigDecimal("-50.00"));

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "validation.amount.positive",
                exception.getErrorCode()
        );
    }

    @Test
    void shouldRejectZeroAmount() {

        TransactionRequest request =
                createRequest(BigDecimal.ZERO);

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "validation.amount.positive",
                exception.getErrorCode()
        );
    }

    @Test
    void shouldRejectNullAmount() {

        TransactionRequest request =
                createRequest(null);

        ValidationException exception =
                assertThrows(
                        ValidationException.class,
                        () -> validator.validate(request)
                );

        assertEquals(
                "validation.amount.required",
                exception.getErrorCode()
        );
    }

    private TransactionRequest createRequest(
            BigDecimal amount) {

        return new TransactionRequest(
                TransactionType.CASH_IN,
                amount,
                "ZAR",
                null,
                null,
                null,
                "MOBILE_APP",
                null
        );
    }
}