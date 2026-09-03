package com.tebogo.eks.bootstrap;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;

import com.tebogo.eks.integration.WeatherClient;
import com.tebogo.eks.integration.WeatherRoute;
import com.tebogo.eks.persistence.TransactionRepository;
import com.tebogo.eks.rules.DroolsRuleService;
import com.tebogo.eks.service.TransactionService;
import com.tebogo.eks.validation.CompositeTransactionValidator;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class ApplicationContext {

    private static EntityManagerFactory entityManagerFactory;

    private static CamelContext camelContext;

    private static TransactionService transactionService;


    private ApplicationContext() {
        // Prevent object creation
    }


    public static void start() throws Exception {

        /*
         * =========================================
         * 1. DATABASE CONFIGURATION
         * =========================================
         */

        Map<String, Object> properties =
                new HashMap<>();


        properties.put(
                "jakarta.persistence.jdbc.url",
                getEnvironmentVariable(
                        "DB_URL",
                        "jdbc:postgresql://localhost:5432/transactions"
                )
        );


        properties.put(
                "jakarta.persistence.jdbc.user",
                getEnvironmentVariable(
                        "DB_USER",
                        "eksuser"
                )
        );


        properties.put(
                "jakarta.persistence.jdbc.password",
                getEnvironmentVariable(
                        "DB_PASSWORD",
                        "ekspassword"
                )
        );


        properties.put(
                "jakarta.persistence.jdbc.driver",
                "org.postgresql.Driver"
        );


        /*
         * =========================================
         * 2. START JPA / HIBERNATE
         * =========================================
         */

        entityManagerFactory =
                Persistence.createEntityManagerFactory(
                        "transactionsPU",
                        properties
                );


        /*
         * =========================================
         * 3. START APACHE CAMEL
         * =========================================
         */

        camelContext =
                new DefaultCamelContext();


        camelContext.addRoutes(
                new WeatherRoute()
        );


        camelContext.start();


        /*
         * =========================================
         * 4. CREATE REPOSITORY
         * =========================================
         */

        TransactionRepository repository =
                new TransactionRepository(
                        entityManagerFactory
                );


        /*
         * =========================================
         * 5. CREATE CAMEL WEATHER CLIENT
         * =========================================
         */

        WeatherClient weatherClient =
                new WeatherClient(
                        camelContext
                );


        /*
         * =========================================
         * 6. CREATE DROOLS SERVICE
         * =========================================
         */

        DroolsRuleService ruleService =
                new DroolsRuleService();


        /*
         * =========================================
         * 7. CREATE VALIDATOR
         * =========================================
         */

        CompositeTransactionValidator validator =
                new CompositeTransactionValidator();


        /*
         * =========================================
         * 8. CREATE MAIN BUSINESS SERVICE
         * =========================================
         */

        transactionService =
                new TransactionService(
                        validator,
                        ruleService,
                        weatherClient,
                        repository
                );
    }


    public static TransactionService transactionService() {

        if (transactionService == null) {

            throw new IllegalStateException(
                    "Application has not been started"
            );
        }

        return transactionService;
    }


    public static void stop() throws Exception {

        /*
         * Stop Camel
         */

        if (camelContext != null) {

            camelContext.stop();

            camelContext = null;
        }


        /*
         * Close Hibernate / JPA
         */

        if (entityManagerFactory != null
                && entityManagerFactory.isOpen()) {

            entityManagerFactory.close();

            entityManagerFactory = null;
        }


        transactionService = null;
    }


    private static String getEnvironmentVariable(
            String name,
            String defaultValue) {

        String value =
                System.getenv(name);

        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value;
    }
}