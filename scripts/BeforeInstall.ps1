
Write-Host "Running BeforeInstall script"

$serviceName = "ussd"
$service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue

if ($service -and $service.Status -eq 'Running') {
    Write-Host "Stopping service $serviceName..."
    Stop-Service -Name $serviceName -Force
    Start-Sleep -Seconds 10
} else {
    Write-Host "Service $serviceName is not running or does not exist."
}
