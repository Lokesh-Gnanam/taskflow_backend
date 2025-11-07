package com.examly.springapp.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public String healthCheck() {
        return """
            {
                "status": "UP",
                "application": "Todo App Backend",
                "port": 8080,
                "timestamp": "%s",
                "message": "✅ Application is running successfully!"
            }
            """.formatted(java.time.LocalDateTime.now());
    }

    @GetMapping("/info")
    public String info() {
        return """
            {
                "application": "Advanced Todo App with Calendar & Reminders",
                "version": "1.0.0",
                "status": "Running",
                "port": 8080,
                "timestamp": "%s",
                "description": "A comprehensive task management system with calendar integration and reminders"
            }
            """.formatted(java.time.LocalDateTime.now());
    }

    @GetMapping("/test")
    public String test() {
        return "Test endpoint working! Server time: " + java.time.LocalDateTime.now();
    }
}