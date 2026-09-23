package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.MarketActivity;
import com.craftmarket.entity.VisitorStatistics;
import com.craftmarket.mapper.MarketActivityMapper;
import com.craftmarket.mapper.VisitorStatisticsMapper;
import com.craftmarket.service.VisitorStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VisitorStatisticsServiceImpl extends ServiceImpl<VisitorStatisticsMapper, VisitorStatistics> implements VisitorStatisticsService {

    @Autowired
    private MarketActivityMapper marketActivityMapper;

    @Override
    @Transactional
    public boolean saveOrUpdateVisitorStatistics(VisitorStatistics statistics) {
        // 验证活动是否存在
        MarketActivity activity = marketActivityMapper.selectById(statistics.getMarketEventId());
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        // 检查是否已存在该日期的统计记录
        VisitorStatistics existing = lambdaQuery()
                .eq(VisitorStatistics::getMarketEventId, statistics.getMarketEventId())
                .eq(VisitorStatistics::getStatisticsDate, statistics.getStatisticsDate())
                .one();
        
        if (existing != null) {
            // 更新现有记录
            existing.setVisitorCount(statistics.getVisitorCount());
            existing.setUpdateTime(LocalDateTime.now());
            return updateById(existing);
        } else {
            // 创建新记录
            statistics.setCreateTime(LocalDateTime.now());
            return save(statistics);
        }
    }

    @Override
    public VisitorStatistics getVisitorStatisticsById(Long id) {
        return getById(id);
    }

    @Override
    public Page<VisitorStatistics> getVisitorStatisticsPage(Integer current, Integer size, Long eventId, LocalDate startDate, LocalDate endDate) {
        Page<VisitorStatistics> page = new Page<>(current, size);
        LambdaQueryWrapper<VisitorStatistics> queryWrapper = new LambdaQueryWrapper<>();
        
        if (eventId != null) {
            queryWrapper.eq(VisitorStatistics::getMarketEventId, eventId);
        }
        
        if (startDate != null) {
            queryWrapper.ge(VisitorStatistics::getStatisticsDate, startDate);
        }
        
        if (endDate != null) {
            queryWrapper.le(VisitorStatistics::getStatisticsDate, endDate);
        }
        
        queryWrapper.orderByDesc(VisitorStatistics::getStatisticsDate);
        return page(page, queryWrapper);
    }

    @Override
    public Integer getTotalVisitorsByEvent(Long eventId) {
        LambdaQueryWrapper<VisitorStatistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VisitorStatistics::getMarketEventId, eventId);
        
        List<VisitorStatistics> statisticsList = list(queryWrapper);
        
        return statisticsList.stream()
                .mapToInt(VisitorStatistics::getVisitorCount)
                .sum();
    }

    @Override
    public List<VisitorStatistics> getTrendData(Long eventId, Integer days) {
        // 计算起始日期
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // 查询指定日期范围内的数据
        LambdaQueryWrapper<VisitorStatistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VisitorStatistics::getMarketEventId, eventId)
                .ge(VisitorStatistics::getStatisticsDate, startDate)
                .le(VisitorStatistics::getStatisticsDate, endDate)
                .orderByAsc(VisitorStatistics::getStatisticsDate);
        
        return list(queryWrapper);
    }

    @Override
    @Transactional
    public boolean batchSaveVisitorStatistics(List<VisitorStatistics> statisticsList) {
        if (statisticsList == null || statisticsList.isEmpty()) {
            return false;
        }
        
        // 验证活动是否存在
        Long eventId = statisticsList.get(0).getMarketEventId();
        MarketActivity activity = marketActivityMapper.selectById(eventId);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        // 批量保存统计信息
        return saveBatch(statisticsList);
    }

    @Override
    @Transactional
    public boolean deleteVisitorStatistics(Long id) {
        return removeById(id);
    }
}