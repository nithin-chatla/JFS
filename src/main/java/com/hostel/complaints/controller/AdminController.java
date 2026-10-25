package com.hostel.complaints.controller;

import com.hostel.complaints.dto.ComplaintActionForm;
import com.hostel.complaints.dto.DashboardStatsDto;
import com.hostel.complaints.dto.NoticeForm;
import com.hostel.complaints.dto.StaffRegistrationForm;
import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.ComplaintCategory;
import com.hostel.complaints.model.ComplaintPriority;
import com.hostel.complaints.model.ComplaintStatus;
import com.hostel.complaints.model.User;
import com.hostel.complaints.service.ComplaintService;
import com.hostel.complaints.service.NoticeService;
import com.hostel.complaints.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final NoticeService noticeService;

    public AdminController(UserService userService, ComplaintService complaintService, NoticeService noticeService) {
        this.userService = userService;
        this.complaintService = complaintService;
        this.noticeService = noticeService;
    }

    private User getAdminUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Logged in admin not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        DashboardStatsDto stats = complaintService.getDashboardStatistics();
        model.addAttribute("stats", stats);
        model.addAttribute("recentComplaints", complaintService.getRecentComplaints());
        model.addAttribute("staffList", userService.findAllStaff());
        model.addAttribute("notices", noticeService.getAllNotices());
        return "admin/dashboard";
    }

    @GetMapping("/complaints")
    public String viewAllComplaints(@RequestParam(value = "status", required = false) String statusFilter,
                                    @RequestParam(value = "category", required = false) String categoryFilter,
                                    @RequestParam(value = "priority", required = false) String priorityFilter,
                                    @RequestParam(value = "search", required = false) String searchQuery,
                                    Model model) {
        List<Complaint> complaints = complaintService.getAllComplaints();

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
            complaints = complaints.stream()
                    .filter(c -> c.getStatus().name().equalsIgnoreCase(statusFilter))
                    .toList();
        }

        if (categoryFilter != null && !categoryFilter.isBlank() && !categoryFilter.equalsIgnoreCase("ALL")) {
            complaints = complaints.stream()
                    .filter(c -> c.getCategory().name().equalsIgnoreCase(categoryFilter))
                    .toList();
        }

        if (priorityFilter != null && !priorityFilter.isBlank() && !priorityFilter.equalsIgnoreCase("ALL")) {
            complaints = complaints.stream()
                    .filter(c -> c.getPriority().name().equalsIgnoreCase(priorityFilter))
                    .toList();
        }

        if (searchQuery != null && !searchQuery.isBlank()) {
            String q = searchQuery.toLowerCase().trim();
            complaints = complaints.stream()
                    .filter(c -> c.getComplaintNumber().toLowerCase().contains(q)
                            || c.getTitle().toLowerCase().contains(q)
                            || c.getStudent().getFullName().toLowerCase().contains(q)
                            || c.getHostelBlock().toLowerCase().contains(q)
                            || c.getRoomNumber().toLowerCase().contains(q))
                    .toList();
        }

        model.addAttribute("complaints", complaints);
        model.addAttribute("categories", ComplaintCategory.values());
        model.addAttribute("statuses", ComplaintStatus.values());
        model.addAttribute("priorities", ComplaintPriority.values());
        model.addAttribute("selectedStatus", statusFilter != null ? statusFilter : "ALL");
        model.addAttribute("selectedCategory", categoryFilter != null ? categoryFilter : "ALL");
        model.addAttribute("selectedPriority", priorityFilter != null ? priorityFilter : "ALL");
        model.addAttribute("searchQuery", searchQuery);

        return "admin/complaints";
    }

    @GetMapping("/complaints/{id}")
    public String complaintDetails(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        Complaint complaint = complaintService.getComplaintById(id).orElse(null);
        if (complaint == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Complaint not found.");
            return "redirect:/admin/complaints";
        }

        model.addAttribute("complaint", complaint);
        model.addAttribute("staffList", userService.findAllStaff());
        model.addAttribute("statuses", ComplaintStatus.values());
        if (!model.containsAttribute("actionForm")) {
            ComplaintActionForm actionForm = new ComplaintActionForm();
            actionForm.setStatus(complaint.getStatus());
            if (complaint.getAssignedStaff() != null) {
                actionForm.setAssignedStaffId(complaint.getAssignedStaff().getId());
            }
            actionForm.setRemarks(complaint.getWardenRemarks());
            model.addAttribute("actionForm", actionForm);
        }

        return "admin/complaint-details";
    }

    @PostMapping("/complaints/{id}/action")
    public String handleComplaintAction(@AuthenticationPrincipal UserDetails userDetails,
                                        @PathVariable("id") Long id,
                                        @ModelAttribute("actionForm") ComplaintActionForm actionForm,
                                        RedirectAttributes redirectAttributes) {
        User admin = getAdminUser(userDetails);
        try {
            complaintService.updateWardenAction(id, actionForm, admin);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating complaint: " + e.getMessage());
        }

        return "redirect:/admin/complaints/" + id;
    }

    @GetMapping("/staff")
    public String staffManagement(Model model) {
        model.addAttribute("staffList", userService.findAllStaff());
        if (!model.containsAttribute("staffForm")) {
            model.addAttribute("staffForm", new StaffRegistrationForm());
        }
        return "admin/staff";
    }

    @PostMapping("/staff/new")
    public String addStaff(@Valid @ModelAttribute("staffForm") StaffRegistrationForm staffForm,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("staffList", userService.findAllStaff());
            return "admin/staff";
        }

        try {
            userService.registerStaff(staffForm);
            redirectAttributes.addFlashAttribute("successMessage", "Staff technician " + staffForm.getFullName() + " hired successfully!");
            return "redirect:/admin/staff";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("staffList", userService.findAllStaff());
            return "admin/staff";
        }
    }

    @PostMapping("/staff/{id}/delete")
    public String deleteStaff(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("successMessage", "Staff technician removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error removing staff member: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    @GetMapping("/students")
    public String studentDirectory(Model model) {
        model.addAttribute("students", userService.findAllStudents());
        return "admin/students";
    }

    @GetMapping("/notices")
    public String noticesManagement(Model model) {
        model.addAttribute("notices", noticeService.getAllNotices());
        if (!model.containsAttribute("noticeForm")) {
            model.addAttribute("noticeForm", new NoticeForm());
        }
        return "admin/notices";
    }

    @PostMapping("/notices/new")
    public String addNotice(@AuthenticationPrincipal UserDetails userDetails,
                            @Valid @ModelAttribute("noticeForm") NoticeForm noticeForm,
                            BindingResult bindingResult,
                            RedirectAttributes redirectAttributes,
                            Model model) {
        User admin = getAdminUser(userDetails);
        if (bindingResult.hasErrors()) {
            model.addAttribute("notices", noticeService.getAllNotices());
            return "admin/notices";
        }

        noticeService.createNotice(noticeForm, admin);
        redirectAttributes.addFlashAttribute("successMessage", "Notice published successfully!");
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/toggle")
    public String toggleNotice(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        noticeService.toggleNoticeStatus(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notice status updated.");
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/{id}/delete")
    public String deleteNotice(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        noticeService.deleteNotice(id);
        redirectAttributes.addFlashAttribute("successMessage", "Notice removed.");
        return "redirect:/admin/notices";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAttribute("stats", complaintService.getDashboardStatistics());
        return "admin/reports";
    }

    @GetMapping("/export-csv")
    public void exportComplaintsCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"hostel_complaints_export.csv\"");

        PrintWriter writer = response.getWriter();
        writer.println("Ticket ID,Category,Priority,Status,Student Name,Hostel Block,Room,Created At,Resolved At,Assigned Staff,Rating,Feedback");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (Complaint c : complaintService.getAllComplaints()) {
            String studentName = c.getStudent() != null ? c.getStudent().getFullName().replace(",", " ") : "N/A";
            String staffName = c.getAssignedStaff() != null ? c.getAssignedStaff().getFullName().replace(",", " ") : "Unassigned";
            String resolvedTime = c.getResolvedAt() != null ? c.getResolvedAt().format(fmt) : "N/A";
            String rating = c.getRating() != null ? String.valueOf(c.getRating()) : "N/A";
            String feedback = c.getFeedback() != null ? c.getFeedback().replace(",", " ").replace("\n", " ") : "N/A";

            writer.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    c.getComplaintNumber(),
                    c.getCategory().getDisplayName(),
                    c.getPriority().getDisplayName(),
                    c.getStatus().getDisplayName(),
                    studentName,
                    c.getHostelBlock(),
                    c.getRoomNumber(),
                    c.getCreatedAt().format(fmt),
                    resolvedTime,
                    staffName,
                    rating,
                    feedback
            );
        }
        writer.flush();
    }
}
