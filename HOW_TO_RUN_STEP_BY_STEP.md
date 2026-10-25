# 🚀 CampusStay HMS - Step-by-Step Run & Demonstration Guide

CampusStay is configured with **zero-setup, automatic persistent file storage** (`./data/hostel_db`). All registered resident accounts, lodged tickets, technician assignments, and reviews persist seamlessly across application restarts.

---

### ⚡ Quick Start: 1-Click Launch

1. Open the project folder in Windows Explorer:
   `c:\Users\nithi\Downloads\temppppppp\JFS`
2. **Double-click** the file:
   👉 **`RUN_PROJECT.bat`**
3. The script will auto-detect your Java JDK and launch the server. Your web browser will automatically open:
   👉 **http://localhost:9090**

---

### 💻 Launch via Terminal / PowerShell

```powershell
# In PowerShell:
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
.\mvnw.cmd spring-boot:run
```
Then visit **http://localhost:9090** in your browser.

---

### 🔑 System Roles & Authentication

| Role | Access URL | Credentials / Action | Key Capabilities |
| :--- | :--- | :--- | :--- |
| **Hostel Resident (Student)** | `/register` &amp; `/login` | **Register your room account** at `/register` | Lodge maintenance issues with category/priority, track live 4-stage resolution stepper, submit 1–5 star reviews, reopen tickets |
| **Chief Warden (Admin)** | `/login` | `nithin@gmail.com` / `810671` | Institutional dashboard, interactive analytics charts, recruit/fire technicians, student directory, notice broadcasting, CSV export |
| **Maintenance Staff** | `/login` | Created &amp; managed via Admin Panel (`/admin/staff`) | Specialized task queues, update status (`IN_PROGRESS`, `RESOLVED`), submit field inspection remarks |
| **Public Visitor** | `/track` | *(No login required)* | Look up any work order using ticket reference (e.g. `HCMS-261025-101`) |

---

### 🗄️ Database Storage Architecture
- **Local Persistent Storage**: All data is automatically stored and updated in `./data/hostel_db.mv.db`.
- **H2 Database Console**: Accessible at **http://localhost:9090/h2-console** (JDBC URL: `jdbc:h2:file:./data/hostel_db`, User: `sa`, Password: *(blank)*).
- **MySQL Production Mode**: Optional support via `RUN_WITH_MYSQL.bat` or `-Dspring-boot.run.profiles=mysql`.

---

### 🛑 How to Stop the Project
Press `Ctrl + C` in the running command window or close the terminal window.
