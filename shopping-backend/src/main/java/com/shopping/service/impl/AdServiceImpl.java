package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.common.Result;
import com.shopping.dto.AdSaveRequest;
import com.shopping.entity.Ad;
import com.shopping.mapper.AdMapper;
import com.shopping.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdServiceImpl implements AdService {

    private final AdMapper adMapper;

    @Override
    public Result<?> listActiveAds(String position) {
        LambdaQueryWrapper<Ad> wrapper = new LambdaQueryWrapper<Ad>()
                .eq(Ad::getStatus, 1)
                .orderByAsc(Ad::getSort);
        if (StringUtils.hasText(position)) {
            wrapper.eq(Ad::getPosition, position);
        }
        List<Ad> list = adMapper.selectList(wrapper);
        return Result.success(list);
    }

    @Override
    public Result<?> adminListAds() {
        List<Ad> list = adMapper.selectList(new LambdaQueryWrapper<Ad>()
                .orderByAsc(Ad::getSort));
        return Result.success(list);
    }

    @Override
    public Result<?> createAd(AdSaveRequest request) {
        Ad ad = new Ad();
        ad.setTitle(request.getTitle());
        ad.setImage(request.getImage());
        ad.setLink(request.getLink());
        ad.setPosition(request.getPosition());
        ad.setSort(request.getSort() != null ? request.getSort() : 0);
        ad.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        adMapper.insert(ad);
        return Result.success(ad);
    }

    @Override
    public Result<?> updateAd(Long id, AdSaveRequest request) {
        Ad ad = adMapper.selectById(id);
        if (ad == null) return Result.error(404, "Ad not found");
        if (request.getTitle() != null) ad.setTitle(request.getTitle());
        if (request.getImage() != null) ad.setImage(request.getImage());
        if (request.getLink() != null) ad.setLink(request.getLink());
        if (request.getPosition() != null) ad.setPosition(request.getPosition());
        if (request.getSort() != null) ad.setSort(request.getSort());
        if (request.getStatus() != null) ad.setStatus(request.getStatus());
        adMapper.updateById(ad);
        return Result.success(ad);
    }

    @Override
    public Result<?> deleteAd(Long id) {
        Ad ad = adMapper.selectById(id);
        if (ad == null) return Result.error(404, "Ad not found");
        adMapper.deleteById(id);
        return Result.success("Deleted");
    }
}
