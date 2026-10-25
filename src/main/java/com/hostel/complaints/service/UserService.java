package com.hostel.complaints.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hostel.complaints.dto.RegisterRequest;
import com.hostel.complaints.dto.StaffRegistrationForm;
import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.ComplaintStatus;
import com.hostel.complaints.model.Role;
import com.hostel.complaints.model.User;
import com.hostel.complaints.repository.ComplaintRepository;
import com.hostel.complaints.repository.UserRepository;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ComplaintRepository complaintRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ComplaintRepository complaintRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.complaintRepository = complaintRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerStudent(RegisterRequest req) {
        String normalizedEmail = req.getEmail() != null ? req.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with institutional email '" + req.getEmail() + "' is already registered. Please sign in or use another email.");
        }

        if (!req.isPasswordConfirmed()) {
            throw new IllegalArgumentException("Passwords do not match. Please re-enter both passwords.");
        }

        User user = new User();
        user.setFullName(req.getFullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(Role.ROLE_STUDENT);
        user.setPhone(req.getPhone() != null ? req.getPhone().trim() : "");
        user.setHostelBlock(req.getHostelBlock() != null ? req.getHostelBlock().trim() : "");
        user.setRoomNumber(req.getRoomNumber() != null ? req.getRoomNumber().trim() : "");
        user.setBedNumber(req.getBedNumber() != null && !req.getBedNumber().isBlank() ? req.getBedNumber().trim() : null);
        user.setDepartment(req.getDepartment() != null && !req.getDepartment().isBlank() ? req.getDepartment().trim() : null);

        return userRepository.save(user);
    }

    public User registerStaff(StaffRegistrationForm req) {
        String normalizedEmail = req.getEmail() != null ? req.getEmail().trim().toLowerCase() : "";
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with email '" + req.getEmail() + "' already exists.");
        }

        User staff = new User();
        staff.setFullName(req.getFullName().trim());
        staff.setEmail(normalizedEmail);
        staff.setPassword(passwordEncoder.encode(req.getPassword()));
        staff.setRole(Role.ROLE_STAFF);
        staff.setPhone(req.getPhone() != null ? req.getPhone().trim() : "");
        staff.setSpecialization(req.getSpecialization() != null ? req.getSpecialization().trim() : "");

        return userRepository.save(staff);
    }

    public void deleteStaff(Long staffId) {
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff member not found with ID: " + staffId));

        if (staff.getRole() != Role.ROLE_STAFF) {
            throw new IllegalArgumentException("User with ID " + staffId + " is not a maintenance staff member.");
        }

        // Before deleting the staff member, unassign any complaints assigned to them
        List<Complaint> assignedComplaints = complaintRepository.findByAssignedStaffOrderByCreatedAtDesc(staff);
        for (Complaint c : assignedComplaints) {
            c.setAssignedStaff(null);
            if (c.getStatus() == ComplaintStatus.ASSIGNED) {
                c.setStatus(ComplaintStatus.PENDING);
            }
            complaintRepository.save(c);
        }

        userRepository.delete(staff);
    }

    public Optional<User> findByEmail(String email) {
        if (email == null) return Optional.empty();
        return userRepository.findByEmail(email.trim().toLowerCase());
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAllStaff() {
        return userRepository.findByRoleOrderByFullNameAsc(Role.ROLE_STAFF);
    }

    public List<User> findAllStudents() {
        return userRepository.findByRoleOrderByFullNameAsc(Role.ROLE_STUDENT);
    }

    public long countStudents() {
        return userRepository.countByRole(Role.ROLE_STUDENT);
    }

    public long countStaff() {
        return userRepository.countByRole(Role.ROLE_STAFF);
    }

    public User updateProfile(User user, String fullName, String phone, String hostelBlock, String roomNumber, String bedNumber, String department) {
        user.setFullName(fullName != null ? fullName.trim() : user.getFullName());
        user.setPhone(phone != null ? phone.trim() : user.getPhone());
        if (hostelBlock != null && !hostelBlock.isBlank()) user.setHostelBlock(hostelBlock.trim());
        if (roomNumber != null && !roomNumber.isBlank()) user.setRoomNumber(roomNumber.trim());
        if (bedNumber != null) user.setBedNumber(bedNumber.trim());
        if (department != null) user.setDepartment(department.trim());
        return userRepository.save(user);
    }
}
