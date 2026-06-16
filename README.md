# Sukrtya Siwan

Backend API for maternal health data collection in Siwan district. CHO and field staff register pregnant women, capture BASIC and follow-up visits (ANC1–ANC4, PNC), while admins configure geography, staff rosters, and survey forms from Excel—without redeploying application code.

**Production API:** `https://sukrtyasiwanapi.psmri.in`

---

## Features

### Field data collection
- JWT login for **Admin** and **CHO / data collectors**
- Facility-scoped access (users only see mapped HWCs)
- **ASHA → beneficiary list** workflow
- Dynamic forms rendered from server schema (questions, dropdowns, skip logic, computed dates)
- **BASIC** registration once per woman; **ANC / PNC** as separate visits
- Duplicate protection (same mobile or ABHA at a facility)
- All form answers stored in a **single JSONB table** (scalable for new survey types)

### Admin configuration
- Master Excel import: district, block, facility, CHO/ANM/ASHA roster
- Form catalog CRUD (BASIC, ANC1–4, PNC, custom forms)
- Question Excel import (`Question Master` + `OptionMaster` sheets)
- Soft delete (deactivate) or permanent delete of forms (with safety checks)
- Dashboard summary counts

### Reporting
- SQL views over JSONB answers for tabular export (`v_form_answers_long`, per-form convenience views)
- Works automatically for newly added forms (no view update required for long format)

---

## Tech stack

| Layer | Technology |
|-------|------------|
| Runtime | Java 17 |
| Framework | Spring Boot 3.5 |
| Database | PostgreSQL |
| Migrations | Flyway |
| Auth | JWT (HS256) |
| Excel parsing | Apache POI |
| API docs / testing | Postman collection in `postman/` |

---

## Prerequisites

- **JDK 17**
- **Maven 3.9+**
- **PostgreSQL 14+** (local or RDS)

---

## Quick start (local)

### 1. Database

Create a database, for example:

```sql
CREATE DATABASE sukrtya_siwan;
```

### 2. Configure connection

Set datasource in `src/main/resources/application.properties` or via environment variables:

| Variable | Description |
|----------|-------------|
| `DB_IP` | Database host |
| `DB_PORT` | Port (default `5432`) |
| `DB_NAME` | Database name |
| `DB_USERNAME` | DB user |
| `DB_PASSWORD` | DB password |

Example `application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://${DB_IP:localhost}:${DB_PORT:5432}/${DB_NAME:sukrtya_siwan}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:postgres}
```

For AWS RDS with SSL, append `?sslmode=require` to the JDBC URL.

Flyway runs automatically on startup (`spring.flyway.enabled=true`).

### 3. Run the application

```bash
mvn spring-boot:run
```

Or build and run the JAR:

```bash
mvn clean package -DskipTests
java -jar target/sukrtya-siwan-0.0.1-SNAPSHOT.jar
```

Default port: **8080** (overridable with `PORT` or `SERVER_PORT`).

### 4. Default users

| User | Username | Default password |
|------|----------|------------------|
| Admin | `admin` | `Admin@123` |
| CHO (from master import) | 10-digit mobile | `User@123` |

Change these in production via `application.properties` (`app.seed.*`) and secure `app.jwt.secret`.

### 5. Health check

```
GET http://localhost:8080/actuator/health
```

---

## Docker

```bash
docker compose up --build
```

Set `DB_IP`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD` in your environment or `.env` before starting.

---

## API overview

### Authentication
| Method | Path | Auth |
|--------|------|------|
| POST | `/api/auth/login` | Public |
| GET | `/api/auth/me` | JWT |

### Field user (CHO)
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/me/master-context` | Facilities, blocks, districts, staff (ASHA list) |
| GET | `/api/asha/{ashaAssignmentId}/beneficiaries` | Pregnant women under one ASHA |
| POST | `/api/asha/{ashaAssignmentId}/beneficiaries` | Register new woman (BASIC) |
| GET | `/api/beneficiaries/{id}` | Beneficiary detail + form status grid |
| PUT | `/api/beneficiaries/{id}` | Update BASIC |
| GET | `/api/forms/{code}/schema` | Dynamic form layout (optional `?beneficiaryId=`) |
| GET | `/api/beneficiaries/{id}/forms/{formCode}` | Read saved form response |
| POST | `/api/beneficiaries/{id}/forms/{formCode}` | Submit/update ANC, PNC, etc. |

