package com.advertising.mapper;

import com.advertising.entity.PlanCommunity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 方案社区关联数据访问层
 */
@Mapper
public interface PlanCommunityMapper {
    
    /**
     * 根据ID查询方案社区关联
     * @param id 关联ID
     * @return 方案社区关联信息
     */
    PlanCommunity selectById(Integer id);
    
    /**
     * 根据方案ID和社区ID查询
     * @param planId 方案ID
     * @param communityId 社区ID
     * @return 方案社区关联信息
     */
    PlanCommunity selectByPlanAndCommunity(@Param("planId") Integer planId, @Param("communityId") Integer communityId);
    
    /**
     * 查询所有方案社区关联
     * @return 方案社区关联列表
     */
    List<PlanCommunity> selectAll();
    
    /**
     * 分页查询方案社区关联列表（带方案和社区信息）
     * @param planCommunity 查询条件
     * @return 方案社区关联列表
     */
    List<PlanCommunity> selectList(PlanCommunity planCommunity);
    
    /**
     * 根据条件统计数量
     * @param planCommunity 查询条件
     * @return 数量
     */
    Long count(PlanCommunity planCommunity);
    
    /**
     * 根据方案ID查询关联列表
     * @param planId 方案ID
     * @return 方案社区关联列表
     */
    List<PlanCommunity> selectByPlanId(Integer planId);
    
    /**
     * 根据社区ID查询关联列表
     * @param communityId 社区ID
     * @return 方案社区关联列表
     */
    List<PlanCommunity> selectByCommunityId(Integer communityId);
    
    /**
     * 新增方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 影响行数
     */
    int insert(PlanCommunity planCommunity);
    
    /**
     * 批量新增方案社区关联
     * @param list 方案社区关联列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<PlanCommunity> list);
    
    /**
     * 更新方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 影响行数
     */
    int update(PlanCommunity planCommunity);
    
    /**
     * 根据ID删除方案社区关联
     * @param id 关联ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除方案社区关联
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);
    
    /**
     * 根据方案ID删除关联
     * @param planId 方案ID
     * @return 影响行数
     */
    int deleteByPlanId(Integer planId);
    
    /**
     * 根据社区ID删除关联
     * @param communityId 社区ID
     * @return 影响行数
     */
    int deleteByCommunityId(Integer communityId);
}
