package com.shopping.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GroupBuyCreateRequest {

    private Long productId;

    private BigDecimal groupPrice;

    /** Minimum number of members to form the group (default 2) */
    private Integer minMembers;

    /** Maximum number of members (optional) */
    private Integer maxMembers;

    /** Hours until expiry (default 24) */
    private Integer expireHours;
}
