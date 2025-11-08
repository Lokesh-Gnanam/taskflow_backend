FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy everything
COPY . .

# Fix permissions and make mvnw executable
RUN chmod +x mvnw
RUN ls -la pom.xml
RUN ls -la src/

# Build
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]