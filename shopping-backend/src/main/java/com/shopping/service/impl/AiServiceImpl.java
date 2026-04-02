package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.shopping.common.Result;
import com.shopping.config.DeepSeekConfig;
import com.shopping.dto.ChatRequest;
import com.shopping.entity.ChatHistory;
import com.shopping.mapper.ChatHistoryMapper;
import com.shopping.service.AiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {

    private final DeepSeekConfig deepSeekConfig;
    private final ChatHistoryMapper chatHistoryMapper;
    private final ObjectMapper objectMapper;

    @Override
    public Result<String> chat(Long userId, ChatRequest request) {
        List<ChatHistory> history = getRecentHistory(userId, request.getSessionId());

        ArrayNode messages = objectMapper.createArrayNode();
        for (ChatHistory h : history) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("role", h.getRole());
            msg.put("content", h.getContent());
            messages.add(msg);
        }
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", request.getContent());
        messages.add(userMsg);

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.set("messages", messages);
            requestBody.put("stream", false);

            String responseBody = callDeepSeekApi(requestBody.toString());
            JsonNode responseJson = objectMapper.readTree(responseBody);
            String aiContent = responseJson
                    .path("choices").get(0)
                    .path("message")
                    .path("content")
                    .asText();

            saveMessage(userId, request.getSessionId(), "user", request.getContent());
            saveMessage(userId, request.getSessionId(), "assistant", aiContent);

            return Result.success(aiContent);
        } catch (Exception e) {
            return Result.error("AI service error: " + e.getMessage());
        }
    }

    @Override
    public void streamChat(Long userId, ChatRequest request, HttpServletResponse response) {
        response.setContentType("text/event-stream;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "keep-alive");

        List<ChatHistory> history = getRecentHistory(userId, request.getSessionId());

        ArrayNode messages = objectMapper.createArrayNode();
        for (ChatHistory h : history) {
            ObjectNode msg = objectMapper.createObjectNode();
            msg.put("role", h.getRole());
            msg.put("content", h.getContent());
            messages.add(msg);
        }
        ObjectNode userMsg = objectMapper.createObjectNode();
        userMsg.put("role", "user");
        userMsg.put("content", request.getContent());
        messages.add(userMsg);

        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", deepSeekConfig.getModel());
            requestBody.set("messages", messages);
            requestBody.put("stream", true);

            saveMessage(userId, request.getSessionId(), "user", request.getContent());

            StringBuilder fullResponse = new StringBuilder();

            URL url = new URL(deepSeekConfig.getApiUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(120000);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(requestBody.toString().getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
                 OutputStream responseOut = response.getOutputStream()) {

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("data: ")) {
                        String data = line.substring(6).trim();
                        if ("[DONE]".equals(data)) {
                            responseOut.write(("data: [DONE]\n\n").getBytes(StandardCharsets.UTF_8));
                            responseOut.flush();
                            break;
                        }
                        try {
                            JsonNode chunk = objectMapper.readTree(data);
                            JsonNode delta = chunk.path("choices").get(0).path("delta");
                            if (delta.has("content")) {
                                String content = delta.path("content").asText();
                                fullResponse.append(content);
                            }
                        } catch (Exception ignored) {
                        }
                        responseOut.write(("data: " + data + "\n\n").getBytes(StandardCharsets.UTF_8));
                        responseOut.flush();
                    }
                }
            }

            saveMessage(userId, request.getSessionId(), "assistant", fullResponse.toString());

        } catch (Exception e) {
            try {
                String errorMsg = "data: {\"error\":\"" + e.getMessage() + "\"}\n\n";
                response.getOutputStream().write(errorMsg.getBytes(StandardCharsets.UTF_8));
                response.getOutputStream().flush();
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public Result<List<ChatHistory>> getChatHistory(Long userId, String sessionId) {
        List<ChatHistory> history = chatHistoryMapper.selectList(new LambdaQueryWrapper<ChatHistory>()
                .eq(ChatHistory::getUserId, userId)
                .eq(ChatHistory::getSessionId, sessionId)
                .orderByAsc(ChatHistory::getCreateTime));
        return Result.success(history);
    }

    @Override
    public Result<?> clearHistory(Long userId, String sessionId) {
        chatHistoryMapper.delete(new LambdaQueryWrapper<ChatHistory>()
                .eq(ChatHistory::getUserId, userId)
                .eq(ChatHistory::getSessionId, sessionId));
        return Result.success("History cleared");
    }

    private List<ChatHistory> getRecentHistory(Long userId, String sessionId) {
        return chatHistoryMapper.selectList(new LambdaQueryWrapper<ChatHistory>()
                .eq(ChatHistory::getUserId, userId)
                .eq(sessionId != null, ChatHistory::getSessionId, sessionId)
                .orderByDesc(ChatHistory::getCreateTime)
                .last("LIMIT 10"));
    }

    private void saveMessage(Long userId, String sessionId, String role, String content) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setUserId(userId);
        chatHistory.setSessionId(sessionId);
        chatHistory.setRole(role);
        chatHistory.setContent(content);
        chatHistoryMapper.insert(chatHistory);
    }

    String callDeepSeekApi(String requestBody) throws IOException {
        URL url = new URL(deepSeekConfig.getApiUrl());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + deepSeekConfig.getApiKey());
        conn.setDoOutput(true);
        conn.setConnectTimeout(30000);
        conn.setReadTimeout(120000);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        return response.toString();
    }
}
