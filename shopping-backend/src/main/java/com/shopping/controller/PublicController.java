package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.service.AdService;
import com.shopping.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final BannerService bannerService;
    private final AdService adService;

    @GetMapping("/banners")
    public Result<?> banners() {
        return bannerService.listActiveBanners();
    }

    @GetMapping("/ads")
    public Result<?> ads(@RequestParam(required = false) String position) {
        return adService.listActiveAds(position);
    }
}
