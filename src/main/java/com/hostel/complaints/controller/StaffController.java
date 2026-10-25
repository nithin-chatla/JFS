package com.hostel.complaints.controller;

import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.model.ComplaintStatus;
import com.hostel.complaints.model.User;
import com.hostel.complaints.service.ComplaintService;
import com.hostel.complaints.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final UserService userService;
    private final ComplaintService complaintService;

    public StaffController(UserService userService, ComplaintService complaintService) {
        this.userService = userService;
        this.complaintService = complaintService;
    }

    private User getStaffUser(UserDetails userDetails) {
        return userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Logged in staff not found"));
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User staff = getStaffUser(userDetails);
        List<Complaint> tasks = complaintService.getComplaintsByStaff(staff);

        long totalAssigned = tasks.size();
        long pending = tasks.stream().filter(t -> t.getStatus() == ComplaintStatus.ASSIGNED).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == ComplaintStatus.IN_PROGRESS || t.getStatus() == ComplaintStatus.REOPENED).count();
        long resolved = tasks.stream().filter(t -> t.getStatus() == ComplaintStatus.RESOLVED).count();

        model.addAttribute("staff", staff);
        model.addAttribute("totalAssigned", totalAssigned);
        model.addAttribute("pendingTasks", pending);
        model.addAttribute("inProgressTasks", inProgress);
        model.addAttribute("resolvedTasks", resolved);
        model.addAttribute("recentTasks", tasks.stream().limit(5).toList());

        return "staff/dashboard";
    }

    @GetMapping("/tasks")
    public String viewTasks(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam(value = "status", required = false) String statusFilter,
                            Model model) {
        User staff = getStaffUser(userDetails);
        List<Complaint> tasks = complaintService.getComplaintsByStaff(staff);

        if (statusFilter != null && !statusFilter.isBlank() && !statusFilter.equalsIgnoreCase("ALL")) {
            tasks = tasks.stream()
                    .filter(t -> t.getStatus().name().equalsIgnoreCase(statusFilter))
                    .toList();
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("selectedStatus", statusFilter != null ? statusFilter : "ALL");
        return "staff/tasks";
    }

    @GetMapping("/tasks/{id}")
    public String taskDetails(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable("id") Long id,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        User staff = getStaffUser(userDetails);
        Complaint task = complaintService.getComplaintById(id).orElse(null);

        if (task == null || task.getAssignedStaff() == null || !task.getAssignedStaff().getId().equals(staff.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Task not found or not assigned to you.");
            return "redirect:/staff/tasks";
        }

        model.addAttribute("task", task);
        model.addAttribute("staffStatuses", new ComplaintStatus[]{
                ComplaintStatus.ASSIGNED,
                ComplaintStatus.IN_PROGRESS,
                ComplaintStatus.RESOLVED
        });

        return "staff/task-details";
    }

    @PostMapping("/tasks/{id}/update")
    public String updateTaskStatus(@AuthenticationPrincipal UserDetails userDetails,
                                   @PathVariable("id") Long id,
                                   @RequestParam("status") ComplaintStatus status,
                                   @RequestParam("staffRemarks") String staffRemarks,
                                   RedirectAttributes redirectAttributes) {
        User staff = getStaffUser(userDetails);

        try {
            complaintService.updateStaffAction(id, status, staffRemarks, staff);
            redirectAttributes.addFlashAttribute("successMessage", "Task updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating task: " + e.getMessage());
        }

        return "redirect:/staff/tasks/" + id;
    }
}
