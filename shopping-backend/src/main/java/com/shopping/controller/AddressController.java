package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.AddressRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public Result<?> list() {
        return addressService.listAddresses(UserContext.get());
    }

    @PostMapping
    public Result<?> add(@RequestBody AddressRequest request) {
        return addressService.addAddress(UserContext.get(), request);
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody AddressRequest request) {
        return addressService.updateAddress(UserContext.get(), id, request);
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        return addressService.deleteAddress(UserContext.get(), id);
    }

    @PutMapping("/{id}/default")
    public Result<?> setDefault(@PathVariable Long id) {
        return addressService.setDefault(UserContext.get(), id);
    }
}
