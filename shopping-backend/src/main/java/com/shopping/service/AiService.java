package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.ChatRequest;
import com.shopping.entity.ChatHistory;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface AiService {

    Result<String> chat(Long userId, ChatRequest request);

    void streamChat(Long userId, ChatRequest request, HttpServletResponse response);

    Result<List<ChatHistory>> getChatHistory(Long userId, String sessionId);

    Result<?> clearHistory(Long userId, String sessionId);
}
