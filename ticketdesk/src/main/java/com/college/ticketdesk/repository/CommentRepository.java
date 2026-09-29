package com.college.ticketdesk.repository;

import com.college.ticketdesk.entity.Comment;
import com.college.ticketdesk.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTicketOrderByCreatedAtAsc(Ticket ticket);
}
