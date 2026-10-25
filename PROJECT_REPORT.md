# PROJECT REPORT
## HOSTEL COMPLAINT MANAGEMENT SYSTEM (HCMS)
**Course:** Full Stack Java Development  
**Technology Stack:** Java 21/23, Spring Boot 3.3.x, Spring Data JPA / Hibernate, Spring Security (BCrypt), Thymeleaf, Bootstrap Icons, Chart.js, MySQL / H2 Database

---

### 1. ABSTRACT
The **Hostel Complaint Management System (HCMS)** is an end-to-end full-stack web application designed to digitize and automate the complaint registration, tracking, allocation, and resolution lifecycle within collegiate student residential hostels. Traditional manual register-based workflows suffer from lost complaints, lack of accountability, delayed maintenance response, and zero feedback mechanism. HCMS overcomes these limitations by delivering a role-based, transparent web platform featuring three distinct actors: **Students**, **Hostel Wardens (Admin)**, and **Maintenance Staff (Technicians)**.

---

### 2. OBJECTIVES & KEY FEATURES
1. **Student Module**:
   - Self-registration with hostel block, room number, bed number, and academic department.
   - Lodge maintenance complaints across standardized categories (*Electrical, Plumbing, Carpentry, Wi-Fi & IT, Housekeeping, Mess, Security*).
   - Priority tagging (*Low, Medium, High, Urgent*).
   - Real-time ticket tracking with status stages (*Pending &rarr; Assigned &rarr; In Progress &rarr; Resolved &rarr; Reopened*).
   - Post-resolution 5-star rating and feedback submission.
   - Option to reopen tickets if the underlying defect reoccurs.

2. **Warden / Administrator Module**:
   - KPI Dashboard with live count of pending, active, and resolved tickets.
   - Interactive visual charts (Complaint distribution by category, block, and priority).
   - Technician allocation engine with workload balancing.
   - Student and staff directories.
   - Notice Board & Announcement broadcast management.
   - One-click CSV export of maintenance reports for institutional audits.

3. **Maintenance Staff / Technician Module**:
   - Technician work-order portal.
   - View assigned room location, contact phone of the resident, and problem description.
   - Real-time status update with field notes and resolution remarks.

4. **Public Tracking Module**:
   - Unauthenticated quick-lookup tracking via unique complaint ticket number (e.g. `HCMS-202610-101`).

---

### 3. SYSTEM ARCHITECTURE & DESIGN

```
+-------------------------------------------------------------+
|                  Client Layer (Browser)                    |
|    HTML5 / Vanilla CSS Design System / Thymeleaf / Chart.js |
+------------------------------+------------------------------+
                               | HTTP / HTTPS Requests
+------------------------------v------------------------------+
|             Spring Security Layer (RBAC & BCrypt)           |
|      Authentication & Authorization Filters (CSRF, Sessions)|
+------------------------------+------------------------------+
                               |
+------------------------------v------------------------------+
|                  Spring MVC Controller Layer                |
|      AuthController | StudentController | AdminController   |
|                     StaffController                         |
+------------------------------+------------------------------+
                               | DTOs & Validation
+------------------------------v------------------------------+
|                    Business Service Layer                   |
|     ComplaintService | UserService | NoticeService          |
|                 CustomUserDetailsService                    |
+------------------------------+------------------------------+
                               |
+------------------------------v------------------------------+
|                Data Access Layer (Spring Data JPA)          |
|   UserRepository | ComplaintRepository | NoticeRepository   |
|                 ComplaintLogRepository                      |
+------------------------------+------------------------------+
                               | Hibernate ORM
+------------------------------v------------------------------+
|                 Relational Database Storage                 |
|            MySQL (hostel_complaints_db) / H2 DB             |
+-------------------------------------------------------------+
```

---

### 4. DATABASE SCHEMA (ENTITY-RELATIONSHIP)

#### 1. `users` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `full_name` (VARCHAR, Not Null)
- `email` (VARCHAR, Unique, Not Null)
- `password` (VARCHAR, BCrypt Hashed, Not Null)
- `role` (VARCHAR: `ROLE_STUDENT`, `ROLE_ADMIN`, `ROLE_STAFF`)
- `phone` (VARCHAR)
- `hostel_block` (VARCHAR)
- `room_number` (VARCHAR)
- `bed_number` (VARCHAR)
- `department` (VARCHAR)
- `specialization` (VARCHAR)
- `created_at` (DATETIME)

#### 2. `complaints` Table
- `id` (BIGINT, Primary Key, Auto Increment)
- `complaint_number` (VARCHAR, Unique, Indexed)
- `title` (VARCHAR)
- `category` (VARCHAR: `ELECTRICAL`, `PLUMBING`, `CARPENTRY`, etc.)
- `priority` (VARCHAR: `LOW`, `MEDIUM`, `HIGH`, `URGENT`)
- `description` (TEXT)
- `hostel_block` (VARCHAR)
- `room_number` (VARCHAR)
- `status` (VARCHAR: `PENDING`, `ASSIGNED`, `IN_PROGRESS`, `RESOLVED`, `REJECTED`, `REOPENED`)
- `student_id` (BIGINT, Foreign Key &rarr; `users.id`)
- `staff_id` (BIGINT, Foreign Key &rarr; `users.id`, Nullable)
- `created_at` (DATETIME)
- `updated_at` (DATETIME)
- `resolved_at` (DATETIME)
- `warden_remarks` (TEXT)
- `staff_remarks` (TEXT)
- `rating` (INT, 1 to 5)
- `feedback` (TEXT)

#### 3. `complaint_logs` Table
- `id` (BIGINT, Primary Key)
- `complaint_id` (BIGINT, Foreign Key &rarr; `complaints.id`)
- `action` (VARCHAR)
- `performed_by` (VARCHAR)
- `remarks` (TEXT)
- `timestamp` (DATETIME)

#### 4. `notices` Table
- `id` (BIGINT, Primary Key)
- `title` (VARCHAR)
- `content` (TEXT)
- `category` (VARCHAR)
- `published_by` (VARCHAR)
- `published_at` (DATETIME)
- `active` (BOOLEAN)

---

### 5. SECURITY & AUTHENTICATION
- **Password Encryption:** Sensitive credentials are encrypted using `BCryptPasswordEncoder` with salt.
- **Role-Based Access Control (RBAC):**
  - `/student/**` restricted strictly to `ROLE_STUDENT`.
  - `/admin/**` restricted strictly to `ROLE_ADMIN`.
  - `/staff/**` restricted strictly to `ROLE_STAFF`.
- **Dynamic Post-Login Redirection:** Automatically routes users based on their granted authority to their relevant portal.
- **SQL Injection Prevention:** Parametric prepared statements through Spring Data JPA & Hibernate ORM.
- **XSS Sanitization:** Thymeleaf automated HTML escaping.

---

### 6. CONCLUSION
The Hostel Complaint Management System modernizes residential hostel maintenance operations, ensuring swift issue turnaround, high accountability for maintenance technicians, transparent status tracking for students, and actionable analytics for campus administrators.
