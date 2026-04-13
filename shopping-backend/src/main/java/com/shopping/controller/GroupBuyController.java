package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.GroupBuyCreateRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.GroupBuyService;
import com.shopping.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groupbuy")
@RequiredArgsConstructor
public class GroupBuyController {

    private final GroupBuyService groupBuyService;
    private final JwtUtils jwtUtils;

    @PostMapping("/create")
    public Result<?> createGroupBuy(
            @RequestHeader(value = "Token", required = false) String token,
            @RequestBody GroupBuyCreateRequest request) {
        Long userId = resolveUserId(token);
        if (userId == null) return Result.error(401, "Unauthorized");
        return groupBuyService.createGroupBuy(userId, request);
    }

    @PostMapping("/join/{shareCode}")
    public Result<?> joinGroupBuy(
            @RequestHeader(value = "Token", required = false) String token,
            @PathVariable String shareCode) {
        Long userId = resolveUserId(token);
        if (userId == null) return Result.error(401, "Unauthorized");
        return groupBuyService.joinGroupBuy(userId, shareCode);
    }

    @GetMapping("/{shareCode}")
    public Result<?> getGroupBuyDetail(@PathVariable String shareCode) {
        return groupBuyService.getGroupBuyDetail(shareCode);
    }

    @GetMapping("/product/{productId}")
    public Result<?> getProductGroupBuys(@PathVariable Long productId) {
        return groupBuyService.getProductGroupBuys(productId);
    }

    @GetMapping("/my")
    public Result<?> getMyGroupBuys(
            @RequestHeader(value = "Token", required = false) String token) {
        Long userId = resolveUserId(token);
        if (userId == null) return Result.error(401, "Unauthorized");
        return groupBuyService.getUserGroupBuys(userId);
    }

    private Long resolveUserId(String token) {
        if (!StringUtils.hasText(token) || !jwtUtils.isTokenValid(token)) {
            return null;
        }
        return jwtUtils.getUserId(token);
    }
}
