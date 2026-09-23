package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.Vendor;
import com.craftmarket.mapper.VendorMapper;
import com.craftmarket.service.VendorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class VendorServiceImpl extends ServiceImpl<VendorMapper, Vendor> implements VendorService {

    @Autowired
    private VendorMapper vendorMapper;

    @Override
    @Transactional
    public boolean saveVendor(Vendor vendor) {
        if (getVendorByPhone(vendor.getPhone()) != null) {
            throw new IllegalArgumentException("该手机号已被使用");
        }
        return save(vendor);
    }

    @Override
    @Transactional
    public boolean updateVendor(Vendor vendor) {
        // 检查手机号是否已被其他用户使用
        Vendor existingVendor = getVendorByPhone(vendor.getPhone());
        if (existingVendor != null && !existingVendor.getId().equals(vendor.getId())) {
            throw new IllegalArgumentException("该手机号已被使用");
        }
        
        return updateById(vendor);
    }

    @Override
    public Vendor getVendorById(Long id) {
        return getById(id);
    }

    @Override
    public Vendor getVendorByPhone(String phone) {
        return vendorMapper.selectByPhone(phone);
    }

    @Override
    public Page<Vendor> getVendorsPage(Integer current, Integer size, String businessType) {
        Page<Vendor> page = new Page<>(current, size);
        LambdaQueryWrapper<Vendor> queryWrapper = new LambdaQueryWrapper<>();
        
        if (businessType != null && !businessType.trim().isEmpty()) {
            queryWrapper.like(Vendor::getBusinessType, businessType);
        }
        
        queryWrapper.orderByDesc(Vendor::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public List<Vendor> getVendorsByBusinessType(String businessType) {
        if (businessType == null || businessType.trim().isEmpty()) {
            return list();
        }
        
        return lambdaQuery()
                .like(Vendor::getBusinessType, businessType)
                .orderByDesc(Vendor::getCreateTime)
                .list();
    }

    @Override
    @Transactional
    public boolean deleteVendor(Long id) {
        return removeById(id);
    }
}