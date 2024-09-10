FROM maven:3.9.6 as build

WORKDIR /workspace/app

COPY src ./src
COPY pom.xml ./
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/app/target/*.jar app.jar

EXPOSE 8080

CMD ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]