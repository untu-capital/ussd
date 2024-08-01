Write-Host "Running AfterInstall script"

# Set JAVA_HOME environment variable
$javaHome = "C:\jdk-17"
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', $javaHome, [System.EnvironmentVariableTarget]::Machine)
Write-Host "JAVA_HOME set to $javaHome"

# Ensure the destination directory exists
$destinationPath = "C:\deployments\ussd"
if (-not (Test-Path $destinationPath)) {
    Write-Host "Creating destination directory: $destinationPath"
    New-Item -ItemType Directory -Path $destinationPath
}

# Define the source and destination JAR file paths
$sourceJarFile = "ussd.jar"
$destinationJarFile = "$destinationPath\ussd.jar"

# Log the copying of the new JAR file
Write-Host "Copying new JAR file from $sourceJarFile to $destinationJarFile"

# Copy the new JAR file to the destination, overwriting the old file if it exists
Copy-Item -Path $sourceJarFile -Destination $destinationJarFile -Force

Write-Host "AfterInstall script completed."
