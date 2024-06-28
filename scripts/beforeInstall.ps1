Write-Output "Running BeforeInstall script"

$process = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($process) {
    Stop-Process -Name "java" -Force
}

Write-Output "BeforeInstall script completed"