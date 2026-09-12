package com.tebogo.eks.soap;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.jws.WebService;

@WebService(
    endpointInterface = "com.tebogo.eks.soap.TransactionSoapService",
    serviceName = "TransactionSoapService",
    portName = "TransactionSoapPort",
    targetNamespace = TransactionSoapService.NAMESPACE
)
public class TransactionSoapServiceImpl implements TransactionSoapService {

    private static final Logger LOGGER =
            LogManager.getLogger(TransactionSoapServiceImpl.class);

    private static final ObjectMapper OBJECT_MAPPER =
            new ObjectMapper();

    private static final HttpClient HTTP_CLIENT =
            HttpClient.newHttpClient();

    private final String restTransactionUrl =
            System.getenv().getOrDefault(
                    "REST_TRANSACTION_URL",
                    "http://127.0.0.1:8080/api/v1/transactions"
            );

    @Override
    public TransactionSoapResponse createTransaction(
            TransactionSoapRequest request)
            throws TransactionSoapFaultException {

        String correlationId = request.getCorrelationId();

        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        ThreadContext.put("correlationId", correlationId);

        try {

            LOGGER.info(
                    "SOAP transaction received correlationId={}",
                    correlationId
            );

            String requestJson = buildRestRequest(request);

            HttpRequest httpRequest =
                    HttpRequest.newBuilder()
                            .uri(URI.create(restTransactionUrl))
                            .header("Content-Type", "application/json")
                            .header("X-Correlation-ID", correlationId)
                            .POST(
                                    HttpRequest.BodyPublishers
                                            .ofString(requestJson)
                            )
                            .build();

            HttpResponse<String> httpResponse =
                    HTTP_CLIENT.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            int statusCode = httpResponse.statusCode();
            String responseBody = httpResponse.body();

            if (statusCode < 200 || statusCode >= 300) {
                throw buildSoapFault(
                        correlationId,
                        statusCode,
                        responseBody
                );
            }

            JsonNode json =
                    OBJECT_MAPPER.readTree(responseBody);

            TransactionSoapResponse response =
                    new TransactionSoapResponse();

            response.setCorrelationId(correlationId);
            response.setHttpStatus(statusCode);
            response.setStatus(
                    json.path("status").asText(null)
            );
            response.setRuleDecision(
                    json.path("ruleDecision").asText(null)
            );
            response.setResponseBody(responseBody);

            LOGGER.info(
                    "SOAP transaction completed correlationId={} status={}",
                    correlationId,
                    statusCode
            );

            return response;

        } catch (TransactionSoapFaultException exception) {

            throw exception;

        } catch (Exception exception) {

            LOGGER.error(
                    "SOAP transaction failed correlationId={}",
                    correlationId,
                    exception
            );

            TransactionSoapFault fault =
                    new TransactionSoapFault(
                            correlationId,
                            "SOAP_INTERNAL_ERROR",
                            exception.getMessage(),
                            500
                    );

            throw new TransactionSoapFaultException(
                    "Unable to process SOAP transaction",
                    fault
            );

        } finally {

            ThreadContext.remove("correlationId");
        }
    }

    private String buildRestRequest(
            TransactionSoapRequest request)
            throws Exception {

        ObjectNode root =
                OBJECT_MAPPER.createObjectNode();

        root.put("type", request.getType());

        if (request.getAmount() != null) {
            root.put("amount", request.getAmount());
        }

        root.put("currency", request.getCurrency());

        ObjectNode initiator =
                root.putObject("initiator");

        initiator.put(
                "name",
                request.getInitiatorName()
        );

        initiator.put(
                "accountReference",
                request.getInitiatorAccountReference()
        );

        initiator.put(
                "channel",
                request.getInitiatorChannel()
        );

        ObjectNode receiver =
                root.putObject("receiver");

        receiver.put(
                "name",
                request.getReceiverName()
        );

        receiver.put(
                "accountReference",
                request.getReceiverAccountReference()
        );

        receiver.put(
                "destination",
                request.getReceiverDestination()
        );

        ObjectNode location =
                root.putObject("location");

        if (request.getLatitude() != null) {
            location.put(
                    "latitude",
                    request.getLatitude()
            );
        }

        if (request.getLongitude() != null) {
            location.put(
                    "longitude",
                    request.getLongitude()
            );
        }

        if (request.getSourceSystem() != null) {
            root.put(
                    "sourceSystem",
                    request.getSourceSystem()
            );
        }

        if (request.getDestinationType() != null) {
            root.put(
                    "destinationType",
                    request.getDestinationType()
            );
        }

        return OBJECT_MAPPER.writeValueAsString(root);
    }

    private TransactionSoapFaultException buildSoapFault(
            String correlationId,
            int statusCode,
            String body) {

        String code = "REST_TRANSACTION_ERROR";
        String message = "Transaction request failed";

        try {

            JsonNode json =
                    OBJECT_MAPPER.readTree(body);

            code =
                    json.path("code").asText(code);

            message =
                    json.path("message").asText(message);

        } catch (Exception ignored) {
            // Keep fallback values.
        }

        TransactionSoapFault fault =
                new TransactionSoapFault(
                        correlationId,
                        code,
                        message,
                        statusCode
                );

        return new TransactionSoapFaultException(
                message,
                fault
        );
    }
}