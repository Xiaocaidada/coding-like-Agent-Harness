package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.Booth;
import com.craftmarket.entity.MarketActivity;
import com.craftmarket.entity.Vendor;
import com.craftmarket.entity.VendorApplication;
import com.craftmarket.enums.BoothStatus;
import com.craftmarket.mapper.BoothMapper;
import com.craftmarket.mapper.MarketActivityMapper;
import com.craftmarket.mapper.VendorMapper;
import com.craftmarket.mapper.VendorApplicationMapper;
import com.craftmarket.service.BoothService;
import com.craftmarket.service.MarketActivityService;
import com.craftmarket.service.VendorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoothServiceImpl extends ServiceImpl<BoothMapper, Booth> implements BoothService {

    @Autowired
    private MarketActivityMapper marketActivityMapper;
    
    @Autowired
    private VendorMapper vendorMapper;
    
    @Autowired
    private VendorApplicationMapper vendorApplicationMapper;

    @Override
    @Transactional
    public BoothVO createBooth(Booth booth) {
        // 验证活动是否存在
        MarketActivity activity = marketActivityMapper.selectById(booth.getMarketEventId());
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        booth.setStatus(BoothStatus.AVAILABLE);
        booth.setCreateTime(LocalDateTime.now());
        
        save(booth);
        
        BoothVO boothVO = new BoothVO();
        BeanUtils.copyProperties(booth, boothVO);
        return boothVO;
    }

    @Override
    @Transactional
    public int batchCreateBooths(Long activityId, String area, String areaName, 
                               BigDecimal areaSize, BigDecimal rentPrice, int boothCount) {
        // 验证活动是否存在
        MarketActivity activity = marketActivityMapper.selectById(activityId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        List<Booth> booths = new java.util.ArrayList<>();
        for (int i = 1; i <= boothCount; i++) {
            Booth booth = new Booth();
            booth.setMarketEventId(activityId);
            booth.setArea(area);
            booth.setAreaName(areaName);
            booth.setAreaSize(areaSize);
            booth.setRentPrice(rentPrice);
            booth.setStatus(BoothStatus.AVAILABLE);
            booth.setBoothNumber(area + "-" + String.format("%03d", i));
            booth.setCreateTime(LocalDateTime.now());
            booths.add(booth);
        }
        
        return saveBatch(booths) ? boothCount : 0;
    }

    @Override
    public List<BoothVO> getBoothsByActivity(Long activityId) {
        LambdaQueryWrapper<Booth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booth::getMarketEventId, activityId)
                .orderByAsc(Booth::getBoothNumber);
        
        List<Booth> booths = list(queryWrapper);
        
        return booths.stream()
                .map(booth -> {
                    BoothVO boothVO = new BoothVO();
                    BeanUtils.copyProperties(booth, boothVO);
                    return boothVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BoothVO> getBoothsByArea(Long activityId, String area) {
        LambdaQueryWrapper<Booth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booth::getMarketEventId, activityId)
                .eq(Booth::getArea, area)
                .orderByAsc(Booth::getBoothNumber);
        
        List<Booth> booths = list(queryWrapper);
        
        return booths.stream()
                .map(booth -> {
                    BoothVO boothVO = new BoothVO();
                    BeanUtils.copyProperties(booth, boothVO);
                    return boothVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<BoothVO> getAvailableBooths(Long activityId) {
        LambdaQueryWrapper<Booth> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Booth::getMarketEventId, activityId)
                .eq(Booth::getStatus, BoothStatus.AVAILABLE)
                .orderByAsc(Booth::getBoothNumber);
        
        List<Booth> booths = list(queryWrapper);
        
        return booths.stream()
                .map(booth -> {
                    BoothVO boothVO = new BoothVO();
                    BeanUtils.copyProperties(booth, boothVO);
                    return boothVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BoothVO updateBoothStatus(Long id, String status) {
        Booth booth = getById(id);
        if (booth == null) {
            throw new IllegalArgumentException("摊位不存在");
        }
        
        booth.setStatus(status);
        booth.setUpdateTime(LocalDateTime.now());
        
        updateById(booth);
        
        BoothVO boothVO = new BoothVO();
        BeanUtils.copyProperties(booth, boothVO);
        return boothVO;
    }

    @Override
    @Transactional
    public boolean assignBoothToVendor(Long boothId, Long vendorApplicationId) {
        // 验证摊位是否存在且可用
        Booth booth = getById(boothId);
        if (booth == null) {
            throw new IllegalArgumentException("摊位不存在");
        }
        
        if (!BoothStatus.AVAILABLE.equals(booth.getStatus())) {
            throw new IllegalStateException("该摊位已被分配或禁用");
        }
        
        // 验证摊主申请是否存在
        VendorApplication application = vendorApplicationMapper.selectById(vendorApplicationId);
        if (application == null) {
            throw new IllegalArgumentException("摊主申请不存在");
        }
        
        // 更新摊位状态
        booth.setStatus(BoothStatus.ASSIGNED);
        booth.setUpdateTime(LocalDateTime.now());
        booth.setVendorId(application.getVendorId());
        
        updateById(booth);
        
        // 更新申请状态
        application.setStatus("APPROVED");
        application.setUpdateTime(LocalDateTime.now());
        vendorApplicationMapper.updateById(application);
        
        // 自动创建订单
        // 这里应该调用OrderService创建订单，为了简化，暂时在这里处理
        
        return true;
    }
}