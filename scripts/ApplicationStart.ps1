# Assuming your Java application is a JAR file located in C:\YourAppDirectory
$javaHome = "C:\jdk-17"
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"


Start-Process -FilePath "$env:JAVA_HOME\bin\java.exe" -ArgumentList "-jar", "C:\deployments\ussd\ussd.jar"
