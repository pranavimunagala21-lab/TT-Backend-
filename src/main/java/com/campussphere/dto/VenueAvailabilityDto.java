package com.campussphere.dto;

import java.time.LocalDateTime;
import java.util.List;

public class VenueAvailabilityDto {
    private Long venueId;
    private String venueName;
    private boolean isAvailable;
    private List<BookingDto> existingBookings;

    public VenueAvailabilityDto() {}

    public VenueAvailabilityDto(Long venueId, String venueName, boolean isAvailable, List<BookingDto> existingBookings) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.isAvailable = isAvailable;
        this.existingBookings = existingBookings;
    }

    public Long getVenueId() { return venueId; }
    public void setVenueId(Long venueId) { this.venueId = venueId; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }

    public List<BookingDto> getExistingBookings() { return existingBookings; }
    public void setExistingBookings(List<BookingDto> existingBookings) { this.existingBookings = existingBookings; }
}
