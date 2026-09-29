package com.college.ticketdesk.controller;

import com.college.ticketdesk.dto.TicketForm;
import com.college.ticketdesk.entity.*;
import com.college.ticketdesk.repository.UserRepository;
import com.college.ticketdesk.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
public class TicketController {

    private final TicketService ticketService;
    private final UserRepository userRepository;

    public TicketController(TicketService ticketService, UserRepository userRepository) {
        this.ticketService = ticketService;
        this.userRepository = userRepository;
    }

    // values for the drop-down lists, available in every page of this controller
    @ModelAttribute("categories")
    public Category[] categories() { return Category.values(); }

    @ModelAttribute("priorities")
    public Priority[] priorities() { return Priority.values(); }

    @ModelAttribute("statuses")
    public Status[] statuses() { return Status.values(); }

    @GetMapping("/tickets")
    public String list(@RequestParam(required = false) Status status, Principal principal, Model model) {
        User me = ticketService.currentUser(principal.getName());
        model.addAttribute("tickets", ticketService.visibleTickets(me, status));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("activePage", "tickets");
        return "tickets";
    }

    @GetMapping("/tickets/new")
    public String newForm(Model model) {
        model.addAttribute("form", new TicketForm());
        model.addAttribute("activePage", "new");
        return "ticket-form";
    }

    @PostMapping("/tickets")
    public String create(@Valid @ModelAttribute("form") TicketForm form, BindingResult result,
                         Principal principal, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("activePage", "new");
            return "ticket-form";
        }
        User me = ticketService.currentUser(principal.getName());
        Ticket saved = ticketService.create(form, me);
        return "redirect:/tickets/" + saved.getId() + "?created";
    }

    @GetMapping("/tickets/{id}")
    public String detail(@PathVariable Long id, Principal principal, Model model) {
        User me = ticketService.currentUser(principal.getName());
        Ticket ticket = ticketService.get(id);
        if (!ticketService.canView(me, ticket)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot view this ticket");
        }
        model.addAttribute("ticket", ticket);
        model.addAttribute("comments", ticketService.comments(ticket));
        model.addAttribute("isStaff", me.isStaff());
        model.addAttribute("staffUsers", userRepository.findByRoleIn(List.of(Role.STAFF, Role.ADMIN)));
        model.addAttribute("activePage", "tickets");
        return "ticket-detail";
    }

    @PostMapping("/tickets/{id}/comments")
    public String addComment(@PathVariable Long id, @RequestParam String text, Principal principal) {
        User me = ticketService.currentUser(principal.getName());
        Ticket ticket = ticketService.get(id);
        if (!ticketService.canView(me, ticket)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot comment on this ticket");
        }
        ticketService.addComment(ticket, me, text);
        return "redirect:/tickets/" + id;
    }

    // ---- staff / admin only (see SecurityConfig) ----
    @PostMapping("/tickets/{id}/status")
    public String changeStatus(@PathVariable Long id, @RequestParam Status status) {
        ticketService.updateStatus(ticketService.get(id), status);
        return "redirect:/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/assign")
    public String assign(@PathVariable Long id, @RequestParam Long assigneeId) {
        User staff = userRepository.findById(assigneeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown user"));
        ticketService.assign(ticketService.get(id), staff);
        return "redirect:/tickets/" + id;
    }
}
