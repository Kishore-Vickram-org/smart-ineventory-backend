param(
  [string]$DataDir = "$(Join-Path $PSScriptRoot 'data')"
)

$ErrorActionPreference = 'Stop'

Write-Host "Resetting H2 database in: $DataDir" -ForegroundColor Cyan

if (-not (Test-Path $DataDir)) {
  Write-Host "Nothing to reset (data directory not found)." -ForegroundColor Yellow
  exit 0
}

$files = Get-ChildItem -Path $DataDir -File -ErrorAction SilentlyContinue
if (-not $files -or $files.Count -eq 0) {
  Write-Host "Nothing to reset (no DB files)." -ForegroundColor Yellow
  exit 0
}

foreach ($f in $files) {
  try {
    Remove-Item -LiteralPath $f.FullName -Force
    Write-Host "Deleted $($f.Name)" -ForegroundColor Green
  } catch {
    Write-Host "Failed to delete $($f.Name): $($_.Exception.Message)" -ForegroundColor Red
    throw
  }
}

Write-Host "Done. Next backend start will create a fresh empty DB." -ForegroundColor Green
