FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy and install the parent POM first
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn install -N

# Copy the module POM and source
COPY recommendation-service/pom.xml recommendation-service/
COPY recommendation-service/src recommendation-service/src

# Build the application
RUN mvn -f recommendation-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /workspace/app/recommendation-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
