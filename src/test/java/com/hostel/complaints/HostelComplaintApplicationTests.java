package com.hostel.complaints;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hostel.complaints.model.Role;
import com.hostel.complaints.repository.ComplaintRepository;
import com.hostel.complaints.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("h2")
class HostelComplaintApplicationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Test
    void contextLoads() {
        // Verify application context boots cleanly
    }

    @Test
    void testInitialDataState_NoDemoStaffNoDemoStudentsAndNoDemoComplaints() {
        // Chief Warden (Admin) must exist
        assertTrue(userRepository.existsByEmail("nithin@gmail.com"), "Chief Warden (Admin) must be initialized");
        assertFalse(userRepository.existsByEmail("admin@hostel.edu"), "Old admin@hostel.edu should not exist");

        // Old demo staff accounts must NOT exist (staff are hired dynamically by Admin)
        assertFalse(userRepository.existsByEmail("electrician@hostel.edu"), "Demo electrician must be removed");
        assertFalse(userRepository.existsByEmail("plumber@hostel.edu"), "Demo plumber must be removed");
        assertFalse(userRepository.existsByEmail("carpenter@hostel.edu"), "Demo carpenter must be removed");
        assertFalse(userRepository.existsByEmail("network@hostel.edu"), "Demo network staff must be removed");
        assertFalse(userRepository.existsByEmail("housekeeping@hostel.edu"), "Demo housekeeping staff must be removed");

        // Old demo students must NOT exist (students register via /register)
        assertFalse(userRepository.existsByEmail("student@hostel.edu"), "Demo student account must be removed");
        assertFalse(userRepository.existsByEmail("priya@hostel.edu"), "Demo student account must be removed");
        assertFalse(userRepository.existsByEmail("amit@hostel.edu"), "Demo student account must be removed");

        // Initial counts must be clean
        assertEquals(0, userRepository.countByRole(Role.ROLE_STAFF), "No demo staff accounts should be pre-seeded");
        assertEquals(0, userRepository.countByRole(Role.ROLE_STUDENT), "No demo student accounts should be pre-seeded");
        assertEquals(0, complaintRepository.count(), "No fake demo complaints should be pre-seeded");
    }
}
