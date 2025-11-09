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
                "environment": "%s",
                "timestamp": "%s",
                "endpoints": {
                    "health": "/api/health",
                    "info": "/api/info",
                    "register": "/api/auth/register",
                    "login": "/api/auth/login",
                    "tasks": "/api/tasks/**"
                },
                "message": "Welcome to Todo App API - Server is running successfully"
            }
            """.formatted(getEnvironment(), java.time.LocalDateTime.now());
    }
    
    private String getEnvironment() {
        String profile = System.getenv("SPRING_PROFILES_ACTIVE");
        return profile != null ? profile : "development";
    }
}