package com.campussphere.dto;

import com.campussphere.entity.Venue;
import java.util.Set;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VenueDto {
    private Long id;
    @NotBlank(message = "Venue name is required")
    private String name;
    @NotBlank(message = "Building is required")
    private String building;
    private String floor;
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    private Set<String> facilities;
    private String description;
    private String status;
    private String imageUrl;
    private Long managerId;
    private String managerName;

    public VenueDto() {}

    public VenueDto(Venue venue) {
        if (venue != null) {
            this.id = venue.getId();
            this.name = venue.getName();
            this.building = venue.getBuilding();
            this.floor = venue.getFloor();
            this.capacity = venue.getCapacity();
            this.facilities = venue.getFacilities();
            this.description = venue.getDescription();
            this.status = venue.getStatus();
            this.imageUrl = venue.getImageUrl();
            if (venue.getManager() != null) {
                this.managerId = venue.getManager().getId();
                this.managerName = venue.getManager().getName();
            }
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBuilding() { return building; }
    public void setBuilding(String building) { this.building = building; }

    public String getFloor() { return floor; }
    public void setFloor(String floor) { this.floor = floor; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Set<String> getFacilities() { return facilities; }
    public void setFacilities(Set<String> facilities) { this.facilities = facilities; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Long getManagerId() { return managerId; }
    public void setManagerId(Long managerId) { this.managerId = managerId; }
    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }
}
