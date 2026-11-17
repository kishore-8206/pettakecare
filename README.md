# PetCare – Smart Pet Take Care Management System

**Full-Stack Architecture:** React 19 + TypeScript + Vite Frontend | Java Spring Boot 3 (Spring Web, Spring Data JPA, Spring Security, JWT, BCrypt) Backend | MySQL 8 Database

---

## 1. Key Upgrade Highlights

- **Removed Smart Live Monitoring:** Simulated heart rate, temperature, activity telemetry, and simulated live camera feeds have been completely removed and replaced with **Pet Health & Care Management** (`/health-care` & `GET /api/health-care`), aggregating real vaccination, medication, feeding compliance, allergy, and clinical notes records.
- **Spring Boot + MySQL Backend (`backend/`):** Complete REST API layer with Spring Security, stateless JWT authentication, BCrypt password hashing, role-based access control (`admin`, `doctor`, `staff`, `owner`), automatic care notifications, 18% GST invoice & payment processing, and Excel `.xlsx` reporting.
- **MySQL Relational Schema (`database/`):** Normalized schema across 14 core tables (`users`, `pet_owners`, `doctors`, `staff`, `pets`, `boarding_bookings`, `feeding_schedules`, `medications`, `vaccinations`, `grooming_appointments`, `medical_records`, `invoices`, `invoice_items`, `payments`, `notifications`).
- **Preserved Existing Frontend:** All existing React UI, Tailwind styling, sidebar navigation, role portals, dashboards, INR formatting, and Excel exports remain intact.

---

## 2. Project Structure

```
PetCare/
├── src/                        # Existing React 19 + TypeScript + Vite frontend
├── public/
├── package.json
├── vite.config.ts
├── backend/                    # Java Spring Boot 3 REST API
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/petcare/
│       │   ├── PetCareApplication.java
│       │   ├── config/         # CORS & DataInitializer
│       │   ├── security/       # JWT filter, JwtUtil, SecurityConfig, UserDetails
│       │   ├── entity/         # JPA entities mapped to MySQL tables
│       │   ├── dto/            # Frontend-compatible request/response DTOs
│       │   ├── repository/     # Spring Data JPA repositories
│       │   ├── service/        # Business logic, RBAC, reminders, Excel export
│       │   └── controller/     # REST controllers + GlobalExceptionHandler
│       ├── main/resources/
│       │   └── application.properties
│       └── test/java/com/petcare/
│           └── PetCareApiTests.java
├── database/
│   ├── schema.sql              # MySQL DDL for all tables, PKs, FKs, indexes
│   └── sample-data.sql         # Sample BCrypt accounts & initial records
└── README.md
```

---

## 3. Sample Login Credentials (All 4 Roles)

| Role | Portal Route | Email | Password |
|---|---|---|---|
| **Admin** | `/admin/login` | `admin@petcare.com` | `admin123` |
| **Doctor / Veterinarian** | `/doctor/login` | `doctor@petcare.com` | `doctor123` |
| **Care Staff** | `/staff/login` | `staff@petcare.com` | `staff123` |
| **Pet Owner** | `/owner/login` | `owner@petcare.com` | `owner123` |

*Public registration (`/api/auth/register`) creates **Pet Owner** accounts only. **Doctor** and **Staff** accounts are provisioned by **Admin** via `POST /api/users`.*

---

## 4. Database Setup (MySQL 8.x)

1. Install **MySQL Server 8.x** and **MySQL Workbench** (or MySQL CLI).
2. Run the schema and sample data scripts in order:

```powershell
mysql -u root -p < database\schema.sql
mysql -u root -p < database\sample-data.sql
```

