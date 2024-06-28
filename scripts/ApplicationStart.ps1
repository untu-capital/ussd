# ApplicationStart.ps1

# Ensure the directory exists
$deployDir = "C:\deployments\ussd"
if (-Not (Test-Path $deployDir)) {
    New-Item -Path $deployDir -ItemType Directory
}

# Start the Java application using Java 17
Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "C:\deployments\ussd\ussd.jar" -NoNewWindow -PassThru
