Write-Host "Generating traffic for Grafana metrics..." -ForegroundColor Cyan

$url = "http://localhost:8089/GestionSalle/salles"

# Generate 100 requests
for ($i = 1; $i -le 100; $i++) {
    try {
        switch ($i % 4) {
            0 { 
                Invoke-RestMethod -Uri $url -Method Get | Out-Null
                Write-Host "." -NoNewline -ForegroundColor Green
            }
            1 { 
                Invoke-RestMethod -Uri "$url/disponibles" -Method Get | Out-Null
                Write-Host "." -NoNewline -ForegroundColor Cyan
            }
            2 { 
                Invoke-RestMethod -Uri "$url/capacite/30" -Method Get | Out-Null
                Write-Host "." -NoNewline -ForegroundColor Yellow
            }
            3 { 
                try {
                    Invoke-RestMethod -Uri "$url/1" -Method Get | Out-Null
                    Write-Host "." -NoNewline -ForegroundColor Blue
                } catch {
                    Write-Host "X" -NoNewline -ForegroundColor Red
                }
            }
        }

        Start-Sleep -Milliseconds 100
    } catch {
        Write-Host "X" -NoNewline -ForegroundColor Red
    }

    if ($i % 50 -eq 0) {
        Write-Host " [$i requests]" -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "Traffic generation complete! Check Grafana now." -ForegroundColor Green
