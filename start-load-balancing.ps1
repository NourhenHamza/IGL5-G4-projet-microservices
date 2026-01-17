# ============================================================
# Script de démarrage pour Load Balancing - 2 instances
# ============================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  LOAD BALANCING - Démarrage de 2 instances" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier que Config Server et Eureka sont actifs
Write-Host "Vérification des services..." -ForegroundColor Yellow

try {
    $configTest = Invoke-WebRequest -Uri "http://localhost:8888/actuator/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "[OK] Config Server actif (port 8888)" -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Config Server non accessible sur le port 8888" -ForegroundColor Red
    Write-Host "Démarrez d'abord Config Server avec: cd config-server && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

try {
    $eurekaTest = Invoke-WebRequest -Uri "http://localhost:8761/actuator/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "[OK] Eureka Server actif (port 8761)" -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Eureka Server non accessible sur le port 8761" -ForegroundColor Red
    Write-Host "Démarrez d'abord Eureka Server avec: cd eureka-server && mvn spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "Préparation du lancement des instances..." -ForegroundColor Yellow
Write-Host ""

# Chemin vers le service
$servicePath = "C:\Users\User-PC\IGL5-G4-projet-microservices\logistique-service"

# Vérifier que le dossier existe
if (-not (Test-Path $servicePath)) {
    Write-Host "[ERREUR] Dossier logistique-service introuvable!" -ForegroundColor Red
    Write-Host "Chemin attendu: $servicePath" -ForegroundColor Yellow
    exit 1
}

# Fonction pour démarrer une instance
function Start-ServiceInstance {
    param(
        [int]$port,
        [string]$instanceName
    )
    
    Write-Host "Démarrage de $instanceName sur le port $port..." -ForegroundColor Cyan
    
    # Créer un nouveau terminal PowerShell pour cette instance
    $command = "cd '$servicePath'; `$env:SERVER_PORT='$port'; mvn spring-boot:run"
    
    Start-Process powershell -ArgumentList "-NoExit", "-Command", $command
    
    Write-Host "[OK] $instanceName lancée dans un nouveau terminal" -ForegroundColor Green
}

# Démarrer Instance 1 (port 8083)
Start-ServiceInstance -port 8083 -instanceName "Instance 1"
Start-Sleep -Seconds 3

# Démarrer Instance 2 (port 8084)
Start-ServiceInstance -port 8084 -instanceName "Instance 2"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Les 2 instances sont en cours de démarrage..." -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Patience, cela peut prendre 30-60 secondes..." -ForegroundColor Yellow
Write-Host ""
Write-Host "Surveillance du démarrage..." -ForegroundColor Yellow

# Attendre et vérifier que les instances sont prêtes
$maxAttempts = 60
$attempt = 0
$instance1Ready = $false
$instance2Ready = $false

while ($attempt -lt $maxAttempts -and (-not $instance1Ready -or -not $instance2Ready)) {
    Start-Sleep -Seconds 2
    $attempt++
    
    # Vérifier Instance 1
    if (-not $instance1Ready) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8083/GestionEvenement/actuator/health" -UseBasicParsing -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                $instance1Ready = $true
                Write-Host "[OK] Instance 1 (port 8083) est prête!" -ForegroundColor Green
            }
        } catch {
            # Instance pas encore prête
        }
    }
    
    # Vérifier Instance 2
    if (-not $instance2Ready) {
        try {
            $response = Invoke-WebRequest -Uri "http://localhost:8084/GestionEvenement/actuator/health" -UseBasicParsing -ErrorAction Stop
            if ($response.StatusCode -eq 200) {
                $instance2Ready = $true
                Write-Host "[OK] Instance 2 (port 8084) est prête!" -ForegroundColor Green
            }
        } catch {
            # Instance pas encore prête
        }
    }
    
    if (-not $instance1Ready -or -not $instance2Ready) {
        Write-Host "." -NoNewline -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host ""

if ($instance1Ready -and $instance2Ready) {
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host "  SUCCÈS - Les 2 instances sont actives!" -ForegroundColor Green
    Write-Host "============================================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "Instance 1: http://localhost:8083/GestionEvenement" -ForegroundColor Cyan
    Write-Host "Instance 2: http://localhost:8084/GestionEvenement" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Attendez 30 secondes pour que Eureka enregistre les 2 instances..." -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Ensuite, exécutez: .\test-load-balancing.ps1" -ForegroundColor Cyan
    Write-Host ""
} else {
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host "  TIMEOUT - Une ou plusieurs instances n'ont pas démarré" -ForegroundColor Red
    Write-Host "============================================================" -ForegroundColor Red
    Write-Host ""
    Write-Host "Vérifiez les logs dans les terminaux ouverts" -ForegroundColor Yellow
}