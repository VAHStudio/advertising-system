package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanCommunity;

import java.util.List;

/**
 * 方案社区关联服务接口
 */
public interface PlanCommunityService {
    
    /**
     * 根据ID查询方案社区关联
     * @param id 关联ID
     * @return 方案社区关联信息
     */
    PlanCommunity getById(Integer id);
    
    /**
     * 根据方案ID和社区ID查询
     * @param planId 方案ID
     * @param communityId 社区ID
     * @return 方案社区关联信息
     */
    PlanCommunity getByPlanAndCommunity(Integer planId, Integer communityId);
    
    /**
     * 查询所有方案社区关联
     * @return 方案社区关联列表
     */
    List<PlanCommunity> getAll();
    
    /**
     * 分页查询方案社区关联列表
     * @param planCommunity 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PlanCommunity> getPage(PlanCommunity planCommunity, Integer pageNum, Integer pageSize);
    
    /**
     * 根据方案ID查询关联列表
     * @param planId 方案ID
     * @return 方案社区关联列表
     */
    List<PlanCommunity> getByPlanId(Integer planId);
    
    /**
     * 根据社区ID查询关联列表
     * @param communityId 社区ID
     * @return 方案社区关联列表
     */
    List<PlanCommunity> getByCommunityId(Integer communityId);
    
    /**
     * 新增方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 影响的行数
     */
    int add(PlanCommunity planCommunity);
    
    /**
     * 批量新增方案社区关联
     * @param list 方案社区关联列表
     * @return 影响的行数
     */
    int batchAdd(List<PlanCommunity> list);
    
    /**
     * 更新方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 影响的行数
     */
    int update(PlanCommunity planCommunity);
    
    /**
     * 根据ID删除方案社区关联
     * @param id 关联ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除方案社区关联
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
}
