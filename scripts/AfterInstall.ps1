# AfterInstall.ps1
Write-Host "Running AfterInstall script"

# Example: Install dependencies
# Add any commands to install required dependencies or perform configuration here

# Example: Set environment variables (if needed)
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Path\To\Java', [System.EnvironmentVariableTarget]::Machine)
