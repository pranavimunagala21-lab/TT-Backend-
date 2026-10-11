package com.campussphere.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String building;

    private String floor;

    @Column(nullable = false)
    private Integer capacity;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "venue_facilities", joinColumns = @JoinColumn(name = "venue_id"))
    @Column(name = "facility")
    private Set<String> facilities = new HashSet<>();

    @Column(length = 2000)
    private String description;

    private String status = "AVAILABLE"; // AVAILABLE, MAINTENANCE, RESERVED

    private String imageUrl;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "manager_id")
    private User manager;

    public Venue() {}

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

    public User getManager() { return manager; }
    public void setManager(User manager) { this.manager = manager; }
}
