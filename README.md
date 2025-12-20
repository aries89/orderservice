# Order Service

## Overview
The Order Service handles order creation, tracking, and management for the E-Commerce system.

## Responsibilities
- Create and manage customer orders
- Track order status
- Validate inventory before confirmation

## Technology Stack
- Java 21
- Spring Boot
- Spring Data JPA
- PostgreSQL

## Running Locally
1. Clone the repository:
```bash
git clone https://github.com/aries89/orderservice.git
2.	Build and run:
mvn spring-boot:run
Endpoints
•	POST /api/orders - Create a new order
•	GET /api/orders/{id} - Fetch order by ID

Future Enhancements
•	Role based access
•	Event-driven communication with Product and Inventory services
