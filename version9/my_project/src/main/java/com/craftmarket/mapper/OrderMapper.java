package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 订单Mapper接口
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据活动ID查询订单列表
     *
     * @param activityId 活动ID
     * @return 订单列表
     */
    List<Order> selectByActivityId(@Param("activityId") Long activityId);

    /**
     * 根据用户ID查询订单列表
     *
     * @param userId 用户ID
     * @return 订单列表
     */
    List<Order> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据活动ID和状态查询订单列表
     *
     * @param activityId 活动ID
     * @param status     状态
     * @return 订单列表
     */
    List<Order> selectByActivityIdAndStatus(@Param("activityId") Long activityId, 
                                           @Param("status") String status);

    /**
     * 更新订单状态
     *
     * @param id     订单ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);

    /**
     * 更新订单状态和支付时间
     *
     * @param id       订单ID
     * @param status   状态
     * @param paymentTime 支付时间
     * @return 影响行数
     */
    int updateStatusWithPaymentTime(@Param("id") Long id, @Param("status") String status, 
                                   @Param("paymentTime") java.time.LocalDateTime paymentTime);
}