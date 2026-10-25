package com.hostel.complaints.service;

import com.hostel.complaints.dto.ComplaintActionForm;
import com.hostel.complaints.dto.ComplaintForm;
import com.hostel.complaints.dto.DashboardStatsDto;
import com.hostel.complaints.dto.FeedbackForm;
import com.hostel.complaints.model.*;
import com.hostel.complaints.repository.ComplaintLogRepository;
import com.hostel.complaints.repository.ComplaintRepository;
import com.hostel.complaints.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintLogRepository complaintLogRepository;
    private final UserRepository userRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            ComplaintLogRepository complaintLogRepository,
                            UserRepository userRepository) {
        this.complaintRepository = complaintRepository;
        this.complaintLogRepository = complaintLogRepository;
        this.userRepository = userRepository;
    }

    public Complaint lodgeComplaint(ComplaintForm form, User student) {
        Complaint complaint = new Complaint();
        
        // Generate unique complaint ticket number
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));
        int randomSuffix = (int) (Math.random() * 900) + 100;
        complaint.setComplaintNumber("HCMS-" + timestamp + "-" + randomSuffix);

        complaint.setTitle(form.getTitle());
        complaint.setCategory(form.getCategory());
        complaint.setPriority(form.getPriority());
        complaint.setDescription(form.getDescription());
        complaint.setHostelBlock(form.getHostelBlock());
        complaint.setRoomNumber(form.getRoomNumber());
        complaint.setStudent(student);
        complaint.setStatus(ComplaintStatus.PENDING);

        Complaint saved = complaintRepository.save(complaint);

        // Audit Log
        ComplaintLog log = new ComplaintLog(
                saved,
                "LODGED",
                student.getFullName() + " (Student)",
                "Complaint lodged under category: " + form.getCategory().getDisplayName() + " with Priority: " + form.getPriority().getDisplayName()
        );
        complaintLogRepository.save(log);

        return saved;
    }

    public Complaint updateWardenAction(Long complaintId, ComplaintActionForm form, User warden) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with ID: " + complaintId));

        StringBuilder logDetails = new StringBuilder();

        if (form.getAssignedStaffId() != null) {
            User staff = userRepository.findById(form.getAssignedStaffId())
                    .orElseThrow(() -> new NoSuchElementException("Staff member not found with ID: " + form.getAssignedStaffId()));
            complaint.setAssignedStaff(staff);
            if (complaint.getStatus() == ComplaintStatus.PENDING) {
                complaint.setStatus(ComplaintStatus.ASSIGNED);
            }
            logDetails.append("Assigned to staff: ").append(staff.getFullName()).append(" (").append(staff.getSpecialization()).append("). ");
        }

        if (form.getStatus() != null) {
            ComplaintStatus oldStatus = complaint.getStatus();
            complaint.setStatus(form.getStatus());
            logDetails.append("Status changed from ").append(oldStatus.getDisplayName()).append(" to ").append(form.getStatus().getDisplayName()).append(". ");
            
            if (form.getStatus() == ComplaintStatus.RESOLVED && complaint.getResolvedAt() == null) {
                complaint.setResolvedAt(LocalDateTime.now());
            }
        }

        if (form.getRemarks() != null && !form.getRemarks().isBlank()) {
            complaint.setWardenRemarks(form.getRemarks());
            logDetails.append("Warden Remarks: ").append(form.getRemarks());
        }

        Complaint updated = complaintRepository.save(complaint);

        ComplaintLog log = new ComplaintLog(
                updated,
                "WARDEN_ACTION",
                warden.getFullName() + " (Warden)",
                logDetails.toString().trim()
        );
        complaintLogRepository.save(log);

        return updated;
    }

    public Complaint updateStaffAction(Long complaintId, ComplaintStatus newStatus, String staffRemarks, User staff) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with ID: " + complaintId));

        complaint.setStatus(newStatus);
        if (staffRemarks != null && !staffRemarks.isBlank()) {
            complaint.setStaffRemarks(staffRemarks);
        }

        if (newStatus == ComplaintStatus.RESOLVED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint updated = complaintRepository.save(complaint);

        ComplaintLog log = new ComplaintLog(
                updated,
                "STAFF_ACTION",
                staff.getFullName() + " (Staff)",
                "Status updated to: " + newStatus.getDisplayName() + (staffRemarks != null && !staffRemarks.isBlank() ? ". Remarks: " + staffRemarks : "")
        );
        complaintLogRepository.save(log);

        return updated;
    }

    public Complaint submitFeedback(Long complaintId, FeedbackForm form, User student) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with ID: " + complaintId));

        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("You are not authorized to submit feedback for this complaint.");
        }

        complaint.setRating(form.getRating());
        complaint.setFeedback(form.getFeedback());

        Complaint updated = complaintRepository.save(complaint);

        ComplaintLog log = new ComplaintLog(
                updated,
                "FEEDBACK_SUBMITTED",
                student.getFullName() + " (Student)",
                "Rating: " + form.getRating() + "/5 Stars. Feedback: " + (form.getFeedback() != null ? form.getFeedback() : "No comment")
        );
        complaintLogRepository.save(log);

        return updated;
    }

    public Complaint reopenComplaint(Long complaintId, String reason, User student) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new NoSuchElementException("Complaint not found with ID: " + complaintId));

        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new SecurityException("You are not authorized to reopen this complaint.");
        }

        complaint.setStatus(ComplaintStatus.REOPENED);
        complaint.setResolvedAt(null);

        Complaint updated = complaintRepository.save(complaint);

        ComplaintLog log = new ComplaintLog(
                updated,
                "REOPENED",
                student.getFullName() + " (Student)",
                "Complaint reopened by student. Reason: " + reason
        );
        complaintLogRepository.save(log);

        return updated;
    }

    public List<Complaint> getComplaintsByStudent(User student) {
        return complaintRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Complaint> getComplaintsByStaff(User staff) {
        return complaintRepository.findByAssignedStaffOrderByCreatedAtDesc(staff);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public Optional<Complaint> getComplaintByNumber(String complaintNumber) {
        return complaintRepository.findByComplaintNumber(complaintNumber);
    }

    public List<Complaint> getRecentComplaints() {
        return complaintRepository.findTop5ByOrderByCreatedAtDesc();
    }

    public List<Complaint> getRecentComplaintsForStudent(User student) {
        return complaintRepository.findTop5ByStudentOrderByCreatedAtDesc(student);
    }

    public DashboardStatsDto getDashboardStatistics() {
        DashboardStatsDto stats = new DashboardStatsDto();

        long total = complaintRepository.count();
        long pending = complaintRepository.countByStatus(ComplaintStatus.PENDING);
        long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS) + complaintRepository.countByStatus(ComplaintStatus.ASSIGNED) + complaintRepository.countByStatus(ComplaintStatus.REOPENED);
        long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED);
        long rejected = complaintRepository.countByStatus(ComplaintStatus.REJECTED);

        stats.setTotalComplaints(total);
        stats.setPendingComplaints(pending);
        stats.setInProgressComplaints(inProgress);
        stats.setResolvedComplaints(resolved);
        stats.setRejectedComplaints(rejected);

        stats.setTotalStudents(userRepository.countByRole(Role.ROLE_STUDENT));
        stats.setTotalStaff(userRepository.countByRole(Role.ROLE_STAFF));

        double rate = total > 0 ? ((double) resolved / total) * 100.0 : 0.0;
        stats.setResolutionRate(Math.round(rate * 10.0) / 10.0);

        // Group counts
        Map<String, Long> categoryMap = new LinkedHashMap<>();
        for (Object[] row : complaintRepository.countComplaintsByCategory()) {
            ComplaintCategory cat = (ComplaintCategory) row[0];
            Long count = (Long) row[1];
            categoryMap.put(cat.getDisplayName(), count);
        }
        stats.setCategoryCounts(categoryMap);

        Map<String, Long> statusMap = new LinkedHashMap<>();
        for (Object[] row : complaintRepository.countComplaintsByStatus()) {
            ComplaintStatus st = (ComplaintStatus) row[0];
            Long count = (Long) row[1];
            statusMap.put(st.getDisplayName(), count);
        }
        stats.setStatusCounts(statusMap);

        Map<String, Long> priorityMap = new LinkedHashMap<>();
        for (Object[] row : complaintRepository.countComplaintsByPriority()) {
            ComplaintPriority p = (ComplaintPriority) row[0];
            Long count = (Long) row[1];
            priorityMap.put(p.getDisplayName(), count);
        }
        stats.setPriorityCounts(priorityMap);

        Map<String, Long> blockMap = new LinkedHashMap<>();
        for (Object[] row : complaintRepository.countComplaintsByHostelBlock()) {
            String block = (String) row[0];
            Long count = (Long) row[1];
            blockMap.put(block != null ? block : "Unassigned", count);
        }
        stats.setBlockCounts(blockMap);

        return stats;
    }
}
