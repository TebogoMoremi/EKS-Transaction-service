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

# Amazon EKS Kubernetes Upgrade & Maintenance

The `eks-transactions-cluster` is maintained using the Amazon EKS Kubernetes lifecycle and managed add-on upgrade process.

The cluster was successfully upgraded from **Kubernetes 1.34 to Kubernetes 1.35** after receiving an AWS Health notification that standard support for Kubernetes 1.34 would end on **2 December 2026**.

## Upgrade Environment

```text
AWS Region:          af-south-1
EKS Cluster:         eks-transactions-cluster
Managed Node Group:  app-nodes
Operating System:    Amazon Linux 2023
Previous Version:    Kubernetes 1.34
Current Version:     Kubernetes 1.35
```

## Upgrade Process

The upgrade was performed in stages to reduce the risk of disrupting the transaction service.

```text
AWS Health Notification
        │
        ▼
Inspect EKS Cluster
        │
        ▼
Check Upgrade Readiness
        │
        ▼
Verify Running Workloads
        │
        ▼
Upgrade EKS Control Plane
     1.34 → 1.35
        │
        ▼
Verify Control Plane
        │
        ▼
Upgrade Managed Node Group
     1.34 → 1.35
        │
        ▼
Verify New Worker Node
        │
        ▼
Review EKS Add-ons
        │
        ▼
Upgrade kube-proxy
        │
        ▼
Upgrade CoreDNS
        │
        ▼
Verify Application
        │
        ▼
Upgrade Complete
```

## Pre-Upgrade Checks

The cluster version and status were checked before performing the upgrade:

```bash
aws eks describe-cluster \
  --name eks-transactions-cluster \
  --region af-south-1 \
  --query "cluster.{Name:name,Version:version,Status:status}"
```

The cluster was initially running:

```text
Version: 1.34
Status: ACTIVE
```

The managed node group was checked using:

```bash
aws eks describe-nodegroup \
  --cluster-name eks-transactions-cluster \
  --nodegroup-name app-nodes \
  --region af-south-1 \
  --query "nodegroup.{Name:nodegroupName,Version:version,Status:status,AmiType:amiType,ReleaseVersion:releaseVersion}"
```

The original node-group version was:

```text
Kubernetes:      1.34
Release Version: 1.34.10-20260903
Status:          ACTIVE
```

Cluster workloads were also verified:

```bash
kubectl get nodes -o wide
kubectl get pods -A
```

The worker node was `Ready`, and the transaction service was running successfully before the upgrade.

## EKS Upgrade Readiness

Amazon EKS Upgrade Insights were checked before upgrading:

```bash
aws eks list-insights \
  --cluster-name eks-transactions-cluster \
  --region af-south-1
```

The EKS add-on compatibility check for Kubernetes 1.35 reported:

```text
Status: PASSING
```

This confirmed that the installed EKS add-ons were compatible with the target Kubernetes version.

## Control Plane Upgrade

The EKS control plane was upgraded using:

```bash
aws eks update-cluster-version \
  --name eks-transactions-cluster \
  --kubernetes-version 1.35 \
  --region af-south-1
```

The upgrade was monitored until the cluster returned to:

```text
Version: 1.35
Status: ACTIVE
```

Verification command:

```bash
aws eks describe-cluster \
  --name eks-transactions-cluster \
  --region af-south-1 \
  --query "cluster.{Version:version,Status:status}"
```

## Managed Node Group Upgrade

After the control-plane upgrade, the managed node group was upgraded separately.

```bash
aws eks update-nodegroup-version \
  --cluster-name eks-transactions-cluster \
  --nodegroup-name app-nodes \
  --kubernetes-version 1.35 \
  --region af-south-1
```

The update was monitored using:

```bash
aws eks list-updates \
  --name eks-transactions-cluster \
  --nodegroup-name app-nodes \
  --region af-south-1
```

and:

```bash
aws eks describe-update \
  --name eks-transactions-cluster \
  --nodegroup-name app-nodes \
  --region af-south-1 \
  --update-id <UPDATE_ID>
```

The completed node group is:

```text
Status:          ACTIVE
Version:         1.35
Release Version: 1.35.8-20260917
OS:              Amazon Linux 2023
```

