package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.AdSaveRequest;

public interface AdService {

    Result<?> listActiveAds(String position);

    Result<?> adminListAds();

    Result<?> createAd(AdSaveRequest request);

    Result<?> updateAd(Long id, AdSaveRequest request);

    Result<?> deleteAd(Long id);
}
