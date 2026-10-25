# 🎓 Hostel Complaint Management System - Viva Voce & Interview Guide

This guide contains the most frequently asked questions by examiners, evaluators, and professors during Java Full Stack project demonstrations, along with crisp, impressive answers.

---

### Q1: What is the architecture of your project?
**Answer:**
"Our project follows a **Layered MVC (Model-View-Controller) Architecture** powered by Spring Boot:
1. **Presentation Layer:** Thymeleaf templating engine with HTML5, responsive CSS, and Chart.js for data visualization.
2. **Security Layer:** Spring Security 6 with BCrypt password encryption and Role-Based Access Control (RBAC).
3. **Controller Layer:** Handles HTTP GET/POST requests and routes data via Model attributes.
4. **Service Layer:** Houses the business logic (e.g., ticket generation, workflow state transitions, rating calculations).
5. **Data Access Layer (DAO):** Spring Data JPA with Hibernate ORM.
6. **Database Layer:** MySQL relational database (with automated H2 fallback support)."

---

### Q2: How does Role-Based Access Control (RBAC) work in your application?
**Answer:**
"We implemented Spring Security's `SecurityFilterChain`. We have three roles: `ROLE_STUDENT`, `ROLE_ADMIN` (Hostel Warden), and `ROLE_STAFF` (Maintenance Technician).
- `/student/**` URLs require `hasRole('STUDENT')`.
- `/admin/**` URLs require `hasRole('ADMIN')`.
- `/staff/**` URLs require `hasRole('STAFF')`.
- Public URLs (`/`, `/login`, `/register`, `/track`) are publicly accessible with `permitAll()`.
A custom `AuthenticationSuccessHandler` automatically redirects users to their appropriate dashboard immediately upon successful authentication."

---

### Q3: How do you secure student and staff passwords?
**Answer:**
"We use `BCryptPasswordEncoder`. BCrypt incorporates a 10-round cryptographic salt, making passwords resistant to rainbow-table and brute-force attacks. Raw passwords are never stored in the database."

---

### Q4: Explain the complaint lifecycle in your system.
**Answer:**
1. **Lodged (`PENDING`):** Student lodges an issue under a category (*Electrical, Plumbing, etc.*) and urgency (*Urgent, High, Medium, Low*). A unique ticket ID like `HCMS-202610-101` is automatically generated.
2. **Assigned (`ASSIGNED`):** Warden reviews the issue and assigns a specialized staff technician (*e.g., Electrician*).
3. **Under Work (`IN_PROGRESS`):** Technician accepts the work order, visits the room, and records field notes.
4. **Resolved (`RESOLVED`):** Technician completes the repair and marks it resolved. The student receives the resolution status and provides a 1–5 star rating and feedback.
5. **Reopened (`REOPENED`):** If the problem persists, the student can reopen the ticket with remarks for warden re-investigation.
Every transition creates an immutable record in the `complaint_logs` audit table.

---

### Q5: What is Spring Data JPA and how does it benefit this project?
**Answer:**
"Spring Data JPA is an abstraction on top of JPA (Hibernate). It eliminates boilerplate DAO and SQL query writing by providing built-in methods like `save()`, `findAll()`, `findById()`, and derived query methods like `findByStudentOrderByCreatedAtDesc()`. It also prevents SQL injection by utilizing prepared statements."

---

### Q6: Why did you choose Thymeleaf over standard JSP?
**Answer:**
"Thymeleaf is a modern server-side Java template engine that supports **natural templating**—meaning templates can be opened directly in browsers without a running server. It integrates seamlessly with Spring Security using the `thymeleaf-extras-springsecurity6` dialect to conditionally render UI buttons and navigation tabs based on user roles."

---

### Q7: How does the public tracking feature work?
**Answer:**
"Students or guardians can track any ticket without logging in by visiting `/track` and supplying their ticket number. The `ComplaintService` fetches the complaint along with its complete chronological timeline from `complaint_logs`."

---

### Q8: How is the database initialized and managed?
**Answer:**
"We utilize Hibernate's `ddl-auto=update` and a Spring `CommandLineRunner` (`DataInitializer.java`) that pre-populates default demo accounts (Admin, Electrician, Plumber, Students) and realistic sample tickets on first boot for instant demonstration."
