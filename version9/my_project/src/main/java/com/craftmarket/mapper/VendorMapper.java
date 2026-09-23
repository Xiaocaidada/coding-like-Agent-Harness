package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.Vendor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface VendorMapper extends BaseMapper<Vendor> {
    
    Vendor selectByPhone(@Param("phone") String phone);
    
    List<Vendor> selectVendorsByBusinessType(@Param("businessType") String businessType);
}