# deploy.ps1

Write-Output "Deploying Java Application"

# Navigate to the directory where the JAR file is deployed
cd C:\deployments\ussd

# Ensure any existing instances of the application are stopped
$process = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($process) {
    Stop-Process -Name "java" -Force
}

# Start the Java application
Start-Process -NoNewWindow -FilePath "java" -ArgumentList "-jar C:\deployments\ussd\ussd.jar"
Write-Output "Java application started"