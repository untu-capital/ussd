# ValidateService.ps1

Write-Output "Running ValidateService script"

# Replace this with your JAR file name (without extension)
$jarFilePath = "C:\deployments\ussd\ussd.jar"

# Get the process running the JAR file
$javaProcess = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*$jarFilePath*" }

if ($javaProcess) {
    Write-Output "Java process running $jarFilePath found."
    exit 0
} else {
    Write-Output "Java process running $jarFilePath is not found."
    exit 1
}
