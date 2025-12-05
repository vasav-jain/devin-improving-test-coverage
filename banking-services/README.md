# Banking Services Mock Codebase

This directory contains three standalone Spring Boot services designed to model realistic banking workflows so that Devin can later generate unit and integration tests.

## Services

1. **payments-service**
   - Payment scheduling & execution
   - Interest accrual and mortgage estimation logic
   - Endpoints: `/payments/schedule`, `/payments/execute/{paymentId}`, `/payments/history/{accountId}`, `/interest/calculate`, `/mortgage/estimate`
2. **mobile-app-service**
   - Mobile authentication, JWT issuing, device fingerprint validation
   - Account summaries and transaction filtering
   - Endpoints: `/auth/register`, `/auth/login`, `/accounts/{userId}`, `/accounts/{userId}/summary`, `/transactions/{accountId}`
3. **compliance-service**
   - AML rules engine, KYC verification, fraud scoring, compliance reporting
   - Endpoints: `/aml/check`, `/kyc/verify`, `/fraud/score`, `/compliance/report/{userId}`

Each service targets Java 17 and Spring Boot 3.2.

## Running Locally

From each service directory:

```bash
cd <service-directory>
mvn spring-boot:run
```

Example:

```bash
cd payments-service
mvn spring-boot:run
```

The applications use default ports (8080) so run them individually or supply `-Dspring-boot.run.arguments="--server.port=8081"` to avoid clashes.

## Running Tests

Each service includes a small number of sample tests. To run all tests for a service:

```bash
cd <service-directory>
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=<TestClassName>
```

Examples:

```bash
cd payments-service
mvn test -Dtest=InterestCalculatorTest

cd mobile-app-service
mvn test -Dtest=DeviceServiceTest

cd compliance-service
mvn test -Dtest=FraudScoringServiceTest
```

**Note:** Some tests are intentionally incomplete or have incorrect assertions to demonstrate where Devin should generate additional coverage and fix edge cases.

## Testing Ideas for Devin

- Payments: coverage of partial payments, interest accrual grace periods, PMI thresholds, prepayment penalties.
- Mobile: hashing failures, device fingerprint edge cases, transaction filtering combinations.
- Compliance: AML velocity rules, KYC failure modes, fraud score weighting, report classification.

These scenarios include realistic branching and validations to exercise Devin's automated test generation capabilities.
