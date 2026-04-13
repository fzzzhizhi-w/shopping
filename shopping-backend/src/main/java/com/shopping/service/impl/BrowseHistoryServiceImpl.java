package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.entity.BrowseHistory;
import com.shopping.entity.Product;
import com.shopping.mapper.BrowseHistoryMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.BrowseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    private final BrowseHistoryMapper historyMapper;
    private final ProductMapper productMapper;

    @Override
    public Result<?> recordHistory(Long userId, Long productId) {
        BrowseHistory existing = historyMapper.selectOne(new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getProductId, productId));
        if (existing != null) {
            existing.setViewTime(LocalDateTime.now());
            historyMapper.updateById(existing);
        } else {
            BrowseHistory h = new BrowseHistory();
            h.setUserId(userId);
            h.setProductId(productId);
            h.setViewTime(LocalDateTime.now());
            historyMapper.insert(h);
        }
        return Result.success("Recorded");
    }

    @Override
    public Result<?> listHistory(Long userId, int pageNum, int pageSize) {
        Page<BrowseHistory> page = new Page<>(pageNum, pageSize);
        Page<BrowseHistory> result = historyMapper.selectPage(page, new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
                .orderByDesc(BrowseHistory::getViewTime));

        List<Map<String, Object>> list = new ArrayList<>();
        for (BrowseHistory h : result.getRecords()) {
            Product product = productMapper.selectById(h.getProductId());
            Map<String, Object> item = new HashMap<>();
            item.put("historyId", h.getId());
            item.put("viewTime", h.getViewTime());
            item.put("product", product);
            list.add(item);
        }
        return Result.success(PageResult.of(result.getTotal(), list, pageNum, pageSize));
    }

    @Override
    public Result<?> deleteHistory(Long userId, Long productId) {
        historyMapper.delete(new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId)
                .eq(BrowseHistory::getProductId, productId));
        return Result.success("Deleted");
    }

    @Override
    public Result<?> clearHistory(Long userId) {
        historyMapper.delete(new LambdaQueryWrapper<BrowseHistory>()
                .eq(BrowseHistory::getUserId, userId));
        return Result.success("History cleared");
    }
}
