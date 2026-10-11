package com.campussphere.dto;

import java.util.List;

public class AIQueryResponse {
    private String answer;
    private List<String> sources;
    private List<RecommendationDto> recommendations;
    private List<CampusSourceReferenceDto> sourceReferences;

    public AIQueryResponse() {}

    public AIQueryResponse(String answer, List<String> sources, List<RecommendationDto> recommendations) {
        this.answer = answer;
        this.sources = sources;
        this.recommendations = recommendations;
    }

    public AIQueryResponse(String answer, List<String> sources, List<RecommendationDto> recommendations, List<CampusSourceReferenceDto> sourceReferences) {
        this(answer, sources, recommendations);
        this.sourceReferences = sourceReferences;
    }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public List<String> getSources() { return sources; }
    public void setSources(List<String> sources) { this.sources = sources; }

    public List<RecommendationDto> getRecommendations() { return recommendations; }
    public void setRecommendations(List<RecommendationDto> recommendations) { this.recommendations = recommendations; }
    public List<CampusSourceReferenceDto> getSourceReferences() { return sourceReferences; }
    public void setSourceReferences(List<CampusSourceReferenceDto> sourceReferences) { this.sourceReferences = sourceReferences; }
}
