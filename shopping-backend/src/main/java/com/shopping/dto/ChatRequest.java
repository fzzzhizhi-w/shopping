package com.shopping.dto;

import lombok.Data;

@Data
public class ChatRequest {

    private String content;
    private String sessionId;
}
