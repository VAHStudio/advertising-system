package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.Plan;

import java.util.List;

/**
 * 投放方案服务接口
 */
public interface PlanService {
    
    /**
     * 根据ID查询方案
     * @param id 方案ID
     * @return 方案信息
     */
    Plan getById(Integer id);
    
    /**
     * 根据方案编号查询
     * @param planNo 方案编号
     * @return 方案信息
     */
    Plan getByPlanNo(String planNo);
    
    /**
     * 查询所有方案
     * @return 方案列表
     */
    List<Plan> getAll();
    
    /**
     * 分页查询方案列表
     * @param plan 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Plan> getPage(Plan plan, Integer pageNum, Integer pageSize);
    
    /**
     * 根据客户查询方案列表
     * @param customer 客户名称
     * @return 方案列表
     */
    List<Plan> getByCustomer(String customer);
    
    /**
     * 根据发布状态查询方案列表
     * @param releaseStatus 发布状态
     * @return 方案列表
     */
    List<Plan> getByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案
     * @param plan 方案信息
     * @return 影响的行数
     */
    int add(Plan plan);
    
    /**
     * 批量新增方案
     * @param list 方案列表
     * @return 影响的行数
     */
    int batchAdd(List<Plan> list);
    
    /**
     * 更新方案
     * @param plan 方案信息
     * @return 影响的行数
     */
    int update(Plan plan);
    
    /**
     * 根据ID删除方案
     * @param id 方案ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除方案
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
}
