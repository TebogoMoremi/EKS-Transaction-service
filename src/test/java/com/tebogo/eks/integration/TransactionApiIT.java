package com.tebogo.eks.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.Test;

class TransactionApiIT {

    private static final String BASE_URL =
            "http://localhost:8081";

    private final HttpClient client =
            HttpClient.newHttpClient();

    @Test
    void healthEndpointShouldReturnUp() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/health"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        assertEquals(200, response.statusCode());
        assertTrue(
                response.body().contains("\"status\":\"UP\"")
        );
    }

    @Test
    void shouldCreateCashInTransaction() throws Exception {

        String body = """
                {
                  "type": "CASH_IN",
                  "amount": 250.00,
                  "currency": "ZAR",
                  "initiator": {
                    "name": "Tebogo",
                    "accountReference": "IT-ACC001",
                    "channel": "MOBILE"
                  },
                  "receiver": {
                    "name": "Receiver",
                    "accountReference": "IT-ACC002",
                    "destination": "WALLET"
                  },
                  "location": {
                    "latitude": -26.2041,
                    "longitude": 28.0473
                  },
                  "sourceSystem": "MOBILE_APP"
                }
                """;

        HttpResponse<String> response =
                post(
                        "IT-CASHIN-001",
                        body
                );

        assertEquals(201, response.statusCode());

        assertTrue(
                response.body()
                        .contains("\"status\":\"APPROVED\"")
        );

        assertTrue(
                response.body()
                        .contains("IT-CASHIN-001")
        );
    }

    @Test
    void shouldRejectNegativeAmount() throws Exception {

        String body = """
                {
                  "type": "CASH_IN",
                  "amount": -50.00,
                  "currency": "ZAR",
                  "initiator": {
                    "name": "Tebogo",
                    "accountReference": "IT-ACC003",
                    "channel": "MOBILE"
                  },
                  "receiver": {
                    "name": "Receiver",
                    "accountReference": "IT-ACC004",
                    "destination": "WALLET"
                  },
                  "location": {
                    "latitude": -26.2041,
                    "longitude": 28.0473
                  },
                  "sourceSystem": "MOBILE_APP"
                }
                """;

        HttpResponse<String> response =
                post(
                        "IT-INVALID-001",
                        body
                );

        assertEquals(400, response.statusCode());

        assertTrue(
                response.body()
                        .contains("validation.amount.positive")
        );
    }

    @Test
    void shouldRejectTransactionAboveLimit()
            throws Exception {

        String body = """
                {
                  "type": "CASH_IN",
                  "amount": 15000.00,
                  "currency": "ZAR",
                  "initiator": {
                    "name": "Tebogo",
                    "accountReference": "IT-ACC005",
                    "channel": "MOBILE"
                  },
                  "receiver": {
                    "name": "Receiver",
                    "accountReference": "IT-ACC006",
                    "destination": "WALLET"
                  },
                  "location": {
                    "latitude": -26.2041,
                    "longitude": 28.0473
                  },
                  "sourceSystem": "MOBILE_APP"
                }
                """;

        HttpResponse<String> response =
                post(
                        "IT-RULE-001",
                        body
                );

        assertEquals(422, response.statusCode());

        assertTrue(
                response.body()
                        .contains("rules.amount.limit")
        );
    }

    private HttpResponse<String> post(
            String correlationId,
            String body) throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(
                        URI.create(
                                BASE_URL
                                        + "/api/v1/transactions"
                        )
                )
                .header(
                        "Content-Type",
                        "application/json"
                )
                .header(
                        "Accept",
                        "application/json"
                )
                .header(
                        "X-Correlation-ID",
                        correlationId
                )
                .POST(
                        HttpRequest.BodyPublishers
                                .ofString(body)
                )
                .build();

        return client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );
    }
}