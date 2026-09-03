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

        /*
         * STEP 1:
         * Validate incoming request
         */
        validator.validate(request);


        /*
         * STEP 2:
         * Get UUID created by CorrelationIdFilter
         */
        String correlationId =
                ThreadContext.get(
                        "correlationId"
                );


        /*
         * STEP 3:
         * Execute Drools rules
         */
        TransactionRuleFact ruleResult =
                ruleService.evaluate(request);


        /*
         * STEP 4:
         * Check Drools decision
         */
        if (!ruleResult.isApproved()) {

            throw new RuntimeException(
                    "rules.amount.limit"
            );
        }


        /*
         * STEP 5:
         * Call weather service through Camel
         */
        String weatherJson =
                weatherClient.getWeather(
                        request.location().latitude(),
                        request.location().longitude(),
                        correlationId
                );


        /*
         * STEP 6:
         * Create Initiator Party
         */
        InitiatorParty initiator =
                new InitiatorParty();

        initiator.setName(
                request.initiator().name()
        );

        initiator.setAccountReference(
                request.initiator()
                        .accountReference()
        );

        initiator.setChannel(
                request.initiator().channel()
        );


        /*
         * STEP 7:
         * Create Receiver Party
         */
        ReceiverParty receiver =
                new ReceiverParty();

        receiver.setName(
                request.receiver().name()
        );

        receiver.setAccountReference(
                request.receiver()
                        .accountReference()
        );

        receiver.setDestination(
                request.receiver().destination()
        );


        /*
         * STEP 8:
         * Create correct transaction subtype
         */
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


        /*
         * STEP 9:
         * Populate common transaction fields
         */
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


        /*
         * STEP 10:
         * Save using JPA/Hibernate
         */
        return repository.save(
                transaction
        );
    }
}