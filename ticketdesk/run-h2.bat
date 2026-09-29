@echo off
REM College TicketDesk on http://localhost:8082 using the in-memory H2 database (no MySQL needed)
where mvn >nul 2>nul
if %errorlevel%==0 (mvn spring-boot:run -Dspring-boot.run.profiles=h2demo) else (call mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2demo)
pause
