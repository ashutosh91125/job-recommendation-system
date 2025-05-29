# AI-Powered Job Recommendation System

This is a microservices-based job recommendation system that uses AI to provide personalized job recommendations based on user skills and preferences.

## Architecture

The system consists of the following microservices:

- **User Service** (Port: 8080): Handles user management and authentication
- **Recommendation Service** (Port: 8081): Provides AI-powered job recommendations
- **Job Posting Service** (Port: 8082): Manages job listings
- **API Gateway** (Port: 8083): Routes requests to appropriate services

## Technologies Used

- Java 17
- Spring Boot 3.1.0
- Spring Cloud
- MySQL (User data)
- MongoDB (Job listings)
- Apache Kafka (Event streaming)
- DeepLearning4J (AI recommendations)
- Docker & Docker Compose
- OAuth 2.0 / JWT (Security)

## Prerequisites

- Java 17 or later
- Maven
- Docker & Docker Compose
- Git

## Getting Started

1. Clone the repository
2. Build all services:
   ```bash
   mvn clean install
   ```
3. Start the services using Docker Compose:
   ```bash
   docker-compose up --build
   ```

## Service URLs

- User Service: http://localhost:8080
- Recommendation Service: http://localhost:8081
- Job Posting Service: http://localhost:8082
- API Gateway: http://localhost:8083

## Development

Each service is a separate Spring Boot application with its own database and configuration. The services communicate through REST APIs and Kafka events.

### Key Features

- User registration and authentication with OAuth 2.0
- AI-powered job recommendations using DeepLearning4J
- Real-time job posting updates via Kafka
- Scalable microservices architecture
- Containerized deployment with Docker

## Testing

Each service includes unit tests and integration tests. To run the tests:

```bash
mvn test
```

## Deployment

The application is containerized using Docker and can be deployed to any cloud platform that supports Docker containers (AWS, Azure, GCP, etc.).

## Project Structure

```
job-recommendation-system/
├── api-gateway/               # API Gateway Service
├── user-service/             # User Management Service
├── recommendation-service/   # AI Recommendation Service
├── job-posting-service/     # Job Posting Management Service
├── docker-compose.yml       # Docker Compose configuration
└── pom.xml                  # Parent POM file
```
