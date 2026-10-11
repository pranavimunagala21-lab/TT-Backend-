package com.campussphere.dto;

import java.util.Map;

public class SearchResultDto {
    private String type; // PROJECT, CLUB, USER, VENUE, EQUIPMENT, KNOWLEDGE
    private Long id;
    private String title;
    private String description;
    private Double score;
    private Map<String, Object> metadata;

    public SearchResultDto() {}

    public SearchResultDto(String type, Long id, String title, String description, Double score, Map<String, Object> metadata) {
        this.type = type;
        this.id = id;
        this.title = title;
        this.description = description;
        this.score = score;
        this.metadata = metadata;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }
}
