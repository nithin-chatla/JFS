
CREATE DATABASE IF NOT EXISTS hostel_complaints_db;
USE hostel_complaints_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS app_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    phone VARCHAR(50),
    hostel_block VARCHAR(100),
    room_number VARCHAR(50),
    bed_number VARCHAR(50),
    department VARCHAR(100),
    specialization VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 2. Complaints Table
CREATE TABLE IF NOT EXISTS complaints (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_number VARCHAR(32) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    description TEXT NOT NULL,
    hostel_block VARCHAR(100) NOT NULL,
    room_number VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    student_id BIGINT NOT NULL,
    staff_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME,
    resolved_at DATETIME,
    warden_remarks TEXT,
    staff_remarks TEXT,
    rating INT,
    feedback TEXT,
    FOREIGN KEY (student_id) REFERENCES app_users(id) ON DELETE CASCADE,
    FOREIGN KEY (staff_id) REFERENCES app_users(id) ON DELETE SET NULL
);

-- 3. Complaint Audit Logs Table
CREATE TABLE IF NOT EXISTS complaint_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    complaint_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    performed_by VARCHAR(255),
    remarks TEXT,
    timestamp DATETIME NOT NULL,
    FOREIGN KEY (complaint_id) REFERENCES complaints(id) ON DELETE CASCADE
);

-- 4. Notices Table
CREATE TABLE IF NOT EXISTS notices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(100),
    published_by VARCHAR(255),
    published_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);
