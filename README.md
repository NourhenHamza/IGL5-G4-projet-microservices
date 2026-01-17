
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