param(
  [int]$Port = 8081
)

$ErrorActionPreference = 'Stop'

$pidFile = Join-Path $PSScriptRoot '.backend.pid'
$logFile = Join-Path $PSScriptRoot 'backend.log'
$errFile = Join-Path $PSScriptRoot 'backend.err.log'

function Test-HttpOk([string]$Url) {
  try {
    $res = Invoke-WebRequest -UseBasicParsing -Uri $Url -TimeoutSec 2
    return $res.StatusCode -ge 200 -and $res.StatusCode -lt 300
  } catch {
    return $false
  }
}

$healthUrl = "http://localhost:$Port/actuator/health"
if (Test-HttpOk $healthUrl) {
  Write-Host "Backend already running on $healthUrl" -ForegroundColor Green
  exit 0
}

$listen = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Where-Object { $_.State -eq 'Listen' }
if ($listen) {
  # Something is already bound to the port. Give it a moment to become healthy
  # (common when the backend is still starting up in another terminal/task).
  for ($i = 0; $i -lt 15; $i++) {
    Start-Sleep -Seconds 1
    if (Test-HttpOk $healthUrl) {
      Write-Host "Backend became ready on $healthUrl" -ForegroundColor Green
      exit 0
    }
  }

  $procId = $listen | Select-Object -First 1 -ExpandProperty OwningProcess
  $proc = Get-Process -Id $procId -ErrorAction SilentlyContinue
  Write-Host "Port $Port is already in use by PID $procId ($($proc.ProcessName))." -ForegroundColor Yellow
  Write-Host "If that's the backend, stop the running instance; otherwise change backend port in backend/src/main/resources/application.yml" -ForegroundColor Yellow
  exit 1
}

Write-Host "Starting backend on port $Port..." -ForegroundColor Cyan
Set-Location "$PSScriptRoot"

# Build a bootable jar (more stable than keeping mvn spring-boot:run alive).
# Use clean to avoid Windows rename failures when a previous *.jar.original exists.
& mvn -DskipTests -q clean package

$jar = Get-ChildItem -LiteralPath (Join-Path $PSScriptRoot 'target') -Filter '*.jar' |
  Where-Object { $_.Name -notlike '*-plain.jar' } |
  Sort-Object LastWriteTime -Descending |
  Select-Object -First 1

if (-not $jar) {
  Write-Host 'Could not find built jar under backend/target. Build failed?' -ForegroundColor Red
  exit 1
}

try {
  if (Test-Path $logFile) { Remove-Item -Force $logFile }
} catch {
  # ignore
}

try {
  if (Test-Path $errFile) { Remove-Item -Force $errFile }
} catch {
  # ignore
}

$proc = Start-Process -FilePath 'java' -ArgumentList @(
  '-jar',
  $jar.FullName,
  "--server.port=$Port"
) -WorkingDirectory $PSScriptRoot -PassThru -RedirectStandardOutput $logFile -RedirectStandardError $errFile

Set-Content -LiteralPath $pidFile -Value $proc.Id -Encoding ascii

# Wait briefly for health.
for ($i = 0; $i -lt 20; $i++) {
  Start-Sleep -Seconds 1
  if (Test-HttpOk $healthUrl) {
    $listen = Get-NetTCPConnection -LocalPort $Port -ErrorAction SilentlyContinue | Where-Object { $_.State -eq 'Listen' } | Select-Object -First 1
    if ($listen) {
      Set-Content -LiteralPath $pidFile -Value $listen.OwningProcess -Encoding ascii
      Write-Host "Backend ready on $healthUrl (PID $($listen.OwningProcess))" -ForegroundColor Green
    } else {
      Write-Host "Backend ready on $healthUrl (PID $($proc.Id))" -ForegroundColor Green
    }
    exit 0
  }
}

Write-Host "Backend process started (PID $($proc.Id)), but health did not become ready yet." -ForegroundColor Yellow
Write-Host "Check logs: $logFile" -ForegroundColor Yellow
Write-Host "Check errors: $errFile" -ForegroundColor Yellow
exit 0
