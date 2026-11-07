@echo off
REM Lance TELEMETRA mobile depuis le bon dossier (mobile-app)
cd /d "%~dp0mobile-app"
echo.
echo  TELEMETRA - Demarrage Expo (mobile-app)
echo  NE PAS lancer "npx expo start" depuis telemetra\
echo.
call npm run start:clear
