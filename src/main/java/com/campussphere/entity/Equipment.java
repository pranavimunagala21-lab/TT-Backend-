package com.campussphere.entity;

import com.campussphere.entity.enums.EquipmentCondition;
import com.campussphere.entity.enums.EquipmentStatus;
import jakarta.persistence.*;

@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(unique = true)
    private String serialNumber;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Enumerated(EnumType.STRING)
    private EquipmentCondition conditionStatus = EquipmentCondition.EXCELLENT;

    private String location;

    @Column(length = 2000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status = EquipmentStatus.AVAILABLE;

    public Equipment() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(Integer availableQuantity) { this.availableQuantity = availableQuantity; }

    public EquipmentCondition getConditionStatus() { return conditionStatus; }
    public void setConditionStatus(EquipmentCondition conditionStatus) { this.conditionStatus = conditionStatus; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public EquipmentStatus getStatus() { return status; }
    public void setStatus(EquipmentStatus status) { this.status = status; }
}
