package com.shopping.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSaveRequest {

    private String name;

    private String description;

    private BigDecimal price;

    private Integer stock;

    private Long categoryId;

    private String images;

    private String mainImage;

    private Integer status;
}
