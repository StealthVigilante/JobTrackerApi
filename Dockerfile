# ---- Stage 1: build the jar ----
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper + pom first, download deps (cached layer)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN ./mvnw clean package -DskipTests

# ---- Stage 2: lean runtime image ----
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]