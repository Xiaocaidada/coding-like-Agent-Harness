package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.MarketEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarketEventMapper extends BaseMapper<MarketEvent> {
    
    List<MarketEvent> selectEventsByStatus(@Param("status") MarketEvent.MarketEventStatus status);
    
    List<MarketEvent> selectAvailableBoothsByEvent(@Param("eventId") Long eventId, @Param("area") String area);
}