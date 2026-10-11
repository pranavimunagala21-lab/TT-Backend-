package com.campussphere.controller;

import com.campussphere.dto.NotificationDto;
import com.campussphere.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@Tag(name="Notifications", description="Persistent in-app notifications for the authenticated user")
@PreAuthorize("isAuthenticated()")
public class NotificationController {
    private final NotificationService service;
    public NotificationController(NotificationService service) { this.service=service; }
    @GetMapping @Operation(summary="List my notifications")
    public Page<NotificationDto> list(Authentication auth,@PageableDefault(size=20,sort="createdAt",direction=Sort.Direction.DESC) Pageable pageable) { return service.list(auth.getName(),pageable); }
    @GetMapping("/unread-count") @Operation(summary="Get my unread notification count")
    public Map<String,Long> unreadCount(Authentication auth) { return Map.of("unreadCount",service.unreadCount(auth.getName())); }
    @PatchMapping("/{id}/read") @Operation(summary="Mark one of my notifications as read")
    public ResponseEntity<NotificationDto> markRead(@PathVariable Long id,Authentication auth) { return ResponseEntity.ok(service.markRead(id,auth.getName())); }
}
