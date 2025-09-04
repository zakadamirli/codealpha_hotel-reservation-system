# Hostify - Hotel Reservation System

Hostify is a comprehensive hotel reservation backend system built with Spring Boot. It provides a robust set of REST APIs for managing users, properties, reservations, and payments. The system is designed to serve as the core service for a hotel booking platform, featuring functionalities for both guests and property owners.

## Features

*   **User Management:** Register, retrieve, update, and delete user accounts.
*   **Property Management:** Full CRUD operations for hotel properties. Properties can be listed by owner or category (e.g., SUITE, DELUXE, SINGLE).
*   **Reservation System:** Users can create, cancel, and view their reservations. Property owners can confirm pending reservations.
*   **Social Features:** Users can create posts associated with properties and leave comments on these posts.
*   **Payment Processing:** Integration with Stripe to handle payment sessions for bookings.
*   **API Documentation:** Integrated Swagger/OpenAPI for easy API exploration and testing.
*   **Validation & Error Handling:** Robust input validation and custom exception handling for clear API responses.

## Tech Stack

*   **Backend:** Java, Spring Boot, Spring Data JPA, Spring Web
*   **Database:** PostgreSQL
*   **Build Tool:** Gradle
*   **API:** REST, OpenAPI 3 (Swagger)
*   **Payment:** Stripe API
*   **Libraries:** Lombok, ModelMapper

## Getting Started

### Prerequisites

*   JDK 17 or later
*   PostgreSQL installed and running
*   A Stripe account to obtain an API secret key

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone https://github.com/zakadamirli/codealpha_hotel-reservation-system.git
    cd codealpha_hotel-reservation-system
    ```

2.  **Configure the database:**
    *   Create a PostgreSQL database named `HostifyDB`.
    *   Open `src/main/resources/application.properties` and update the following properties with your PostgreSQL credentials:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/HostifyDB
        spring.datasource.username=your_postgres_username
        spring.datasource.password=your_postgres_password
        ```

3.  **Configure Stripe:**
    *   Set your Stripe secret key as an environment variable named `STRIPE_SK`. The application reads this key from the environment.
    *   For Linux/macOS:
        ```sh
        export STRIPE_SK='your_stripe_secret_key'
        ```
    *   For Windows (PowerShell):
        ```powershell
        $env:STRIPE_SK="your_stripe_secret_key"
        ```

4.  **Run the application:**
    *   Use the Gradle wrapper to build and run the project:
        ```sh
        ./gradlew bootRun
        ```
    *   The application will start on `http://localhost:8080`.

## API Endpoints

The API is documented using OpenAPI (Swagger). Once the application is running, you can access the interactive API documentation at:
`http://localhost:8080/swagger-ui.html`

Here is a summary of the main endpoints:

| Endpoint | Method | Description |
| --- | --- | --- |
| `/api/v1/users/register` | POST | Register a new user. |
| `/api/v1/users/{userId}` | GET | Get user details by ID. |
| `/api/properties` | POST | Add a new property. |
| `/api/properties/{id}` | GET | Get property details by ID. |
| `/api/properties/owner/{ownerId}` | GET | Get all properties for a specific owner. |
| `/api/properties/category/{category}` | GET | Get all properties in a specific category. |
| `/api/reservations` | POST | Create a new reservation. |
| `/api/reservations/{id}/cancel` | PATCH | Cancel a reservation. |
| `/api/reservations/{id}/confirm` | PATCH | Confirm a reservation (for hosts). |
| `/api/v1/posts/addPost` | POST | Create a new post for a property. |
| `/api/v1/comments/addComment` | POST | Add a comment to a post. |
| `/api/product/v1/create-booking-session` | POST | Create a Stripe checkout session for a booking. |

## Sample Frontend

The repository includes a basic HTML frontend for demonstrating the Stripe payment flow. You can access it at:
`http://localhost:8080/index.html`

This page shows sample properties and allows you to proceed to a Stripe checkout page to simulate a booking payment. Payment success and cancellation will redirect to `success.html` and `cancel.html` respectively.