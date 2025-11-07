package com.examly.springapp.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class RootController {

    @GetMapping("/")
    public String home() {
        return """
            {
                "application": "Advanced Todo App with Calendar & Reminders",
                "status": "Running",
                "version": "1.0.0",
                "timestamp": "%s",
                "endpoints": {
                    "health": "/api/health",
                    "info": "/api/info",
                    "register": "/api/auth/register",
                    "login": "/api/auth/login",
                    "tasks": "/api/tasks/**"
                },
                "message": "Welcome to Todo App API - Server is running on port 8080"
            }
            """.formatted(java.time.LocalDateTime.now());
    }
}