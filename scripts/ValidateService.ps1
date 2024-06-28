# ValidateService.ps1

Write-Output "Running ValidateService script"

$serviceName = "ussd"

# Check if the service exists
if (-not (Get-Service -Name $serviceName -ErrorAction SilentlyContinue)) {
    Write-Output "Service $serviceName does not exist or cannot be found."
    exit 1
}

# Check if the service is running
$status = (Get-Service -Name $serviceName).Status
if ($status -ne "Running") {
    Write-Output "Service $serviceName is not running (Current status: $status)."
    exit 1
}

Write-Output "Service $serviceName is running."
exit 0

