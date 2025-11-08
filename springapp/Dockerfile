FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy everything
COPY . .

# Debug: Show what files were copied
RUN ls -la
RUN find . -name "pom.xml" | head -5
RUN find . -name "*.java" | head -5

# Use direct mvn command (not mvnw)
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]