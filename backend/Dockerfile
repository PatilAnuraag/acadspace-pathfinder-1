# Naviksha AI Backend Dockerfile
# 
# Multi-stage build for optimized production image
# 1. Build stage: Compile and package the application
# 2. Runtime stage: Run the application with minimal dependencies
#
# BUILD COMMAND: docker build -t naviksha-backend .
# RUN COMMAND: docker run -p 4000:4000 -e MONGO_URI=mongodb://mongo:27017/naviksha naviksha-backend
#
# ENVIRONMENT VARIABLES REQUIRED:
# - MONGO_URI: MongoDB connection string
# - JWT_SECRET: JWT signing secret (use strong secret in production)
# - ADMIN_SECRET: Admin access secret (optional)

# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml first to leverage Docker cache for dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

# Install curl for health checks
RUN apk add --no-cache curl

# Create non-root user for security
RUN addgroup -g 1000 naviksha && \
    adduser -D -s /bin/sh -u 1000 -G naviksha naviksha

WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/naviksha-backend-*.jar app.jar

# Copy data files needed for seeding
COPY --from=build /app/src/main/resources/data ./data

# Set ownership
RUN chown -R naviksha:naviksha /app

# Switch to non-root user
USER naviksha

# Expose port
EXPOSE 4000

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4000/health || exit 1

# Environment variables with defaults
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=4000
ENV MONGO_URI=mongodb://mongo:27017/naviksha
ENV JWT_SECRET=change-this-secret-in-production

# JVM optimization for containers
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Labels for metadata
LABEL maintainer="Naviksha AI Team"
LABEL version="1.0.0"
LABEL description="Naviksha AI Career Guidance Backend"