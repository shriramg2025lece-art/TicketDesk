package com.college.ticketdesk.controller;

import com.college.ticketdesk.dto.RegisterForm;
import com.college.ticketdesk.entity.Role;
import com.college.ticketdesk.entity.User;
import com.college.ticketdesk.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegisterForm form, BindingResult result) {
        if (!result.hasErrors() && userRepository.existsByUsername(form.getUsername().trim())) {
            result.rejectValue("username", "duplicate", "This username is already taken");
        }
        if (result.hasErrors()) {
            return "register";
        }
        // self-registration always creates a STUDENT account
        userRepository.save(new User(form.getUsername().trim(), form.getFullName().trim(),
                passwordEncoder.encode(form.getPassword()), Role.STUDENT));
        return "redirect:/login?registered";
    }
}
