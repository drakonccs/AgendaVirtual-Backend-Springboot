# ---------- Etapa 1: Build ----------
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copiar pom.xml primero para cachear dependencias
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Copiar código fuente y compilar
COPY src ./src
RUN mvn -B -q package -DskipTests

# ---------- Etapa 2: Runtime ----------
FROM eclipse-temurin:21-jre



WORKDIR /app

# Copiar JAR generado en la etapa anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080


ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]