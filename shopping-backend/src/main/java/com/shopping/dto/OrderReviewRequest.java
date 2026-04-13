package com.shopping.dto;

import lombok.Data;

@Data
public class OrderReviewRequest {

    private Long orderItemId;

    private Long productId;

    private Integer rating;

    private String content;

    private String images;
}
