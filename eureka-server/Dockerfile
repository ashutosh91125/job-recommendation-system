FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy and install the parent POM first
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn install -N

# Copy the module POM and source
COPY eureka-server/pom.xml eureka-server/
COPY eureka-server/src eureka-server/src

# Build the application
RUN mvn -f eureka-server/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /workspace/app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
