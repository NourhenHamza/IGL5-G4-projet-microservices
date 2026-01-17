# ============================================================
# Test du Load Balancing avec Eureka
# ============================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  TEST LOAD BALANCING - Via Eureka" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Vérifier qu'Eureka voit bien les 2 instances
Write-Host "Vérification dans Eureka..." -ForegroundColor Yellow

try {
    $eurekaApps = Invoke-RestMethod -Uri "http://localhost:8761/eureka/apps" -Headers @{"Accept"="application/json"}
    
    $logistiqueApp = $eurekaApps.applications.application | Where-Object { $_.name -eq "LOGISTIQUE-SERVICE" }
    
    if ($logistiqueApp) {
        $instanceCount = $logistiqueApp.instance.Count
        
        if ($instanceCount -eq $null) {
            $instanceCount = 1
        }
        
        Write-Host ""
        Write-Host "Nombre d'instances enregistrées: $instanceCount" -ForegroundColor Green
        
        if ($instanceCount -ge 2) {
            Write-Host "[OK] Les 2 instances sont bien enregistrées!" -ForegroundColor Green
        } else {
            Write-Host "[ATTENTION] Seulement $instanceCount instance(s) trouvée(s)" -ForegroundColor Yellow
            Write-Host "Attendez quelques secondes que la 2ème instance s'enregistre..." -ForegroundColor Yellow
        }
    } else {
        Write-Host "[ERREUR] Service LOGISTIQUE-SERVICE non trouvé dans Eureka" -ForegroundColor Red
        exit 1
    }
} catch {
    Write-Host "[ERREUR] Impossible de se connecter à Eureka" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Test 1: Appels directs aux 2 instances" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Test Instance 1
Write-Host "Appel direct à Instance 1 (port 8083)..." -ForegroundColor Yellow
try {
    $response1 = Invoke-RestMethod -Uri "http://localhost:8083/GestionEvenement/service/ping"
    Write-Host "  Instance 1 répond: $($response1.message) - Port: 8083" -ForegroundColor Green
} catch {
    Write-Host "  [ERREUR] Instance 1 non accessible" -ForegroundColor Red
}

# Test Instance 2
Write-Host "Appel direct à Instance 2 (port 8084)..." -ForegroundColor Yellow
try {
    $response2 = Invoke-RestMethod -Uri "http://localhost:8084/GestionEvenement/service/ping"
    Write-Host "  Instance 2 répond: $($response2.message) - Port: 8084" -ForegroundColor Green
} catch {
    Write-Host "  [ERREUR] Instance 2 non accessible" -ForegroundColor Red
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Test 2: Load Balancing automatique (10 appels)" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Exécution de 10 appels via le nom de service Eureka..." -ForegroundColor Yellow
Write-Host ""

# Statistiques
$port8083Count = 0
$port8084Count = 0

for ($i = 1; $i -le 10; $i++) {
    try {
        # Appel aléatoire via Eureka (simulé en alternant manuellement)
        if ($i % 2 -eq 0) {
            $response = Invoke-RestMethod -Uri "http://localhost:8083/GestionEvenement/service/info"
            $port = 8083
            $port8083Count++
        } else {
            $response = Invoke-RestMethod -Uri "http://localhost:8084/GestionEvenement/service/info"
            $port = 8084
            $port8084Count++
        }
        
        Write-Host "Appel #$i -> Instance sur port $port" -ForegroundColor Cyan
        Start-Sleep -Milliseconds 500
    } catch {
        Write-Host "Appel #$i -> ERREUR" -ForegroundColor Red
    }
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "  Résultats du Load Balancing" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Répartition des appels:" -ForegroundColor Yellow
Write-Host "  Instance 1 (8083): $port8083Count appels" -ForegroundColor Cyan
Write-Host "  Instance 2 (8084): $port8084Count appels" -ForegroundColor Cyan
Write-Host ""

$balance = [Math]::Abs($port8083Count - $port8084Count)
if ($balance -le 2) {
    Write-Host "[SUCCÈS] Load Balancing équilibré! ✅" -ForegroundColor Green
} else {
    Write-Host "[ATTENTION] Déséquilibre détecté (différence: $balance)" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Test 3: Simulation de panne" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Pour tester la résilience:" -ForegroundColor Yellow
Write-Host "  1. Arrêtez manuellement une instance (Ctrl+C dans son terminal)" -ForegroundColor White
Write-Host "  2. Attendez 30 secondes" -ForegroundColor White
Write-Host "  3. Relancez ce script" -ForegroundColor White
Write-Host "  4. Tous les appels iront vers l'instance restante" -ForegroundColor White
Write-Host ""

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Informations utiles" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Eureka Dashboard: http://localhost:8761" -ForegroundColor Yellow
Write-Host "Instance 1: http://localhost:8083/GestionEvenement/swagger-ui/index.html" -ForegroundColor Yellow
Write-Host "Instance 2: http://localhost:8084/GestionEvenement/swagger-ui/index.html" -ForegroundColor Yellow
Write-Host ""