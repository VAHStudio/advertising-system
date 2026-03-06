package com.touhuwai.mapper;

import com.touhuwai.entity.Plan;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 投放方案数据访问层
 */
@Mapper
public interface PlanMapper {
    
    /**
     * 根据ID查询方案
     * @param id 方案ID
     * @return 方案信息
     */
    Plan selectById(Integer id);
    
    /**
     * 根据方案编号查询
     * @param planNo 方案编号
     * @return 方案信息
     */
    Plan selectByPlanNo(String planNo);
    
    /**
     * 查询所有方案
     * @return 方案列表
     */
    List<Plan> selectAll();
    
    /**
     * 分页查询方案列表
     * @param plan 查询条件
     * @return 方案列表
     */
    List<Plan> selectList(Plan plan);
    
    /**
     * 根据条件统计数量
     * @param plan 查询条件
     * @return 数量
     */
    Long count(Plan plan);
    
    /**
     * 根据客户查询方案列表
     * @param customer 客户名称
     * @return 方案列表
     */
    List<Plan> selectByCustomer(String customer);
    
    /**
     * 根据发布状态查询方案列表
     * @param releaseStatus 发布状态
     * @return 方案列表
     */
    List<Plan> selectByReleaseStatus(Integer releaseStatus);
    
    /**
     * 新增方案
     * @param plan 方案信息
     * @return 影响行数
     */
    int insert(Plan plan);
    
    /**
     * 批量新增方案
     * @param list 方案列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Plan> list);
    
    /**
     * 更新方案
     * @param plan 方案信息
     * @return 影响行数
     */
    int update(Plan plan);
    
    /**
     * 根据ID删除方案
     * @param id 方案ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除方案
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);

    /**
     * 根据查询参数查询方案列表
     * @param param 查询参数
     * @return 方案列表
     */
    List<Plan> selectListByParam(com.touhuwai.dto.param.PlanQueryParam param);

    /**
     * 根据查询参数统计数量
     * @param param 查询参数
     * @return 数量
     */
    Long countByParam(com.touhuwai.dto.param.PlanQueryParam param);
}
