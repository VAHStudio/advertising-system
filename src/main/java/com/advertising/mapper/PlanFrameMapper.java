package com.advertising.mapper;

import com.advertising.entity.PlanFrame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 方案框架明细数据访问层
 */
@Mapper
public interface PlanFrameMapper {
    
    /**
     * 根据ID查询方案框架明细
     * @param id 明细ID
     * @return 方案框架明细信息
     */
    PlanFrame selectById(Integer id);
    
    /**
     * 根据方案ID、框架ID和方案社区ID查询
     * @param planId 方案ID
     * @param frameId 框架ID
     * @param planCommunityId 方案社区ID
     * @return 方案框架明细信息
     */
    PlanFrame selectByUniqueKey(@Param("planId") Integer planId, 
                                 @Param("frameId") Integer frameId, 
                                 @Param("planCommunityId") Integer planCommunityId);
    
    /**
     * 查询所有方案框架明细
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectAll();
    
    /**
     * 分页查询方案框架明细列表（带关联信息）
     * @param planFrame 查询条件
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectList(PlanFrame planFrame);
    
    /**
     * 根据条件统计数量
     * @param planFrame 查询条件
     * @return 数量
     */
    Long count(PlanFrame planFrame);
    
    /**
     * 根据方案ID查询明细列表
     * @param planId 方案ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectByPlanId(Integer planId);
    
    /**
     * 根据框架ID查询明细列表
     * @param frameId 框架ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectByFrameId(Integer frameId);
    
    /**
     * 根据方案社区ID查询明细列表
     * @param planCommunityId 方案社区ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectByPlanCommunityId(Integer planCommunityId);
    
    /**
     * 根据发布状态查询明细列表
     * @param releaseStatus 发布状态
     * @return 方案框架明细列表
     */
    List<PlanFrame> selectByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案框架明细
     * @param planFrame 方案框架明细信息
     * @return 影响行数
     */
    int insert(PlanFrame planFrame);
    
    /**
     * 批量新增方案框架明细
     * @param list 方案框架明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<PlanFrame> list);
    
    /**
     * 更新方案框架明细
     * @param planFrame 方案框架明细信息
     * @return 影响行数
     */
    int update(PlanFrame planFrame);
    
    /**
     * 根据ID删除方案框架明细
     * @param id 明细ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除方案框架明细
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);
    
    /**
     * 根据方案ID删除明细
     * @param planId 方案ID
     * @return 影响行数
     */
    int deleteByPlanId(Integer planId);
    
    /**
     * 根据框架ID删除明细
     * @param frameId 框架ID
     * @return 影响行数
     */
    int deleteByFrameId(Integer frameId);
}
