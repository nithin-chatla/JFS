# 🌐 HCMS Live Deployment Guide

This guide details the fastest ways to deploy the **Hostel Complaint Management System (HCMS)** live to the internet with a public HTTPS URL.

---

## 🚀 Method 1: Deploy Live on Render.com (Recommended Free Cloud Hosting)

Render provides free hosting for Docker / Java web applications with automatic HTTPS (`https://your-app.onrender.com`).

### Steps:
1. **Push your code to GitHub:**
   ```bash
   git init
   git add .
   git commit -m "Deploy HCMS live"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<your-repo-name>.git
   git push -u origin main
   ```
2. **Deploy on Render:**
   - Go to [render.com](https://render.com) and sign in with GitHub.
   - Click **New +** > **Web Service**.
   - Select your GitHub repository.
   - Configure:
     - **Runtime:** `Docker` (Render will automatically detect the [Dockerfile](Dockerfile)).
     - **Instance Type:** `Free`
     - **Environment Variables:**
       - `PORT` = `8080`
       - `SPRING_PROFILES_ACTIVE` = `h2` *(or connect external MySQL via `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`)*
   - Click **Deploy Web Service**.
3. **Your Live URL:** Render will build the container and provide your live link (e.g., `https://hostel-complaints.onrender.com`).

---

## ⚡ Method 2: Deploy Live on Railway.app (1-Click with MySQL)

Railway supports automated deployments with 1-click managed MySQL.

### Steps:
1. Go to [railway.app](https://railway.app) and sign in.
2. Click **New Project** > **Deploy from GitHub repo**.
3. (Optional for MySQL): Click **+ New** > **Database** > **Add MySQL**.
   - Railway will provide `MYSQL_URL`, `MYSQLUSER`, `MYSQLPASSWORD`.
   - Link these variables to your Spring Boot service.
4. Click **Deploy Now**. Railway automatically builds using Dockerfile and generates a public domain under **Settings > Networking > Generate Domain**.

---

## 🌐 Method 3: Instant Live Demo via Cloudflare Tunnel / Ngrok (Instant 30-Sec Live URL)

If you want to run the project locally and instantly share a public HTTPS link with evaluators or teammates:

### A. Run HCMS locally:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-23"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=h2"
```

### B. Expose to the Internet:
**Using Cloudflare Tunnel (No account required):**
```powershell
winget install Cloudflare.cloudflared
cloudflared tunnel --url http://localhost:8080
```
*Cloudflare will print a live public HTTPS URL (e.g., `https://random-word.trycloudflare.com`).*

**Or using ngrok:**
```powershell
ngrok http 9090
```

---

## 🔑 Initial Authorized Accounts & Student Registration

| Role | Access URL | Credentials / Action |
| :--- | :--- | :--- |
| **Chief Warden (Admin)** | `/login` | `nithin@gmail.com` / `810671` |
| **Maintenance Staff** | `/login` | Hired & managed directly via Admin Desk (`/admin/staff`) |
| **Students / Residents** | `/register` &amp; `/login` | Register room account via `/register` (User-defined password) |
| **Public Visitor** | `/track` | Free unauthenticated ticket tracker |

