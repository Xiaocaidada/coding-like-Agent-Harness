package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Vendor;

import java.util.List;

public interface VendorService {

    // 保存摊主信息
    boolean saveVendor(Vendor vendor);

    // 更新摊主信息
    boolean updateVendor(Vendor vendor);

    // 根据ID查询摊主
    Vendor getVendorById(Long id);

    // 根据电话查询摊主
    Vendor getVendorByPhone(String phone);

    // 分页查询摊主列表
    Page<Vendor> getVendorsPage(Integer current, Integer size, String businessType);

    // 根据经营品类查询摊主
    List<Vendor> getVendorsByBusinessType(String businessType);

    // 删除摊主
    boolean deleteVendor(Long id);
}