package com.hostel.complaints.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hostel.complaints.dto.RegisterRequest;
import com.hostel.complaints.model.Complaint;
import com.hostel.complaints.service.ComplaintService;
import com.hostel.complaints.service.NoticeService;
import com.hostel.complaints.service.UserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final NoticeService noticeService;

    public AuthController(UserService userService, ComplaintService complaintService, NoticeService noticeService) {
        this.userService = userService;
        this.complaintService = complaintService;
        this.noticeService = noticeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("notices", noticeService.getActiveNotices());
        model.addAttribute("stats", complaintService.getDashboardStatistics());
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "logout", required = false) String logout,
                            Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "Invalid email or password. Please verify your credentials and try again.");
        }
        if (logout != null) {
            model.addAttribute("successMessage", "You have been safely signed out.");
        }
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("registerRequest") RegisterRequest registerRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (!registerRequest.isPasswordConfirmed()) {
            bindingResult.rejectValue("confirmPassword", "error.confirmPassword", "Passwords do not match. Please re-enter.");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            userService.registerStudent(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Registration successful! Welcome to CampusStay, " + registerRequest.getFullName() + ". Please sign in with your email and password.");
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/track")
    public String trackComplaint(@RequestParam(value = "complaintNumber", required = false) String complaintNumber,
                                 @RequestParam(value = "ticketNumber", required = false) String ticketNumber,
                                 Model model) {
        String query = (complaintNumber != null && !complaintNumber.isBlank()) ? complaintNumber : ticketNumber;
        if (query != null && !query.isBlank()) {
            String searchRef = query.trim().toUpperCase();
            Optional<Complaint> complaint = complaintService.getComplaintByNumber(searchRef);
            if (complaint.isPresent()) {
                model.addAttribute("complaint", complaint.get());
            } else {
                model.addAttribute("notFoundMessage", "No maintenance ticket found with reference ID: " + searchRef);
            }
            model.addAttribute("complaintNumber", searchRef);
        }
        return "auth/track";
    }
}
