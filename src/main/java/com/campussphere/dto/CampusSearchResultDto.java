package com.campussphere.dto;

public class CampusSearchResultDto {
    private String resourceType;
    private Long id;
    private String title;
    private String summary;
    private double relevance;
    private String explanation;
    private String reference;
    public CampusSearchResultDto(String resourceType,Long id,String title,String summary,double relevance,String explanation,String reference){this.resourceType=resourceType;this.id=id;this.title=title;this.summary=summary;this.relevance=relevance;this.explanation=explanation;this.reference=reference;}
    public String getResourceType(){return resourceType;} public Long getId(){return id;} public String getTitle(){return title;} public String getSummary(){return summary;} public double getRelevance(){return relevance;} public String getExplanation(){return explanation;} public String getReference(){return reference;}
}