3. Update `backend/src/main/resources/application.properties` (or set environment variables `DB_USERNAME` and `DB_PASSWORD`) to match your local MySQL credentials:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/petcare_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}
```

---

## 5. Running Locally on Windows

### Prerequisites
- **Java JDK 17+** (`java -version`)
- **Apache Maven 3.9+** (`mvn -version`)
- **MySQL Server 8.x** running on port `3306`
- **Node.js 20+ LTS** & **npm** (`node -v`, `npm -v`)

### Terminal 1 – Start Spring Boot Backend (Port 8080)
```powershell
cd backend
mvn clean spring-boot:run
```
Run backend integration tests:
```powershell
cd backend
mvn test
```

### Terminal 2 – Start React Frontend (Port 3000)
```powershell
npm install
npm run dev
```
Open `http://localhost:3000` in your browser.

---

## 6. REST API Endpoints Reference

| Module | Endpoints | Access |
|---|---|---|
| **Auth** | `POST /api/auth/login`, `POST /api/auth/register`, `GET /api/auth/me` | Public (`login`/`register`), Authenticated (`me`) |
| **Users** | `GET /api/users`, `GET /api/users/{id}`, `POST /api/users`, `DELETE /api/users/{id}` | Admin (GET allowed for Doctor/Staff lookup) |
| **Pets** | `GET /api/pets`, `GET /api/pets/{id}`, `POST /api/pets`, `PUT /api/pets/{id}`, `DELETE /api/pets/{id}` | Owner (own pets only), Admin/Doctor/Staff (all) |
| **Health & Care** | `GET /api/health-care` | Owner (own pets), Admin/Doctor/Staff (all) |
| **Boarding** | `GET /api/bookings`, `POST /api/bookings`, `PUT/PATCH /api/bookings/{id}`, `DELETE /api/bookings/{id}` | Owner (create/view own), Staff/Admin (check-in/out) |
| **Feeding** | `GET /api/feedings`, `POST /api/feedings`, `PATCH /api/feedings/{id}/toggle`, `DELETE /api/feedings/{id}` | Owner (read-only), Staff/Doctor/Admin (manage) |
| **Medications** | `GET /api/medications`, `POST /api/medications`, `PATCH /api/medications/{id}/toggle`, `DELETE /api/medications/{id}` | Owner (read-only), Doctor/Staff/Admin (manage) |
| **Vaccinations** | `GET /api/vaccinations`, `POST /api/vaccinations`, `PUT /api/vaccinations/{id}`, `DELETE /api/vaccinations/{id}` | Owner (read-only), Doctor/Admin/Staff (manage) |
| **Grooming** | `GET /api/grooming`, `POST /api/grooming`, `PATCH /api/grooming/{id}/status`, `DELETE /api/grooming/{id}` | Owner (book/view), Staff/Admin (status updates) |
| **Medical Records** | `GET /api/medical-records`, `POST /api/medical-records`, `DELETE /api/medical-records/{id}` | Admin/Doctor/Staff only (Owners forbidden) |
| **Invoices & Payments** | `GET /api/invoices`, `POST /api/invoices`, `POST /api/invoices/{id}/pay`, `GET /api/payments` | Owner (own invoices/pay), Admin/Staff (create/all) |
| **Notifications** | `GET /api/notifications`, `POST /api/notifications`, `PATCH /api/notifications/{id}/read`, `PATCH /api/notifications/read-all`, `DELETE /api/notifications/{id}` | Role-scoped |
| **Reports** | `GET /api/reports/dashboard`, `GET /api/reports/export` | Dashboard (All), Excel `.xlsx` (Admin/Doctor/Staff) |

---

## 7. Frontend Integration Notes (Awaiting Approval Before Modifying Frontend Services)

Because the original React frontend uses synchronous `localStorage` calls in `src/services.ts` (`DataService.getPets()` etc. called directly inside `useState(...)`), connecting the UI to live Spring Boot HTTP endpoints requires converting `DataService` to `async` `fetch()` calls with JWT Bearer tokens and updating components to load data via `useEffect`. Those required frontend changes are documented in full for approval prior to modifying `src/services.ts`, `src/AuthContext.tsx`, `src/Navigation.tsx`, `src/pages/Dashboards.tsx`, and `src/pages/Modules.tsx`.
