package com.advertising.mapper;

import com.advertising.entity.PlanBarrier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 方案道闸明细数据访问层
 */
@Mapper
public interface PlanBarrierMapper {
    
    /**
     * 根据ID查询方案道闸明细
     * @param id 明细ID
     * @return 方案道闸明细信息
     */
    PlanBarrier selectById(Integer id);
    
    /**
     * 根据方案ID、道闸ID和方案社区ID查询
     * @param planId 方案ID
     * @param barrierGateId 道闸ID
     * @param planCommunityId 方案社区ID
     * @return 方案道闸明细信息
     */
    PlanBarrier selectByUniqueKey(@Param("planId") Integer planId, 
                                   @Param("barrierGateId") Integer barrierGateId, 
                                   @Param("planCommunityId") Integer planCommunityId);
    
    /**
     * 查询所有方案道闸明细
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectAll();
    
    /**
     * 分页查询方案道闸明细列表（带关联信息）
     * @param planBarrier 查询条件
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectList(PlanBarrier planBarrier);
    
    /**
     * 根据条件统计数量
     * @param planBarrier 查询条件
     * @return 数量
     */
    Long count(PlanBarrier planBarrier);
    
    /**
     * 根据方案ID查询明细列表
     * @param planId 方案ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectByPlanId(Integer planId);
    
    /**
     * 根据道闸ID查询明细列表
     * @param barrierGateId 道闸ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectByBarrierGateId(Integer barrierGateId);
    
    /**
     * 根据方案社区ID查询明细列表
     * @param planCommunityId 方案社区ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectByPlanCommunityId(Integer planCommunityId);
    
    /**
     * 根据发布状态查询明细列表
     * @param releaseStatus 发布状态
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> selectByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 影响行数
     */
    int insert(PlanBarrier planBarrier);
    
    /**
     * 批量新增方案道闸明细
     * @param list 方案道闸明细列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<PlanBarrier> list);
    
    /**
     * 更新方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 影响行数
     */
    int update(PlanBarrier planBarrier);
    
    /**
     * 根据ID删除方案道闸明细
     * @param id 明细ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除方案道闸明细
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
     * 根据道闸ID删除明细
     * @param barrierGateId 道闸ID
     * @return 影响行数
     */
    int deleteByBarrierGateId(Integer barrierGateId);
    
    /**
     * 根据方案ID统计道闸数量
     * @param planId 方案ID
     * @return 数量
     */
    int countByPlanId(Integer planId);
}
