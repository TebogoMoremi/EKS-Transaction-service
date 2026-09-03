package com.tebogo.eks.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.tebogo.eks.dto.TransactionRequest;

public class DroolsRuleService {

    private final KieContainer kieContainer;

    public DroolsRuleService() {

        KieServices kieServices =
                KieServices.Factory.get();

        this.kieContainer =
                kieServices.getKieClasspathContainer();
    }

    public TransactionRuleFact evaluate(
            TransactionRequest request) {

        TransactionRuleFact fact =
                new TransactionRuleFact();

        fact.setType(
                request.type().name()
        );

        fact.setAmount(
                request.amount()
        );

        KieSession kieSession =
                kieContainer.newKieSession(
                        "transactionSession"
                );

        try {

            kieSession.insert(fact);

            kieSession.fireAllRules();

            return fact;

        } finally {

            kieSession.dispose();
        }
    }
}