package com.hostel.complaints.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.hostel.complaints.model.Notice;
import com.hostel.complaints.model.Role;
import com.hostel.complaints.model.User;
import com.hostel.complaints.repository.NoticeRepository;
import com.hostel.complaints.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NoticeRepository noticeRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           NoticeRepository noticeRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.noticeRepository = noticeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize Chief Warden (System Administrator) only
        if (!userRepository.existsByEmail("nithin@gmail.com")) {
            User admin = new User();
            admin.setFullName("Nithin Chatla");
            admin.setEmail("nithin@gmail.com");
            admin.setPassword(passwordEncoder.encode("810671"));
            admin.setRole(Role.ROLE_ADMIN);
            admin.setPhone("+91 98765 43210");
            admin.setDepartment("Hostel Administration & Residence Life");
            userRepository.save(admin);
        }

        // NOTE: No demo staff accounts (electrician, plumber, etc.) or demo student accounts are seeded.
        // The Administrator hires/recruits and deletes staff technicians directly from the Admin Panel (/admin/staff).
        // Residents register their own accounts via the registration page (/register).

        // 2. Initialize Official Campus Notices (if notice board is empty)
        if (noticeRepository.count() == 0) {
            noticeRepository.save(new Notice(
                    "Campus Wi-Fi Upgradation & Infrastructure Maintenance",
                    "High-speed fiber connectivity and access point maintenance will take place on Saturday from 10:00 AM to 2:00 PM across all hostel blocks. Minor disruptions may occur.",
                    "Maintenance",
                    "Chief Warden Office"
            ));

            noticeRepository.save(new Notice(
                    "Water Tank Cleaning Schedule - Block A, B & Girls Hostels",
                    "Overhead and underground reservoir water tanks will be sanitized this coming Sunday. Water supply will be temporarily paused between 9:00 AM - 1:00 PM.",
                    "Maintenance",
                    "Hostel Maintenance Cell"
            ));

            noticeRepository.save(new Notice(
                    "Monthly Hostel Mess Committee Review Meeting",
                    "Elected student representatives are invited to attend the monthly mess menu and dining review session on Friday at 5:00 PM in the Common Conference Room.",
                    "Mess & Dining",
                    "Chief Warden Office"
            ));
        }
    }
}
