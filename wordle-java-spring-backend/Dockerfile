FROM maven:3.8.4-eclipse-temurin-17 AS build
WORKDIR /src
COPY . .
RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /src/target/wordle-java-spring-backend.jar .
EXPOSE 9090
CMD java -jar wordle-java-spring-backend.jar $APP_PARAMS