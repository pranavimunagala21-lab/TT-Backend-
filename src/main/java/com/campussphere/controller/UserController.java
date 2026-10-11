package com.campussphere.controller;

import com.campussphere.dto.UserDto;
import com.campussphere.entity.enums.Role;
import com.campussphere.service.UserService;
import com.campussphere.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for managing user profiles, searching users, and updating skills")
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping
    @Operation(summary = "Search users with pagination & filters", description = "Search users by query, department, and role")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) Role role,
            @PageableDefault(size = 20) Pageable pageable,
            Authentication authentication) {
        boolean includeInactive = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        Page<UserDto> users = userService.searchUsers(query, department, role, pageable, includeInactive);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users list", description = "Retrieves all users without pagination")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user profile by ID", description = "Retrieves user profile details by ID")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user profile", description = "Updates user profile information")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto, Authentication authentication) {
        requireSelfOrAdmin(id, authentication);
        return ResponseEntity.ok(userService.updateUser(id, userDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate student account", description = "Revokes student sign-in while retaining linked campus history")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id, Authentication authentication) {
        UserDto current = authService.getCurrentUser(authentication.getName());
        if (current.getId().equals(id)) throw new org.springframework.security.access.AccessDeniedException("You cannot deactivate your own account from user management");
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/skills")
    @Operation(summary = "Get skills for a user", description = "Retrieves the set of skills associated with a user")
    public ResponseEntity<Set<String>> getUserSkills(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserSkills(id));
    }

    @PostMapping("/{id}/skills")
    @Operation(summary = "Add a skill to a user profile", description = "Adds a new skill to the user's skill set")
    public ResponseEntity<Set<String>> addUserSkill(@PathVariable Long id, @RequestParam String skill, Authentication authentication) {
        requireSelfOrAdmin(id, authentication);
        if (skill.isBlank()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Skill is required");
        return ResponseEntity.ok(userService.addUserSkill(id, skill));
    }

    @DeleteMapping("/{id}/skills/{skill}")
    @Operation(summary = "Remove a skill from a user profile", description = "Removes a skill from the user's skill set")
    public ResponseEntity<Set<String>> removeUserSkill(@PathVariable Long id, @PathVariable String skill, Authentication authentication) {
        requireSelfOrAdmin(id, authentication);
        return ResponseEntity.ok(userService.removeUserSkill(id, skill));
    }

    private void requireSelfOrAdmin(Long id, Authentication authentication) {
        UserDto current = authService.getCurrentUser(authentication.getName());
        if (!id.equals(current.getId()) && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            throw new org.springframework.security.access.AccessDeniedException("Users may modify only their own profile");
        }
    }
}
