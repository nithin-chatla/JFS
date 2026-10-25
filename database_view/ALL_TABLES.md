# 🗄️ Database Architecture & Initial Table State
**Database:** `hostel_db` (Persistent H2) / `hostel_complaints_db` (MySQL)

---

## 1. Table: `APP_USERS`
*Stores all registered Residents (Students), Wardens (Admin), and Maintenance Staff.*

| ID | Full Name | Email | Role | Phone | Block | Room | Bed | Department / Specialization |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `1` | Nithin Chatla (Chief Warden) | `nithin@gmail.com` | `ROLE_ADMIN` | `+91 98765 43210` | N/A | N/A | N/A | Hostel Administration |
| *--* | *(Technicians hired dynamically by Admin via `/admin/staff`)* | *...* | `ROLE_STAFF` | *...* | N/A | N/A | N/A | *Electrician, Plumber, etc.* |
| *--* | *(Residents register dynamically via `/register`)* | *...* | `ROLE_STUDENT` | *...* | *...* | *...* | *...* | *...* |

---

## 2. Table: `COMPLAINTS`
*Stores all resident maintenance work orders, status progression, technician assignments, and ratings.*

| Field | Type | Description |
| :--- | :--- | :--- |
| `id` | BIGINT PK | Auto-increment unique internal ID |
| `complaint_number` | VARCHAR(32) | Public ticket reference code (`HCMS-yyMMddHHmmss-XXX`) |
| `title` | VARCHAR(255) | Brief summary of maintenance issue |
| `category` | VARCHAR(50) | `ELECTRICAL`, `PLUMBING`, `CARPENTRY`, `INTERNET_WIFI`, `HOUSEKEEPING`, `CIVIL_GENERAL` |
| `priority` | VARCHAR(50) | `LOW`, `MEDIUM`, `HIGH`, `URGENT` |
| `status` | VARCHAR(50) | `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `REOPENED`, `REJECTED` |
| `student_id` | BIGINT FK | References student resident in `APP_USERS(id)` |
| `staff_id` | BIGINT FK | References assigned technician in `APP_USERS(id)` |
| `rating` | INT | Student review score (1–5 Stars) |
| `feedback` | TEXT | Student written review comments |

---

## 3. Table: `COMPLAINT_LOGS` (Audit History)
*Tracks every step, status transition, remarks, and user action for full transparency.*

| Log ID | Complaint Ticket | Action | Performed By | Details / Remarks | Timestamp |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `PK` | `FK -> COMPLAINTS` | `LODGED`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `FEEDBACK_SUBMITTED`, `REOPENED` | Actor Name (Role) | Action notes & transition details | `DATETIME` |

---

## 4. Table: `NOTICES`
*Stores official hostel bulletins and announcements.*

| ID | Title | Category | Published By | Active |
| :--- | :--- | :--- | :--- | :--- |
| `1` | Campus Wi-Fi Upgradation & Infrastructure Maintenance | `Maintenance` | Chief Warden Office | `TRUE` |
| `2` | Water Tank Cleaning Schedule - Block A, B & Girls Hostels | `Maintenance` | Hostel Maintenance Cell | `TRUE` |
| `3` | Monthly Hostel Mess Committee Review Meeting | `Mess & Dining` | Chief Warden Office | `TRUE` |
