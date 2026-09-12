package com.tebogo.eks.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.dto.TransactionType;

class DroolsRuleServiceTest {

    private DroolsRuleService ruleService;

    @BeforeEach
    void setUp() {
        ruleService = new DroolsRuleService();
    }

    @Test
    void shouldApproveCashInBelowLimit() {

        TransactionRequest request =
                createRequest(
                        TransactionType.CASH_IN,
                        new BigDecimal("250.00")
                );

        TransactionRuleFact result =
                ruleService.evaluate(request);

        assertTrue(result.isApproved());

        assertEquals(
                "APPROVED",
                result.getDecision()
        );
    }

    @Test
    void shouldRejectTransactionAboveLimit() {

        TransactionRequest request =
                createRequest(
                        TransactionType.CASH_IN,
                        new BigDecimal("15000.00")
                );

        TransactionRuleFact result =
                ruleService.evaluate(request);

        assertFalse(result.isApproved());

        assertEquals(
                "AMOUNT_LIMIT_EXCEEDED",
                result.getDecision()
        );
    }

    @Test
    void shouldMarkLargeCashOutAsHighRisk() {

        TransactionRequest request =
                createRequest(
                        TransactionType.CASH_OUT,
                        new BigDecimal("6000.00")
                );

        TransactionRuleFact result =
                ruleService.evaluate(request);

        assertTrue(result.isApproved());

        assertEquals(
                "HIGH",
                result.getRiskLevel()
        );
    }

    private TransactionRequest createRequest(
            TransactionType type,
            BigDecimal amount) {

        return new TransactionRequest(
                type,
                amount,
                "ZAR",
                null,
                null,
                null,
                null,
                null
        );
    }
}