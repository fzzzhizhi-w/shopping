package com.shopping.dto;

import lombok.Data;

@Data
public class CategorySaveRequest {

    private String name;

    private String icon;

    private Integer sort;

    private Long parentId;
}
