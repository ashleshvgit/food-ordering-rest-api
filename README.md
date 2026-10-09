# Food Ordering REST API

A backend REST API application for managing restaurants, food items, customers, shopping carts, and orders. Developed using Java, Spring Boot, Hibernate/JPA, and PostgreSQL.

## Overview

The Food Ordering REST API provides endpoints to manage restaurant information, maintain food menus, register customers, manage shopping carts, and place and track orders.

The application follows a layered architecture to separate HTTP request handling, business logic, data mapping, and database operations.

## Tech Stack

* **Language:** Java
* **Framework:** Spring Boot
* **ORM:** Hibernate
* **Persistence:** Spring Data JPA
* **Database:** PostgreSQL
* **API Documentation:** Swagger UI / OpenAPI
* **Build Tool:** Maven
* **Testing Tool:** Swagger UI and Postman

## Architecture

The application follows a layered architecture:

**Controller → Service → Service Implementation → Mapper → Repository → Hibernate/JPA → PostgreSQL**

### Layer Responsibilities

* **Controller:** Handles HTTP requests and responses.
* **Service:** Defines business operations.
* **Service Implementation:** Implements business logic and validation of business rules.
* **Mapper:** Converts between DTOs and entities.
* **Repository:** Provides database access through Spring Data JPA.
* **Hibernate/JPA:** Maps Java entities to database tables and manages persistence.
* **PostgreSQL:** Stores application data.

## Features

### Restaurant Management

* Create, retrieve, update, and delete restaurants.
* Activate or deactivate restaurants.
* Retrieve restaurants with pagination, sorting, and supported filters.

### Food Item Management

* Add, view, update, and delete food items.
* Associate food items with restaurants.
* Manage food availability.
* Filter food items by restaurant and category.
* Support pagination and sorting.

### Customer Management

* Register customers and manage customer details.
* Retrieve customer information by ID.
* Search customers by name.
* Validate customer data and prevent duplicate email registrations.

### Shopping Cart

* Add food items to a customer's cart.
* Update item quantities.
* Remove individual items.
* View cart items, subtotals, and total amount.
* Clear the cart.
* Enforce the rule that a cart can contain food items from only one restaurant.

### Order Management

* Place orders using the customer's shopping cart.
* Calculate order totals.
* Store order items with their purchase-time names and prices.
* Retrieve orders by ID.
* Filter orders by customer and status.
* Update order status through permitted transitions.
* Clear the cart after successful order placement.

### Validation and Exception Handling

* Validate incoming request data.
* Handle missing resources and duplicate records.
* Handle business-rule violations.
* Return structured error responses through a global exception handler.

### API Documentation

* Document REST endpoints using Swagger/OpenAPI.
* Test API operations through the Swagger UI interface.

## Main API Endpoints

All endpoints use the base URL:

`http://localhost:8080`

| Module      | HTTP Method | Endpoint                                                 | Purpose                  |
| ----------- | ----------- | -------------------------------------------------------- | ------------------------ |
| Restaurants | POST        | `/api/v1/restaurants`                                    | Create a restaurant      |
| Restaurants | GET         | `/api/v1/restaurants`                                    | Retrieve restaurants     |
| Restaurants | GET         | `/api/v1/restaurants/{id}`                               | Retrieve a restaurant    |
| Restaurants | PUT         | `/api/v1/restaurants/{id}`                               | Update a restaurant      |
| Restaurants | PATCH       | `/api/v1/restaurants/{id}/status`                        | Update restaurant status |
| Restaurants | DELETE      | `/api/v1/restaurants/{id}`                               | Delete a restaurant      |
| Food Items  | POST        | `/api/v1/food-items`                                     | Create a food item       |
| Food Items  | GET         | `/api/v1/food-items`                                     | Retrieve food items      |
| Food Items  | GET         | `/api/v1/food-items/{id}`                                | Retrieve a food item     |
| Food Items  | PUT         | `/api/v1/food-items/{id}`                                | Update a food item       |
| Food Items  | DELETE      | `/api/v1/food-items/{id}`                                | Delete a food item       |
| Customers   | POST        | `/api/v1/customers`                                      | Register a customer      |
| Customers   | GET         | `/api/v1/customers`                                      | Retrieve customers       |
| Customers   | GET         | `/api/v1/customers/{id}`                                 | Retrieve a customer      |
| Customers   | PUT         | `/api/v1/customers/{id}`                                 | Update customer details  |
| Customers   | DELETE      | `/api/v1/customers/{id}`                                 | Delete a customer        |
| Cart        | GET         | `/api/v1/customers/{customerId}/cart`                    | View the cart            |
| Cart        | POST        | `/api/v1/customers/{customerId}/cart/items`              | Add an item              |
| Cart        | PUT         | `/api/v1/customers/{customerId}/cart/items/{foodItemId}` | Update quantity          |
| Cart        | DELETE      | `/api/v1/customers/{customerId}/cart/items/{foodItemId}` | Remove an item           |
| Cart        | DELETE      | `/api/v1/customers/{customerId}/cart`                    | Clear the cart           |
| Orders      | POST        | `/api/v1/orders`                                         | Place an order           |
| Orders      | GET         | `/api/v1/orders`                                         | Retrieve orders          |
| Orders      | GET         | `/api/v1/orders/{id}`                                    | Retrieve an order        |
| Orders      | PATCH       | `/api/v1/orders/{id}/status`                             | Update order status      |

