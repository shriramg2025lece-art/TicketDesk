package com.college.ticketdesk.repository;

import com.college.ticketdesk.entity.Status;
import com.college.ticketdesk.entity.Ticket;
import com.college.ticketdesk.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findAllByOrderByCreatedAtDesc();
    List<Ticket> findByStatusOrderByCreatedAtDesc(Status status);
    List<Ticket> findByCreatedByOrderByCreatedAtDesc(User createdBy);
    List<Ticket> findByCreatedByAndStatusOrderByCreatedAtDesc(User createdBy, Status status);
}
