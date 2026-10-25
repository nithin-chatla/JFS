package com.hostel.complaints.controller;

import com.hostel.complaints.dto.ComplaintForm;
import com.hostel.complaints.dto.FeedbackForm;
import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.User;
import com.hostel.complaints.service.ComplaintService;
import com.hostel.complaints.service.NoticeService;
import com.hostel.complaints.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final NoticeService noticeService;

    public StudentController(UserService userService, ComplaintService complaintService, NoticeService noticeService) {
        this.userService = userService;
        this.complaintService = complaintService;
        this.noticeService = noticeService;
    }

    private User getStudentUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Logged in user not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = getStudentUser(userDetails);
        List<Complaint> complaints = complaintService.getComplaintsByStudent(student);
        
        long total = complaints.size();
        long pending = complaints.stream().filter(c -> c.getStatus().name().equals("PENDING")).count();
        long inProgress = complaints.stream().filter(c -> c.getStatus().name().equals("IN_PROGRESS") || c.getStatus().name().equals("ASSIGNED") || c.getStatus().name().equals("REOPENED")).count();
        long resolved = complaints.stream().filter(c -> c.getStatus().name().equals("RESOLVED")).count();

        model.addAttribute("student", student);
        model.addAttribute("totalComplaints", total);
        model.addAttribute("pendingComplaints", pending);
        model.addAttribute("inProgressComplaints", inProgress);
        model.addAttribute("resolvedComplaints", resolved);
        model.addAttribute("recentComplaints", complaintService.getRecentComplaintsForStudent(student));
        model.addAttribute("notices", noticeService.getActiveNotices());

        return "student/dashboard";
    }

    @GetMapping("/lodge")
    public String lodgeComplaintForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = getStudentUser(userDetails);
        if (!model.containsAttribute("complaintForm")) {
            ComplaintForm form = new ComplaintForm();
            form.setHostelBlock(student.getHostelBlock());
            form.setRoomNumber(student.getRoomNumber());
            model.addAttribute("complaintForm", form);
        }
        return "student/lodge";
    }

    @PostMapping("/lodge")
    public String handleLodgeComplaint(@AuthenticationPrincipal UserDetails userDetails,
                                       @Valid @ModelAttribute("complaintForm") ComplaintForm complaintForm,
                                       BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes,
                                       Model model) {
        User student = getStudentUser(userDetails);
        if (bindingResult.hasErrors()) {
            return "student/lodge";
        }

        Complaint created = complaintService.lodgeComplaint(complaintForm, student);
        redirectAttributes.addFlashAttribute("successMessage", 
                "Complaint lodged successfully! Your Ticket Number is " + created.getComplaintNumber());
        return "redirect:/student/complaints/" + created.getId();
    }

    @GetMapping("/complaints")
    public String viewMyComplaints(@AuthenticationPrincipal UserDetails userDetails,
                                   @RequestParam(value = "status", required = false) String statusFilter,
                                   Model model) {
        User student = getStudentUser(userDetails);
        List<Complaint> complaints = complaintService.getComplaintsByStudent(student);

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
            complaints = complaints.stream()
                    .filter(c -> c.getStatus().name().equalsIgnoreCase(statusFilter))
                    .toList();
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("selectedStatus", statusFilter != null ? statusFilter : "ALL");
        return "student/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {
        User student = getStudentUser(userDetails);
        Complaint complaint = complaintService.getComplaintById(id).orElse(null);

        if (complaint == null || !complaint.getStudent().getId().equals(student.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Complaint not found or unauthorized access.");
            return "redirect:/student/complaints";
        }

        model.addAttribute("complaint", complaint);
        if (!model.containsAttribute("feedbackForm")) {
            FeedbackForm feedbackForm = new FeedbackForm();
            if (complaint.getRating() != null) feedbackForm.setRating(complaint.getRating());
            if (complaint.getFeedback() != null) feedbackForm.setFeedback(complaint.getFeedback());
            model.addAttribute("feedbackForm", feedbackForm);
        }
        return "student/complaint-details";
    }

    @PostMapping("/complaints/{id}/feedback")
    public String submitFeedback(@AuthenticationPrincipal UserDetails userDetails,
                                 @PathVariable("id") Long id,
                                 @Valid @ModelAttribute("feedbackForm") FeedbackForm feedbackForm,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        User student = getStudentUser(userDetails);
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a valid rating between 1 and 5.");
            return "redirect:/student/complaints/" + id;
        }

        try {
            complaintService.submitFeedback(id, feedbackForm, student);
            redirectAttributes.addFlashAttribute("successMessage", "Thank you for your feedback!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/student/complaints/" + id;
    }

    @PostMapping("/complaints/{id}/reopen")
    public String reopenComplaint(@AuthenticationPrincipal UserDetails userDetails,
                                  @PathVariable("id") Long id,
                                  @RequestParam("reason") String reason,
                                  RedirectAttributes redirectAttributes) {
        User student = getStudentUser(userDetails);
        if (reason == null || reason.trim().length() < 5) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please provide a specific reason for reopening (at least 5 characters).");
            return "redirect:/student/complaints/" + id;
        }

        try {
            complaintService.reopenComplaint(id, reason.trim(), student);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint has been reopened and flagged for Warden review.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/student/complaints/" + id;
    }

    @GetMapping("/profile")
    public String profilePage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User student = getStudentUser(userDetails);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam("fullName") String fullName,
                                @RequestParam("phone") String phone,
                                @RequestParam("hostelBlock") String hostelBlock,
                                @RequestParam("roomNumber") String roomNumber,
                                @RequestParam(value = "bedNumber", required = false) String bedNumber,
                                @RequestParam(value = "department", required = false) String department,
                                RedirectAttributes redirectAttributes) {
        User student = getStudentUser(userDetails);
        userService.updateProfile(student, fullName, phone, hostelBlock, roomNumber, bedNumber, department);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/student/profile";
    }
}
