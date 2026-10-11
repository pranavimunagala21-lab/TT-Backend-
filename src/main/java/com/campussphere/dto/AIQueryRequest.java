package com.campussphere.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIQueryRequest {
    @NotBlank(message = "Query string cannot be blank")
    @Size(max = 1000, message = "Query must be at most 1000 characters")
    private String query;

    public AIQueryRequest() {}

    public AIQueryRequest(String query) {
        this.query = query;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
