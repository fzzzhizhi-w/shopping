package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.BannerSaveRequest;

public interface BannerService {

    Result<?> listActiveBanners();

    Result<?> adminListBanners();

    Result<?> createBanner(BannerSaveRequest request);

    Result<?> updateBanner(Long id, BannerSaveRequest request);

    Result<?> deleteBanner(Long id);
}
