# ========================================================
# Stage 1: Maven Build
# ========================================================
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:resolve
COPY . .
RUN mvn clean package -DskipTests

# ========================================================
# Stage 2: Runtime (Minimal image)
# ========================================================
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy only the built JAR
COPY --from=build /app/target/*.jar app.jar

# Expose port (documentation only, not binding)
EXPOSE 8080

# AWS RDS Connection Variables
ENV DB_URL=jdbc:mysql://library-db-portfolio.cnciwacas5l1.ap-northeast-1.rds.amazonaws.com:3306/library_db?useSSL=true
ENV DB_USERNAME=admin
ENV DB_PASSWORD=elaine1031

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]
