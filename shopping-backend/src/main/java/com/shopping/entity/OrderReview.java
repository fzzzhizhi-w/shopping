package com.shopping.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_review")
public class OrderReview {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;

    private Long orderId;

    private Long orderItemId;

    private Long productId;

    private Integer rating;

    private String content;

    private String images;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
