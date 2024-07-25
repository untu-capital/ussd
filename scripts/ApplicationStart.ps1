
$javaHome = "C:\jdk-17"
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"


Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "C:\deployments\ussd\ussd.jar"
