
Write-Output "Running ValidateService script"

$jarFilePath = "C:\deployments\ussd\ussd.jar"

$javaProcess = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*$jarFilePath*" }

if ($javaProcess) {
    Write-Output "Java process running $jarFilePath found."
    exit 0
} else {
    Write-Output "Java process running $jarFilePath is not found."
    exit 1
}
