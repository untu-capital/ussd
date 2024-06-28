# ValidateService.ps1

Write-Output "Running ValidateService script"

# Replace this with your JAR file name (without extension)
$applicationName = "ussd"

# Check if the process is running
if (Get-Process -Name $applicationName -ErrorAction SilentlyContinue) {
    Write-Output "Process $applicationName is running."
    exit 0
} else {
    Write-Output "Process $applicationName is not running or does not exist."
    exit 1
}
