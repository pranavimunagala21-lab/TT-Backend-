package com.campussphere.dto;

public class CampusSourceReferenceDto {
    private String resourceType;
    private Long id;
    private String title;
    private String reference;
    public CampusSourceReferenceDto(String resourceType,Long id,String title,String reference){this.resourceType=resourceType;this.id=id;this.title=title;this.reference=reference;}
    public String getResourceType(){return resourceType;} public Long getId(){return id;} public String getTitle(){return title;} public String getReference(){return reference;}
}
