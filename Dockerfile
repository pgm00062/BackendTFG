# Etapa de build
FROM eclipse-temurin:8-jdk AS build
WORKDIR /app

COPY . .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Build del proyecto
RUN ./mvnw clean package -DskipTests

# Etapa de runtime
FROM eclipse-temurin:8-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
