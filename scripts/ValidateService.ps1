# ValidateService.ps1

Write-Output "Running ValidateService script"

$serviceName = "MyUSSDService"  # Replace with the actual service name

# Attempt to retrieve the service information
try {
    $service = Get-Service -Name $serviceName -ErrorAction Stop
}
catch {
    Write-Output "Failed to retrieve service information for $serviceName: $_"
    exit 1
}

# Check if the service exists
if (-not $service) {
    Write-Output "Service $serviceName does not exist or cannot be found."
    exit 1
}

# Check if the service is running
if ($service.Status -ne "Running") {
    Write-Output "Service $serviceName is not running (Current status: $($service.Status))."
    exit 1
}

Write-Output "Service $serviceName is running."
exit 0
