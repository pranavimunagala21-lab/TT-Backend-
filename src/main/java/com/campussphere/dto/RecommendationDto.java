package com.campussphere.dto;

public class RecommendationDto {
    private String resource;
    private Double score;
    private String reason;
    private String itemType;
    private Long itemId;

    public RecommendationDto() {}

    public RecommendationDto(String resource, Double score, String reason) {
        this.resource = resource;
        this.score = score;
        this.reason = reason;
    }

    public RecommendationDto(String resource, Double score, String reason, String itemType, Long itemId) {
        this.resource = resource;
        this.score = score;
        this.reason = reason;
        this.itemType = itemType;
        this.itemId = itemId;
    }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
}
