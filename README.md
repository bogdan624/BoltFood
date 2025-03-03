# BoltFood

**BoltFood** is a full-stack food ordering web application developed as a personal project. It combines modern web technologies to offer a secure and intuitive experience for both users and developers.

## Features

- **RESTful API:** Implements complete CRUD operations for orders, users, and menu items.
- **Secure Authentication:** Uses Spring Security and JWT for robust user authentication.
- **Responsive UI:** Developed with Thymeleaf, HTML, and CSS for a seamless user experience.
- **Database Integration:** Leverages Spring Data JPA with MySQL (with H2 for testing purposes).
- **API Documentation:** Provides interactive documentation via Swagger.
- **Comprehensive Testing:** Ensures quality with JUnit and Mockito tests.

## Technologies

- **Backend:** Java, Spring Boot, Spring Data JPA, Spring Security, JWT
- **Frontend:** HTML, CSS, Thymeleaf
- **Database:** MySQL (or H2 for testing)
- **Build Tool:** Maven

## Getting Started

### Prerequisites

- Java 22 (or a compatible version)
- Maven
- MySQL (if using a relational database; H2 is available for testing)

### Installation

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/bogdan624/BoltFood.git
   ```

2. **Navigate to the Project Directory:**

   ```bash
   cd BoltFood
   ```

3. **Configure the Database:**

   Edit the `application.properties` (or `application.yml`) file with your MySQL credentials, or use the default settings for H2.

4. **Build and Run the Application:**

   ```bash
   mvn spring-boot:run
   ```

5. **Access the Application:**

   - Open [http://localhost:8080](http://localhost:8080) in your browser.
   - View the API documentation at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

## Contributing

Feedback and contributions are welcome! If you have suggestions or improvements, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
