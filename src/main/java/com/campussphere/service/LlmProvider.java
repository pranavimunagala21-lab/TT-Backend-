package com.campussphere.service;

import java.util.List;

/** Provider boundary for chat completion and text embeddings. */
public interface LlmProvider {
    String answer(String systemPrompt,String userPrompt);
    List<List<Double>> embed(List<String> texts);
    String providerName();
}
