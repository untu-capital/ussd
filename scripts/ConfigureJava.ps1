
$javaHome = "C:\jdk-17"
$env:JAVA_HOME = $javaHome
$env:PATH = "$javaHome\bin;$env:PATH"

java -version
