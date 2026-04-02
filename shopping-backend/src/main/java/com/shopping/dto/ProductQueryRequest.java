package com.shopping.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductQueryRequest {

    private String keyword;
    private Long categoryId;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private String sortBy;
    private String sortOrder;
}
