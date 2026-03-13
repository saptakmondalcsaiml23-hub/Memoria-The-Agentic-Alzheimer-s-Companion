FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY . .
RUN mvn -q -DskipTests -pl identity-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /workspace/identity-service/target/identity-service-0.1.0.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app/app.jar"]
