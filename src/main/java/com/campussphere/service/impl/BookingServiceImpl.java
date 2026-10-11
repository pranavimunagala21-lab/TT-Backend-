package com.campussphere.service.impl;

import com.campussphere.dto.BookingCreateRequest;
import com.campussphere.dto.BookingDto;
import com.campussphere.dto.VenueAvailabilityDto;
import com.campussphere.entity.Booking;
import com.campussphere.entity.Project;
import com.campussphere.entity.User;
import com.campussphere.entity.Venue;
import com.campussphere.entity.enums.BookingStatus;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ForbiddenException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.BookingRepository;
import com.campussphere.repository.ProjectRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.repository.VenueRepository;
import com.campussphere.service.BookingService;
import com.campussphere.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final VenueRepository venueRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final NotificationService notifications;

    public BookingServiceImpl(BookingRepository bookingRepository, VenueRepository venueRepository,
                              UserRepository userRepository, ProjectRepository projectRepository, NotificationService notifications) {
        this.bookingRepository = bookingRepository;
        this.venueRepository = venueRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.notifications = notifications;
    }

    @Override
    public List<BookingDto> getBookings(String requesterEmail, boolean includeAll) {
        User user = userByEmail(requesterEmail);
        List<Booking> bookings = includeAll ? bookingRepository.findAll() : bookingRepository.findByUserId(user.getId());
        return bookings.stream().map(BookingDto::new).collect(Collectors.toList());
    }

    @Override
    public BookingDto getBooking(Long id) { return new BookingDto(bookingById(id)); }

    @Override
    @Transactional
    public BookingDto createBooking(BookingCreateRequest request, String requesterEmail) {
        if (request.getVenueId() == null) throw new BadRequestException("Venue ID is required");
        validateRange(request.getStartTime(), request.getEndTime());
        if (!request.getStartTime().isAfter(LocalDateTime.now())) throw new BadRequestException("Booking start time must be in the future");
        if (request.getPurpose() == null || request.getPurpose().isBlank()) throw new BadRequestException("Purpose is required");
        Venue venue = venueRepository.findByIdForUpdate(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + request.getVenueId()));
        if (!"AVAILABLE".equals(venue.getStatus())) throw new ConflictException("Venue is not available for booking");
        if (request.getAttendeesCount() == null || request.getAttendeesCount() < 1 || request.getAttendeesCount() > venue.getCapacity()) {
            throw new BadRequestException("Attendee count must be between 1 and venue capacity (" + venue.getCapacity() + ")");
        }
        if (!bookingRepository.findConflictingBookings(venue.getId(), request.getStartTime(), request.getEndTime()).isEmpty()) {
            throw new ConflictException("Venue already has a pending or approved booking during this time");
        }
        Booking booking = new Booking();
        booking.setVenue(venue);
        booking.setUser(userByEmail(requesterEmail));
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setPurpose(request.getPurpose().trim());
        booking.setAttendeesCount(request.getAttendeesCount());
        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.getProjectId()));
            booking.setProject(project);
        }
        booking.setStatus(BookingStatus.PENDING);
        return new BookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long id, String approverEmail) {
        Booking booking = bookingForUpdate(id);
        if (booking.getStatus() != BookingStatus.PENDING) throw new ConflictException("Only pending bookings can be approved");
        if (!booking.getStartTime().isAfter(LocalDateTime.now())) throw new ConflictException("A booking cannot be approved after its start time");
        Venue venue = venueRepository.findByIdForUpdate(booking.getVenue().getId()).orElseThrow();
        if (!"AVAILABLE".equals(venue.getStatus())) throw new ConflictException("Venue is not available");
        if (booking.getAttendeesCount() != null && booking.getAttendeesCount() > venue.getCapacity()) throw new ConflictException("Attendee count exceeds the venue's current capacity");
        if (!bookingRepository.findConflictingBookingsExcluding(venue.getId(), booking.getStartTime(), booking.getEndTime(), id).isEmpty()) {
            throw new ConflictException("Another pending or approved booking overlaps this booking");
        }
        booking.setStatus(BookingStatus.APPROVED);
        booking.setReviewedBy(userByEmail(approverEmail));
        booking.setReviewedAt(LocalDateTime.now());
        booking.setRejectionReason(null);
        notifications.notifyOnce(booking.getUser(), "Venue booking approved", "Your booking for " + booking.getVenue().getName() + " was approved.", "BOOKING", "/api/bookings/" + id, "booking:" + id + ":approved");
        return new BookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto rejectBooking(Long id, String approverEmail, String reason) {
        Booking booking = bookingForUpdate(id);
        if (booking.getStatus() != BookingStatus.PENDING) throw new ConflictException("Only pending bookings can be rejected");
        if (reason == null || reason.isBlank()) throw new BadRequestException("Rejection reason is required");
        booking.setStatus(BookingStatus.REJECTED);
        booking.setReviewedBy(userByEmail(approverEmail));
        booking.setReviewedAt(LocalDateTime.now());
        booking.setRejectionReason(reason.trim());
        notifications.notifyOnce(booking.getUser(), "Venue booking rejected", "Your booking for " + booking.getVenue().getName() + " was rejected: " + reason.trim(), "BOOKING", "/api/bookings/" + id, "booking:" + id + ":rejected");
        return new BookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto cancelBooking(Long id, String requesterEmail, boolean manager) {
        Booking booking = bookingForUpdate(id);
        User requester = userByEmail(requesterEmail);
        if (!manager && !booking.getUser().getId().equals(requester.getId())) throw new ForbiddenException("Only the booking owner or a resource manager can cancel this booking");
        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.APPROVED) throw new ConflictException("Only pending or approved bookings can be cancelled");
        booking.setStatus(BookingStatus.CANCELLED);
        if (manager) notifications.notifyOnce(booking.getUser(), "Venue booking cancelled", "Your booking for " + booking.getVenue().getName() + " was cancelled by a resource manager.", "BOOKING", "/api/bookings/" + id, "booking:" + id + ":cancelled");
        return new BookingDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDto> getVenueBookings(Long venueId, LocalDateTime start, LocalDateTime end) {
        validateRange(start, end);
        if (!venueRepository.existsById(venueId)) throw new ResourceNotFoundException("Venue not found with id: " + venueId);
        return bookingRepository.findByVenueIdAndStartTimeLessThanAndEndTimeGreaterThan(venueId, end, start)
                .stream().filter(b -> b.getStatus() == BookingStatus.PENDING || b.getStatus() == BookingStatus.APPROVED)
                .map(this::bookingSummary).collect(Collectors.toList());
    }

    @Override
    public List<VenueAvailabilityDto> searchAvailability(LocalDateTime start, LocalDateTime end) {
        validateRange(start, end);
        return venueRepository.findAll().stream().map(venue -> {
            List<BookingDto> conflicts = bookingRepository.findConflictingBookings(venue.getId(), start, end).stream().map(this::bookingSummary).collect(Collectors.toList());
            boolean available = "AVAILABLE".equals(venue.getStatus()) && conflicts.isEmpty();
            return new VenueAvailabilityDto(venue.getId(), venue.getName(), available, conflicts);
        }).collect(Collectors.toList());
    }

    private Booking bookingById(Long id) { return bookingRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id)); }
    private Booking bookingForUpdate(Long id) { return bookingRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + id)); }
    private User userByEmail(String email) { return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email)); }
    private void validateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !end.isAfter(start)) throw new BadRequestException("End time must be after start time");
    }
    private BookingDto bookingSummary(Booking booking) {
        BookingDto dto = new BookingDto(booking);
        dto.setUser(null);
        dto.setReviewedBy(null);
        dto.setRejectionReason(null);
        dto.setProjectId(null);
        dto.setProjectTitle(null);
        dto.setPurpose(null);
        return dto;
    }
}
