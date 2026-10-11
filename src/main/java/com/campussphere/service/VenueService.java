package com.campussphere.service;

import com.campussphere.dto.VenueDto;
import java.util.List;

public interface VenueService {
    List<VenueDto> findVenues(String query, String building, Integer minCapacity, String status);
    VenueDto getVenue(Long id);
    VenueDto createVenue(VenueDto dto, String managerEmail);
    VenueDto updateVenue(Long id, VenueDto dto);
    void deleteVenue(Long id);
}
