package com.campussphere.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="ai")
public class AiProperties {
    private String provider="disabled";
    private String apiKey="";
    private String baseUrl="https://api.openai.com/v1";
    private String model="gpt-4o-mini";
    private String embeddingModel="text-embedding-3-small";
    private int timeoutSeconds=20;
    private int maxCandidates=500;
    private int maxIndexRecords=250;
    private int maxContextSources=8;
    public String getProvider(){return provider;} public void setProvider(String provider){this.provider=provider;}
    public String getApiKey(){return apiKey;} public void setApiKey(String apiKey){this.apiKey=apiKey;}
    public String getBaseUrl(){return baseUrl;} public void setBaseUrl(String baseUrl){this.baseUrl=baseUrl;}
    public String getModel(){return model;} public void setModel(String model){this.model=model;}
    public String getEmbeddingModel(){return embeddingModel;} public void setEmbeddingModel(String embeddingModel){this.embeddingModel=embeddingModel;}
    public int getTimeoutSeconds(){return timeoutSeconds;} public void setTimeoutSeconds(int timeoutSeconds){this.timeoutSeconds=timeoutSeconds;}
    public int getMaxCandidates(){return maxCandidates;} public void setMaxCandidates(int maxCandidates){this.maxCandidates=maxCandidates;}
    public int getMaxIndexRecords(){return maxIndexRecords;} public void setMaxIndexRecords(int maxIndexRecords){this.maxIndexRecords=maxIndexRecords;}
    public int getMaxContextSources(){return maxContextSources;} public void setMaxContextSources(int maxContextSources){this.maxContextSources=maxContextSources;}
}
