package com.examly.springapp.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20) // Increased length for status
    private Status status = Status.PENDING;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Priority priority = Priority.MEDIUM;
    
    @Column(name = "deadline_date")
    private LocalDate deadlineDate;
    
    @Transient
    private String date;
    
    @Column(name = "reminder_time", length = 50)
    private String reminderTime;
    
    private Boolean archived = false;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    public enum Status {
        PENDING, IN_PROGRESS, COMPLETED
    }
    
    public enum Priority {
        HIGH, MEDIUM, LOW
    }
    
    public Task() {}
    
    public Task(String title, String description, Status status, Priority priority, 
                LocalDate deadlineDate, String reminderTime, User user) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
        this.deadlineDate = deadlineDate;
        this.reminderTime = reminderTime;
        this.user = user;
        this.archived = false;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    
    public void setStatus(String status) { 
        try {
            this.status = Status.valueOf(status.toUpperCase()); 
        } catch (IllegalArgumentException e) {
            this.status = Status.PENDING;
        }
    }
    
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    
    public void setPriority(String priority) { 
        try {
            this.priority = Priority.valueOf(priority.toUpperCase()); 
        } catch (IllegalArgumentException e) {
            this.priority = Priority.MEDIUM;
        }
    }
    
    public LocalDate getDeadlineDate() { return deadlineDate; }
    public void setDeadlineDate(LocalDate deadlineDate) { this.deadlineDate = deadlineDate; }
    
    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }
    
    public Boolean getArchived() { return archived; }
    public void setArchived(Boolean archived) { this.archived = archived; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getDate() {
        if (deadlineDate != null) {
            return deadlineDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
        if (date != null && !date.isEmpty()) {
            try {
                this.deadlineDate = LocalDate.parse(date);
            } catch (Exception e) {
                System.err.println("Invalid date format: " + date);
            }
        }
    }
}