## Order Status Flow

The application supports the following order-status lifecycle:

`PLACED → CONFIRMED → PREPARING → OUT_FOR_DELIVERY → DELIVERED`

Orders in the `PLACED` or `CONFIRMED` status can also be cancelled, subject to the implemented business rules.

## Database Relationships

The application uses JPA entity relationships to represent the data model.

* **Restaurant and FoodItem:** One restaurant can have multiple food items.
* **Customer and Cart:** A customer has an associated shopping cart.
* **Cart and CartItem:** A cart contains multiple cart items.
* **Order and OrderItem:** An order contains multiple order items.

The application uses Hibernate/JPA annotations to manage entity relationships and database persistence.

## Setup and Installation

### Prerequisites

* Java Development Kit (JDK 17 or compatible version required by the project).
* PostgreSQL.
* Maven.
* IntelliJ IDEA or another Java IDE.

### 1. Clone the Repository

```bash
git clone YOUR_GITHUB_REPOSITORY_URL
```

Navigate into the project directory:

```bash
cd food-ordering-rest-api
```

Replace the repository URL with the actual GitHub repository URL.

### 2. Create the Database

Open PostgreSQL and execute:

```sql
CREATE DATABASE food_ordering;
```

If your database already exists, you can skip this step.

### 3. Configure Database Credentials

Open:

`src/main/resources/application.properties`

Configure the PostgreSQL connection URL, username, and password for your local environment.

For example, the connection URL may look like:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/food_ordering
```

Set the appropriate username and password for your PostgreSQL installation.

**Security:** Do not commit real database passwords, API keys, or other secrets to GitHub. Use environment variables or another appropriate local configuration method for sensitive values.

### 4. Run the Application

Open the project in IntelliJ IDEA, allow Maven to resolve dependencies, and run the main Spring Boot application class.

Alternatively, if Maven is available, run:

```bash
mvn spring-boot:run
```

Ensure that PostgreSQL is running and the database configuration is correct.

### 5. Access Swagger UI

After the application starts, open:

**http://localhost:8080/swagger-ui/index.html**

Use Swagger UI to explore the available endpoints, provide request bodies, execute API operations, and inspect responses.

The exact Swagger UI path may depend on the project's OpenAPI configuration.

## Example: Place an Order

Send a POST request to:

`/api/v1/orders`

Example request body:

```json
{
  "customerId": 1,
  "deliveryAddress": "12 MG Road, Bengaluru"
}
```

Use an existing customer ID and ensure the customer's cart contains valid items.

A successful request creates an order and clears the cart according to the application's order-placement logic.

## Project Structure

The application is organized into packages such as:

```text
src/main/java/com/ashlesh/mycoolproject/
├── controller/
├── service/
├── service/impl/
├── repository/
├── entity/
├── dto/
│   ├── request/
│   └── response/
├── mapper/
├── exception/
├── enums/
└── util/
```

The exact files may vary as the project evolves.

## Learning Outcomes

This project demonstrates practical implementation of:

* REST API development using Spring Boot.
* Layered application architecture.
* DTO-based request and response handling.
* Hibernate/JPA entity mapping.
* PostgreSQL database integration.
* CRUD operations.
* Request validation and global exception handling.
* Pagination, sorting, and filtering.
* Shopping cart and order-management workflows.
* API documentation and testing using Swagger UI.

## Future Enhancements

Potential improvements for future versions include:

* Authentication and authorization using Spring Security and JWT.
* Unit and integration tests.
* Payment integration.
* Automated API testing.
* Docker-based application deployment.

---

**Developed as a Java backend project to practise Spring Boot, Hibernate/JPA, PostgreSQL, and REST API development.**