### Admin
| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/admin/dashboard/summary` | Counts dashboard |
| POST | `/api/admin/masters/import-excel` | Geography + staff Excel |
| GET/POST/PUT | `/api/admin/forms` | Form catalog CRUD |
| DELETE | `/api/admin/forms/{code}` | Soft delete (`active=false`) |
| DELETE | `/api/admin/forms/{code}?permanent=true` | Hard delete (if no responses) |
| POST | `/api/admin/forms/import-excel` | Question + option Excel |
| GET | `/api/admin/forms/option-sets` | Dropdown option sets |

Full runnable examples: **`postman/sukrtya-siwan.postman_collection.json`**

---

## Typical workflows

### A. Admin setup (one-time / when Excel changes)

1. `POST /api/admin/masters/import-excel` — roster workbook  
2. `POST /api/admin/forms` — create catalog rows: BASIC, ANC1, ANC2, ANC3, ANC4, PNC  
3. `POST /api/admin/forms/import-excel` — `SukrtyaQuestion.xlsx` (questions + options)  
4. `GET /api/admin/forms` — verify forms and question counts  

Re-importing question Excel **replaces** questions per form and bumps `form.version`. **No manual DB cleanup required.**

### B. CHO daily use

1. `POST /api/auth/login`  
2. `GET /api/me/master-context` → pick ASHA `assignmentId`  
3. `GET /api/asha/{id}/beneficiaries`  
4. **New woman:** `GET /api/forms/BASIC/schema` → `POST /api/asha/{id}/beneficiaries`  
5. **ANC visit:** `GET /api/forms/ANC1/schema?beneficiaryId=` → `POST /api/beneficiaries/{id}/forms/ANC1`  

### C. Adding a new form (e.g. PNC)

1. Create catalog entry (`POST /api/admin/forms` with `sequence` and `prerequisiteCode`)  
2. Set **Form** column in Excel for those question rows  
3. Re-upload question Excel  

---

## Excel files

| File | Purpose |
|------|---------|
| Master roster `.xlsx` | District, block, facility, CHO/ANM/ASHA |
| `SukrtyaQuestion.xlsx` | `Question Master` + `OptionMaster` sheets |

Utility scripts (optional, in `scripts/`):

- `preview_form_import.py` — dry-run form parsing before upload  
- `fill_form_column.py` — populate Form column in Excel  
- `inspect_question_xlsx.py` — dump sheet headers and rows  

---

## Database

### Main tables

| Area | Tables |
|------|--------|
| Login | `portal_role`, `portal_user`, `portal_user_facility` |
| Geography | `district`, `block`, `facility` |
| Staff | `health_worker`, `facility_worker_assignment` |
| Forms config | `form`, `form_question`, `option_set`, `option_value` |
| Data | `beneficiary`, `beneficiary_form_response` |

### Reporting views

| View | Purpose |
|------|---------|
| `v_form_answers_long` | All forms — one row per answer key (auto works for new forms) |
| `v_basic_answers_long`, `v_anc1_answers_long`, … `v_pnc_answers_long` | Filtered convenience views |

Example:

```sql
SELECT beneficiary_code, beneficiary_full_name, form_code, question_code, answer_text
FROM v_form_answers_long
WHERE form_code = 'BASIC'
ORDER BY beneficiary_id, question_code;
```

### Flyway migrations

Located in `src/main/resources/db/migration/` (`V1` … `V7`).

---

## Form submission rules

| Action | Endpoint |
|--------|----------|
| Register new woman (BASIC) | `POST /api/asha/{ashaAssignmentId}/beneficiaries` |
| Edit BASIC | `PUT /api/beneficiaries/{id}` |
| Submit ANC / PNC / other follow-up | `POST /api/beneficiaries/{id}/forms/{formCode}` |

Answer keys use question **codes** from the schema (e.g. `BASIC_16`, `ANC1_25`), not Excel row numbers.

---

## Project structure

```
src/main/java/com/sukrtya/siwan/
├── auth/           # JWT login
├── beneficiary/    # Pregnant women + form responses
├── config/         # Security, CORS, web
├── forms/          # Form catalog, Excel import, schema
├── master/         # Geography + staff import
├── portal/         # Users and roles
└── web/            # REST controllers

