Write-Host "Running BeforeInstall script"

# Path to the old JAR file
$jarFilePath = "C:\deployments\ussd\ussd.jar"

# Stop existing Java processes using the old JAR file
$javaProcess = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*$jarFilePath*" }

if ($javaProcess) {
    Write-Host "Stopping Java process running $jarFilePath..."
    Stop-Process -Id $javaProcess.ProcessId -Force
    Start-Sleep -Seconds 10
} else {
    Write-Host "No running Java process found for $jarFilePath."
}

# Remove the old JAR file if it exists
if (Test-Path $jarFilePath) {
    Write-Host "Removing old JAR file: $jarFilePath"
    Remove-Item -Path $jarFilePath -Force
} else {
    Write-Host "Old JAR file $jarFilePath not found."
}

Write-Host "BeforeInstall script completed."
