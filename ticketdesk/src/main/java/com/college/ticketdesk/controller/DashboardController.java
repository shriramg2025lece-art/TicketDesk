package com.college.ticketdesk.controller;

import com.college.ticketdesk.entity.Category;
import com.college.ticketdesk.entity.Status;
import com.college.ticketdesk.entity.Ticket;
import com.college.ticketdesk.entity.User;
import com.college.ticketdesk.service.TicketService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {

    private final TicketService ticketService;

    public DashboardController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        User me = ticketService.currentUser(principal.getName());
        List<Ticket> tickets = ticketService.visibleTickets(me, null);

        // Pie chart data: tickets per status
        List<String> statusLabels = new ArrayList<>();
        List<Long> statusCounts = new ArrayList<>();
        for (Status s : Status.values()) {
            statusLabels.add(s.name());
            statusCounts.add(tickets.stream().filter(t -> t.getStatus() == s).count());
        }

        // Bar chart data: tickets per category
        List<String> categoryLabels = new ArrayList<>();
        List<Long> categoryCounts = new ArrayList<>();
        for (Category c : Category.values()) {
            categoryLabels.add(c.name());
            categoryCounts.add(tickets.stream().filter(t -> t.getCategory() == c).count());
        }

        model.addAttribute("total", tickets.size());
        model.addAttribute("open", statusCounts.get(0));
        model.addAttribute("inProgress", statusCounts.get(1));
        model.addAttribute("resolved", statusCounts.get(2));
        model.addAttribute("closed", statusCounts.get(3));
        model.addAttribute("statusLabels", statusLabels);
        model.addAttribute("statusCounts", statusCounts);
        model.addAttribute("categoryLabels", categoryLabels);
        model.addAttribute("categoryCounts", categoryCounts);
        model.addAttribute("recent", tickets.stream().limit(5).toList());
        model.addAttribute("activePage", "dashboard");
        return "dashboard";
    }
}
