package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.VendorApplication;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 摊主报名Mapper接口
 */
@Mapper
public interface VendorApplicationMapper extends BaseMapper<VendorApplication> {

    /**
     * 根据活动ID查询报名列表
     *
     * @param activityId 活动ID
     * @return 报名列表
     */
    List<VendorApplication> selectByActivityId(@Param("activityId") Long activityId);

    /**
     * 根据用户ID查询报名列表
     *
     * @param userId 用户ID
     * @return 报名列表
     */
    List<VendorApplication> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据活动ID和状态查询报名列表
     *
     * @param activityId 活动ID
     * @param status     状态
     * @return 报名列表
     */
    List<VendorApplication> selectByActivityIdAndStatus(@Param("activityId") Long activityId, 
                                                      @Param("status") String status);

    /**
     * 更新报名状态
     *
     * @param id     报名ID
     * @param status 状态
     * @param rejectReason 驳回原因（可选）
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status, 
                    @Param("rejectReason") String rejectReason);
}