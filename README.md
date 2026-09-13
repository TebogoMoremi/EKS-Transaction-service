# EKS Transaction Service

[![Build and Deploy to EKS](https://github.com/TebogoMoremi/EKS-Transaction-service/actions/workflows/deploy.yml/badge.svg)](https://github.com/TebogoMoremi/EKS-Transaction-service/actions/workflows/deploy.yml)

Enterprise-style Java transaction processing service demonstrating REST and SOAP APIs, validation, business rules, integration patterns, persistence, containerisation, Kubernetes, AWS, secure secret management, and automated CI/CD.

The project processes `CASH_IN` and `CASH_OUT` transactions while maintaining a correlation ID across the complete request lifecycle.

---

## Overview

The EKS Transaction Service is an enterprise-style Java backend project demonstrating how multiple technologies can work together in a production-oriented transaction processing flow.

Transactions can be submitted through:

* REST
* SOAP

SOAP requests are routed through the REST transaction API so that both interfaces reuse the same transaction-processing logic.

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
* Amazon RDS PostgreSQL
* AWS Secrets Manager
* EKS Pod Identity
* Secrets Store CSI Driver
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
* Automated ECR image publishing
* Automated EKS deployment

---

## Architecture

```text
                        ┌────────────────────┐
                        │      Clients       │
                        │    REST / SOAP     │
                        └─────────┬──────────┘
                                  │
                 ┌────────────────┴────────────────┐
                 │                                 │
                 ▼                                 ▼
        ┌─────────────────┐              ┌─────────────────┐
        │ Jersey REST API │              │ Apache CXF SOAP │
        └────────┬────────┘              └────────┬────────┘
                 │                                │
                 │                       SOAP → REST Adapter
                 │                                │
                 └──────────────┬─────────────────┘
                                │
                                ▼
                    ┌──────────────────────┐
                    │ Correlation ID       │
                    │ Log4j2 + AspectJ AOP │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Validator Pattern    │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Drools Rules Engine  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Apache Camel         │
                    │ Weather Integration  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Transaction Service  │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ JPA / Hibernate      │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ EKS Pod Identity     │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ AWS Secrets Manager  │
                    │ DB Credentials       │
                    └──────────┬───────────┘
                               │
                               ▼
                    ┌──────────────────────┐
                    │ Amazon RDS           │
                    │ PostgreSQL           │
                    └──────────────────────┘
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
* Amazon RDS PostgreSQL
* AWS Secrets Manager
* EKS Pod Identity
* Secrets Store CSI Driver
* AWS CLI
* kubectl
* eksctl
* GitHub Actions
* GitHub OIDC

---

# Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/tebogo/eks/
│   │       ├── api/
│   │       ├── bootstrap/
│   │       ├── dto/
│   │       ├── error/
│   │       ├── integration/
│   │       ├── logging/
│   │       ├── model/
│   │       ├── persistence/
│   │       ├── rules/
│   │       ├── service/
│   │       ├── soap/
│   │       └── validation/
│   │
│   ├── resources/
│   │   ├── META-INF/
│   │   │   ├── persistence.xml
│   │   │   └── kmodule.xml
│   │   ├── i18n/
│   │   └── log4j2.xml
│   │
│   └── webapp/
│       └── WEB-INF/
│           └── web.xml
│
└── test/
    ├── java/
    └── resources/
        └── wiremock/

k8s/
├── app.yaml
└── rds-secret-provider.yaml

.github/
└── workflows/
    └── deploy.yml
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

# SOAP → REST Integration

The SOAP layer does not duplicate transaction business logic.

Instead:

```text
SOAP Client
     ↓
Apache CXF
     ↓
TransactionSoapServiceImpl
     ↓
POST /api/v1/transactions
     ↓
TransactionService
```

Both REST and SOAP therefore reuse the same:

* validation
* Drools rules
* Apache Camel integrations
* persistence
* exception handling
* correlation ID tracking
* logging
* RDS database
* business logic

---

# Database

The application uses **Amazon RDS for PostgreSQL**.

The RDS instance is deployed privately inside the same VPC as the Amazon EKS cluster and is not publicly accessible.

```text
EKS Transaction Service
        │
        │ PostgreSQL :5432
        ▼
Private Amazon RDS
```

The database used by the service is:

```text
transactions
```

The application connects using:

```text
jdbc:postgresql://<rds-endpoint>:5432/transactions
```

The RDS security group allows PostgreSQL access only from the security group used by the EKS worker nodes.

The database is therefore not directly exposed to the public internet.

---

# AWS Secrets Manager

Database credentials are managed using AWS Secrets Manager.

The RDS master password is generated and managed by AWS rather than being stored directly inside:

* Git
* GitHub Actions
* Docker images
* Kubernetes YAML files
* application source code

The application obtains the database username and password through:

```text
Application Pod
      ↓
Kubernetes ServiceAccount
      ↓
EKS Pod Identity
      ↓
IAM Role
      ↓
AWS Secrets Manager
      ↓
RDS Credentials
```

The dedicated IAM role used by the application is:

```text
EKSTransactionsAppPodRole
```

It is restricted to reading only the RDS credential secret.

---

# EKS Pod Identity

The application uses a dedicated Kubernetes service account:

```text
eks-transactions-service
```

Namespace:

```text
eks-transactions
```

The service account is associated with:

```text
EKSTransactionsAppPodRole
```

through EKS Pod Identity.

This gives the application temporary AWS credentials without storing permanent AWS access keys in Kubernetes.

---

# Secrets Store CSI Driver

The AWS Secrets Store CSI Driver mounts the RDS credentials into the application pod.

Configuration:

```text
k8s/rds-secret-provider.yaml
```

The application receives:

```text
dbUsername
dbPassword
```

as mounted files.

The application reads the credentials during startup and exports them as:

```text
DB_USER
DB_PASSWORD
```

The RDS endpoint is configured separately using:

```text
DB_URL
```

---

# Secure Database Flow

```text
Amazon EKS Pod
      ↓
ServiceAccount
      ↓
EKS Pod Identity
      ↓
IAM Role
      ↓
AWS Secrets Manager
      ↓
Database Username + Password
      ↓
Amazon RDS PostgreSQL
```

No database password is stored directly in the Git repository.

---

# Current Infrastructure

```text
GitHub
   ↓
GitHub Actions
   ↓
OIDC Authentication
   ↓
AWS IAM
   ↓
Docker Build
   ↓
Amazon ECR
   ↓
Amazon EKS
   ↓
Transaction Service
   ↓
EKS Pod Identity
   ↓
AWS Secrets Manager
   ↓
Amazon RDS PostgreSQL
```

---

# Current Project Status

| Feature                  | Status |
| ------------------------ | ------ |
| Java 25                  | ✅      |
| Maven                    | ✅      |
| REST API                 | ✅      |
| SOAP API                 | ✅      |
| Jersey                   | ✅      |
| Apache CXF               | ✅      |
| Validator Pattern        | ✅      |
| Internationalisation     | ✅      |
| Error Handling           | ✅      |
| Drools                   | ✅      |
| Apache Camel             | ✅      |
| Weather Integration      | ✅      |
| JPA / Hibernate          | ✅      |
| Log4j2                   | ✅      |
| AspectJ AOP              | ✅      |
| Correlation ID Tracking  | ✅      |
| Unit Tests               | ✅      |
| Integration Tests        | ✅      |
| WireMock                 | ✅      |
| Docker                   | ✅      |
| Amazon ECR               | ✅      |
| Kubernetes               | ✅      |
| Amazon EKS               | ✅      |
| Amazon RDS PostgreSQL    | ✅      |
| Private RDS Networking   | ✅      |
| AWS Secrets Manager      | ✅      |
| EKS Pod Identity         | ✅      |
| Secrets Store CSI Driver | ✅      |
| GitHub Actions           | ✅      |
| GitHub OIDC              | ✅      |
| Automated ECR Push       | ✅      |
| Automated EKS Deployment | ✅      |
| Deployment Verification  | ✅      |
