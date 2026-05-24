FROM node:20-alpine AS frontend-builder
WORKDIR /build

COPY src/ ./src/

WORKDIR /build/src/webApp
RUN npm install
RUN npm run build

FROM maven:3.9-eclipse-temurin-21-alpine AS backend-builder
WORKDIR /build

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY --from=frontend-builder /build/src ./src

RUN mvn clean package -DskipTests


FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=backend-builder /build/target/*.jar app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]
