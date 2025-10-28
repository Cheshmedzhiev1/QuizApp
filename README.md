# Quiz Application

A web-based quiz application built with Spring Boot and Thymeleaf. This application allows users to take interactive quizzes loaded from a JSON data source and view their results.

## Features

- Interactive quiz interface
- Multiple-choice questions
- Score calculation and results display
- Custom error handling
- Quiz data loaded from JSON configuration

## Technologies Used

- **Java 21**
- **Spring Boot 3.5.7**
- **Thymeleaf** (templating engine)
- **Maven** (dependency management)

## Prerequisites

Before running the application, ensure you have the following installed:

- Java Development Kit (JDK) 21 or higher
- Maven 3.6+ (optional, as the project includes Maven Wrapper)

## How to Run

### Option 1: Using Maven Wrapper (Recommended)

The project includes a Maven Wrapper, so you don't need Maven installed separately.

**On macOS/Linux:**
```bash
./mvnw spring-boot:run
```

**On Windows:**
```bash
mvnw.cmd spring-boot:run
```

### Option 2: Using Maven

If you have Maven installed on your system:

```bash
mvn spring-boot:run
```

### Option 3: Build and Run JAR

1. Build the project:
   ```bash
   ./mvnw clean package
   ```

2. Run the generated JAR file:
   ```bash
   java -jar target/quiz-app-0.0.1-SNAPSHOT.jar
   ```

## Accessing the Application

Once the application is running, open your web browser and navigate to:

```
http://localhost:8080
```

The application will be accessible at the default Spring Boot port (8080).

## Project Structure

```
quiz-app/
├── src/
│   ├── main/
│   │   ├── java/com/quiz/
│   │   │   ├── controller/     # Controllers for handling HTTP requests
│   │   │   ├── model/          # Data models (Quiz, Question, QuizResult)
│   │   │   ├── service/        # Business logic
│   │   │   └── exception/      # Custom exception handling
│   │   └── resources/
│   │       ├── data.json       # Quiz questions data
│   │       ├── templates/      # Thymeleaf HTML templates
│   │       └── application.properties
│   └── test/                   # Unit tests
├── pom.xml                     # Maven configuration
└── README.md
```

## Stopping the Application

To stop the application, press `Ctrl + C` in the terminal where the application is running.
