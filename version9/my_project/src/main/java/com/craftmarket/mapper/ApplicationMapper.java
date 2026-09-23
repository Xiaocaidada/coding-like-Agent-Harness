package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
    
    List<Application> selectApplicationsByEvent(@Param("eventId") Long eventId, @Param("status") Application.ApplicationStatus status);
    
    List<Application> selectApplicationsByVendor(@Param("vendorId") Long vendorId);
    
    List<Application> selectApplicationsByVendorAndEvent(@Param("vendorId") Long vendorId, @Param("eventId") Long eventId);
}