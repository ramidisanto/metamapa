FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

COPY . .

RUN mvn clean package -pl ModuloAuth -am -DskipTests

FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY --from=build /app/ModuloAuth/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]