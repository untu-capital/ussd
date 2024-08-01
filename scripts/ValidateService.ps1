Write-Output "Running ValidateService script"

$jarFilePath = "C:\deployments\ussd\ussd.jar"
$maxRetries = 5
$delay = 10
$found = $false

for ($i = 1; $i -le $maxRetries; $i++) {
    Write-Output "Attempt ${i}: Checking for Java process running $jarFilePath..."

    # Get Java processes
    $javaProcesses = Get-CimInstance Win32_Process | Where-Object { $_.CommandLine -like "*java*" }

    # Check each Java process
    foreach ($process in $javaProcesses) {
        if ($process.CommandLine -like "*-jar $jarFilePath*") {
            Write-Output "Java process running $jarFilePath found."
            $found = $true
            break
        }
    }

    if ($found) {
        break
    } else {
        Write-Output "Java process running $jarFilePath is not found. Retrying in $delay seconds..."
        Start-Sleep -Seconds $delay
    }
}

if (-not $found) {
    Write-Output "Java process running $jarFilePath is not found after $maxRetries attempts."
    exit 1
} else {
    Write-Output "Java process running $jarFilePath successfully found."
    exit 0
}
