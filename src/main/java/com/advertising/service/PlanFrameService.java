package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanFrame;

import java.util.List;

/**
 * 方案框架明细服务接口
 */
public interface PlanFrameService {
    
    /**
     * 根据ID查询方案框架明细
     * @param id 明细ID
     * @return 方案框架明细信息
     */
    PlanFrame getById(Integer id);
    
    /**
     * 查询所有方案框架明细
     * @return 方案框架明细列表
     */
    List<PlanFrame> getAll();
    
    /**
     * 分页查询方案框架明细列表
     * @param planFrame 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PlanFrame> getPage(PlanFrame planFrame, Integer pageNum, Integer pageSize);
    
    /**
     * 根据方案ID查询明细列表
     * @param planId 方案ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> getByPlanId(Integer planId);
    
    /**
     * 根据框架ID查询明细列表
     * @param frameId 框架ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> getByFrameId(Integer frameId);
    
    /**
     * 根据方案社区ID查询明细列表
     * @param planCommunityId 方案社区ID
     * @return 方案框架明细列表
     */
    List<PlanFrame> getByPlanCommunityId(Integer planCommunityId);
    
    /**
     * 根据发布状态查询明细列表
     * @param releaseStatus 发布状态
     * @return 方案框架明细列表
     */
    List<PlanFrame> getByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案框架明细
     * @param planFrame 方案框架明细信息
     * @return 影响的行数
     */
    int add(PlanFrame planFrame);
    
    /**
     * 批量新增方案框架明细
     * @param list 方案框架明细列表
     * @return 影响的行数
     */
    int batchAdd(List<PlanFrame> list);
    
    /**
     * 更新方案框架明细
     * @param planFrame 方案框架明细信息
     * @return 影响的行数
     */
    int update(PlanFrame planFrame);
    
    /**
     * 根据ID删除方案框架明细
     * @param id 明细ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除方案框架明细
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
}
