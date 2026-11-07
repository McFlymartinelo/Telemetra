# Libere le port Metro (8081) avant de lancer Expo
$ports = 8081, 8082
foreach ($port in $ports) {
  Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue |
    ForEach-Object {
      Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue
      Write-Host "Stopped process $($_.OwningProcess) on port $port"
    }
}
