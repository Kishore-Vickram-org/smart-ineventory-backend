param(
  [int]$Port = 8081
)

$ErrorActionPreference = 'Stop'

$pidFile = Join-Path $PSScriptRoot '.backend.pid'

$listen = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Where-Object { $_.State -eq 'Listen' }
if (-not $listen) {
  # If we launched via run-backend.ps1, we may still have a PID even if the port isn't listening.
  if (Test-Path $pidFile) {
    $raw = (Get-Content -LiteralPath $pidFile -ErrorAction SilentlyContinue | Select-Object -First 1)
    $savedPid = 0
    [void][int]::TryParse($raw, [ref]$savedPid)
    if ($savedPid -gt 0) {
      try {
        $proc = Get-Process -Id $savedPid -ErrorAction Stop
        Write-Host "Stopping recorded PID $savedPid ($($proc.ProcessName))..." -ForegroundColor Yellow
        Stop-Process -Id $savedPid -Force
        Remove-Item -Force $pidFile -ErrorAction SilentlyContinue
        Write-Host "Stopped PID $savedPid." -ForegroundColor Green
        exit 0
      } catch {
        # PID file is stale
        Remove-Item -Force $pidFile -ErrorAction SilentlyContinue
      }
    }
  }

  Write-Host "No process is listening on port $Port." -ForegroundColor Green
  exit 0
}

$procIds = $listen | Select-Object -ExpandProperty OwningProcess -Unique
foreach ($procId in $procIds) {
  try {
    $proc = Get-Process -Id $procId -ErrorAction Stop
    Write-Host "Stopping PID $procId ($($proc.ProcessName)) listening on port $Port..." -ForegroundColor Yellow
    Stop-Process -Id $procId -Force
  } catch {
    Write-Host "Failed to stop PID ${procId}: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
  }
}

Remove-Item -Force $pidFile -ErrorAction SilentlyContinue

Start-Sleep -Seconds 1
$still = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Where-Object { $_.State -eq 'Listen' }
if ($still) {
  Write-Host "Port $Port is still in use after stop attempt." -ForegroundColor Red
  exit 1
}

Write-Host "Port $Port is now free." -ForegroundColor Green
exit 0
