package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.common.Result;
import com.shopping.dto.GroupBuyCreateRequest;
import com.shopping.entity.GroupBuy;
import com.shopping.entity.GroupBuyMember;
import com.shopping.entity.Product;
import com.shopping.mapper.GroupBuyMapper;
import com.shopping.mapper.GroupBuyMemberMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.GroupBuyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupBuyServiceImpl implements GroupBuyService {

    private final GroupBuyMapper groupBuyMapper;
    private final GroupBuyMemberMapper groupBuyMemberMapper;
    private final ProductMapper productMapper;

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private String generateShareCode() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            sb.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }
        return sb.toString();
    }

    @Override
    @Transactional
    public Result<?> createGroupBuy(Long userId, GroupBuyCreateRequest request) {
        if (request.getProductId() == null || request.getGroupPrice() == null) {
            return Result.error(400, "productId and groupPrice are required");
        }

        Product product = productMapper.selectById(request.getProductId());
        if (product == null || product.getStatus() != 1) {
            return Result.error(400, "Product not available");
        }

        String shareCode;
        int attempts = 0;
        do {
            shareCode = generateShareCode();
            attempts++;
            if (attempts > 10) return Result.error(500, "Failed to generate unique share code");
        } while (groupBuyMapper.selectCount(
                new LambdaQueryWrapper<GroupBuy>().eq(GroupBuy::getShareCode, shareCode)) > 0);

        int expireHours = request.getExpireHours() != null ? request.getExpireHours() : 24;
        int minMembers = request.getMinMembers() != null ? request.getMinMembers() : 2;

        GroupBuy groupBuy = new GroupBuy();
        groupBuy.setProductId(request.getProductId());
        groupBuy.setGroupPrice(request.getGroupPrice());
        groupBuy.setMinMembers(minMembers);
        groupBuy.setMaxMembers(request.getMaxMembers());
        groupBuy.setCurrentMembers(1);
        groupBuy.setStatus(0);
        groupBuy.setShareCode(shareCode);
        groupBuy.setCreatorId(userId);
        groupBuy.setExpireTime(LocalDateTime.now().plusHours(expireHours));
        groupBuyMapper.insert(groupBuy);

        GroupBuyMember member = new GroupBuyMember();
        member.setGroupBuyId(groupBuy.getId());
        member.setUserId(userId);
        member.setJoinTime(LocalDateTime.now());
        groupBuyMemberMapper.insert(member);

        Map<String, Object> data = new HashMap<>();
        data.put("groupBuy", groupBuy);
        data.put("shareCode", shareCode);
        return Result.success(data);
    }

    @Override
    @Transactional
    public Result<?> joinGroupBuy(Long userId, String shareCode) {
        GroupBuy groupBuy = groupBuyMapper.selectOne(
                new LambdaQueryWrapper<GroupBuy>().eq(GroupBuy::getShareCode, shareCode));
        if (groupBuy == null) {
            return Result.error(404, "Group buy not found");
        }
        if (groupBuy.getStatus() != 0) {
            return Result.error(400, "Group buy is no longer open");
        }
        if (LocalDateTime.now().isAfter(groupBuy.getExpireTime())) {
            groupBuy.setStatus(3);
            groupBuyMapper.updateById(groupBuy);
            return Result.error(400, "Group buy has expired");
        }

        Long existingCount = groupBuyMemberMapper.selectCount(
                new LambdaQueryWrapper<GroupBuyMember>()
                        .eq(GroupBuyMember::getGroupBuyId, groupBuy.getId())
                        .eq(GroupBuyMember::getUserId, userId));
        if (existingCount > 0) {
            return Result.error(400, "You have already joined this group buy");
        }

        if (groupBuy.getMaxMembers() != null && groupBuy.getCurrentMembers() >= groupBuy.getMaxMembers()) {
            return Result.error(400, "Group buy is full");
        }

        GroupBuyMember member = new GroupBuyMember();
        member.setGroupBuyId(groupBuy.getId());
        member.setUserId(userId);
        member.setJoinTime(LocalDateTime.now());
        groupBuyMemberMapper.insert(member);

        groupBuy.setCurrentMembers(groupBuy.getCurrentMembers() + 1);
        if (groupBuy.getCurrentMembers() >= groupBuy.getMinMembers()) {
            groupBuy.setStatus(1);
        }
        groupBuyMapper.updateById(groupBuy);

        return Result.success(groupBuy);
    }

    @Override
    public Result<?> getGroupBuyDetail(String shareCode) {
        GroupBuy groupBuy = groupBuyMapper.selectOne(
                new LambdaQueryWrapper<GroupBuy>().eq(GroupBuy::getShareCode, shareCode));
        if (groupBuy == null) {
            return Result.error(404, "Group buy not found");
        }

        Product product = productMapper.selectById(groupBuy.getProductId());

        List<GroupBuyMember> members = groupBuyMemberMapper.selectList(
                new LambdaQueryWrapper<GroupBuyMember>().eq(GroupBuyMember::getGroupBuyId, groupBuy.getId()));

        Map<String, Object> data = new HashMap<>();
        data.put("groupBuy", groupBuy);
        data.put("product", product);
        data.put("members", members);
        return Result.success(data);
    }

    @Override
    public Result<?> getProductGroupBuys(Long productId) {
        List<GroupBuy> groupBuys = groupBuyMapper.selectList(
                new LambdaQueryWrapper<GroupBuy>()
                        .eq(GroupBuy::getProductId, productId)
                        .eq(GroupBuy::getStatus, 0)
                        .gt(GroupBuy::getExpireTime, LocalDateTime.now())
                        .orderByAsc(GroupBuy::getExpireTime));
        return Result.success(groupBuys);
    }

    @Override
    public Result<?> getUserGroupBuys(Long userId) {
        // Groups created by user
        List<GroupBuy> created = groupBuyMapper.selectList(
                new LambdaQueryWrapper<GroupBuy>().eq(GroupBuy::getCreatorId, userId)
                        .orderByDesc(GroupBuy::getCreateTime));

        // Groups joined by user (excluding ones they created)
        List<GroupBuyMember> memberships = groupBuyMemberMapper.selectList(
                new LambdaQueryWrapper<GroupBuyMember>().eq(GroupBuyMember::getUserId, userId));

        List<Long> joinedGroupIds = memberships.stream()
                .map(GroupBuyMember::getGroupBuyId)
                .filter(gid -> created.stream().noneMatch(g -> g.getId().equals(gid)))
                .collect(Collectors.toList());

        List<GroupBuy> joined = new ArrayList<>();
        if (!joinedGroupIds.isEmpty()) {
            joined = groupBuyMapper.selectBatchIds(joinedGroupIds);
        }

        // Enrich with product info
        List<Map<String, Object>> enriched = new ArrayList<>();
        for (GroupBuy gb : created) {
            Product p = productMapper.selectById(gb.getProductId());
            Map<String, Object> entry = new HashMap<>();
            entry.put("groupBuy", gb);
            entry.put("product", p);
            entry.put("role", "creator");
            enriched.add(entry);
        }
        for (GroupBuy gb : joined) {
            Product p = productMapper.selectById(gb.getProductId());
            Map<String, Object> entry = new HashMap<>();
            entry.put("groupBuy", gb);
            entry.put("product", p);
            entry.put("role", "member");
            enriched.add(entry);
        }

        return Result.success(enriched);
    }
}
