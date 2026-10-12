package com.campussphere.service.impl;

import com.campussphere.dto.VenueDto;
import com.campussphere.entity.User;
import com.campussphere.entity.Venue;
import com.campussphere.exception.BadRequestException;
import com.campussphere.exception.ConflictException;
import com.campussphere.exception.ResourceNotFoundException;
import com.campussphere.repository.BookingRepository;
import com.campussphere.repository.UserRepository;
import com.campussphere.repository.VenueRepository;
import com.campussphere.service.VenueService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import com.campussphere.entity.enums.BookingStatus;

@Service
public class VenueServiceImpl implements VenueService {
    private static final Set<String> STATUSES = Set.of("AVAILABLE", "MAINTENANCE", "RESERVED");
    private final VenueRepository venueRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public VenueServiceImpl(VenueRepository venueRepository, BookingRepository bookingRepository, UserRepository userRepository) {
        this.venueRepository = venueRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<VenueDto> findVenues(String query, String building, Integer minCapacity, String status) {
        if (minCapacity != null && minCapacity < 1) throw new BadRequestException("Minimum capacity must be at least 1");
        validateStatus(status);
        return venueRepository.searchVenues(blankToNull(query), blankToNull(building), minCapacity, status)
                .stream().map(VenueDto::new).collect(Collectors.toList());
    }

    @Override
    public VenueDto getVenue(Long id) {
        return new VenueDto(venueRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id)));
    }

    @Override
    @Transactional
    public VenueDto createVenue(VenueDto dto, String managerEmail) {
        validateVenue(dto);
        ensureUniqueVenue(dto, null);
        User manager = userRepository.findByEmail(managerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + managerEmail));
        Venue venue = new Venue();
        copy(dto, venue);
        venue.setManager(manager);
        venue.setStatus("AVAILABLE");
        return new VenueDto(venueRepository.save(venue));
    }

    @Override
    @Transactional
    public VenueDto updateVenue(Long id, VenueDto dto) {
        validateVenue(dto);
        Venue venue = venueRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
        ensureUniqueVenue(dto, id);
        List<BookingStatus> blockingStatuses = List.of(BookingStatus.PENDING, BookingStatus.APPROVED);
        if (dto.getCapacity() < venue.getCapacity()
                && bookingRepository.existsByVenueIdAndStatusInAndStartTimeAfterAndAttendeesCountGreaterThan(id, blockingStatuses, LocalDateTime.now(), dto.getCapacity())) {
            throw new ConflictException("Capacity cannot be reduced below attendee counts on future bookings");
        }
        copy(dto, venue);
        // Reservation status is derived from operations; clients cannot manually mark a venue reserved.
        if (dto.getStatus() != null) {
            if (!Set.of("AVAILABLE", "MAINTENANCE").contains(dto.getStatus())) throw new BadRequestException("Venue status may only be AVAILABLE or MAINTENANCE");
            if ("MAINTENANCE".equals(dto.getStatus())
                    && bookingRepository.existsByVenueIdAndStatusInAndStartTimeAfter(id, blockingStatuses, LocalDateTime.now())) {
                throw new ConflictException("Venue cannot enter maintenance while it has future bookings");
            }
            venue.setStatus(dto.getStatus());
        }
        return new VenueDto(venueRepository.save(venue));
    }

    @Override
    @Transactional
    public void deleteVenue(Long id) {
        Venue venue = venueRepository.findByIdForUpdate(id).orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
        if (bookingRepository.existsByVenueId(id)) throw new ConflictException("Venue has booking history and cannot be deleted");
        venueRepository.delete(venue);
    }

    private void copy(VenueDto dto, Venue venue) {
        venue.setName(dto.getName().trim());
        venue.setBuilding(dto.getBuilding().trim());
        venue.setFloor(dto.getFloor());
        venue.setCapacity(dto.getCapacity());
        venue.setFacilities(dto.getFacilities() == null ? new HashSet<>() : dto.getFacilities());
        venue.setDescription(dto.getDescription());
        venue.setImageUrl(dto.getImageUrl());
    }

    private void validateVenue(VenueDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Venue name is required");
        if (dto.getBuilding() == null || dto.getBuilding().isBlank()) throw new BadRequestException("Building is required");
        if (dto.getCapacity() == null || dto.getCapacity() < 1) throw new BadRequestException("Capacity must be at least 1");
    }

    private void ensureUniqueVenue(VenueDto dto, Long currentId) {
        boolean duplicate = currentId == null
                ? venueRepository.existsByNameIgnoreCaseAndBuildingIgnoreCase(dto.getName().trim(), dto.getBuilding().trim())
                : venueRepository.existsByNameIgnoreCaseAndBuildingIgnoreCaseAndIdNot(dto.getName().trim(), dto.getBuilding().trim(), currentId);
        if (duplicate) throw new ConflictException("A venue with this name already exists in this building");
    }

    private void validateStatus(String status) {
        if (status != null && !STATUSES.contains(status)) throw new BadRequestException("Unsupported venue status: " + status);
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
