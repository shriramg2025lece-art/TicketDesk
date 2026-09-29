package com.college.ticketdesk.config;

import com.college.ticketdesk.entity.*;
import com.college.ticketdesk.repository.CommentRepository;
import com.college.ticketdesk.repository.TicketRepository;
import com.college.ticketdesk.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Creates demo accounts and a few sample tickets on the first run. */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, TicketRepository ticketRepository,
                           CommentRepository commentRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        User admin = createUserIfMissing(adminUsername, "System Admin", adminPassword, Role.ADMIN);
        User staff = createUserIfMissing("staff", "Ravi Kumar (Support Staff)", "staff123", Role.STAFF);
        User student = createUserIfMissing("student", "Priya Sharma", "student123", Role.STUDENT);

        if (ticketRepository.count() == 0) {
            createTicket("Wi-Fi not working in Hostel Block B", "The Wi-Fi has been down on the 2nd floor since morning.",
                    Category.HOSTEL, Priority.HIGH, Status.IN_PROGRESS, student, staff);
            createTicket("Unable to access library e-journals", "Login to the e-journal portal shows an error.",
                    Category.LIBRARY, Priority.MEDIUM, Status.OPEN, student, null);
            createTicket("Projector not working in Lab 3", "The projector does not turn on during practical sessions.",
                    Category.LAB, Priority.MEDIUM, Status.RESOLVED, student, staff);
            createTicket("Student portal password reset", "I forgot my portal password and the reset mail does not arrive.",
                    Category.IT_SUPPORT, Priority.LOW, Status.CLOSED, student, admin);
            createTicket("Timetable clash for Semester 5", "Two subjects are scheduled at the same time on Thursday.",
                    Category.ACADEMIC, Priority.HIGH, Status.OPEN, student, null);
            createTicket("Water cooler repair near canteen", "The water cooler is leaking.",
                    Category.OTHER, Priority.LOW, Status.OPEN, student, null);
        }
    }

    private User createUserIfMissing(String username, String fullName, String rawPassword, Role role) {
        return userRepository.findByUsername(username).orElseGet(() ->
                userRepository.save(new User(username, fullName, passwordEncoder.encode(rawPassword), role)));
    }

    private void createTicket(String title, String description, Category category, Priority priority,
                              Status status, User createdBy, User assignedTo) {
        Ticket t = new Ticket();
        t.setTitle(title);
        t.setDescription(description);
        t.setCategory(category);
        t.setPriority(priority);
        t.setStatus(status);
        t.setCreatedBy(createdBy);
        t.setAssignedTo(assignedTo);
        t = ticketRepository.save(t);
        if (assignedTo != null) {
            commentRepository.save(new Comment(t, assignedTo, "We are looking into this."));
        }
    }
}
