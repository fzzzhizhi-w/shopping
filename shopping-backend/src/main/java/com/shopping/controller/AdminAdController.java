package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.AdSaveRequest;
import com.shopping.service.AdService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/ads")
@RequiredArgsConstructor
public class AdminAdController {

    private final AdService adService;

    @GetMapping
    public Result<?> list() {
        return adService.adminListAds();
    }

    @PostMapping
    public Result<?> create(@RequestBody AdSaveRequest request) {
        return adService.createAd(request);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody AdSaveRequest request) {
        return adService.updateAd(id, request);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return adService.deleteAd(id);
    }
}