postman/            # API collection for testing
scripts/            # Excel helper scripts
```

---

## Vision & future roadmap

Sukrtya is designed as a **configurable health data platform**, not a one-time ANC application. The current release already proves the core idea: forms, questions, and master data can be changed through Excel and admin APIs without rewriting the mobile or web client. The roadmap extends that model so **programs, users, and business rules** are all admin-managed.

### What is already dynamic today

| Capability | How it works now |
|------------|------------------|
| **Survey forms** | Admin creates form catalog (BASIC, ANC1–4, PNC, …) and uploads Question Master Excel |
| **Questions & options** | Text, number, date, single/multi choice, skip logic, computed dates (LMP + N days) |
| **New survey types** | Add a form code → set Form column in Excel → re-import (no new DB tables for answers) |
| **Geography & staff** | District / block / facility / CHO / ANM / ASHA from master Excel |
| **User access scope** | CHO mapped to facilities via `portal_user_facility` |
| **Reporting** | SQL views flatten JSONB answers for any form |

### Where Sukrtya is heading

#### 1. Fully dynamic forms (zero-code surveys)

**Goal:** Any new health program (PNC, delivery, immunization, NCD screening) is launched by configuration only.

| Future enhancement | Benefit |
|--------------------|---------|
| Admin UI for form builder (instead of Excel-only) | Faster edits, validation, preview on screen |
| Form versioning with diff history | See what changed between Excel uploads |
| Conditional sections & multi-page wizards | Complex flows without frontend hardcoding |
| Local language labels (Hindi/regional) from config | Same schema drives all locales |
| Offline-first mobile sync | ASHA works without continuous network |

The frontend always calls **`GET /api/forms/{code}/schema`** and submits answers to the same response APIs—the UI never hardcodes field lists.

#### 2. Configurable user logins & roles

**Goal:** Admins define who can log in, what they see, and what they can do—without developer involvement.

| Future enhancement | Benefit |
|--------------------|---------|
| Admin screen to create / disable portal users | No dependency on Excel import for one-off accounts |
| Custom roles beyond ADMIN and DATA_COLLECTOR | e.g. Block coordinator, district reviewer, read-only analyst |
| Facility / block / district scope rules | Fine-grained “see only my block” access |
| Password reset, OTP login, ABHA-linked identity | Align with national digital health standards |
| Audit log (who submitted / edited what, when) | Accountability for field data |

Today CHO users are seeded from the master roster; the platform model (`portal_user` + `portal_user_facility` + JWT) is ready to support richer role matrices.

#### 3. Business logic configured dynamically

**Goal:** Rules that today live in Excel columns (`isMandatory`, `min`/`max`, `skipAnswer`, `Remarks` for computed fields) expand into a **rule engine** admins can maintain.

| Rule type | Example | Status |
|-----------|---------|--------|
| Mandatory fields | “LMP is required on BASIC” | Live (from Excel) |
| Min / max validation | Age 15–49, BP range | Live (from Excel) |
| Option membership | Dropdown value must be in Option Master | Live |
| Skip logic | If answer = No, jump to question 30 | Live (from Excel) |
| Computed fields | EDD = LMP + 280; ANC due = LMP + 84 | Live (from Excel) |
| **Prerequisite visits** | Enable PNC only after ANC4 submitted | Metadata today; enforce in API next |
| **Cross-field rules** | If HRP flagged, show extra questions | Planned |
| **Alerts & referrals** | High BP → flag for CHO review | Planned |
| **Workflow states** | DRAFT → SUBMITTED → VERIFIED | Partial (DRAFT/SUBMITTED exist) |

Business logic stays **data-driven**: rules are stored with `form_question` (or a future `form_rule` table), evaluated server-side on submit, and returned in the schema so the client knows what to show.

#### 4. One platform, many programs

Because all answers sit in **`beneficiary_form_response`** (JSONB keyed by question code), Sukrtya can support:

- Maternal health (current focus)
- Child immunization registers
- NCD / hypertension camps
- Outbreak line lists
- Any repeating visit model (Form 1 → Form 2 → Form 3)

Each program = new rows in **`form`** + question config. The beneficiary (or “subject”) layer can generalize to other entity types over time while keeping the same response pattern.

#### 5. Analytics & integrations

| Direction | Description |
|-----------|-------------|
| **Wide SQL / BI exports** | Dynamic pivot views per form; Power BI / Metabase dashboards |
| **DHIS2 / HMIS export** | Map question codes to national indicators |
| **SMS / WhatsApp reminders** | ANC due date alerts from LMP + visit schedule |
| **ABHA / FHIR** | Structured exchange with Ayushman Bharat Digital Mission |

### Architecture principle

```
┌─────────────────────────────────────────────────────────────┐
│  Admin configures: forms, questions, rules, users, masters   │
└────────────────────────────┬────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Sukrtya API: schema + validation + storage (JSONB)          │
└────────────────────────────┬────────────────────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────┐
│  Mobile / Web UI: renders any form from schema (no hardcode) │
└─────────────────────────────────────────────────────────────┘
```

**Configure once, collect everywhere** — that is the long-term product direction for Sukrtya Siwan and sibling deployments.

---

## Configuration reference

| Property | Description |
|----------|-------------|
| `server.port` | HTTP port (`PORT` / `SERVER_PORT`, default 8080) |
| `spring.datasource.*` | PostgreSQL connection |
| `spring.flyway.enabled` | Auto-run migrations (default `true`) |
| `app.jwt.secret` | JWT signing key (use a long random value in production) |
| `app.jwt.access-token-validity-seconds` | Token lifetime (default 28800 = 8h) |
| `app.seed.default-admin-username` | Bootstrap admin username |
| `app.seed.default-admin-password` | Bootstrap admin password |
| `app.seed.cho-collector-default-password` | Default password for CHO users from import |

---

## Security notes (production)

- Set a strong `app.jwt.secret`
- Restrict `/api/admin/**` to authenticated admin users (currently open in dev for Postman)
- Use environment variables for DB credentials—do not commit secrets
- Enable HTTPS at the reverse proxy (Coolify / load balancer)

---

## License

Internal / project-specific — confirm with your organization before external distribution.
