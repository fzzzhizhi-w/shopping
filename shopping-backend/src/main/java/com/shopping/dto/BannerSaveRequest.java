package com.shopping.dto;

import lombok.Data;

@Data
public class BannerSaveRequest {

    private String title;

    private String image;

    private String link;

    private Integer sort;

    private Integer status;
}
