@echo off
rem Compila el juego y las pruebas y corre todas las pruebas de unidad del dominio.
setlocal
cd /d "%~dp0"
call "%~dp0compilar.bat" || exit /b 1
"%JAVA_BIN%java" -cp "bin;lib\*" EjecutarPruebas
endlocal
