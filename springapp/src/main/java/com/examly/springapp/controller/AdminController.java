// package com.examly.springapp.controller;

// import com.examly.springapp.model.User;
// import com.examly.springapp.Service.UserService;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.web.bind.annotation.*;

// import java.util.List;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/admin")
// @PreAuthorize("hasRole('ADMIN')")
// public class AdminController {

//     @Autowired
//     private UserService userService;

//     @GetMapping("/users")
//     public ResponseEntity<List<User>> getAllUsers() {
//         try {
//             List<User> users = userService.getAllUsers();
//             System.out.println("Admin: Fetching " + users.size() + " users");
//             return ResponseEntity.ok(users);
//         } catch (Exception e) {
//             System.err.println("Error fetching users: " + e.getMessage());
//             return ResponseEntity.internalServerError().build();
//         }
//     }

//     @PutMapping("/users/{userId}/status")
//     public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Boolean> status) {
//         try {
//             Boolean active = status.get("active");
//             if (active == null) {
//                 return ResponseEntity.badRequest().body(Map.of("error", "Active status is required"));
//             }
            
//             User user = userService.updateUserStatus(userId, active);
//             return ResponseEntity.ok(user);
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//         }
//     }

//     @DeleteMapping("/users/{userId}")
//     public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
//         try {
//             userService.deleteUser(userId);
//             return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
//         } catch (Exception e) {
//             return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//         }
//     }

//     @GetMapping("/users/count")
//     public ResponseEntity<Map<String, Long>> getUserCount() {
//         try {
//             long count = userService.getAllUsers().size();
//             return ResponseEntity.ok(Map.of("count", count));
//         } catch (Exception e) {
//             System.err.println("Error counting users: " + e.getMessage());
//             return ResponseEntity.internalServerError().build();
//         }
//     }
// }  


package com.examly.springapp.controller;

import com.examly.springapp.model.User;
import com.examly.springapp.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            System.out.println("Admin: Fetching " + users.size() + " users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long userId, @RequestBody Map<String, Boolean> status) {
        try {
            Boolean active = status.get("active");
            if (active == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "Active status is required"));
            }
            
            User user = userService.updateUserStatus(userId, active);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<Map<String, Long>> getUserCount() {
        try {
            long count = userService.getAllUsers().size();
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            System.err.println("Error counting users: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    // NEW: Promote/Demote user role endpoint
    @PutMapping("/users/{userId}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long userId, @RequestBody Map<String, String> roleData) {
        try {
            String newRole = roleData.get("role");
            if (newRole == null || newRole.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Role is required"));
            }
            
            try {
                User.Role roleEnum = User.Role.valueOf(newRole.toUpperCase());
                User updatedUser = userService.updateUserRole(userId, roleEnum);
                return ResponseEntity.ok(updatedUser);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(Map.of("error", "Invalid role: " + newRole + ". Valid values: USER, ADMIN"));
            }
        } catch (Exception e) {
            System.err.println("Error updating user role: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}