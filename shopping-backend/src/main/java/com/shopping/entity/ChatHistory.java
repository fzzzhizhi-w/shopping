package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("chat_history")
public class ChatHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    /**
     * Role: "user" or "assistant"
     */
    private String role;

    private String content;

    private String sessionId;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
