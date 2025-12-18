
FROM maven:3.9.6-eclipse-temurin-17 AS build

RUN apt-get update && apt-get install -y curl \
    && curl -fsSL https://deb.nodesource.com/setup_20.x | bash - \
    && apt-get install -y nodejs

WORKDIR /app

COPY . .

WORKDIR /app/FrontEnd
RUN npm install

RUN npm run build:css

WORKDIR /app
RUN mvn clean package -pl FrontEnd -am -DskipTests

FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y curl

WORKDIR /app

COPY --from=build /app/FrontEnd/target/*.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]