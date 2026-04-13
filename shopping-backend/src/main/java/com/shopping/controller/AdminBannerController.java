package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.BannerSaveRequest;
import com.shopping.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
public class AdminBannerController {

    private final BannerService bannerService;

    @GetMapping
    public Result<?> list() {
        return bannerService.adminListBanners();
    }

    @PostMapping
    public Result<?> create(@RequestBody BannerSaveRequest request) {
        return bannerService.createBanner(request);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody BannerSaveRequest request) {
        return bannerService.updateBanner(id, request);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return bannerService.deleteBanner(id);
    }
}
