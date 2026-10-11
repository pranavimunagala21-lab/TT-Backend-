package com.campussphere.controller;

import com.campussphere.dto.BookingCreateRequest;
import com.campussphere.dto.BookingDecisionRequest;
import com.campussphere.dto.BookingDto;
import com.campussphere.dto.VenueAvailabilityDto;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@Tag(name = "Venue Bookings", description = "Create, inspect, approve, reject, and cancel venue reservations")
public class BookingController {
    private final BookingService bookingService;
    public BookingController(BookingService bookingService) { this.bookingService = bookingService; }

    @GetMapping
    @Operation(summary = "List bookings", description = "List the authenticated user's bookings, or all bookings for resource managers")
    public ResponseEntity<List<BookingDto>> list(Authentication authentication) {
        boolean manager = hasManagerRole(authentication);
        return ResponseEntity.ok(bookingService.getBookings(authentication.getName(), manager));
    }

    @GetMapping("/availability")
    @Operation(summary = "Search venue availability", description = "Find venues without overlapping pending or approved bookings in a time range")
    public ResponseEntity<List<VenueAvailabilityDto>> availability(@RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        return ResponseEntity.ok(bookingService.searchAvailability(startTime, endTime));
    }

    @GetMapping("/venue/{venueId}")
    @Operation(summary = "List venue bookings", description = "List pending or approved bookings overlapping the requested time range")
    public ResponseEntity<List<BookingDto>> venueBookings(@PathVariable Long venueId, @RequestParam LocalDateTime startTime, @RequestParam LocalDateTime endTime) {
        return ResponseEntity.ok(bookingService.getVenueBookings(venueId, startTime, endTime));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get booking", description = "Get a booking owned by the caller or accessible to a resource manager")
    public ResponseEntity<BookingDto> get(@PathVariable Long id, Authentication authentication) {
        BookingDto booking = bookingService.getBooking(id);
        if (!hasManagerRole(authentication) && !authentication.getName().equalsIgnoreCase(booking.getUser().getEmail())) throw new ForbiddenException("You can only view your own bookings");
        return ResponseEntity.ok(booking);
    }

    @PostMapping
    @Operation(summary = "Create booking request", description = "Submit a venue booking request; overlapping pending or approved requests are rejected")
    public ResponseEntity<BookingDto> create(@Valid @RequestBody BookingCreateRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request, authentication.getName()));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking", description = "Cancel a pending or approved booking as its owner or a resource manager")
    public ResponseEntity<BookingDto> cancel(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(bookingService.cancelBooking(id, authentication.getName(), hasManagerRole(authentication)));
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'RESOURCE_MANAGER')")
    @Operation(summary = "Approve booking", description = "Approve a pending booking after checking venue conflicts")
    public ResponseEntity<BookingDto> approve(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(bookingService.approveBooking(id, authentication.getName()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN', 'FACULTY', 'RESOURCE_MANAGER')")
    @Operation(summary = "Reject booking", description = "Reject a pending booking with a required reason")
    public ResponseEntity<BookingDto> reject(@PathVariable Long id, @Valid @RequestBody BookingDecisionRequest request, Authentication authentication) {
        return ResponseEntity.ok(bookingService.rejectBooking(id, authentication.getName(), request.getReason()));
    }

    private boolean hasManagerRole(Authentication authentication) {
        return authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_FACULTY") || a.getAuthority().equals("ROLE_RESOURCE_MANAGER"));
    }
}
