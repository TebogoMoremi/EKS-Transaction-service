package com.tebogo.eks.integration;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;

public class WeatherClient {

    private final ProducerTemplate producerTemplate;

    public WeatherClient(CamelContext camelContext) {

        this.producerTemplate =
                camelContext.createProducerTemplate();
    }

    public String getWeather(
            double latitude,
            double longitude,
            String correlationId) {

        WeatherQuery query =
                new WeatherQuery(
                        latitude,
                        longitude
                );

        Exchange exchange =
                producerTemplate.request(
                        "direct:weather",
                        requestExchange -> {

                            requestExchange
                                    .getMessage()
                                    .setBody(query);

                            requestExchange
                                    .setProperty(
                                            "correlationId",
                                            correlationId
                                    );
                        }
                );

        if (exchange.getException() != null) {

            throw new RuntimeException(
                    "integration.weather.failed",
                    exchange.getException()
            );
        }

        return exchange
                .getMessage()
                .getBody(String.class);
    }
}