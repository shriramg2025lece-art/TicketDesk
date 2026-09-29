@echo off
REM College TicketDesk on http://localhost:8082 using MySQL (root / BORN2007)
REM Start the MySQL80 service first (Win+R -> services.msc)
where mvn >nul 2>nul
if %errorlevel%==0 (mvn spring-boot:run) else (call mvnw.cmd spring-boot:run)
pause
