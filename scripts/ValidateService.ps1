Write-Output "Running ValidateService script"

# Replace this with your JAR file name
$jarFileName = "ussd.jar"

# Get the list of Java processes
$javaProcesses = Get-Process -Name java -ErrorAction SilentlyContinue

if ($null -eq $javaProcesses) {
    Write-Output "No Java processes are running."
    exit 1
}

# Check if any Java process is running your JAR file
$processFound = $false
foreach ($process in $javaProcesses) {
    $commandLine = (Get-CimInstance Win32_Process -Filter "ProcessId = $($process.Id)").CommandLine
    if ($commandLine -match $jarFileName) {
        Write-Output "Java process running $jarFileName is found."
        $processFound = $true
        break
    }
}

if (-not $processFound) {
    Write-Output "Java process running $jarFileName is not found."
    exit 1
}

Write-Output "Java process running $jarFileName is running."
exit 0
