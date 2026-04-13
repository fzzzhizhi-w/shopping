package com.shopping.dto;

import lombok.Data;

@Data
public class AdSaveRequest {

    private String title;

    private String image;

    private String link;

    private String position;

    private Integer sort;

    private Integer status;
}
