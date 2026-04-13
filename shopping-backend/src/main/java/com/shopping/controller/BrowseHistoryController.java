package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.interceptor.UserContext;
import com.shopping.service.BrowseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService historyService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "20") int pageSize) {
        return historyService.listHistory(UserContext.get(), pageNum, pageSize);
    }

    @PostMapping("/{productId}")
    public Result<?> record(@PathVariable Long productId) {
        return historyService.recordHistory(UserContext.get(), productId);
    }

    @DeleteMapping("/{productId}")
    public Result<?> delete(@PathVariable Long productId) {
        return historyService.deleteHistory(UserContext.get(), productId);
    }

    @DeleteMapping
    public Result<?> clear() {
        return historyService.clearHistory(UserContext.get());
    }
}
