FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

# Copy and install the parent POM first
COPY pom.xml .
RUN apk add --no-cache maven && \
    mvn install -N

# Copy the module POM and source
COPY user-service/pom.xml user-service/
COPY user-service/src user-service/src

# Build the application
RUN mvn -f user-service/pom.xml clean package -DskipTests

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /workspace/app/user-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
