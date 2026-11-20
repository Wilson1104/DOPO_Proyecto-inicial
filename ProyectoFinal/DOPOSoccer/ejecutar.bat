@echo off
rem Compila y abre el juego.
setlocal
cd /d "%~dp0"
call "%~dp0compilar.bat" || exit /b 1
"%JAVA_BIN%java" -cp bin presentacion.DOPOSoccerGUI
endlocal
