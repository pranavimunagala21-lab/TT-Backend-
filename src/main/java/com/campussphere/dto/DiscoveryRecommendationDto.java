package com.campussphere.dto;

import java.util.List;

public class DiscoveryRecommendationDto {
    private String itemType;
    private Long itemId;
    private String title;
    private double score;
    private List<String> reasons;
    private ProjectDto project;
    private KnowledgeRecordDto knowledgeRecord;
    public DiscoveryRecommendationDto(String itemType, Long itemId, String title, double score, List<String> reasons, ProjectDto project, KnowledgeRecordDto knowledgeRecord) { this.itemType=itemType; this.itemId=itemId; this.title=title; this.score=score; this.reasons=reasons; this.project=project; this.knowledgeRecord=knowledgeRecord; }
    public String getItemType(){return itemType;} public Long getItemId(){return itemId;} public String getTitle(){return title;} public double getScore(){return score;} public List<String> getReasons(){return reasons;} public ProjectDto getProject(){return project;} public KnowledgeRecordDto getKnowledgeRecord(){return knowledgeRecord;}
}
