package com.hostel.complaints.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.hostel.complaints.dto.RegisterRequest;
import com.hostel.complaints.dto.StaffRegistrationForm;
import com.hostel.complaints.model.Role;
import com.hostel.complaints.model.User;
import com.hostel.complaints.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("h2")
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testRegisterStudent_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Aditya Sharma");
        req.setEmail("aditya.test@hostel.edu");
        req.setPassword("securePassword123");
        req.setConfirmPassword("securePassword123");
        req.setPhone("+91 99887 76655");
        req.setHostelBlock("Block A");
        req.setRoomNumber("101");
        req.setBedNumber("Bed 1");
        req.setDepartment("Computer Science");

        User registered = userService.registerStudent(req);

        assertNotNull(registered.getId());
        assertEquals("Aditya Sharma", registered.getFullName());
        assertEquals("aditya.test@hostel.edu", registered.getEmail());
        assertEquals(Role.ROLE_STUDENT, registered.getRole());
        assertEquals("Block A", registered.getHostelBlock());
        assertEquals("101", registered.getRoomNumber());

        assertTrue(userRepository.findByEmail("aditya.test@hostel.edu").isPresent());
    }

    @Test
    void testRegisterStudent_DuplicateEmail_ThrowsException() {
        RegisterRequest req1 = new RegisterRequest();
        req1.setFullName("User One");
        req1.setEmail("duplicate@hostel.edu");
        req1.setPassword("password123");
        req1.setConfirmPassword("password123");
        req1.setPhone("+91 91234 56789");
        req1.setHostelBlock("Block B");
        req1.setRoomNumber("201");
        userService.registerStudent(req1);

        RegisterRequest req2 = new RegisterRequest();
        req2.setFullName("User Two");
        req2.setEmail("Duplicate@Hostel.edu"); // case variation
        req2.setPassword("password123");
        req2.setConfirmPassword("password123");
        req2.setPhone("+91 91234 56780");
        req2.setHostelBlock("Block B");
        req2.setRoomNumber("202");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerStudent(req2);
        });

        assertTrue(ex.getMessage().contains("already registered"));
    }

    @Test
    void testRegisterStudent_PasswordMismatch_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setFullName("Mismatch User");
        req.setEmail("mismatch@hostel.edu");
        req.setPassword("password123");
        req.setConfirmPassword("differentPassword");
        req.setPhone("+91 98765 43210");
        req.setHostelBlock("Block C");
        req.setRoomNumber("303");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerStudent(req);
        });

        assertTrue(ex.getMessage().contains("Passwords do not match"));
    }

    @Test
    void testHireAndFireStaff_AdminPanelLifecycle() {
        // 1. Hire a new staff technician
        StaffRegistrationForm staffForm = new StaffRegistrationForm();
        staffForm.setFullName("Rajesh Verma");
        staffForm.setEmail("rajesh.electrician@hostel.edu");
        staffForm.setPassword("staffSecret123");
        staffForm.setPhone("+91 98888 77777");
        staffForm.setSpecialization("Electrician");

        User hired = userService.registerStaff(staffForm);
        assertNotNull(hired.getId());
        assertEquals(Role.ROLE_STAFF, hired.getRole());
        assertEquals("Electrician", hired.getSpecialization());
        assertTrue(userRepository.findByEmail("rajesh.electrician@hostel.edu").isPresent());

        // 2. Delete / fire the staff technician from admin panel
        userService.deleteStaff(hired.getId());
        assertFalse(userRepository.findByEmail("rajesh.electrician@hostel.edu").isPresent(), "Staff member should be removed from database");
    }
}
