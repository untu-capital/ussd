# ValidateService.ps1
Write-Host "Running ValidateService script"

# Example: Validate the Java application service
$serviceName = "ussd"
$service = Get-Service -Name $serviceName -ErrorAction SilentlyContinue

if ($service -and $service.Status -eq 'Running') {
    Write-Host "Service $serviceName is running."
    # Add any additional validation logic here, such as checking logs or endpoints
} else {
    Write-Host "Service $serviceName is not running or does not exist."
    Exit 1
}
