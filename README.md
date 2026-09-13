# EKS Transaction Service

[![Build and Deploy to EKS](https://github.com/TebogoMoremi/EKS-Transaction-service/actions/workflows/deploy.yml/badge.svg)](https://github.com/TebogoMoremi/EKS-Transaction-service/actions/workflows/deploy.yml)

Enterprise-style Java transaction processing service demonstrating REST and SOAP APIs, validation, business rules, integration patterns, persistence, containerisation, Kubernetes, AWS, and automated CI/CD.

The project processes `CASH_IN` and `CASH_OUT` transactions while maintaining a correlation ID across the complete request lifecycle.

---

## Overview

The EKS Transaction Service was built as an enterprise Java backend project demonstrating how multiple technologies can work together in a production-style transaction processing flow.

Transactions can be submitted through:

* REST
* SOAP

SOAP requests are routed through the REST transaction API so that both interfaces reuse the same business logic.

The service includes:

* REST API with Jersey
* SOAP API with Apache CXF
* Java 25
* Maven
* Validator pattern
* Internationalised validation messages
* Centralised exception handling
* Drools business rules
* Apache Camel integration
* Weather API integration
* JPA / Hibernate
* PostgreSQL
* Log4j2
* AspectJ AOP
* UUID correlation tracking
* JUnit testing
* WireMock
* Docker
* Amazon ECR
* Amazon EKS
* Kubernetes
* GitHub Actions
* GitHub OIDC authentication
* Automated ECR publishing
* Automated EKS deployments

---

# Architecture

```text
                         
                               Clients       
                                             
                            REST / SOAP      
                         
                                    
                   
                                                    
                                                    
                        
           Jersey REST API                Apache CXF SOAP 
                        
                                                   
                                          SOAP  REST Adapter
                                                   
                   
                                    
                                    
                         
                          Correlation ID     
                          Log4j2 + AOP       
                         
                                   
                                   
                         
                          Validator Pattern  
                         
                                   
                                   
                         
                          Drools Rules       
                         
                                   
                                   
                         
                          Apache Camel       
                          Weather Service    
                         
                                   
                                   
                         
                          TransactionService 
                         
                                   
                                   
                         
                          JPA / Hibernate    
                         
                                   
                                   
                         
                          PostgreSQL         
                         
```

---

# Technology Stack

## Backend

* Java 25
* Jakarta EE
* Jersey REST
* Apache CXF
* Apache Camel
* Drools
* Hibernate ORM
* JPA
* Jackson
* PostgreSQL
* Maven
* Apache Tomcat 11

## Logging

* Log4j2
* AspectJ
* AOP
* ThreadContext / MDC
* UUID correlation IDs

## Testing

* JUnit
* Maven Surefire
* Maven Failsafe
* WireMock
* Docker-based integration testing

## DevOps & Cloud

* Docker
* Fabric8 Docker Maven Plugin
* Kubernetes
* Amazon ECR
* Amazon EKS
* AWS CLI
* kubectl
* eksctl
* GitHub Actions
* GitHub OIDC

---

# Project Structure

```text
src/
 main/
    java/
       com/tebogo/eks/
           api/
           bootstrap/
           dto/
           error/
           integration/
           logging/
           model/
           persistence/
           rules/
           service/
           soap/
           validation/
   
    resources/
       META-INF/
          persistence.xml
          kmodule.xml
       i18n/
       log4j2.xml
   
    webapp/
        WEB-INF/
            web.xml

 test/
     java/
     resources/
         wiremock/

k8s/
 app.yaml
 postgres.yaml

.github/
 workflows/
     deploy.yml
```

---

# REST API

## Health Check

```http
GET /api/health
```

Example:

```bash
curl http://localhost:8081/api/health
```

Response:

```json
{
  "status": "UP"
}
```

---

## Create Transaction

```http
POST /api/v1/transactions
```

Example:

```bash
curl -X POST \
  http://localhost:8081/api/v1/transactions \
  -H "Content-Type: application/json" \
  -H "X-Correlation-ID: REST-CASHIN-001" \
  -d '{
    "type": "CASH_IN",
    "amount": 250.00,
    "currency": "ZAR",
    "initiator": {
      "name": "Tebogo",
      "accountReference": "ACC-001",
      "channel": "MOBILE"
    },
    "receiver": {
      "name": "Merchant",
      "accountReference": "MERCHANT-001",
      "destination": "WALLET"
    },
    "location": {
      "latitude": -26.2041,
      "longitude": 28.0473
    },
    "sourceSystem": "REST"
  }'
```

Example response:

```json
{
  "id": 1,
  "correlationId": "REST-CASHIN-001",
  "amount": 250.00,
  "currency": "ZAR",
  "status": "APPROVED",
  "ruleDecision": "APPROVED",
  "sourceSystem": "REST"
}
```

---

# SOAP API

The SOAP interface is implemented using Apache CXF.

## WSDL

```text
http://localhost:8081/soap/transactions?wsdl
```

## Endpoint

```text
http://localhost:8081/soap/transactions
```

## Operation

```text
CreateTransaction
```

Example SOAP request:

```xml
<soapenv:Envelope
    xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
    xmlns:tran="http://soap.eks.tebogo.com/transactions">

    <soapenv:Header/>

    <soapenv:Body>
        <tran:CreateTransaction>

            <tran:transactionRequest>

                <correlationId>SOAP-CASHIN-001</correlationId>

                <type>CASH_IN</type>

                <amount>250.00</amount>

                <currency>ZAR</currency>

                <initiatorName>Tebogo</initiatorName>

                <initiatorAccountReference>
                    SOAP-ACC-001
                </initiatorAccountReference>

                <initiatorChannel>
                    MOBILE
                </initiatorChannel>

                <receiverName>
                    Merchant
                </receiverName>

                <receiverAccountReference>
                    SOAP-MERCHANT-001
                </receiverAccountReference>

                <receiverDestination>
                    WALLET
                </receiverDestination>

                <latitude>-26.2041</latitude>

                <longitude>28.0473</longitude>

                <sourceSystem>SOAP</sourceSystem>

            </tran:transactionRequest>

        </tran:CreateTransaction>
    </soapenv:Body>

</soapenv:Envelope>
```

Example successful result:

```text
correlationId = SOAP-CASHIN-001
httpStatus     = 201
status         = APPROVED
ruleDecision   = APPROVED
```

---

# SOAP  REST Integration

The SOAP layer does not duplicate transaction business logic.

Instead:

```text
SOAP Client
     
Apache CXF
     
TransactionSoapServiceImpl
     
POST /api/v1/transactions
     
TransactionService
```

This means both REST and SOAP reuse:

* validation
* Drools rules


