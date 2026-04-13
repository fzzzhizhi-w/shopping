package com.shopping.service;

import com.shopping.common.Result;

public interface BrowseHistoryService {

    Result<?> recordHistory(Long userId, Long productId);

    Result<?> listHistory(Long userId, int pageNum, int pageSize);

    Result<?> deleteHistory(Long userId, Long productId);

    Result<?> clearHistory(Long userId);
}
