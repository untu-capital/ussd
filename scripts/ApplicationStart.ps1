# ApplicationStart.ps1
Write-Host "Running ApplicationStart script"

# Example: Start the Java application service
$serviceName = "YourJavaAppService"
$service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue

if ($service -and $service.Status -ne 'Running') {
    Write-Host "Starting service $serviceName..."
    Start-Service -Name $serviceName
    Start-Sleep -Seconds 10
} else {
    Write-Host "Service $serviceName is already running."
}
