package com.examly.springapp.controller;

import com.examly.springapp.Service.TaskService;
import com.examly.springapp.Service.UserService;
import com.examly.springapp.model.Task;
import com.examly.springapp.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userService.findByEmail(email);
    }

    @PostMapping("/add")
    public ResponseEntity<?> createTask(@RequestBody Task task, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            task.setUser(currentUser);
            
            if (task.getStatus() == null) {
                task.setStatus(Task.Status.PENDING);
            }
            if (task.getPriority() == null) {
                task.setPriority(Task.Priority.MEDIUM);
            }
            if (task.getArchived() == null) {
                task.setArchived(false);
            }
            
            Task createdTask = taskService.createTask(task);
            System.out.println("Task created: " + createdTask.getId());
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            System.err.println("Error creating task: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Task>> getAllTasks(Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        List<Task> tasks = taskService.getAllTasksByUser(currentUser);
        System.out.println("Fetched " + tasks.size() + " tasks for user: " + currentUser.getEmail());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        Optional<Task> task = taskService.getTaskById(id);
        
        if (task.isPresent() && task.get().getUser().getId().equals(currentUser.getId())) {
            return ResponseEntity.ok(task.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody Task taskDetails, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            Optional<Task> existingTask = taskService.getTaskById(id);
            
            if (existingTask.isPresent() && existingTask.get().getUser().getId().equals(currentUser.getId())) {
                Task updatedTask = taskService.updateTask(id, taskDetails);
                return ResponseEntity.ok(updatedTask);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            Optional<Task> task = taskService.getTaskById(id);
            
            if (task.isPresent() && task.get().getUser().getId().equals(currentUser.getId())) {
                taskService.deleteTask(id);
                return ResponseEntity.ok(Map.of("message", "Task deleted successfully"));
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable String status, Authentication authentication) {
        User currentUser = getCurrentUser(authentication);
        try {
            Task.Status taskStatus = Task.Status.valueOf(status.toUpperCase());
            List<Task> tasks = taskService.getTasksByUserAndStatus(currentUser, taskStatus);
            return ResponseEntity.ok(tasks);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // FIXED: Mark task as completed
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> markTaskComplete(@PathVariable Long id, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            Optional<Task> task = taskService.getTaskById(id);
            
            if (task.isPresent() && task.get().getUser().getId().equals(currentUser.getId())) {
                Task updatedTask = taskService.markTaskComplete(id);
                return ResponseEntity.ok(updatedTask);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error marking task complete: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // FIXED: Update task status with better error handling
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id, @RequestBody Map<String, String> statusData, Authentication authentication) {
        try {
            User currentUser = getCurrentUser(authentication);
            Optional<Task> task = taskService.getTaskById(id);
            
            if (!task.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            if (!task.get().getUser().getId().equals(currentUser.getId())) {
                return ResponseEntity.status(403).body(Map.of("error", "Access denied"));
            }
            
            String newStatus = statusData.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Status is required"));
            }
            
            try {
                Task.Status statusEnum = Task.Status.valueOf(newStatus.toUpperCase());
                Task updatedTask = taskService.updateTaskStatus(id, statusEnum);
                return ResponseEntity.ok(updatedTask);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid status: " + newStatus + ". Valid values: PENDING, IN_PROGRESS, COMPLETED"));
            }
        } catch (Exception e) {
            System.err.println("Error updating task status: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}