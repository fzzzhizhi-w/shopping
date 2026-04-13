package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.common.Result;
import com.shopping.dto.BannerSaveRequest;
import com.shopping.entity.Banner;
import com.shopping.mapper.BannerMapper;
import com.shopping.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerMapper bannerMapper;

    @Override
    public Result<?> listActiveBanners() {
        List<Banner> list = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .eq(Banner::getStatus, 1)
                .orderByAsc(Banner::getSort));
        return Result.success(list);
    }

    @Override
    public Result<?> adminListBanners() {
        List<Banner> list = bannerMapper.selectList(new LambdaQueryWrapper<Banner>()
                .orderByAsc(Banner::getSort));
        return Result.success(list);
    }

    @Override
    public Result<?> createBanner(BannerSaveRequest request) {
        Banner banner = new Banner();
        banner.setTitle(request.getTitle());
        banner.setImage(request.getImage());
        banner.setLink(request.getLink());
        banner.setSort(request.getSort() != null ? request.getSort() : 0);
        banner.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        bannerMapper.insert(banner);
        return Result.success(banner);
    }

    @Override
    public Result<?> updateBanner(Long id, BannerSaveRequest request) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) return Result.error(404, "Banner not found");
        if (request.getTitle() != null) banner.setTitle(request.getTitle());
        if (request.getImage() != null) banner.setImage(request.getImage());
        if (request.getLink() != null) banner.setLink(request.getLink());
        if (request.getSort() != null) banner.setSort(request.getSort());
        if (request.getStatus() != null) banner.setStatus(request.getStatus());
        bannerMapper.updateById(banner);
        return Result.success(banner);
    }

    @Override
    public Result<?> deleteBanner(Long id) {
        Banner banner = bannerMapper.selectById(id);
        if (banner == null) return Result.error(404, "Banner not found");
        bannerMapper.deleteById(id);
        return Result.success("Deleted");
    }
}
