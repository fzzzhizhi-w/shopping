package com.shopping.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shopping.common.Result;
import com.shopping.config.DeepSeekConfig;
import com.shopping.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {

    private final DeepSeekConfig deepSeekConfig;
    private final AiServiceImpl aiServiceImpl;
    private final ObjectMapper objectMapper;

    @Override
    public Result<String> getAiRecommendations(Long userId, String category, String budget) {
        String userPrompt = String.format(
                "Please recommend suitable products for me. My preferred category is: %s. My budget is: %s. " +
                "Please provide specific product recommendations with reasons.",
                category != null ? category : "any",
                budget != null ? budget : "no limit"
        );

        try {
            ArrayNode messages = objectMapper.createArrayNode();

            ObjectNode systemMsg = objectMapper.createObjectNode();
            systemMsg.put("role", "system");
            systemMsg.put("content", "You are a professional shopping assistant. Help users find the best products based on their preferences and budget. Provide clear, concise recommendations with reasons.");
            messages.add(systemMsg);

            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userPrompt);
            messages.add(userMsg);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.set("messages", messages);
            requestBody.put("stream", false);

            String responseBody = aiServiceImpl.callDeepSeekApi(requestBody.toString());
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String aiContent = responseJson
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            return Result.success(aiContent);
        } catch (Exception e) {
            return Result.error("Recommendation service error: " + e.getMessage());
        }
    }
}
