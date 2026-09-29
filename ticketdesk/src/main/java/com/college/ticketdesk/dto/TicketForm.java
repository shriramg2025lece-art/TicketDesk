package com.college.ticketdesk.dto;

import com.college.ticketdesk.entity.Category;
import com.college.ticketdesk.entity.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class TicketForm {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title can be at most 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description can be at most 2000 characters")
    private String description;

    @NotNull
    private Category category = Category.IT_SUPPORT;

    @NotNull
    private Priority priority = Priority.MEDIUM;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
}
