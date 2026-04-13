package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shopping.common.Result;
import com.shopping.dto.AddressRequest;
import com.shopping.entity.Address;
import com.shopping.mapper.AddressMapper;
import com.shopping.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressMapper addressMapper;

    @Override
    public Result<?> listAddresses(Long userId) {
        List<Address> list = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .orderByDesc(Address::getIsDefault)
                .orderByDesc(Address::getCreateTime));
        return Result.success(list);
    }

    @Override
    @Transactional
    public Result<?> addAddress(Long userId, AddressRequest request) {
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearDefault(userId);
        }
        Address address = new Address();
        address.setUserId(userId);
        address.setName(request.getName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        addressMapper.insert(address);
        return Result.success(address);
    }

    @Override
    @Transactional
    public Result<?> updateAddress(Long userId, Long addressId, AddressRequest request) {
        Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (address == null) {
            return Result.error(404, "Address not found");
        }
        if (request.getIsDefault() != null && request.getIsDefault() == 1) {
            clearDefault(userId);
        }
        if (request.getName() != null) address.setName(request.getName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getProvince() != null) address.setProvince(request.getProvince());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict());
        if (request.getDetail() != null) address.setDetail(request.getDetail());
        if (request.getIsDefault() != null) address.setIsDefault(request.getIsDefault());
        addressMapper.updateById(address);
        return Result.success(address);
    }

    @Override
    public Result<?> deleteAddress(Long userId, Long addressId) {
        Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (address == null) {
            return Result.error(404, "Address not found");
        }
        addressMapper.deleteById(addressId);
        return Result.success("Deleted");
    }

    @Override
    @Transactional
    public Result<?> setDefault(Long userId, Long addressId) {
        Address address = addressMapper.selectOne(new LambdaQueryWrapper<Address>()
                .eq(Address::getId, addressId)
                .eq(Address::getUserId, userId));
        if (address == null) {
            return Result.error(404, "Address not found");
        }
        clearDefault(userId);
        address.setIsDefault(1);
        addressMapper.updateById(address);
        return Result.success("Default address updated");
    }

    private void clearDefault(Long userId) {
        List<Address> defaults = addressMapper.selectList(new LambdaQueryWrapper<Address>()
                .eq(Address::getUserId, userId)
                .eq(Address::getIsDefault, 1));
        for (Address a : defaults) {
            a.setIsDefault(0);
            addressMapper.updateById(a);
        }
    }
}
