# College TicketDesk

Support-ticket system for a college: students raise tickets, staff/admin handle them.
Java 17, Spring Boot 3.2.5, Spring Security, Spring Data JPA, Thymeleaf, MySQL, Chart.js.

## Run (MySQL)
1. Start MySQL (root / BORN2007). The database `ticketdesk` is created automatically.
2. Double-click `run-mysql.bat` or run `mvn spring-boot:run`.
3. Open http://localhost:8082/login

No MySQL? Use `run-h2.bat` (in-memory database).
Other port: `mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=9090`

## Demo accounts
| Role    | Username | Password   |
|---------|----------|------------|
| Admin   | admin    | admin123   |
| Staff   | staff    | staff123   |
| Student | student  | student123 |

## Features
- Login, student self-registration (BCrypt passwords, Spring Security)
- Create tickets (category, priority), list with status filter
- Ticket detail with comments; staff/admin can change status and assign tickets
- Students only see their own tickets; staff/admin see all
- Admin user management (create users, enable/disable)
- Dashboard with status cards, pie chart (by status), bar chart (by category)
"# shriram" 
