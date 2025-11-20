# Stage 1: Build avec Maven
FROM maven:3.9.5-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source
COPY src ./src

# Compiler et packager l'application (skip tests pour accélérer le build)
RUN mvn clean package -DskipTests

# Stage 2: Runtime avec JRE léger
FROM eclipse-temurin:17-jre-alpine

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis le stage de build
COPY --from=build /app/target/gestion_projet-1.0-SNAPSHOT.jar app.jar

# Exposer le port 8080 (port par défaut Spring Boot)
EXPOSE 8080

# Variables d'environnement (peuvent être surchargées au démarrage)
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]