package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.GroupBuyCreateRequest;

public interface GroupBuyService {

    Result<?> createGroupBuy(Long userId, GroupBuyCreateRequest request);

    Result<?> joinGroupBuy(Long userId, String shareCode);

    Result<?> getGroupBuyDetail(String shareCode);

    Result<?> getProductGroupBuys(Long productId);

    Result<?> getUserGroupBuys(Long userId);
}
