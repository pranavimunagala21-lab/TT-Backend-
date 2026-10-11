package com.campussphere.service.impl;

import com.campussphere.config.AiProperties;
import com.campussphere.exception.AiProviderException;
import com.campussphere.service.LlmProvider;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.*;

public class OpenAiCompatibleProvider implements LlmProvider {
    private final AiProperties properties;
    private final RestClient client;

    public OpenAiCompatibleProvider(AiProperties properties){
        this.properties=properties;
        HttpClient http=HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(Math.max(1,properties.getTimeoutSeconds()))).build();
        JdkClientHttpRequestFactory factory=new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(Math.max(1,properties.getTimeoutSeconds())));
        String base=properties.getBaseUrl()==null?"":properties.getBaseUrl().replaceAll("/+$","");
        this.client=RestClient.builder().baseUrl(base).requestFactory(factory).defaultHeader("Authorization","Bearer "+(properties.getApiKey()==null?"":properties.getApiKey())).build();
    }

    @Override public String answer(String systemPrompt,String userPrompt){
        requireKey();
        Map<String,Object> request=Map.of("model",properties.getModel(),"temperature",0.1,"max_tokens",700,
                "messages",List.of(Map.of("role","system","content",systemPrompt),Map.of("role","user","content",userPrompt)));
        JsonNode response=post("/chat/completions",request);
        JsonNode content=response.path("choices").path(0).path("message").path("content");
        if(!content.isTextual()||content.asText().isBlank())throw invalidResponse();
        return content.asText().trim();
    }

    @Override public List<List<Double>> embed(List<String> texts){
        requireKey(); if(texts==null||texts.isEmpty())return List.of();
        JsonNode response=post("/embeddings",Map.of("model",properties.getEmbeddingModel(),"input",texts));
        JsonNode data=response.path("data"); if(!data.isArray()||data.size()!=texts.size())throw invalidResponse();
        List<List<Double>> vectors=new ArrayList<>();
        for(JsonNode row:data){JsonNode vector=row.path("embedding");if(!vector.isArray()||vector.isEmpty())throw invalidResponse();List<Double> values=new ArrayList<>();for(JsonNode v:vector){if(!v.isNumber()||!Double.isFinite(v.asDouble()))throw invalidResponse();values.add(v.asDouble());}vectors.add(values);}
        return vectors;
    }
    @Override public String providerName(){return "openai-compatible";}

    private JsonNode post(String path,Object body){
        try {JsonNode response=client.post().uri(path).contentType(MediaType.APPLICATION_JSON).body(body).retrieve().body(JsonNode.class);if(response==null||response.isMissingNode())throw invalidResponse();return response;}
        catch(RestClientResponseException ex){int status=ex.getStatusCode().value();if(status==429)throw new AiProviderException(HttpStatus.TOO_MANY_REQUESTS,"AI_RATE_LIMITED","AI provider rate limit reached; retry later");if(status==401||status==403)throw new AiProviderException(HttpStatus.SERVICE_UNAVAILABLE,"AI_PROVIDER_AUTH","AI provider credentials were rejected");if(status==400||status==422)throw new AiProviderException(HttpStatus.BAD_GATEWAY,"AI_PROVIDER_INVALID_REQUEST","AI provider rejected the configured model or request");throw new AiProviderException(HttpStatus.SERVICE_UNAVAILABLE,"AI_PROVIDER_UNAVAILABLE","AI provider is temporarily unavailable");}
        catch(ResourceAccessException ex){throw new AiProviderException(HttpStatus.GATEWAY_TIMEOUT,"AI_PROVIDER_TIMEOUT","AI provider request timed out or could not be reached");}
        catch(AiProviderException ex){throw ex;}
        catch(RuntimeException ex){throw new AiProviderException(HttpStatus.BAD_GATEWAY,"AI_PROVIDER_INVALID_RESPONSE","AI provider returned an invalid response");}
    }
    private void requireKey(){if(properties.getApiKey()==null||properties.getApiKey().isBlank())throw new AiProviderException(HttpStatus.SERVICE_UNAVAILABLE,"AI_NOT_CONFIGURED","AI provider is enabled but no API key is configured");}
    private AiProviderException invalidResponse(){return new AiProviderException(HttpStatus.BAD_GATEWAY,"AI_PROVIDER_INVALID_RESPONSE","AI provider returned an invalid response");}
}
