# PaymentService

RESTful microservice handling payment processing, refunds, and order lookups with Hibernate ORM and JWT auth.

## Stack

- Java 21
- Spring Boot 2.7
- H2 Database (in-memory)
- Spring Data JPA
- Spring Security

## Building

```bash
mvn clean install
```

## Running

```bash
mvn spring-boot:run
```

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | /api/orders | List all orders |
| GET | /api/orders/{id} | Get order by ID |
| GET | /api/orders/search?customerId= | Search orders by customer |
| POST | /api/orders | Create a new order |
| POST | /api/orders/{id}/pay | Process payment for order |
| POST | /api/orders/{id}/refund | Process refund for order |
