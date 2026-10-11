package com.campussphere.service;

import com.campussphere.dto.BookingCreateRequest;
import com.campussphere.dto.BookingDto;
import com.campussphere.dto.VenueAvailabilityDto;
import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    List<BookingDto> getBookings(String requesterEmail, boolean includeAll);
    BookingDto getBooking(Long id);
    BookingDto createBooking(BookingCreateRequest request, String requesterEmail);
    BookingDto cancelBooking(Long id, String requesterEmail, boolean manager);
    BookingDto approveBooking(Long id, String approverEmail);
    BookingDto rejectBooking(Long id, String approverEmail, String reason);
    List<BookingDto> getVenueBookings(Long venueId, LocalDateTime start, LocalDateTime end);
    List<VenueAvailabilityDto> searchAvailability(LocalDateTime start, LocalDateTime end);
}