## EKS Add-on Maintenance

The cluster uses the following managed EKS add-ons:

```text
aws-secrets-store-csi-driver-provider
coredns
eks-pod-identity-agent
kube-proxy
vpc-cni
```

Installed add-ons can be listed using:

```bash
aws eks list-addons \
  --cluster-name eks-transactions-cluster \
  --region af-south-1
```

Compatible/default versions for Kubernetes 1.35 were checked using:

```bash
aws eks describe-addon-versions \
  --addon-name <ADDON_NAME> \
  --kubernetes-version 1.35 \
  --region af-south-1
```

### kube-proxy

`kube-proxy` was aligned with the Kubernetes 1.35 cluster.

Target version:

```text
v1.35.3-eksbuild.29
```

Upgrade:

```bash
aws eks update-addon \
  --cluster-name eks-transactions-cluster \
  --addon-name kube-proxy \
  --addon-version v1.35.3-eksbuild.29 \
  --resolve-conflicts PRESERVE \
  --region af-south-1
```

### CoreDNS

The Kubernetes 1.35 default CoreDNS version was identified as:

```text
v1.13.2-eksbuild.31
```

Upgrade:

```bash
aws eks update-addon \
  --cluster-name eks-transactions-cluster \
  --addon-name coredns \
  --addon-version v1.13.2-eksbuild.31 \
  --resolve-conflicts PRESERVE \
  --region af-south-1
```

### Other Add-ons

The remaining add-ons were already on the Kubernetes 1.35 default versions:

```text
vpc-cni
v1.22.4-eksbuild.3

eks-pod-identity-agent
v1.3.10-eksbuild.3

aws-secrets-store-csi-driver-provider
v3.1.3-eksbuild.1
```

No additional version changes were required for these add-ons.

## Post-Upgrade Verification

The worker node was verified after the managed node-group replacement:

```bash
kubectl get nodes -o wide
```

Expected state:

```text
STATUS   VERSION
Ready    v1.35.x
```

System workloads were checked using:

```bash
kubectl get pods -n kube-system -o wide
```

Verified components included:

```text
CoreDNS                 Running
EKS Pod Identity Agent  Running
kube-proxy              Running
```

Application health was then checked:

```bash
kubectl get pods -n eks-transactions
```

Result:

```text
READY   STATUS    RESTARTS
1/1     Running   0
```

The deployment was also verified:

```bash
kubectl get deployment -n eks-transactions
```

Result:

```text
NAME                       READY   UP-TO-DATE   AVAILABLE
eks-transactions-service   1/1     1            1
```

The service remained available after the Kubernetes control-plane and worker-node upgrades.

## Current EKS State

```text
eks-transactions-cluster
│
├── Kubernetes Control Plane
│   └── 1.35
│
├── Managed Node Group
│   └── app-nodes
│       ├── Kubernetes 1.35
│       ├── Amazon Linux 2023
│       └── Release 1.35.8-20260917
│
├── EKS Add-ons
│   ├── kube-proxy
│   │   └── v1.35.3-eksbuild.29
│   │
│   ├── CoreDNS
│   │   └── v1.13.2-eksbuild.31
│   │
│   ├── VPC CNI
│   │   └── v1.22.4-eksbuild.3
│   │
│   ├── EKS Pod Identity Agent
│   │   └── v1.3.10-eksbuild.3
│   │
│   └── AWS Secrets Store Provider
│       └── v3.1.3-eksbuild.1
│
└── Workload
    └── eks-transactions-service
        └── 1/1 Running
```

## Operational Lessons

This upgrade demonstrates that an Amazon EKS Kubernetes upgrade involves more than changing the control-plane version.

The maintenance process includes:

* reviewing AWS Health lifecycle notifications
* checking EKS Upgrade Insights
* verifying workloads before maintenance
* upgrading the EKS control plane
* upgrading managed node groups
* reviewing EKS managed add-ons
* aligning Kubernetes-specific components such as `kube-proxy`
* validating CoreDNS and networking components
* checking worker-node health
* verifying application availability after node replacement

This provides practical experience with Kubernetes lifecycle management and production-style Amazon EKS operations.

