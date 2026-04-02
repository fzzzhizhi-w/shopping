package com.shopping.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderCreateRequest {

    private String address;
    private String receiverName;
    private String receiverPhone;
    private List<Long> cartIds;
}
