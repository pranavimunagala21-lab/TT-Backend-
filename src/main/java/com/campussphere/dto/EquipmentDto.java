package com.campussphere.dto;

import com.campussphere.entity.Equipment;
import com.campussphere.entity.enums.EquipmentCondition;
import com.campussphere.entity.enums.EquipmentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EquipmentDto {
    private Long id;
    @NotBlank(message = "Equipment name is required")
    private String name;
    @NotBlank(message = "Equipment category is required")
    private String category;
    private String serialNumber;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    private Integer availableQuantity;
    private EquipmentCondition conditionStatus;
    private String location;
    private String description;
    private String imageUrl;
    private EquipmentStatus status;

    public EquipmentDto() {}

    public EquipmentDto(Equipment equipment) {
        if (equipment != null) {
            this.id = equipment.getId();
            this.name = equipment.getName();
            this.category = equipment.getCategory();
            this.serialNumber = equipment.getSerialNumber();
            this.quantity = equipment.getQuantity();
            this.availableQuantity = equipment.getAvailableQuantity();
            this.conditionStatus = equipment.getConditionStatus();
            this.location = equipment.getLocation();
            this.description = equipment.getDescription();
            this.imageUrl = equipment.getImageUrl();
            this.status = equipment.getStatus();
        }
    }

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
