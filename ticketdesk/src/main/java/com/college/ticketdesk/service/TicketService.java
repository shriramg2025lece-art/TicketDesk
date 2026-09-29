package com.college.ticketdesk.service;

import com.college.ticketdesk.dto.TicketForm;
import com.college.ticketdesk.entity.*;
import com.college.ticketdesk.repository.CommentRepository;
import com.college.ticketdesk.repository.TicketRepository;
import com.college.ticketdesk.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public TicketService(TicketRepository ticketRepository, CommentRepository commentRepository,
                         UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public User currentUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    /** Staff and admin see every ticket, a student sees only his or her own. status may be null (= all). */
    public List<Ticket> visibleTickets(User user, Status status) {
        if (user.isStaff()) {
            return status == null
                    ? ticketRepository.findAllByOrderByCreatedAtDesc()
                    : ticketRepository.findByStatusOrderByCreatedAtDesc(status);
        }
        return status == null
                ? ticketRepository.findByCreatedByOrderByCreatedAtDesc(user)
                : ticketRepository.findByCreatedByAndStatusOrderByCreatedAtDesc(user, status);
    }

    public Ticket create(TicketForm form, User creator) {
        Ticket t = new Ticket();
        t.setTitle(form.getTitle().trim());
        t.setDescription(form.getDescription().trim());
        t.setCategory(form.getCategory());
        t.setPriority(form.getPriority());
        t.setCreatedBy(creator);
        return ticketRepository.save(t);
    }

    public Ticket get(Long id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));
    }

    public boolean canView(User user, Ticket ticket) {
        return user.isStaff() || ticket.getCreatedBy().getId().equals(user.getId());
    }

    public List<Comment> comments(Ticket ticket) {
        return commentRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    public void addComment(Ticket ticket, User author, String text) {
        if (text == null || text.isBlank()) {
            return;
        }
        String clean = text.trim();
        if (clean.length() > 1000) {
            clean = clean.substring(0, 1000);
        }
        commentRepository.save(new Comment(ticket, author, clean));
        touch(ticket);
    }

    public void updateStatus(Ticket ticket, Status status) {
        ticket.setStatus(status);
        touch(ticket);
    }

    public void assign(Ticket ticket, User staff) {
        ticket.setAssignedTo(staff);
        if (ticket.getStatus() == Status.OPEN) {
            ticket.setStatus(Status.IN_PROGRESS);
        }
        touch(ticket);
    }

    private void touch(Ticket ticket) {
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }
}
