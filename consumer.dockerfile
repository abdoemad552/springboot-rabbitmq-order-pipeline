
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY order-common/ order-common/
RUN mvn -f order-common/pom.xml clean install -DskipTests

COPY order-consumer/ order-consumer/
RUN mvn -f order-consumer/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/order-consumer/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
