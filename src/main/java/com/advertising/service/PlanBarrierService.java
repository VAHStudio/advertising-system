package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanBarrier;

import java.util.List;

/**
 * 方案道闸明细服务接口
 */
public interface PlanBarrierService {
    
    /**
     * 根据ID查询方案道闸明细
     * @param id 明细ID
     * @return 方案道闸明细信息
     */
    PlanBarrier getById(Integer id);
    
    /**
     * 查询所有方案道闸明细
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> getAll();
    
    /**
     * 分页查询方案道闸明细列表
     * @param planBarrier 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<PlanBarrier> getPage(PlanBarrier planBarrier, Integer pageNum, Integer pageSize);
    
    /**
     * 根据方案ID查询明细列表
     * @param planId 方案ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> getByPlanId(Integer planId);
    
    /**
     * 根据道闸ID查询明细列表
     * @param barrierGateId 道闸ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> getByBarrierGateId(Integer barrierGateId);
    
    /**
     * 根据方案社区ID查询明细列表
     * @param planCommunityId 方案社区ID
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> getByPlanCommunityId(Integer planCommunityId);
    
    /**
     * 根据发布状态查询明细列表
     * @param releaseStatus 发布状态
     * @return 方案道闸明细列表
     */
    List<PlanBarrier> getByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 影响的行数
     */
    int add(PlanBarrier planBarrier);
    
    /**
     * 批量新增方案道闸明细
     * @param list 方案道闸明细列表
     * @return 影响的行数
     */
    int batchAdd(List<PlanBarrier> list);
    
    /**
     * 更新方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 影响的行数
     */
    int update(PlanBarrier planBarrier);
    
    /**
     * 根据ID删除方案道闸明细
     * @param id 明细ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除方案道闸明细
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
}
