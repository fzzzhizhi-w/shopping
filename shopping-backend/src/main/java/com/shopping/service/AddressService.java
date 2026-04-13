package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.AddressRequest;

public interface AddressService {

    Result<?> listAddresses(Long userId);

    Result<?> addAddress(Long userId, AddressRequest request);

    Result<?> updateAddress(Long userId, Long addressId, AddressRequest request);

    Result<?> deleteAddress(Long userId, Long addressId);

    Result<?> setDefault(Long userId, Long addressId);
}
