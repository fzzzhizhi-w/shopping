package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.ChatRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.AiService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/chat")
    public Result<?> chat(@RequestBody ChatRequest request) {
        Long userId = UserContext.get();
        return aiService.chat(userId, request);
    }

    @GetMapping("/stream")
    public void streamChat(
            @RequestParam String content,
            @RequestParam(required = false) String sessionId,
            HttpServletResponse response) {
        Long userId = UserContext.get();
        ChatRequest request = new ChatRequest();
        request.setContent(content);
        request.setSessionId(sessionId);
        aiService.streamChat(userId, request, response);
    }

    @GetMapping("/history")
    public Result<?> getHistory(@RequestParam(required = false) String sessionId) {
        Long userId = UserContext.get();
        return aiService.getChatHistory(userId, sessionId);
    }

    @DeleteMapping("/history")
    public Result<?> clearHistory(@RequestParam(required = false) String sessionId) {
        Long userId = UserContext.get();
        return aiService.clearHistory(userId, sessionId);
    }
}
