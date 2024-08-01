# Setting up Java environment
$javaHome = "C:\jdk-17"
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

# Define paths
$jarFilePath = "C:\deployments\ussd\ussd.jar"
$logFilePath = "C:\deployments\ussd\ussd.log"

# Start the JAR file and redirect output to a log file
Write-Host "Starting JAR file $jarFilePath..."
Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -ArgumentList "-jar $jarFilePath" -RedirectStandardOutput $logFilePath -RedirectStandardError $logFilePath -NoNewWindow -PassThru

Write-Host "JAR file $jarFilePath started. Logs are being written to $logFilePath."
