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

### Starting with Docker Compose

1. Make sure Docker Desktop is running on your machine

2. Build and start all services:
   ```bash
   docker-compose up --build -d
   ```
   This will:
   - Build all service images
   - Start Zookeeper and Kafka
   - Start MySQL and MongoDB
   - Start all microservices in the correct order

3. Verify services are running:
   ```bash
   docker-compose ps
   ```

4. Check service logs:
   ```bash
   # View all logs
   docker-compose logs

   # View logs for a specific service
   docker-compose logs user-service
   docker-compose logs job-posting-service
   docker-compose logs recommendation-service
   ```

5. Stop all services:
   ```bash
   docker-compose down
   ```

### Service Dependencies

The services will start in the following order:
1. Zookeeper
2. Kafka
3. MySQL & MongoDB
4. Eureka Server
5. User Service
6. Job Posting Service
7. Recommendation Service
8. API Gateway

### Troubleshooting

If any service fails to start:
1. Check the logs: `docker-compose logs [service-name]`
2. Ensure all ports are available (8080, 8081, 8082, 8083, 29092, 2181, 3307, 27017)
3. Verify Docker has enough resources allocated
4. Try rebuilding: `docker-compose up --build --force-recreate`

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
