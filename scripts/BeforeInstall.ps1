Write-Host "Running BeforeInstall script"

$jarFilePath = "C:\deployments\ussd\ussd.jar"

# Get the process running the JAR file
$javaProcess = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*$jarFilePath*" }

if ($javaProcess) {
    Write-Host "Stopping Java process running $jarFilePath..."
    Stop-Process -Id $javaProcess.ProcessId -Force
    Start-Sleep -Seconds 10

    # Check if the process has stopped
    $javaProcess = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*$jarFilePath*" }
    if ($javaProcess) {
        Write-Host "Failed to stop Java process running $jarFilePath."
        exit 1
    } else {
        Write-Host "Java process running $jarFilePath stopped successfully."
    }
} else {
    Write-Host "Java process running $jarFilePath is not running or does not exist."
}
