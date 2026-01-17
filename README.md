curl http://localhost:8082/GestionEvenement/logistique/health

curl http://localhost:8082/GestionEvenement/service/ping
 curl http://localhost:8082/GestionEvenement/service/info



##tester communication:
GET http://localhost:8089/GestionEvenement/feign-test/info
GET http://localhost:8089/GestionEvenement/feign-test/test-service-not-found
GET http://localhost:8089/GestionEvenement/feign-test/test-timeout/1




##resilience:
# Test 1 : Info
Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/info'

# Test 2 : Circuit Breaker
Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/circuit-breaker?fail=false'

# Test 3 : Retry
Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/retry'

# Test 4 : Rate Limiter (3x rapidement)
1..5 | ForEach-Object { 
    Write-Host "Requête $_" -ForegroundColor Yellow
    try {
        Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/rate-limiter'
        Write-Host "  ✅ Acceptée" -ForegroundColor Green
    } catch {
        Write-Host "  ❌ Rejetée (rate limited)" -ForegroundColor Red
    }
    Start-Sleep -Milliseconds 400
}

# Test 5 : Timeout OK (3 secondes)
Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/timeout?delaySeconds=3'

# Test 6 : Timeout KO (7 secondes)
Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/timeout?delaySeconds=7'





teste grafana:Monitoring avec Micrometer, Prometheus et GrafanaPartagerjai fait tous ces etatpes:1️⃣ Microservice Logistique autonome (DÉJÀ FAIT – VALIDÉ)
Exigence cahier de charge
Microservices métiers autonomes et modulaires
État
✅ Controller ✅ Service ✅ Entity ✅ Repository ✅ DTO ✅ Base de données indépendante
🎯 Conformité : 100 %
⏱️ Temps : déjà fait
2️⃣ Externalisation de la configuration (Config Server)
Exigence
Gestion centralisée des configurations
Adaptation (sans autres services)
Étapes
1. Créer Config Server
2. Créer un dépôt Git (local ou distant)
3. Mettre logistique-service.yml
4. Charger dynamiquement la config
👉 Aucun autre microservice requis
📌 À dire :
Même avec un seul microservice, le Config Server reste pertinent pour démontrer la centralisation.
⏱️ Temps : 45 min – 1 h
3️⃣ Enregistrement dynamique (Eureka Server)
Exigence
Découverte et enregistrement dynamique des services
Adaptation
* Créer Eureka Server
* Enregistrer Logistique uniquement
📌 À dire :
D’autres microservices peuvent s’enregistrer ultérieurement sans modifier Logistique.
⏱️ Temps : 30 – 45 min
4️⃣ API Gateway (Routage centralisé)
Exigence
Gestionnaire central de routage
Adaptation
* Créer API Gateway
* Route /logistique/** → Logistique
📌 À dire :
La Gateway est prête à intégrer les futurs microservices.
⏱️ Temps : 30 – 45 min
5️⃣ Communication inter-services (Feign – SIMULÉE)
Exigence
Communication synchrone REST
Adaptation SANS CODE DES AUTRES SERVICES
Étapes
* Créer un @FeignClient
* Nom du service fictif : evenement-service
* Simuler erreurs :
   * Service non trouvé
   * Timeout
   * 404
📌 À dire :
Le contrat REST suffit, le code du service distant n’est pas nécessaire.
⏱️ Temps : 45 min – 1 h
6️⃣ Résilience (OBLIGATOIRE)
Exigence
Retry – RateLimiter – CircuitBreaker – Fallback
Adaptation
* Appliquer Résilience4j sur :
   * Appel Feign simulé
   * Méthodes Logistique
Types de défaillances démontrées
TypeCommentService downFeign service inexistantTimeoutsleep(6000)Réponse incorrecteexceptionErreur réseauURL invalideDB inaccessiblearrêter MySQL
⏱️ Temps : 1 h – 1 h 30 mainetanta je veux faire cette etapes:7️⃣ Monitoring & Surveillance
Exigence
Micrometer + Prometheus + Grafana
Adaptation
* Surveiller Logistique seul
* Visualiser :
   * erreurs
   * latence
   * taux de fallback
📌 À dire :
Le monitoring est par microservice, pas global.
⏱️ Temps : 1 h – 1 h 30 je veux que tu me donne un script complet a compier coller seulement   mais avant ca tu doit me demander tout les information necessaire pour travailler ca correctement ne commence le travail que si tu a tout les info necessaireAfficher plus19:04📋 Questionnaire - Configuration Monitoring (Micrometer + Prometheus + Grafana)
Avant de te fournir le script complet, j'ai besoin des informations suivantes pour garantir un travail précis et fonctionnel :

🔧 1. Informations sur ton projet Logistique
Architecture actuelle

Nom exact de ton microservice : (ex: logistique-service, logistics-ms, etc.)
Port du microservice Logistique : (ex: 8081, 8080, etc.)
Version de Spring Boot : (ex: 3.2.0, 3.1.5, 2.7.x, etc.)
Gestionnaire de dépendances : Maven ou Gradle ?
Version Java : (ex: 17, 21, 11, etc.)


🗂️ 2. Services déjà configurés

Config Server : port ? (ex: 8888)
Eureka Server : port ? (ex: 8761)
API Gateway : port ? (ex: 8080)


🐳 3. Environnement de déploiement

Système d'exploitation : Windows, Linux, macOS ?
Docker installé ? : Oui / Non
Préférence de déploiement :

 Docker Compose (Prometheus + Grafana conteneurisés)
 Installation locale (Prometheus + Grafana en binaire)
 Les deux versions




📊 4. Métriques à surveiller (confirme ou ajoute)
Je propose de surveiller :

✅ Nombre de requêtes HTTP (http.server.requests)
✅ Latence des endpoints
✅ Taux d'erreurs (4xx, 5xx)
✅ Circuit Breaker : état (ouvert/fermé/semi-ouvert), taux de fallback
✅ Retry : nombre de tentatives
✅ Rate Limiter : requêtes autorisées/rejetées
✅ Utilisation CPU/Mémoire JVM
✅ Connexions base de données (HikariCP)

Veux-tu ajouter d'autres métriques spécifiques ?

🎯 5. Configuration Resilience4j

As-tu déjà configuré Resilience4j ? : Oui / Non
Si oui, quels patterns utilises-tu ?

 Circuit Breaker
 Retry
 Rate Limiter
 Time Limiter
 Bulkhead


Noms des instances configurées (ex: logistiqueCircuitBreaker, feignRetry, etc.) :


📁 6. Structure de ton projet
Pour placer correctement les fichiers, donne-moi :
ton-projet/
├── logistique-service/
│   ├── src/main/resources/
│   │   └── application.yml (ou .properties ?)
│   └── pom.xml (ou build.gradle ?)
├── config-server/
├── eureka-server/
├── gateway/
└── docker/ (si tu utilises Docker Compose)
Utilises-tu application.yml ou application.properties ?

🔐 7. Sécurité (optionnel)

Veux-tu sécuriser Prometheus/Grafana ?

 Oui (avec login/password)
 Non (accès libre en local)




📦 8. Livrables souhaités
Je vais te fournir :

✅ Dépendances Maven/Gradle à ajouter
✅ Configuration application.yml complète
✅ Fichier prometheus.yml
✅ docker-compose.yml pour Prometheus + Grafana
✅ Dashboard Grafana pré-configuré (JSON à importer)
✅ Script de test pour vérifier les métriques
✅ Guide de vérification étape par étape

Veux-tu quelque chose en plus ?

⏱️ 9. Délai souhaité

As-tu une deadline ? (ex: ce soir, demain, semaine prochaine)


🚦 Instruction finale
Réponds à toutes ces questions, et je te fournirai un script complet copy-paste avec :

Tous les fichiers configurés
Les commandes exactes à exécuter
Les URLs de vérification
Un dashboard Grafana prêt à l'emploi

Ne commence pas tant que tu n'as pas TOUTES les réponses ! 🎯c ets mon microservice:C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service Config Server demarre avec succes!
Configuration disponible sur: http://localhost:8888Eureka Server demarre avec succes!
Dashboard Eureka disponible sur: http://localhost:8761 x.discovery.DiscoveryClient - DiscoveryClient_GESTION-EVENEMENT-SERVICE/192.168.1.172:gestion-evenement-service:8083 - registration status: 204
19:10:19.028 [main] INFO  t.e.s.GestionEvenementServiceApplication - Started GestionEvenementServiceApplication in 7.698 seconds (process running for 8.054) API Gateway : port ( 8089)  <?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.1.6</version>
        <relativePath/>
    </parent>

    <groupId>tn.esprit.spring</groupId>
    <artifactId>gestion_projet</artifactId>
    <version>1.0</version>
    <packaging>pom</packaging>
    
    <modules>
        <module>config-server</module>
        <module>eureka-server</module>
        <module>gestion-evenement-service</module>
        <module>api-gateway</module>
    </modules>

    <properties>
        <java.version>17</java.version>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <spring-cloud.version>2022.0.5</spring-cloud.version>

        <sonar.projectKey>spring-project</sonar.projectKey>
        <sonar.projectName>Gestion Projet Spring Boot</sonar.projectName>
        <sonar.host.url>http://localhost:9000</sonar.host.url>
        <sonar.java.coveragePlugin>jacoco</sonar.java.coveragePlugin>
        <sonar.coverage.jacoco.xmlReportPaths>
            ${project.build.directory}/site/jacoco/jacoco.xml
        </sonar.coverage.jacoco.xmlReportPaths>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <version>3.1.6</version>
            </plugin>

            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>0.8.11</version>
                <executions>
                    <execution>
                        <id>prepare-agent</id>
                        <goals>
                            <goal>prepare-agent</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report</id>
                        <phase>test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.0.0</version>
            </plugin>

            <plugin>
                <groupId>org.sonarsource.scanner.maven</groupId>
                <artifactId>sonar-maven-plugin</artifactId>
                <version>3.10.0.2594</version>
            </plugin>
        </plugins>
    </build>

</project>    je suis avec windows11 je veux des metrique qui marche pour ce tp pas trrooos et pas sous note je veux une chose accepté pas plus je ne cherche pas la mention  oui jeai configuré resilience  C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 1 : Info
PS C:\Users\User-PC\IGL5-G4-projet-microservices> Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/info'

tests_disponibles
-----------------
@{test_4=GET /resilience-test/timeout?delaySeconds=6; test_3=GET /resili...


PS C:\Users\User-PC\IGL5-G4-projet-microservices>
PS C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 2 : Circuit Breaker

PS C:\Users\User-PC\IGL5-G4-projet-microservices> Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/circuit-breaker?fail=false'

pattern         message                                status
-------         -------                                ------
Circuit Breaker Circuit Breaker : Service op?rationnel SUCCESS


PS C:\Users\User-PC\IGL5-G4-projet-microservices>
PS C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 3 : Retry
PS C:\Users\User-PC\IGL5-G4-projet-microservices> Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/retry'

pattern message                           status
------- -------                           ------
Retry   Retry : Succ?s apr?s 0 tentatives SUCCESS


PS C:\Users\User-PC\IGL5-G4-projet-microservices>
PS C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 4 : Rate Limiter (3x rapidement)
PS C:\Users\User-PC\IGL5-G4-projet-microservices> 1..5 | ForEach-Object {
>>     Write-Host "Requête $_" -ForegroundColor Yellow
>>     try {
>>         Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/rate-limiter'
>>         Write-Host "  ✅ Acceptée" -ForegroundColor Green
>>     } catch {
>>         Write-Host "  ❌ Rejetée (rate limited)" -ForegroundColor Red
>>     }
>>     Start-Sleep -Milliseconds 400
>> }
Requête 1

  ✅ Acceptée
Requête 2
note                           pattern      message
----                           -------      -------
Limite: 2 requ?tes par seconde Rate Limiter Rate Limiter : Requ?te trait...
Limite: 2 requ?tes par seconde Rate Limiter Rate Limiter : Requ?te trait...
  ✅ Acceptée
Requête 3
Limite: 2 requ?tes par seconde Rate Limiter Rate Limiter : Requ?te trait...
  ✅ Acceptée
Requête 4
Limite: 2 requ?tes par seconde Rate Limiter Rate Limiter : Requ?te trait...
  ✅ Acceptée
Requête 5
Limite: 2 requ?tes par seconde Rate Limiter Rate Limiter : Requ?te trait...
  ✅ Acceptée


PS C:\Users\User-PC\IGL5-G4-projet-microservices>
PS C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 5 : Timeout OK (3 secondes)
PS C:\Users\User-PC\IGL5-G4-projet-microservices> Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/timeout?delaySeconds=3'


delaySeconds pattern      message                                 status
------------ -------      -------                                 ------
           3 Time Limiter Time Limiter : R?ponse apr?s 3 secondes SUCCESS


PS C:\Users\User-PC\IGL5-G4-projet-microservices>
PS C:\Users\User-PC\IGL5-G4-projet-microservices> # Test 6 : Timeout KO (7 secondes)
PS C:\Users\User-PC\IGL5-G4-projet-microservices> Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/timeout?delaySeconds=7'


delaySeconds pattern      message
------------ -------      -------
           7 Time Limiter FALLBACK : Timeout d?pass? - Le service a mis ...

 je ne veux pas des chose optionnel je cherche que le necessairev logistique-service
V src

V main

v java \ tn \ esprit \ spring
v client
J EvenementClient.java
J EvenementClientFallba ... 8
v config
J FeignConfig.java
> configuration
> controllers
> dto

> persistence
> resilience
service
J GestionEvenementService ... 4

4

9+  sivous avez encore des questions demandes avant de commencer le travail je peux repondreAfficher plus19:15📋 Questions complémentaires AVANT de commencer
Merci pour ces infos ! J'ai presque tout, mais il me manque quelques détails critiques :

✅ Ce que j'ai compris :

Service : gestion-evenement-service (port 8083)
Spring Boot : 3.1.6
Java : 17
Maven
Windows 11
Config Server : 8888
Eureka : 8761
Gateway : 8089
Resilience4j : ✅ Configuré (Circuit Breaker, Retry, Rate Limiter, Time Limiter)


❓ Questions restantes (IMPORTANTES) :
1️⃣ Nom du microservice dans Eureka
Dans tes logs, je vois :
DiscoveryClient_GESTION-EVENEMENT-SERVICE/192.168.1.172:gestion-evenement-service:8083
Quel est le nom exact dans application.yml ?

GESTION-EVENEMENT-SERVICE ?
gestion-evenement-service ?

👉 Donne-moi cette ligne de ton application.yml :
yamlspring:
  application:
    name: ??? # <-- Donne-moi cette valeur exacte
```

---

### **2️⃣ Fichier de configuration**
**Utilises-tu :**
- [ ] `application.yml`
- [ ] `application.properties`

**Et où se trouve-t-il ?**
```
gestion-evenement-service/
  src/main/resources/
    └── application.yml ??? <-- Confirme

3️⃣ Noms des instances Resilience4j
Dans ton test, je vois que ça fonctionne, mais donne-moi les noms exacts utilisés dans ta config :
Exemple de ce que je cherche :
yamlresilience4j:
  circuitbreaker:
    instances:
      evenementCircuitBreaker: # <-- Ce nom
  retry:
    instances:
      evenementRetry: # <-- Ce nom
  ratelimiter:
    instances:
      evenementRateLimiter: # <-- Ce nom
  timelimiter:
    instances:
      evenementTimeLimiter: # <-- Ce nom
```

👉 **Donne-moi ces 4 noms** (ou copie-colle ta section `resilience4j` du `application.yml`)

---

### **4️⃣ Base de données**
**Utilises-tu une base de données ?**
- [ ] MySQL
- [ ] PostgreSQL
- [ ] H2 (en mémoire)
- [ ] Autre : ___________

**Si oui, quel est le nom de la base ?** (pour surveiller HikariCP)

---

### **5️⃣ Docker ou installation locale ?**
**Pour Prometheus + Grafana, tu préfères :**
- [ ] **Docker Compose** (plus simple, je te donne 1 fichier `docker-compose.yml`)
- [ ] **Installation Windows** (télécharger les .exe)

👉 **Si tu choisis Docker, as-tu Docker Desktop installé ?** Oui / Non

---

### **6️⃣ Endpoints à surveiller**
**Quels endpoints veux-tu voir dans Grafana ?**

Je propose :
- ✅ `/resilience-test/circuit-breaker`
- ✅ `/resilience-test/retry`
- ✅ `/resilience-test/rate-limiter`
- ✅ `/resilience-test/timeout`

**Y a-t-il d'autres endpoints importants ?** (ex: `/evenements`, `/api/...`)

---

### **7️⃣ Métriques Resilience4j**
**Pour le tableau de bord, je vais afficher :**
- 📊 Circuit Breaker : état (CLOSED/OPEN/HALF_OPEN)
- 📊 Retry : nombre de tentatives réussies/échouées
- 📊 Rate Limiter : requêtes autorisées/rejetées
- 📊 Time Limiter : timeouts

**Tu veux ajouter quelque chose ?** (CPU, mémoire, requêtes HTTP, etc.)

---

## 🎯 **Réponse attendue (format simple) :**

Réponds comme ça :
```
1. spring.application.name = GESTION-EVENEMENT-SERVICE
2. Fichier : application.yml
3. Noms Resilience4j :
   - Circuit Breaker : evenementCircuitBreaker
   - Retry : evenementRetry
   - Rate Limiter : evenementRateLimiter
   - Time Limiter : evenementTimeLimiter
4. Base de données : MySQL (nom: gestion_evenement_db)
5. Docker : Oui (Docker Desktop installé)
6. Endpoints : ceux proposés OK
7. Métriques : celles proposées OK

⏳ Une fois que tu réponds, je te livre :

✅ Dépendances Maven à ajouter dans pom.xml
✅ Configuration complète application.yml
✅ Fichier prometheus.yml
✅ docker-compose.yml (Prometheus + Grafana)
✅ Dashboard Grafana JSON (à importer en 1 clic)
✅ Script PowerShell de test
✅ Guide de vérification étape par étape

📢 Réponds maintenant, et je commence ! 🚀```spring-boot-properties-yaml
server:
  port: 8083
  servlet:
    context-path: /GestionEvenementspring:
  application:
    name: gestion-evenement-service  datasource:
    url: jdbc:mysql://localhost:3306/gestionevenement?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: admin
    driver-class-name: com.mysql.cj.jdbc.Driver  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true# Configuration Feign
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: full
  circuitbreaker:
    enabled: true# Configuration Resilience4j
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        minimumNumberOfCalls: 5
        registerHealthIndicator: true
    instances:
      testCircuitBreaker:
        baseConfig: default
      testCombined:
        baseConfig: default  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
    instances:
      testRetry:
        baseConfig: default
      testCombined:
        baseConfig: default  ratelimiter:
    configs:
      default:
        limitForPeriod: 2
        limitRefreshPeriod: 1s
        timeoutDuration: 0s
    instances:
      testRateLimiter:
        baseConfig: default  timelimiter:
    configs:
      default:
        timeoutDuration: 5s
        cancelRunningFuture: true
    instances:
      testTimeLimiter:
        baseConfig: defaultlogging:
  level:
    root: INFO
    tn.esprit.spring: DEBUG
    tn.esprit.spring.client: DEBUG
    feign: DEBUG
    org.hibernate.SQL: DEBUG
    io.github.resilience4j: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,circuitbreakers,ratelimiters
  endpoint:
    health:
      show-details: always
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true```  si tu a compris tout et vous navez pas besoin de rien donner un script a copier collerAfficher plus19:182/2Script Monitoring Complet - Prometheus + GrafanaCode # 🎯 SCRIPT COMPLET MONITORING - COPY/PASTE

## ✅ PRÉREQUIS
- Docker Desktop installé et démarré
- Ton service sur port 8083 (en cours d'exécution)

---

## 📦 ÉTAPE 1 : AJOUTER LES DÉPENDANCES

### Fichier : `gestion-evenement-service/pom.xml`

**CO✅ SCRIPT COMPLET PRÊT !
J'ai créé un guide complet copy-paste dans l'artifact à droite avec :
📦 Ce que tu vas faire :

Ajouter 3 dépendances dans pom.xml (Micrometer + Prometheus)
Mettre à jour la config application.yml (section management)
Créer 2 fichiers :

prometheus.yml (config Prometheus)
docker-compose.yml (lancer Prometheus + Grafana)


Rebuild ton service avec Maven
Démarrer Prometheus + Grafana avec Docker
Configurer Grafana (3 clics)
Tester et voir les métriques en temps réel

🎯 Résultat final :

Dashboard Grafana avec 5 graphiques :

📊 Requêtes HTTP totales
🔴 État du Circuit Breaker
🔄 Tentatives de Retry
⏱️ Rate Limiter (permissions disponibles)
📈 Temps de réponse (p95)



⏱️ Temps : 15-20 minutes

👉 TOUT EST DANS L'ARTIFACT À DROITE → Suis les étapes dans l'ordre → Copy/Paste → C'est parti ! 🚀
Des questions avant de commencer ?C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service et pas  gestion-evenement-service19:21Script Monitoring Complet - Prometheus + GrafanaCode ∙ Version 2 # 🎯 SCRIPT COMPLET MONITORING - COPY/PASTE

## ✅ PRÉREQUIS
- Docker Desktop installé et démarré
- Ton service logistique sur port 8083 (en cours d'exécution)

---

## 📦 ÉTAPE 1 : AJOUTER LES DÉPENDANCES

### Fichier : `logistique-service/pom.xml`

✅ SCRIPT CORRIGÉ POUR LOGISTIQUE-SERVICE !
J'ai mis à jour l'artifact avec les bons chemins :
🔄 Changements appliqués :

✅ Chemin du service : logistique-service (au lieu de gestion-evenement-service)
✅ Nom de l'application : logistique-service dans Prometheus
✅ Métriques : configurées pour application="logistique-service"
✅ Dashboard Grafana : titre "Logistique Service - Resilience4j Monitoring"

📁 Structure des fichiers à créer :
C:\Users\User-PC\IGL5-G4-projet-microservices\
├── logistique-service/
│   ├── pom.xml (ajouter 3 dépendances)
│   └── src/main/resources/
│       └── application.yml (mettre à jour section management)
└── monitoring/
    ├── prometheus.yml (à créer)
    └── docker-compose.yml (à créer)

👉 TOUT EST PRÊT DANS L'ARTIFACT → Suis les 8 étapes → C'est parti ! 🚀
Tu peux commencer maintenant ! Des questions ?C:\Users\User-PC> cd .\IGL5-G4-projet-microservices\
PS C:\Users\User-PC\IGL5-G4-projet-microservices> mvn clean install -DskipTests
[INFO] Scanning for projects...
[ERROR] [ERROR] Some problems were encountered while processing the POMs:
[ERROR] Child module C:\Users\User-PC\IGL5-G4-projet-microservices\gestion-evenement-service of C:\Users\User-PC\IGL5-G4-projet-microservices\pom.xml does not exist @
 @
[ERROR] The build could not read 1 project -> [Help 1]
[ERROR]
[ERROR]   The project tn.esprit.spring:gestion_projet:1.0 (C:\Users\User-PC\IGL5-G4-projet-microservices\pom.xml) has 1 error
[ERROR]     Child module C:\Users\User-PC\IGL5-G4-projet-microservices\gestion-evenement-service of C:\Users\User-PC\IGL5-G4-projet-microservices\pom.xml does not exist
[ERROR]
[ERROR] To see the full stack trace of the errors, re-run Maven with the -e switch.
[ERROR] Re-run Maven using the -X switch to enable full debug logging.
[ERROR]
[ERROR] For more information about the errors and possible solutions, please read the following articles:
[ERROR] [Help 1] http://cwiki.apache.org/confluence/display/MAVEN/ProjectBuildingException
PS C:\Users\User-PC\IGL5-G4-projet-microservices>Afficher plus19:27🔴 ERREUR MAVEN - SOLUTION RAPIDE
Le problème : ton pom.xml parent référence gestion-evenement-service qui n'existe pas dans ton projet.

🛠️ SOLUTION : 2 OPTIONS
OPTION 1 : Build uniquement logistique-service (RAPIDE)
powershell# Au lieu de builder tout le projet, build juste logistique-service
cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean install -DskipTests

OPTION 2 : Corriger le pom.xml parent (SI TU VEUX)
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\pom.xml
TROUVE cette section :
xml<modules>
    <module>config-server</module>
    <module>eureka-server</module>
    <module>gestion-evenement-service</module>  <!-- ❌ Cette ligne pose problème -->
    <module>api-gateway</module>
</modules>
REMPLACE par :
xml<modules>
    <module>config-server</module>
    <module>eureka-server</module>
    <module>logistique-service</module>  <!-- ✅ Corrigé -->
    <module>api-gateway</module>
</modules>
Puis rebuild :
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices
mvn clean install -DskipTests

👉 MA RECOMMANDATION
Utilise l'OPTION 1 (build juste logistique-service) car c'est plus rapide et ça évite de toucher au pom parent.
Exécute ça maintenant :
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean install -DskipTests
Une fois que ça marche, redémarre ton service logistique et continue avec l'étape 6 du script (démarrer Docker).
✅ Ça devrait résoudre le problème !PS C:\Users\User-PC> 1..20 | ForEach-Object {
>>     Write-Host "Requête $_" -ForegroundColor Cyan
>>     try {
>>         # Remplace par tes vrais endpoints logistique
>>         Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'
>>         Start-Sleep -Milliseconds 500
>>     } catch {
>>         Write-Host "Erreur attendue pour test" -ForegroundColor Yellow
>>     }
>> }
Requête 1
Erreur attendue pour test
Requête 2
Erreur attendue pour test
Requête 3
Erreur attendue pour test
Requête 4
Erreur attendue pour test
Requête 5
Erreur attendue pour test
Requête 6
Erreur attendue pour test
Requête 7
Erreur attendue pour test
Requête 8
Erreur attendue pour test
Requête 9
Erreur attendue pour test
Requête 10
Erreur attendue pour test
Requête 11
Erreur attendue pour test
Requête 12
Erreur attendue pour test
Requête 13
Erreur attendue pour test
Requête 14
Erreur attendue pour test
Requête 15
Erreur attendue pour test
Requête 16
Erreur attendue pour test
Requête 17
Erreur attendue pour test
Requête 18
Erreur attendue pour test
Requête 19
Erreur attendue pour test
Requête 20
Erreur attendue pour test
PS C:\Users\User-PC>Afficher plus21:30🔴 PROBLÈME : Le service logistique n'est PAS démarré !
Toutes les requêtes échouent car ton service sur le port 8083 n'est pas actif.

✅ SOLUTION ÉTAPE PAR ÉTAPE
1️⃣ Vérifie que ton service est bien rebuilded
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean install -DskipTests
✅ Tu dois voir BUILD SUCCESS à la fin.

2️⃣ Démarre ton service logistique
Option A : Avec Maven
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn spring-boot:run
```

**Option B : Avec IntelliJ/Eclipse**
- Ouvre le projet dans ton IDE
- Trouve la classe principale (avec `@SpringBootApplication`)
- Clique droit → Run

---

### **3️⃣ Attends que le service démarre**

Tu dois voir dans les logs :
```
Started LogistiqueServiceApplication in X.XXX seconds
Tomcat started on port(s): 8083

4️⃣ Vérifie que le service est UP
powershell# Test simple
Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'
✅ Tu dois voir :
json{
  "status": "UP"
}

5️⃣ Vérifie les métriques Prometheus
powershellInvoke-RestMethod -Uri 'http://localhost:8083/actuator/prometheus'
```

✅ **Tu dois voir une longue liste de métriques** commençant par :
```
# HELP jvm_memory_used_bytes
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{...}
...

6️⃣ Une fois que le service tourne, reteste
powershell# Test simple
Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'

# Génère du trafic
1..20 | ForEach-Object {
    Write-Host "Requête $_" -ForegroundColor Cyan
    Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'
    Start-Sleep -Milliseconds 500
}

7️⃣ Vérifie Prometheus
Ouvre : http://localhost:9090/targets
✅ Tu dois voir :

State : UP (en vert)
Labels : application="logistique-service"


8️⃣ Vérifie Grafana

Va sur http://localhost:3000
Clique sur "Import dashboard" (bouton bleu en bas à droite dans ton screenshot)
Colle le JSON que je t'ai donné dans l'artifact (Étape 7, point 3)
Clique Load → Import


🎯 CHECKLIST DE DEBUG
Exécute ces commandes pour vérifier :
powershell# 1. Le service tourne-t-il ?
Test-NetConnection -ComputerName localhost -Port 8083

# 2. Actuator répond ?
curl http://localhost:8083/actuator/health

# 3. Prometheus répond ?
curl http://localhost:8083/actuator/prometheus | Select-String "http_server_requests"

# 4. Docker tourne ?
docker ps

🚨 SI ÇA NE MARCHE TOUJOURS PAS
Envoie-moi :

Les logs de démarrage de ton service (les 20 dernières lignes)
Le résultat de cette commande :

powershellTest-NetConnection -ComputerName localhost -Port 8083

👉 COMMENCE PAR DÉMARRER TON SERVICE avec mvn spring-boot:run et dis-moi ce que tu vois ! 🚀       references evenement (id)
19:30:23.969 [main] INFO  o.s.o.j.LocalContainerEntityManagerFactoryBean - Initialized JPA EntityManagerFactory for persistence unit 'default'
19:30:24.102 [main] WARN  o.s.b.a.o.j.JpaBaseConfiguration$JpaWebConfiguration - spring.jpa.open-in-view is enabled by defpasted17 21:31:42 - DEBUG - [DiscoveryClient-CacheRefreshExecutor-0] - o.s.c.g.r.RouteDefinitionRouteLocator - RouteDefinition ReactiveCompositeDiscoveryClient_API-GATEWAY applying {pattern=/api-gateway/**} to Path
2026-01-17 21:31:42 - DEBUG - [DiscoveryClient-CacheRefreshExecutor-0] - o.s.c.g.r.RouteDepasted2026-01-17 21:10:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:11:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 9ms
2026-01-17 21:12:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 4ms
2026-01-17 21:13:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:14:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 2ms
2026-01-17 21:15:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 9ms
2026-01-17 21:16:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:17:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 3ms
2026-01-17 21:18:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 13ms
2026-01-17 21:19:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:20:22 - WARN  - c.n.eureka.cluster.PeerEurekaNodes - The replica size seems to be empty. Check the route 53 DNS Registry
2026-01-17 21:20:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 5ms
2026-01-17 21:21:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 14ms
2026-01-17 21:22:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:23:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:24:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:25:22 - INFO  - c.n.e.r.PeerAwareInstanceRegistryImpl - Current renewal threshold is : 0
2026-01-17 21:25:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:26:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 6ms
2026-01-17 21:27:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 14ms
2026-01-17 21:28:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 0ms
2026-01-17 21:29:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 11ms
2026-01-17 21:30:22 - WARN  - c.n.eureka.cluster.PeerEurekaNodes - The replica size seems to be empty. Check the route 53 DNS Registry
2026-01-17 21:30:23 - INFO  - c.n.e.r.AbstractInstanceRegistry - Running the evict task with compensationTime 16ms
Config Server demarre avec succes!
Configuration disponible sur: http://localhost:8888
2026-01-17T19:30:18.387+01:00  INFO 21004 --- [nio-8888-exec-1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2026-01-17T19:30:18.388+01:00  INFO 21004 --- [nio-8888-exec-1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2026-01-17T19:30:18.390+01:00  INFO 21004 --- [nio-8888-exec-1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 2 ms
2026-01-17T19:30:18.769+01:00  WARN 21004 --- [nio-8888-exec-1] .c.s.e.MultipleJGitEnvironmentRepository : Dirty files found: [api-gateway.yml]
2026-01-17T19:30:18.830+01:00  WARN 21004 --- [nio-8888-exec-1] .c.s.e.MultipleJGitEnvironmentRepository : Could not fetch remote for main remote: null
2026-01-17T19:30:18.890+01:00  WARN 21004 --- [nio-8888-exec-1] .c.s.e.MultipleJGitEnvironmentRepository : Could not merge remote for main remote: null
2026-01-17T19:30:18.898+01:00  WARN 21004 --- [nio-8888-exec-1] .c.s.e.MultipleJGitEnvironmentRepository : The local repository is dirty or ahead of origin. Resetting it to origin/main.
2026-01-17T19:30:18.909+01:00  WARN 21004 --- [nio-8888-exec-1] .c.s.e.MultipleJGitEnvironmentRepository : Could not reset to remote for main (current ref=refs/remotes/origin/main), remote: null
2026-01-17T19:30:18.946+01:00  INFO 21004 --- [nio-8888-exec-1] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: Config resource 'file [C:\Users\User-PC\IGL5-G4-projet-microservices\config-repo\gestion-evenement-service.yml]' via location 'file:/C:/Users/User-PC/IGL5-G4-projet-microservices/config-repo/'
2026-01-17T19:30:22.419+01:00  WARN 21004 --- [nio-8888-exec-2] .c.s.e.MultipleJGitEnvironmentRepository : Dirty files found: [api-gateway.yml]
2026-01-17T19:30:22.422+01:00  WARN 21004 --- [nio-8888-exec-2] .c.s.e.MultipleJGitEnvironmentRepository : Could not fetch remote for main remote: null
2026-01-17T19:30:22.437+01:00  WARN 21004 --- [nio-8888-exec-2] .c.s.e.MultipleJGitEnvironmentRepository : Could not merge remote for main remote: null
2026-01-17T19:30:22.442+01:00  WARN 21004 --- [nio-8888-exec-2] .c.s.e.MultipleJGitEnvironmentRepository : The local repository is dirty or ahead of origin. Resetting it to origin/main.
2026-01-17T19:30:22.444+01:00  WARN 21004 --- [nio-8888-exec-2] .c.s.e.MultipleJGitEnvironmentRepository : Could not reset to remote for main (current ref=refs/remotes/origin/main), remote: null
2026-01-17T19:30:22.455+01:00  INFO 21004 --- [nio-8888-exec-2] o.s.c.c.s.e.NativeEnvironmentRepository  : Adding property source: Config resource 'file [C:\Users\User-PC\IGL5-G4-projet-microservices\config-repo\api-gateway.yml]' via location 'file:/C:/Users/User-PC/IGL5-G4-projet-microservices/config-repo/'Afficher plus21:32🎯 JE COMPRENDS LE PROBLÈME !
Tu as démarré gestion-evenement-service (port 8083) au lieu de logistique-service !
Regarde tes logs :
Tomcat started on port(s): 8083 (http) with context path '/GestionEvenement'
Started GestionEvenementServiceApplication in 10.309 seconds

✅ SOLUTION : Démarrer le BON service
1️⃣ Arrête gestion-evenement-service
Appuie sur Ctrl+C dans le terminal où il tourne.

2️⃣ Démarre logistique-service
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn spring-boot:run
```

✅ **Tu dois voir quelque chose comme :**
```
Tomcat started on port(s): 8083 (http) with context path '/logistique'
Started LogistiqueServiceApplication in X seconds

3️⃣ Vérifie que logistique tourne
powershell# Test health
Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'

# Test métriques Prometheus
Invoke-RestMethod -Uri 'http://localhost:8083/actuator/prometheus' | Select-String "resilience4j"

4️⃣ Une fois que ça marche, reteste le trafic
powershell# Génère du trafic
1..20 | ForEach-Object {
    Write-Host "Requête $_" -ForegroundColor Cyan
    Invoke-RestMethod -Uri 'http://localhost:8083/actuator/health'
    Start-Sleep -Milliseconds 500
}

5️⃣ Vérifie Prometheus
Ouvre : http://localhost:9090/targets
✅ Tu dois voir logistique-service UP en vert.

6️⃣ Importe le dashboard Grafana

Va sur http://localhost:3000
Clique "Import dashboard" (en bas à droite)
Colle ce JSON complet :

json{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "id": null,
  "links": [],
  "liveNow": false,
  "panels": [
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "reqps"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "rate(http_server_requests_seconds_count{application=\"logistique-service\"}[1m])",
          "legendFormat": "{{uri}} - {{method}}",
          "refId": "A"
        }
      ],
      "title": "HTTP Requests Total",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "0": {
                  "text": "CLOSED"
                },
                "1": {
                  "text": "OPEN"
                },
                "2": {
                  "text": "HALF_OPEN"
                }
              },
              "type": "value"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 1
              },
              {
                "color": "yellow",
                "value": 2
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 4,
        "w": 6,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "resilience4j_circuitbreaker_state{application=\"logistique-service\"}",
          "legendFormat": "{{name}}",
          "refId": "A"
        }
      ],
      "title": "Circuit Breaker State",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 8
      },
      "id": 3,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "rate(resilience4j_retry_calls_total{application=\"logistique-service\"}[1m])",
          "legendFormat": "{{name}} - {{kind}}",
          "refId": "A"
        }
      ],
      "title": "Retry Attempts",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "max": 2,
          "min": 0,
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "red",
                "value": null
              },
              {
                "color": "yellow",
                "value": 1
              },
              {
                "color": "green",
                "value": 2
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 12,
        "y": 8
      },
      "id": 4,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "resilience4j_ratelimiter_available_permissions{application=\"logistique-service\"}",
          "legendFormat": "{{name}}",
          "refId": "A"
        }
      ],
      "title": "Rate Limiter - Available Permissions",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "s"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 24,
        "x": 0,
        "y": 16
      },
      "id": 5,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application=\"logistique-service\"}[1m]))",
          "legendFormat": "{{uri}}",
          "refId": "A"
        }
      ],
      "title": "HTTP Response Time (p95)",
      "type": "timeseries"
    }
  ],
  "refresh": "5s",
  "schemaVersion": 38,
  "style": "dark",
  "tags": ["resilience4j", "monitoring"],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-15m",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "",
  "title": "Logistique Service - Resilience4j Monitoring",
  "uid": "logistique-monitoring",
  "version": 0,
  "weekStart": ""
}

Clique Load → Import


👉 DÉMARRE LOGISTIQUE-SERVICE MAINTENANT et dis-moi ce que tu vois dans les logs ! 🚀gestion-evenement-service et logistique-service sont la meme chose21:33😅 AH D'ACCORD ! C'est le MÊME service !
Donc gestion-evenement-service EST ton service logistique. Parfait !

✅ DANS CE CAS, le service TOURNE DÉJÀ !
Le problème c'est que l'URL testée était incorrecte.
Le service tourne sur :
http://localhost:8083/GestionEvenement/actuator/health
Pas sur :
http://localhost:8083/actuator/health  ❌

🔧 CORRECTION DU SCRIPT DE TEST
powershell# Test 1 : Health check (avec le bon context-path)
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/health'

# Test 2 : Métriques Prometheus
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "resilience4j"

# Test 3 : Générer du trafic
1..20 | ForEach-Object {
    Write-Host "Requête $_" -ForegroundColor Cyan
    Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/health'
    Start-Sleep -Milliseconds 500
}

🐳 CORRECTION DU PROMETHEUS.YML
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring\prometheus.yml
REMPLACE le contenu par :
yamlglobal:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'gestion-evenement-service'
    metrics_path: '/GestionEvenement/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8083']
        labels:
          application: 'gestion-evenement-service'

🔄 REDÉMARRER PROMETHEUS
powershell# Arrête et redémarre Docker Compose
cd C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring
docker-compose down
docker-compose up -d

# Vérifie que ça tourne
docker ps

✅ VÉRIFICATION FINALE
powershell# 1. Métriques accessibles ?
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "http_server_requests"

# 2. Prometheus voit le service ?
Start-Process "http://localhost:9090/targets"
✅ Tu dois voir le target UP en vert !

📊 DASHBOARD GRAFANA CORRIGÉ
Dans Grafana, importe ce JSON corrigé avec application="gestion-evenement-service" :
json{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "id": null,
  "links": [],
  "panels": [
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "fillOpacity": 10,
            "gradientMode": "none",
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "showPoints": "never",
            "spanNulls": false
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "reqps"
        }
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "targets": [
        {
          "expr": "rate(http_server_requests_seconds_count{application=\"gestion-evenement-service\"}[1m])",
          "legendFormat": "{{uri}} - {{method}}",
          "refId": "A"
        }
      ],
      "title": "HTTP Requests Total",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "0": {
                  "text": "CLOSED"
                },
                "1": {
                  "text": "OPEN"
                },
                "2": {
                  "text": "HALF_OPEN"
                }
              },
              "type": "value"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 1
              },
              {
                "color": "yellow",
                "value": 2
              }
            ]
          }
        }
      },
      "gridPos": {
        "h": 4,
        "w": 6,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "targets": [
        {
          "expr": "resilience4j_circuitbreaker_state{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "refId": "A"
        }
      ],
      "title": "Circuit Breaker State",
      "type": "stat"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "fillOpacity": 10,
            "gradientMode": "none",
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "showPoints": "never",
            "spanNulls": false
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          }
        }
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 8
      },
      "id": 3,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "targets": [
        {
          "expr": "rate(resilience4j_retry_calls_total{application=\"gestion-evenement-service\"}[1m])",
          "legendFormat": "{{name}} - {{kind}}",
          "refId": "A"
        }
      ],
      "title": "Retry Attempts",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "max": 2,
          "min": 0,
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "red",
                "value": null
              },
              {
                "color": "yellow",
                "value": 1
              },
              {
                "color": "green",
                "value": 2
              }
            ]
          }
        }
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 12,
        "y": 8
      },
      "id": 4,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "targets": [
        {
          "expr": "resilience4j_ratelimiter_available_permissions{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "refId": "A"
        }
      ],
      "title": "Rate Limiter",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "fillOpacity": 10,
            "gradientMode": "none",
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "showPoints": "never",
            "spanNulls": false
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "s"
        }
      },
      "gridPos": {
        "h": 8,
        "w": 24,
        "x": 0,
        "y": 16
      },
      "id": 5,
      "options": {
        "legend": {
          "calcs": [],
          "displayMode": "list",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "targets": [
        {
          "expr": "histogram_quantile(0.95, rate(http_server_requests_seconds_bucket{application=\"gestion-evenement-service\"}[1m]))",
          "legendFormat": "{{uri}}",
          "refId": "A"
        }
      ],
      "title": "HTTP Response Time (p95)",
      "type": "timeseries"
    }
  ],
  "refresh": "5s",
  "schemaVersion": 38,
  "style": "dark",
  "tags": ["resilience4j", "monitoring"],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-15m",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "",
  "title": "Gestion Événement - Resilience4j Monitoring",
  "uid": "gestion-evenement-monitoring",
  "version": 0,
  "weekStart": ""
}

🎯 CHECKLIST RAPIDE
powershell# 1. Corrige prometheus.yml (ajoute /GestionEvenement)
# 2. Redémarre Docker
cd C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring
docker-compose down
docker-compose up -d

# 3. Teste les métriques
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "resilience4j"

# 4. Vérifie Prometheus
Start-Process "http://localhost:9090/targets"

# 5. Importe le dashboard dans Grafana

👉 EXÉCUTE CES COMMANDES et dis-moi si Prometheus voit le service UP ! 🚀PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring> Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "resilience4j"
Invoke-RestMethod : Le serveur distant a retourné une erreur : (404) Introuvable.
Au caractère Ligne:1 : 1
+ Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuat ...
+ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    + CategoryInfo          : InvalidOperation : (System.Net.HttpWebRequest:HttpWebRequest) [Invoke-RestMethod], WebException
    + FullyQualifiedErrorId : WebCmdletWebResponseException,Microsoft.PowerShell.Commands.InvokeRestMethodCommand
PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring>
PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring> # Test 3 : Générer du trafic
PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring> 1..20 | ForEach-Object {
>>     Write-Host "Requête $_" -ForegroundColor Cyan
>>     Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/health'
>>     Start-Sleep -Milliseconds 500
>> }
Requête 1
Requête 2
status components
------ ----------
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 3
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 4
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 5
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 6
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 7
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 8
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 9
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 10
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 11
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 12
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 13
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 14
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 15
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 16
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 17
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 18
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 19
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
Requête 20
UP     @{circuitBreakers=; clientConfigServer=; db=; discoveryComposite=; diskSpace=; ping=; rateLimiters=; refreshScope=}
PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring>Afficher plus21:34🔴 PROBLÈME : L'endpoint Prometheus n'est PAS exposé !
Le service tourne ✅ (health check fonctionne), MAIS l'endpoint /actuator/prometheus retourne 404.

🛠️ SOLUTION : Ajouter les dépendances Micrometer
Tu n'as PAS ENCORE ajouté les dépendances Maven que je t'ai données !

✅ ÉTAPES À SUIVRE MAINTENANT
1️⃣ Ajoute les dépendances dans pom.xml
Fichier : gestion-evenement-service/pom.xml
OUVRE le fichier et AJOUTE ces 3 dépendances dans <dependencies> :
xml<!-- Micrometer + Prometheus -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>

2️⃣ Mets à jour application.yml
Fichier : gestion-evenement-service/src/main/resources/application.yml
REMPLACE la section management: par celle-ci :
yamlmanagement:
  endpoints:
    web:
      exposure:
        include: '*'
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,200ms,500ms,1s,2s

3️⃣ Rebuild le service
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\gestion-evenement-service
mvn clean install -DskipTests

4️⃣ Redémarre le service
Arrête le service (Ctrl+C) puis redémarre :
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\gestion-evenement-service
mvn spring-boot:run

5️⃣ Vérifie que Prometheus fonctionne
powershell# Test 1 : L'endpoint existe maintenant ?
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "jvm_memory"

# Test 2 : Cherche les métriques resilience4j
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "resilience4j"
✅ Tu dois voir une LONGUE liste de métriques !

6️⃣ Vérifie tous les endpoints actuator
powershellInvoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'
✅ Tu dois voir prometheus dans la liste !

📋 CHECKLIST

 Dépendances ajoutées dans pom.xml
 Section management: mise à jour dans application.yml
 Service rebuilded (mvn clean install -DskipTests)
 Service redémarré
 Endpoint /actuator/prometheus répond (pas de 404)


👉 AJOUTE LES DÉPENDANCES, REBUILD, REDÉMARRE et teste à nouveau ! 🚀
Dis-moi ce que tu vois après le redémarrage !
global:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'logistique-service'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8083']
        labels:
          application: 'logistique-service'Afficher plus21:35🔴 ERREUR : Le fichier prometheus.yml est INCORRECT !
Tu utilises encore logistique-service, mais ton service s'appelle gestion-evenement-service !

✅ CORRECTION IMMÉDIATE
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring\prometheus.yml
REMPLACE TOUT le contenu par :
yamlglobal:
  scrape_interval: 15s
  evaluation_interval: 15s

scrape_configs:
  - job_name: 'gestion-evenement-service'
    metrics_path: '/GestionEvenement/actuator/prometheus'
    static_configs:
      - targets: ['host.docker.internal:8083']
        labels:
          application: 'gestion-evenement-service'

🔄 REDÉMARRE PROMETHEUS
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring
docker-compose down
docker-compose up -d

✅ VÉRIFICATION COMPLÈTE
1️⃣ Les dépendances sont ajoutées ?
Vérifie que tu as bien ajouté ces 3 lignes dans gestion-evenement-service/pom.xml :
xml<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>

2️⃣ Le service a été rebuilded et redémarré ?
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\gestion-evenement-service
mvn clean install -DskipTests
mvn spring-boot:run

3️⃣ L'endpoint Prometheus répond ?
powershell# Doit retourner des métriques (pas 404)
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "jvm_memory"

4️⃣ Prometheus voit le service ?
powershellStart-Process "http://localhost:9090/targets"
✅ Tu dois voir :

State : UP (en vert)
Endpoint : http://host.docker.internal:8083/GestionEvenement/actuator/prometheus


🎯 RÉSUMÉ DES 3 CHANGEMENTS CRITIQUES
FichierChangementpom.xml➕ Ajouter 3 dépendances Micrometerapplication.yml✏️ Remplacer section management:prometheus.yml✏️ Corriger le metrics_path → /GestionEvenement/actuator/prometheus

👉 EXÉCUTE CES 3 CORRECTIONS dans l'ordre, puis teste avec les commandes ci-dessus ! 🚀
Dis-moi :

As-tu ajouté les dépendances dans pom.xml ? (Oui/Non)
As-tu rebuilded le service ? (Oui/Non)
Le service est redémarré ? (Oui/Non)
Résultat de la commande Prometheus (UP ou DOWN ?)
dans Start-Process "http://localhost:9090/targets":gestion-evenement-service
0 / 1 up
EndpointLabelsLast scrapeState
http://host.docker.internal:8083/GestionEvenement/actuator/prometheus
application="gestion-evenement-service"instance="host.docker.internal:8083"job="gestion-evenement-service"
16.228s ago
7ms
down
Error scraping target: server returned HTTP status 404 PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring> Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "jvm_memory"
Invoke-RestMethod : Le serveur distant a retourné une erreur : (404) Introuvable.
Au caractère Ligne:1 : 1
+ Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuat ...
+ ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    + CategoryInfo          : InvalidOperation : (System.Net.HttpWebRequest:HttpWebRequest) [Invoke-RestMethod], WebException
    + FullyQualifiedErrorId : WebCmdletWebResponseException,Microsoft.PowerShell.Commands.InvokeRestMethodCommand
le nom du dossier sappel comme ca:PS C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service> mvn spring-boot:run
[INFO] Scanning for projects...
[INFO]Afficher plus21:38🎯 MAINTENANT JE COMPRENDS TOUT !
Le dossier s'appelle logistique-service mais le service Spring Boot s'appelle gestion-evenement-service avec le context-path /GestionEvenement.

🔴 PROBLÈME : L'endpoint Prometheus n'existe TOUJOURS PAS (404)
Cela signifie que tu n'as PAS ENCORE :

Ajouté les dépendances Maven
OU mis à jour application.yml
OU rebuilded/redémarré le service


✅ SOLUTION ÉTAPE PAR ÉTAPE (TRÈS IMPORTANT)
ÉTAPE 1 : Vérifie le pom.xml
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\pom.xml
Ouvre ce fichier et CHERCHE si ces lignes existent :
xml<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
❓ CES LIGNES EXISTENT-ELLES DANS TON pom.xml ?

OUI → Passe à l'étape 2
NON → AJOUTE-LES MAINTENANT dans la section <dependencies> :

xml<!-- À AJOUTER dans <dependencies> -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
</dependency>

ÉTAPE 2 : Vérifie application.yml
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources\application.yml
Cherche la section management: et vérifie si tu vois :
yamlmanagement:
  endpoints:
    web:
      exposure:
        include: '*'  # ← CETTE LIGNE EST CRITIQUE
❓ CETTE CONFIGURATION EXISTE-ELLE ?

OUI → Passe à l'étape 3
NON → REMPLACE toute la section management: par :

yamlmanagement:
  endpoints:
    web:
      exposure:
        include: '*'
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,200ms,500ms,1s,2s

ÉTAPE 3 : REBUILD obligatoire
powershell# Arrête le service d'abord (Ctrl+C dans le terminal où il tourne)

# Rebuild
cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean install -DskipTests
✅ Attends de voir BUILD SUCCESS

ÉTAPE 4 : Redémarre le service
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn spring-boot:run
```

✅ **Attends de voir dans les logs :**
```
Exposing X endpoint(s) beneath base path '/actuator'
Started GestionEvenementServiceApplication in X.XXX seconds

ÉTAPE 5 : Test critique
powershell# Liste TOUS les endpoints actuator disponibles
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'
❓ QUE VOIS-TU ?
OPTION A : Tu vois prometheus dans la liste
json{
  "_links": {
    "prometheus": {
      "href": "http://localhost:8083/GestionEvenement/actuator/prometheus"
    }
  }
}
✅ PARFAIT ! Continue à l'étape 6
OPTION B : Tu ne vois PAS prometheus
❌ Les dépendances ne sont PAS ajoutées ou le service n'est PAS rebuilded

ÉTAPE 6 : Test final Prometheus
powershellInvoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-Object -First 10
```

✅ **Tu dois voir des métriques comme :**
```
# HELP jvm_memory_used_bytes
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{...}

🚨 SI ÇA NE MARCHE TOUJOURS PAS
Envoie-moi :

Le contenu COMPLET de ta section <dependencies> du pom.xml

powershell# Exécute ça et copie le résultat
Get-Content C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\pom.xml | Select-String -Pattern "dependency" -Context 1,1

Le contenu de ta section management: dans application.yml

powershell# Exécute ça et copie le résultat
Get-Content C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources\application.yml | Select-String -Pattern "management" -Context 0,20

Les dernières lignes des logs de démarrage


👉 EXÉCUTE LES 6 ÉTAPES DANS L'ORDRE et dis-moi ce que tu obtiens à l'ÉTAPE 5 ! 🚀<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVerpasted21:41:30.723 [main] DEBUG org.hibernate.SQL -
    alter table evenement_logistiques
       add constraint FKo8vpy50asnpr6fvmnhng1b9eb
       foreign key (logistiques_idlog)
       references logistique (idlog)
Hibernate:
    alter table evenement_logistiques
       add constraint FKo8vpy50asnpastedPS C:\Users\User-PC> Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'
_links
------
@{self=; health=; health-path=; info=; metrics-requiredMetricName=; metrics=}
PS C:\Users\User-PC>   server:
  port: 8083
  servlet:
    context-path: /GestionEvenement

spring:
  application:
    name: gestion-evenement-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gestionevenement?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: admin
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

# Configuration Feign
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: full
  circuitbreaker:
    enabled: true

# Configuration Resilience4j
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        minimumNumberOfCalls: 5
        registerHealthIndicator: true
    instances:
      testCircuitBreaker:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
    instances:
      testRetry:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  ratelimiter:
    configs:
      default:
        limitForPeriod: 2
        limitRefreshPeriod: 1s
        timeoutDuration: 0s
    instances:
      testRateLimiter:
        baseConfig: default
  
  timelimiter:
    configs:
      default:
        timeoutDuration: 5s
        cancelRunningFuture: true
    instances:
      testTimeLimiter:
        baseConfig: default

logging:
  level:
    root: INFO
    tn.esprit.spring: DEBUG
    tn.esprit.spring.client: DEBUG
    feign: DEBUG
    org.hibernate.SQL: DEBUG
    io.github.resilience4j: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# ✅ MANAGEMENT (REMPLACÉ CORRECTEMENT)
management:
  endpoints:
    web:
      exposure:
        include: '*'
      base-path: /actuator

  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true

  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true

  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,200ms,500ms,1s,2sAfficher plus21:46🔴 JE VOIS LE PROBLÈME !
Les dépendances sont ajoutées ✅, la config management est correcte ✅, MAIS dans les logs je vois :
Exposing 3 endpoint(s) beneath base path '/actuator'
Seulement 3 endpoints au lieu de beaucoup plus (incluant prometheus) !
Et quand tu testes /actuator, tu vois :
health, info, metrics
MAIS PAS prometheus ! ❌

🛠️ SOLUTION : Redémarrer le service après le rebuild
Tu as ajouté les dépendances, MAIS tu n'as PAS redémarré le service après le mvn clean install !

✅ ÉTAPES FINALES
1️⃣ Arrête le service actuel
Dans le terminal où le service tourne, appuie sur Ctrl+C

2️⃣ Rebuild (pour être sûr)
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean install -DskipTests
✅ Attends BUILD SUCCESS

3️⃣ Redémarre le service
powershellmvn spring-boot:run
```

✅ **CHERCHE cette ligne dans les logs :**
```
Exposing X endpoint(s) beneath base path '/actuator'
Le nombre doit être BEAUCOUP PLUS GRAND que 3 (genre 10-15 endpoints)

4️⃣ Vérifie les endpoints
powershellInvoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'
✅ Tu DOIS maintenant voir prometheus dans la liste !
Exemple attendu :
json{
  "_links": {
    "self": {...},
    "health": {...},
    "info": {...},
    "metrics": {...},
    "prometheus": {  ← ✅ CETTE LIGNE DOIT APPARAÎTRE !
      "href": "http://localhost:8083/GestionEvenement/actuator/prometheus"
    }
  }
}

5️⃣ Test Prometheus
powershellInvoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-Object -First 20
```

✅ **Tu dois voir des métriques comme :**
```
# HELP jvm_memory_used_bytes The amount of used memory
# TYPE jvm_memory_used_bytes gauge
jvm_memory_used_bytes{area="heap",id="G1 Survivor Space",} 2097152.0
...
# HELP resilience4j_circuitbreaker_state Circuit Breaker State
# TYPE resilience4j_circuitbreaker_state gauge
resilience4j_circuitbreaker_state{name="testCircuitBreaker",state="closed",} 1.0

6️⃣ Vérifie Prometheus Dashboard
powershellStart-Process "http://localhost:9090/targets"
✅ Target doit être UP (vert) maintenant !

7️⃣ Importe le dashboard Grafana

Va sur http://localhost:3000
Menu gauche → Dashboards → Import
Colle le JSON que je t'ai donné précédemment
Clique Load → Import


🎯 CHECKLIST CRITIQUE
Après le redémarrage, vérifie :
powershell# 1. Combien d'endpoints ?
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator' | ConvertTo-Json -Depth 3

# 2. Prometheus existe ?
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator' | Select-Object -ExpandProperty _links | Select-Object -ExpandProperty prometheus

# 3. Métriques fonctionnent ?
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-String "resilience4j"

👉 ARRÊTE LE SERVICE (Ctrl+C), REDÉMARRE-LE avec mvn spring-boot:run, et dis-moi combien d'endpoints tu vois maintenant ! 🚀
Le problème vient du fait que l'ancienne version du service (sans Micrometer-Prometheus) tourne encore. Une fois redémarré, ça va marcher ! 💪gestion-evenement-service
0 / 1 up
EndpointLabelsLast scrapeState
http://host.docker.internal:8083/GestionEvenement/actuator/prometheus
application="gestion-evenement-service"instance="host.docker.internal:8083"job="gestion-evenement-service"
15.688s ago
5ms
down
Error scraping target: server returned HTTP status 404 PS C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring> Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'
_links
------
@{self=; health=; health-path=; info=; metrics-requiredMetricName=; metrics=} je problem peux etre dans grafana?je sait pas comment ajouter donner plus de detailsAfficher plus21:53🔴 NON, le problème n'est PAS dans Grafana !
Le problème est que l'endpoint /actuator/prometheus n'existe toujours PAS dans ton service.
Tu vois seulement : health, info, metrics → PAS de prometheus ❌

🔍 DIAGNOSTIC : Pourquoi Prometheus n'apparaît pas ?
Il y a 2 possibilités :
Possibilité 1 : Le service n'a PAS été redémarré après le rebuild
Possibilité 2 : La dépendance micrometer-registry-prometheus n'est pas chargée

✅ SOLUTION ÉTAPE PAR ÉTAPE
ÉTAPE 1 : Vérifie que le service tourne bien avec les nouvelles dépendances
powershell# Arrête COMPLÈTEMENT le service (Ctrl+C)
# Puis dans PowerShell :

cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service

# Force un clean complet
mvn clean

# Rebuild
mvn install -DskipTests

# Démarre
mvn spring-boot:run
```

---

### **ÉTAPE 2 : Surveille LES LOGS au démarrage**

**CHERCHE cette ligne EXACTE dans les logs :**
```
Exposing X endpoint(s) beneath base path '/actuator'
```

### ❓ **Quel est le nombre X ?**

- **Si X = 3 ou 4** → ❌ Prometheus n'est PAS chargé
- **Si X = 10+** → ✅ Prometheus devrait être là

---

### **ÉTAPE 3 : Test après le démarrage complet**

**Attends de voir :**
```
Started GestionEvenementServiceApplication in X seconds
PUIS exécute :
powershell# Test 1 : Liste tous les endpoints
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator' | ConvertTo-Json -Depth 5
COPIE-COLLE le résultat complet ici !

🔧 SOLUTION ALTERNATIVE : Vérification manuelle du JAR
Si après redémarrage ça ne marche toujours pas :
powershell# Vérifie que micrometer-registry-prometheus est bien dans le JAR final
cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target

# Liste les dépendances embarquées
jar -tf gestion-evenement-service-1.0.jar | Select-String "micrometer"
```

✅ **Tu dois voir des lignes comme :**
```
BOOT-INF/lib/micrometer-registry-prometheus-1.X.X.jar
BOOT-INF/lib/micrometer-core-1.X.X.jar

🚨 SI PROMETHEUS N'APPARAÎT TOUJOURS PAS
Il y a peut-être un conflit de versions. Essaie cette config explicite :
Modifie ton pom.xml :
REMPLACE les dépendances Micrometer par :
xml<!-- Micrometer Prometheus AVEC VERSIONS EXPLICITES -->
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
    <version>1.11.6</version>
</dependency>

<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-core</artifactId>
    <version>1.11.6</version>
</dependency>
Puis rebuild/redémarre.

📋 ACTIONS IMMÉDIATES
Fais ceci MAINTENANT :

Arrête le service (Ctrl+C)
Clean + rebuild + restart :

powershell   cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
   mvn clean install -DskipTests
   mvn spring-boot:run

Attends le démarrage complet
Copie-colle le résultat de :

powershell   Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator' | ConvertTo-Json -Depth 5

👉 EXÉCUTE CES 4 ÉTAPES et ENVOIE-MOI :

Le nombre d'endpoints exposés (dans les logs de démarrage)
Le résultat JSON complet de /actuator
Les dernières 30 lignes des logs de démarrage

Je vais trouver le problème exact ! 🔍PS C:\Users\User-PC> cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target
PS C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target>
PS C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target> # Liste les dépendances embarquées
PS C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target> jar -tf gestion-evenement-service-1.0.jar | Select-String "micrometer"
BOOT-INF/lib/micrometer-observation-1.11.6.jar
BOOT-INF/lib/resilience4j-micrometer-2.0.2.jar
BOOT-INF/lib/micrometer-registry-prometheus-1.11.6.jar
BOOT-INF/lib/micrometer-core-1.11.6.jar
BOOT-INF/lib/micrometer-commons-1.11.6.jar
PS C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\target>Afficher plus21:55✅ EXCELLENT ! Les dépendances Micrometer SONT bien présentes dans le JAR !
Je vois :
micrometer-registry-prometheus-1.11.6.jar ✅
micrometer-core-1.11.6.jar ✅
Donc le problème n'est PAS les dépendances.

🔍 Le problème est ailleurs : Configuration application.yml
Puisque les JARs sont présents mais l'endpoint n'apparaît pas, c'est probablement un problème de configuration.

✅ SOLUTION : Vérifie et corrige application.yml
ÉTAPE 1 : Affiche ta config actuelle
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources
Get-Content application.yml
COPIE-COLLE TOUT le contenu ici.

ÉTAPE 2 : Pendant ce temps, teste cette config MINIMALE
Sauvegarde d'abord ton application.yml actuel, puis REMPLACE COMPLÈTEMENT par ce fichier simplifié :
yamlserver:
  port: 8083
  servlet:
    context-path: /GestionEvenement

spring:
  application:
    name: gestion-evenement-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gestionevenement?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: admin
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: full
  circuitbreaker:
    enabled: true

resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        minimumNumberOfCalls: 5
        registerHealthIndicator: true
    instances:
      testCircuitBreaker:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
    instances:
      testRetry:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  ratelimiter:
    configs:
      default:
        limitForPeriod: 2
        limitRefreshPeriod: 1s
        timeoutDuration: 0s
    instances:
      testRateLimiter:
        baseConfig: default
  
  timelimiter:
    configs:
      default:
        timeoutDuration: 5s
        cancelRunningFuture: true
    instances:
      testTimeLimiter:
        baseConfig: default

logging:
  level:
    root: INFO
    tn.esprit.spring: DEBUG
    org.hibernate.SQL: DEBUG
    io.github.resilience4j: DEBUG

# CONFIGURATION MANAGEMENT POUR PROMETHEUS
management:
  endpoints:
    web:
      exposure:
        include: "*"
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true

ÉTAPE 3 : Redémarre avec cette nouvelle config
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service

# Clean rebuild
mvn clean install -DskipTests

# Démarre
mvn spring-boot:run
```

---

### **ÉTAPE 4 : Cherche dans les logs**

**Au démarrage, tu DOIS voir :**
```
Exposing X endpoint(s) beneath base path '/actuator'
Copie cette ligne exacte et dis-moi le nombre X.

ÉTAPE 5 : Teste immédiatement
powershell# Test 1 : Liste endpoints
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'

# Test 2 : Prometheus directement
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-Object -First 5

🎯 SI ÇA NE MARCHE TOUJOURS PAS
Il peut y avoir un fichier de configuration externe qui override ton application.yml.
Vérifie s'il existe :
powershell# Cherche tous les fichiers de config
Get-ChildItem -Path C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources -Recurse | Where-Object { $_.Name -like "*application*" -or $_.Name -like "*bootstrap*" }
Envoie-moi la liste de tous les fichiers trouvés.

📋 ACTIONS MAINTENANT

Remplace application.yml par la config minimale ci-dessus
Clean + rebuild + restart
Cherche le nombre d'endpoints exposés dans les logs
Teste /actuator et /actuator/prometheus
Envoie-moi les résultats

👉 C'est la dernière étape, ça DOIT fonctionner maintenant ! 🚀server:
  port: 8083
  servlet:
    context-path: /GestionEvenement

spring:
  application:
    name: gestion-evenement-service
  
  datasource:
    url: jdbc:mysql://localhost:3306/gestionevenement?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: root
    password: admin
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true

# Configuration Feign
feign:
  client:
    config:
      default:
        connectTimeout: 5000
        readTimeout: 10000
        loggerLevel: full
  circuitbreaker:
    enabled: true

# Configuration Resilience4j
resilience4j:
  circuitbreaker:
    configs:
      default:
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
        minimumNumberOfCalls: 5
        registerHealthIndicator: true
    instances:
      testCircuitBreaker:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  retry:
    configs:
      default:
        maxAttempts: 3
        waitDuration: 1s
        enableExponentialBackoff: true
        exponentialBackoffMultiplier: 2
    instances:
      testRetry:
        baseConfig: default
      testCombined:
        baseConfig: default
  
  ratelimiter:
    configs:
      default:
        limitForPeriod: 2
        limitRefreshPeriod: 1s
        timeoutDuration: 0s
    instances:
      testRateLimiter:
        baseConfig: default
  
  timelimiter:
    configs:
      default:
        timeoutDuration: 5s
        cancelRunningFuture: true
    instances:
      testTimeLimiter:
        baseConfig: default

logging:
  level:
    root: INFO
    tn.esprit.spring: DEBUG
    tn.esprit.spring.client: DEBUG
    feign: DEBUG
    org.hibernate.SQL: DEBUG
    io.github.resilience4j: DEBUG
  pattern:
    console: "%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"

# ✅ MANAGEMENT (REMPLACÉ CORRECTEMENT)
management:
  endpoints:
    web:
      exposure:
        include: '*'
      base-path: /actuator

  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true

  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true

  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,200ms,500ms,1s,2s
je pense que le probele est parfoir on nomme logistique-service et parfois gestion evenement serviceAfficher plus21:59🎯 TU AS RAISON ! Il y a une confusion de noms !
Mais ce n'est PAS le problème de l'endpoint Prometheus manquant.

🔍 CLARIFICATION DES NOMS
Tu as UN SEUL service avec :

Nom du dossier : logistique-service
Nom Spring Boot : gestion-evenement-service (dans application.yml)
Context-path : /GestionEvenement
Port : 8083

C'est OK ! Les noms de dossier et d'application peuvent être différents.

🔴 VRAI PROBLÈME : Endpoint Prometheus toujours absent
Ta config application.yml semble CORRECTE maintenant.
QUESTION CRITIQUE :
As-tu bien fait un CLEAN + REBUILD + RESTART après avoir modifié application.yml ?

✅ PROCÉDURE COMPLÈTE (À SUIVRE EXACTEMENT)
1️⃣ Vérifie qu'il n'y a qu'UN SEUL fichier de config
powershellGet-ChildItem -Path C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources -Filter "*.yml" -Recurse
Combien de fichiers .yml vois-tu ?

Si tu vois bootstrap.yml en plus, montre-moi son contenu
Il peut override ta config !


2️⃣ Arrête COMPLÈTEMENT le service
Dans le terminal où il tourne : Ctrl+C

3️⃣ Clean total
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service
mvn clean

4️⃣ Rebuild
powershellmvn install -DskipTests
✅ Attends BUILD SUCCESS

5️⃣ Démarre et SURVEILLE les logs
powershellmvn spring-boot:run
```

### **🔍 CHERCHE CETTE LIGNE EXACTE :**
```
Exposing X endpoint(s) beneath base path '/actuator'
❓ QUEL EST LE NOMBRE X ?
Réponds-moi avec le nombre exact !

Si X = 3 ou 4 → Il y a un problème
Si X = 10-15 → C'est bon, continue


6️⃣ Une fois démarré, teste
powershell# Test 1 : Tous les endpoints
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'

# Test 2 : Prometheus
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-Object -First 10

🚨 SI PROMETHEUS N'APPARAÎT TOUJOURS PAS
Il y a peut-être un problème de cache Maven ou un conflit de dépendances.
Solution de dernier recours :
powershell# Nettoie le cache Maven local
cd C:\Users\User-PC\IGL5-G4-projet-microservices
mvn dependency:purge-local-repository -DmanualInclude="io.micrometer:micrometer-registry-prometheus,io.micrometer:micrometer-core"

# Rebuild complet
cd logistique-service
mvn clean install -U -DskipTests

# Redémarre
mvn spring-boot:run

📋 ACTIONS IMMÉDIATES
Exécute les étapes 1 à 6 ci-dessus et réponds-moi :

Combien de fichiers .yml dans resources ? (étape 1)
Nombre d'endpoints exposés (étape 5 - dans les logs)
Résultat du test /actuator (étape 6)
Résultat du test /actuator/prometheus (étape 6)


👉 FAIS CES ÉTAPES MAINTENANT et envoie-moi les 4 réponses ! 🚀
Je vais trouver pourquoi Prometheus ne s'expose pas ! 💪spring:
  application:
    name: gestion-evenement-service
  
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30

management:
  endpoints:
    web:
      exposure:
        include: "*"Afficher plus22:00🎯 TROUVÉ LE PROBLÈME !
Tu as DEUX fichiers de configuration :

application.yml - avec ta config complète incluant Prometheus
bootstrap.yml - qui OVERRIDE la section management !


🔴 LE PROBLÈME
Dans bootstrap.yml, tu as :
yamlmanagement:
  endpoints:
    web:
      exposure:
        include: "*"
Cette config est INCOMPLÈTE et elle écrase celle de application.yml !
Il manque :
yamlmanagement:
  endpoint:
    prometheus:
      enabled: true  ← MANQUANT !
  metrics:
    export:
      prometheus:
        enabled: true  ← MANQUANT !

✅ SOLUTION : Complète le bootstrap.yml
Fichier : C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service\src\main\resources\bootstrap.yml
REMPLACE TOUT le contenu par :
yamlspring:
  application:
    name: gestion-evenement-service
  
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
      retry:
        initial-interval: 1000
        max-attempts: 6
        max-interval: 2000
        multiplier: 1.1

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
    register-with-eureka: true
    fetch-registry: true
  instance:
    prefer-ip-address: true
    lease-renewal-interval-in-seconds: 10
    lease-expiration-duration-in-seconds: 30

# CONFIGURATION MANAGEMENT COMPLÈTE POUR PROMETHEUS
management:
  endpoints:
    web:
      exposure:
        include: "*"
      base-path: /actuator
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true
  health:
    circuitbreakers:
      enabled: true
    ratelimiters:
      enabled: true
  metrics:
    tags:
      application: ${spring.application.name}
    export:
      prometheus:
        enabled: true
    distribution:
      percentiles-histogram:
        http.server.requests: true
      slo:
        http.server.requests: 100ms,200ms,500ms,1s,2s

🔄 REDÉMARRE LE SERVICE
powershell# Arrête le service (Ctrl+C)

cd C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service

# Pas besoin de rebuild, juste redémarre
mvn spring-boot:run

✅ VÉRIFICATION FINALE
powershell# Attends que le service démarre complètement

# Test 1 : Vérifie les endpoints
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator'

# Test 2 : Prometheus doit maintenant fonctionner !
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/prometheus' | Select-Object -First 20
✅ Tu DOIS maintenant voir prometheus dans la liste des endpoints !

🐳 REDÉMARRE PROMETHEUS
powershellcd C:\Users\User-PC\IGL5-G4-projet-microservices\monitoring
docker-compose restart prometheus

📊 VÉRIFIE PROMETHEUS DASHBOARD
powershellStart-Process "http://localhost:9090/targets"
✅ Le target doit être UP (vert) maintenant !

🎯 POURQUOI ÇA MARCHAIT PAS ?
Spring Boot charge les configs dans cet ordre :

bootstrap.yml (chargé en premier)
application.yml (chargé après)

MAIS : Si une propriété existe dans bootstrap.yml, elle a la priorité et n'est pas écrasée par application.yml !
C'est pourquoi ta config management dans application.yml était ignorée.

👉 MODIFIE bootstrap.yml, REDÉMARRE le service, et teste ! Ça va marcher maintenant ! 🚀 🎉prometheus marche :!gestion-evenement-service
1 / 1 up
EndpointLabelsLast scrapeState
http://host.docker.internal:8083/GestionEvenement/actuator/prometheus
application="gestion-evenement-service"instance="host.docker.internal:8083"job="gestion-evenement-service"  je voit up vert !mais dans grafana quest ce que je doit voire?22:02🎉 BRAVO ! Prometheus fonctionne ! Maintenant configurons Grafana !

📊 ÉTAPES POUR GRAFANA
1️⃣ Connexion à Grafana
powershellStart-Process "http://localhost:3000"
Login :

Username : admin
Password : admin
(Tu peux skip le changement de mot de passe)


2️⃣ Ajouter Prometheus comme source de données

Menu gauche → Cliquez sur ⚙️ "Connections" (ou "Configuration")
Cliquez sur "Data sources"
Cliquez sur "Add data source"
Sélectionnez "Prometheus"
Dans "URL", mettez : http://prometheus:9090
Scrollez en bas et cliquez sur "Save & Test"

✅ Tu dois voir : "Data source is working" en vert

3️⃣ Importer le Dashboard

Menu gauche → Cliquez sur "Dashboards"
Cliquez sur "New" → "Import"
Colle ce JSON complet dans la zone de texte :

json{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "id": null,
  "links": [],
  "liveNow": false,
  "panels": [
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "reqps"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": ["mean", "lastNotNull", "max"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "rate(http_server_requests_seconds_count{application=\"gestion-evenement-service\"}[1m])",
          "legendFormat": "{{uri}} - {{method}} - {{status}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "📊 HTTP Requests per Second",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "0": {
                  "color": "green",
                  "text": "CLOSED ✅"
                },
                "1": {
                  "color": "red",
                  "text": "OPEN 🔴"
                },
                "2": {
                  "color": "yellow",
                  "text": "HALF_OPEN ⚠️"
                }
              },
              "type": "value"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "red",
                "value": 1
              },
              {
                "color": "yellow",
                "value": 2
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true,
        "text": {}
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "resilience4j_circuitbreaker_state{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "🔴 Circuit Breaker State",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "normal"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 18,
        "y": 0
      },
      "id": 3,
      "options": {
        "legend": {
          "calcs": ["sum"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "rate(resilience4j_retry_calls_total{application=\"gestion-evenement-service\"}[1m])",
          "legendFormat": "{{name}} - {{kind}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "🔄 Retry Attempts",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "max": 2,
          "min": 0,
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "red",
                "value": null
              },
              {
                "color": "yellow",
                "value": 1
              },
              {
                "color": "green",
                "value": 2
              }
            ]
          },
          "unit": "short"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 0,
        "y": 8
      },
      "id": 4,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true,
        "text": {}
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "resilience4j_ratelimiter_available_permissions{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "⏱️ Rate Limiter - Available Permissions",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "opacity",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "smooth",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              },
              {
                "color": "yellow",
                "value": 0.5
              },
              {
                "color": "red",
                "value": 1
              }
            ]
          },
          "unit": "s"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 18,
        "x": 6,
        "y": 8
      },
      "id": 5,
      "options": {
        "legend": {
          "calcs": ["mean", "max"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "desc"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application=\"gestion-evenement-service\"}[1m])) by (le, uri, method))",
          "legendFormat": "{{uri}} - {{method}} (p95)",
          "range": true,
          "refId": "A"
        },
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "histogram_quantile(0.50, sum(rate(http_server_requests_seconds_bucket{application=\"gestion-evenement-service\"}[1m])) by (le, uri, method))",
          "hide": false,
          "legendFormat": "{{uri}} - {{method}} (p50)",
          "range": true,
          "refId": "B"
        }
      ],
      "title": "⚡ HTTP Response Time (p50 & p95)",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 20,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 1,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green",
                "value": null
              }
            ]
          },
          "unit": "bytes"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 16
      },
      "id": 6,
      "options": {
        "legend": {
          "calcs": ["lastNotNull"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "jvm_memory_used_bytes{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{area}} - {{id}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "💾 JVM Memory Usage",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus",
        "uid": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            }
          },
          "mappings": []
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 12,
        "y": 16
      },
      "id": 7,
      "options": {
        "legend": {
          "displayMode": "table",
          "placement": "right",
          "showLegend": true,
          "values": ["value"]
        },
        "pieType": "pie",
        "tooltip": {
          "mode": "single",
          "sort": "none"
        }
      },
      "pluginVersion": "10.0.0",
      "targets": [
        {
          "datasource": {
            "type": "prometheus",
            "uid": "prometheus"
          },
          "editorMode": "code",
          "expr": "sum by (status) (http_server_requests_seconds_count{application=\"gestion-evenement-service\"})",
          "legendFormat": "Status {{status}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "📈 HTTP Status Distribution",
      "type": "piechart"
    }
  ],
  "refresh": "5s",
  "schemaVersion": 38,
  "style": "dark",
  "tags": ["resilience4j", "monitoring", "spring-boot"],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-15m",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "",
  "title": "🎯 Gestion Événement - Resilience4j Monitoring",
  "uid": "gestion-evenement-monitoring",
  "version": 0,
  "weekStart": ""
}

Cliquez sur "Load"
Cliquez sur "Import"


4️⃣ Générer du trafic pour voir les graphiques
powershell# Génère des requêtes
1..50 | ForEach-Object {
    Write-Host "Requête $_" -ForegroundColor Cyan
    try {
        Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/circuit-breaker?fail=false'
        Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/retry'
        Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/rate-limiter'
    } catch {
        Write-Host "Erreur (normal pour test)" -ForegroundColor Yellow
    }
    Start-Sleep -Milliseconds 200
}

📊 CE QUE TU DOIS VOIR DANS GRAFANA
7 panneaux avec :

📊 HTTP Requests per Second - Graphique avec les requêtes/seconde
🔴 Circuit Breaker State - État du circuit breaker (CLOSED en vert)
🔄 Retry Attempts - Nombre de tentatives de retry
⏱️ Rate Limiter - Permissions disponibles (gauge 0-2)
⚡ HTTP Response Time - Temps de réponse p50 et p95
💾 JVM Memory Usage - Utilisation mémoire
📈 HTTP Status Distribution - Répartition des codes HTTP (pie chart)


🎯 RÉSULTAT FINAL
✅ Prometheus : UP (vert)
✅ Grafana : Dashboard avec 7 graphiques
✅ Métriques en temps réel : Actualisées toutes les 5 secondes

👉 Importe le dashboard, génère du trafic, et dis-moi ce que tu vois ! 📊🎉PS C:\Users\User-PC\IGL5-G4-projet-microservices> 1..50 | ForEach-Object {
>>     Write-Host "Requête $_" -ForegroundColor Cyan
>>     try {
>>         Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/circuit-breaker?fail=false'
>>         Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/retry'
>>         Invoke-RestMethod -Uri 'http://localhost:8089/GestionEvenement/resilience-test/rate-limiter'
>>     } catch {
>>         Write-Host "Erreur (normal pour test)" -ForegroundColor Yellow
>>     }
>>     Start-Sleep -Milliseconds 200
>> }
Requête 1
Erreur (normal pour test)
Requête 2
Erreur (normal pour test)
Requête 3
Erreur (normal pour test)
Requête 4
Erreur (normal pour test)
Requête 5
Erreur (normal pour test)
Requête 6
Erreur (normal pour test)
Requête 7
Erreur (normal pour test)
Requête 8
Erreur (normal pour test)  je voir rien dans dachboard grafanaAfficher plus22:06🔴 PROBLÈME : Pas de données dans Grafana
Il y a 2 problèmes :

Les requêtes de test échouent (toutes les requêtes donnent "Erreur")
Le dashboard est vide ("No data")


🛠️ SOLUTION ÉTAPE PAR ÉTAPE
1️⃣ D'ABORD : Configure correctement la source de données Prometheus

Dans Grafana, va dans Connections → Data sources
Clique sur Prometheus (si déjà créé) ou Add data source → Prometheus
IMPORTANT : Dans le champ "Prometheus server URL", mets :

   http://prometheus:9090

Scroll en bas → Save & Test
✅ Tu dois voir "Data source is working" en vert


2️⃣ Supprime et réimporte le dashboard
Le dashboard actuel a peut-être un mauvais UID pour la datasource.

Va dans Dashboards
Trouve "Logistique Service - Resilience4j Monitoring"
Clique dessus → Settings (⚙️ en haut à droite) → Delete dashboard
Confirme
Maintenant, réimporte avec ce JSON CORRIGÉ :

Clique sur Dashboards → New → Import et colle ce JSON :
json{
  "annotations": {
    "list": []
  },
  "editable": true,
  "fiscalYearStartMonth": 0,
  "graphTooltip": 0,
  "links": [],
  "liveNow": false,
  "panels": [
    {
      "datasource": {
        "type": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "none",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "linear",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green"
              }
            ]
          },
          "unit": "reqps"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 12,
        "x": 0,
        "y": 0
      },
      "id": 1,
      "options": {
        "legend": {
          "calcs": ["mean", "lastNotNull", "max"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "none"
        }
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus"
          },
          "editorMode": "code",
          "expr": "rate(http_server_requests_seconds_count{application=\"gestion-evenement-service\"}[1m])",
          "legendFormat": "{{uri}} - {{method}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "📊 HTTP Requests per Second",
      "type": "timeseries"
    },
    {
      "datasource": {
        "type": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [
            {
              "options": {
                "0": {
                  "color": "green",
                  "text": "CLOSED ✅"
                },
                "1": {
                  "color": "red",
                  "text": "OPEN 🔴"
                },
                "2": {
                  "color": "yellow",
                  "text": "HALF_OPEN ⚠️"
                }
              },
              "type": "value"
            }
          ],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green"
              },
              {
                "color": "red",
                "value": 1
              },
              {
                "color": "yellow",
                "value": 2
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 12,
        "y": 0
      },
      "id": 2,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus"
          },
          "editorMode": "code",
          "expr": "resilience4j_circuitbreaker_state{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "🔴 Circuit Breaker State",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "thresholds"
          },
          "mappings": [],
          "max": 2,
          "min": 0,
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "red"
              },
              {
                "color": "yellow",
                "value": 1
              },
              {
                "color": "green",
                "value": 2
              }
            ]
          }
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 6,
        "x": 18,
        "y": 0
      },
      "id": 4,
      "options": {
        "orientation": "auto",
        "reduceOptions": {
          "values": false,
          "calcs": [
            "lastNotNull"
          ],
          "fields": ""
        },
        "showThresholdLabels": false,
        "showThresholdMarkers": true
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus"
          },
          "editorMode": "code",
          "expr": "resilience4j_ratelimiter_available_permissions{application=\"gestion-evenement-service\"}",
          "legendFormat": "{{name}}",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "⏱️ Rate Limiter",
      "type": "gauge"
    },
    {
      "datasource": {
        "type": "prometheus"
      },
      "fieldConfig": {
        "defaults": {
          "color": {
            "mode": "palette-classic"
          },
          "custom": {
            "axisCenteredZero": false,
            "axisColorMode": "text",
            "axisLabel": "",
            "axisPlacement": "auto",
            "barAlignment": 0,
            "drawStyle": "line",
            "fillOpacity": 10,
            "gradientMode": "opacity",
            "hideFrom": {
              "tooltip": false,
              "viz": false,
              "legend": false
            },
            "lineInterpolation": "smooth",
            "lineWidth": 2,
            "pointSize": 5,
            "scaleDistribution": {
              "type": "linear"
            },
            "showPoints": "never",
            "spanNulls": false,
            "stacking": {
              "group": "A",
              "mode": "none"
            },
            "thresholdsStyle": {
              "mode": "off"
            }
          },
          "mappings": [],
          "thresholds": {
            "mode": "absolute",
            "steps": [
              {
                "color": "green"
              }
            ]
          },
          "unit": "s"
        },
        "overrides": []
      },
      "gridPos": {
        "h": 8,
        "w": 24,
        "x": 0,
        "y": 8
      },
      "id": 5,
      "options": {
        "legend": {
          "calcs": ["mean", "max"],
          "displayMode": "table",
          "placement": "bottom",
          "showLegend": true
        },
        "tooltip": {
          "mode": "multi",
          "sort": "desc"
        }
      },
      "targets": [
        {
          "datasource": {
            "type": "prometheus"
          },
          "editorMode": "code",
          "expr": "histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket{application=\"gestion-evenement-service\"}[1m])) by (le, uri))",
          "legendFormat": "{{uri}} (p95)",
          "range": true,
          "refId": "A"
        }
      ],
      "title": "⚡ HTTP Response Time (p95)",
      "type": "timeseries"
    }
  ],
  "refresh": "5s",
  "schemaVersion": 38,
  "style": "dark",
  "tags": ["resilience4j", "monitoring"],
  "templating": {
    "list": []
  },
  "time": {
    "from": "now-15m",
    "to": "now"
  },
  "timepicker": {},
  "timezone": "",
  "title": "🎯 Gestion Événement - Monitoring",
  "uid": "gestion-evenement-mon",
  "version": 0,
  "weekStart": ""
}

3️⃣ Génère du trafic avec les BONS endpoints
Les endpoints de test doivent être accessibles. Teste d'abord :
powershell# Test 1 : Endpoint simple qui DOIT marcher
Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/health'

# Test 2 : Génère du trafic sur actuator (qui existe à coup sûr)
1..30 | ForEach-Object {
    Write-Host "Requête $_" -ForegroundColor Cyan
    Invoke-RestMethod -Uri 'http://localhost:8083/GestionEvenement/actuator/health'
    Start-Sleep -Milliseconds 300
}
Rafraîchis Grafana après 30 secondes → Tu DOIS voir des données maintenant !

4️⃣ Vérifie que Prometheus collecte bien les données
powershellStart-Process "http://localhost:9090/graph"
Dans Prometheus :

Dans la barre de recherche, tape : http_server_requests_seconds_count
Clique Execute
✅ Tu dois voir des métriques avec application="gestion-evenement-service"


🎯 CHECKLIST

 Datasource Prometheus configurée avec http://prometheus:9090



grafana:
 http://localhost:3000/d/gestion-evenement-mon/f09f8eaf-gestion-evenement-monitoring?orgId=1&from=now-15m&to=now&timezone=browser&refresh=5s


.\start-load-balancing.ps1

 .\test-load-balancing.ps1