package com.tebogo.eks.service;

import org.apache.logging.log4j.ThreadContext;

import com.tebogo.eks.dto.TransactionRequest;
import com.tebogo.eks.dto.TransactionType;
import com.tebogo.eks.integration.WeatherClient;
import com.tebogo.eks.logging.BusinessOperation;
import com.tebogo.eks.model.CashInTransaction;
import com.tebogo.eks.model.CashOutTransaction;
import com.tebogo.eks.model.InitiatorParty;
import com.tebogo.eks.model.PaymentTransaction;
import com.tebogo.eks.model.ReceiverParty;
import com.tebogo.eks.persistence.TransactionRepository;
import com.tebogo.eks.rules.DroolsRuleService;
import com.tebogo.eks.rules.TransactionRuleFact;
import com.tebogo.eks.validation.CompositeTransactionValidator;

public class TransactionService {

    private final CompositeTransactionValidator validator;
    private final DroolsRuleService ruleService;
    private final WeatherClient weatherClient;
    private final TransactionRepository repository;

    public TransactionService(
            CompositeTransactionValidator validator,
            DroolsRuleService ruleService,
            WeatherClient weatherClient,
            TransactionRepository repository) {

        this.validator = validator;
        this.ruleService = ruleService;
        this.weatherClient = weatherClient;
        this.repository = repository;
    }

    @BusinessOperation("create-transaction")
    public PaymentTransaction create(
            TransactionRequest request) {

        // 1. Validate request
        validator.validate(request);

        // 2. Retrieve UUID created by the REST filter
        String correlationId =
                ThreadContext.get("correlationId");

        // 3. Execute Drools rules
        TransactionRuleFact ruleResult =
                ruleService.evaluate(request);

        // 4. Reject transaction if Drools says no
        if (!ruleResult.isApproved()) {
            throw new RuntimeException(
                    "rules.amount.limit"
            );
        }

        // 5. Call weather API through Apache Camel
        String weatherJson =
                weatherClient.getWeather(
                        request.location().latitude(),
                        request.location().longitude(),
                        correlationId
                );

        // 6. Create initiator
        InitiatorParty initiator =
                new InitiatorParty();

        initiator.setName(
                request.initiator().name()
        );

        initiator.setAccountReference(
                request.initiator().accountReference()
        );

        initiator.setChannel(
                request.initiator().channel()
        );

        // 7. Create receiver
        ReceiverParty receiver =
                new ReceiverParty();

        receiver.setName(
                request.receiver().name()
        );

        receiver.setAccountReference(
                request.receiver().accountReference()
        );

        receiver.setDestination(
                request.receiver().destination()
        );

        // 8. Create correct transaction subtype
        PaymentTransaction transaction;

        if (request.type() == TransactionType.CASH_IN) {

            CashInTransaction cashIn =
                    new CashInTransaction();

            cashIn.setSourceSystem(
                    request.sourceSystem()
            );

            transaction = cashIn;

        } else {

            CashOutTransaction cashOut =
                    new CashOutTransaction();

            cashOut.setDestinationType(
                    request.destinationType()
            );

            transaction = cashOut;
        }

        // 9. Set common fields
        transaction.setCorrelationId(
                correlationId
        );

        transaction.setAmount(
                request.amount()
        );

        transaction.setCurrency(
                request.currency()
        );

        transaction.setStatus(
                "APPROVED"
        );

        transaction.setRuleDecision(
                ruleResult.getDecision()
        );

        transaction.setInitiator(
                initiator
        );

        transaction.setReceiver(
                receiver
        );

        transaction.setWeatherJson(
                weatherJson
        );

        // 10. Persist through Hibernate/JPA
        return repository.save(transaction);
    }
}