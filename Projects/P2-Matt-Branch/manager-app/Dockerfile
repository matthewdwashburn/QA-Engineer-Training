FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn package -DskipTests -B

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/manager-app.jar /app/manager-app.jar

ENV DB_URL=jdbc:sqlite:/app/database/expense_manager.db

EXPOSE 7001

CMD ["java", "-jar", "manager-app.jar"]