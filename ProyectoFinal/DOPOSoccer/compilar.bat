@echo off
rem Compila src y test en la carpeta bin. Usa el JDK de BlueJ si existe; si no, el javac del PATH.
cd /d "%~dp0"
set "JAVA_BIN="
if exist "C:\Program Files\BlueJ\jdk\bin\javac.exe" set "JAVA_BIN=C:\Program Files\BlueJ\jdk\bin\"
if exist bin rmdir /s /q bin
mkdir bin
"%JAVA_BIN%javac" -encoding UTF-8 -d bin -cp "lib\*" src\dominio\*.java src\presentacion\*.java test\*.java test\dominio\*.java
if errorlevel 1 (
    echo No se pudo compilar.
    exit /b 1
)
echo Compilado en bin.
exit /b 0
