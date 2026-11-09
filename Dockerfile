# Multi-stage build para otimizar tamanho da imagem
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage de execução com JRE 21 leve
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Porta padrão de cloud (Render, Railway, etc. injetam a variável PORT)
EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
