FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy and install the parent POM first
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn install -N

# Copy the module POM and source
COPY job-posting-service/pom.xml job-posting-service/
COPY job-posting-service/src job-posting-service/src

# Build the application
RUN mvn -f job-posting-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /workspace/app/job-posting-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
