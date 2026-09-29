package com.college.ticketdesk.controller;

import com.college.ticketdesk.entity.Role;
import com.college.ticketdesk.entity.User;
import com.college.ticketdesk.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Admin-only user management (see /admin/** rule in SecurityConfig). */
@Controller
public class UserAdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAdminController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAllByOrderByUsernameAsc());
        model.addAttribute("roles", Role.values());
        model.addAttribute("activePage", "users");
        return "users";
    }

    @PostMapping("/admin/users")
    public String create(@RequestParam String fullName, @RequestParam String username,
                         @RequestParam String password, @RequestParam Role role,
                         RedirectAttributes redirect) {
        String uname = username.trim();
        if (uname.length() < 3 || fullName.isBlank() || password.length() < 6) {
            redirect.addFlashAttribute("error", "Enter a name, a username (3+ chars) and a password (6+ chars).");
        } else if (userRepository.existsByUsername(uname)) {
            redirect.addFlashAttribute("error", "Username already exists.");
        } else {
            userRepository.save(new User(uname, fullName.trim(), passwordEncoder.encode(password), role));
            redirect.addFlashAttribute("message", "User " + uname + " created.");
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/{id}/toggle")
    public String toggle(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            redirect.addFlashAttribute("error", "User not found.");
        } else if (user.getUsername().equals(principal.getName())) {
            redirect.addFlashAttribute("error", "You cannot disable your own account.");
        } else {
            user.setEnabled(!user.isEnabled());
            userRepository.save(user);
            redirect.addFlashAttribute("message", "User " + user.getUsername()
                    + (user.isEnabled() ? " enabled." : " disabled."));
        }
        return "redirect:/admin/users";
    }
}
