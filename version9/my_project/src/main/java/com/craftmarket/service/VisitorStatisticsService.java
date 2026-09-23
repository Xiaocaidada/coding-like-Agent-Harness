package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.VisitorStatistics;

import java.time.LocalDate;
import java.util.List;

public interface VisitorStatisticsService {

    // 添加或更新客流统计
    boolean saveOrUpdateVisitorStatistics(VisitorStatistics statistics);

    // 根据ID查询客流统计
    VisitorStatistics getVisitorStatisticsById(Long id);

    // 分页查询客流统计列表
    Page<VisitorStatistics> getVisitorStatisticsPage(Integer current, Integer size, Long eventId, LocalDate startDate, LocalDate endDate);

    // 获取某个活动的总客流
    Integer getTotalVisitorsByEvent(Long eventId);

    // 获取某活动的日客流趋势数据
    List<VisitorStatistics> getTrendData(Long eventId, Integer days);

    // 批量录入客流数据
    boolean batchSaveVisitorStatistics(List<VisitorStatistics> statisticsList);

    // 删除客流统计
    boolean deleteVisitorStatistics(Long id);
}