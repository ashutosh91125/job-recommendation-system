FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy and install the parent POM first
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn install -N

# Copy the module POM and source
COPY api-gateway/pom.xml api-gateway/
COPY api-gateway/src api-gateway/src

# Build the application
RUN mvn -f api-gateway/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /workspace/app/api-gateway/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
