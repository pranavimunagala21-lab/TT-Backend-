package com.campussphere.config;

import com.campussphere.exception.AiProviderException;
import com.campussphere.service.LlmProvider;
import com.campussphere.service.impl.OpenAiCompatibleProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import java.util.List;

@Configuration
@EnableConfigurationProperties(AiProperties.class)
public class AiProviderConfiguration {
    @Bean
    public LlmProvider llmProvider(AiProperties properties){
        String provider=properties.getProvider()==null?"disabled":properties.getProvider().trim().toLowerCase();
        if(provider.equals("openai")||provider.equals("openai-compatible"))return new OpenAiCompatibleProvider(properties);
        if(provider.equals("disabled")||provider.equals("mock"))return new LlmProvider(){
            public String answer(String systemPrompt,String userPrompt){throw unavailable();}
            public List<List<Double>> embed(List<String> texts){throw unavailable();}
            public String providerName(){return "disabled";}
            private AiProviderException unavailable(){return new AiProviderException(HttpStatus.SERVICE_UNAVAILABLE,"AI_DISABLED","AI provider is disabled; configure AI_PROVIDER and provider credentials");}
        };
        throw new IllegalStateException("Unsupported AI_PROVIDER. Use disabled, openai, or openai-compatible.");
    }
}
