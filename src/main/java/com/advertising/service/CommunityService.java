package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.Community;

import java.util.List;

/**
 * 社区信息服务接口
 */
public interface CommunityService {
    
    /**
     * 根据ID查询社区
     * @param id 社区ID
     * @return 社区信息
     */
    Community getById(Integer id);
    
    /**
     * 根据社区编号查询
     * @param communityNo 社区编号
     * @return 社区信息
     */
    Community getByCommunityNo(String communityNo);
    
    /**
     * 查询所有社区
     * @return 社区列表
     */
    List<Community> getAll();
    
    /**
     * 分页查询社区列表
     * @param community 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Community> getPage(Community community, Integer pageNum, Integer pageSize);
    
    /**
     * 新增社区
     * @param community 社区信息
     * @return 影响的行数
     */
    int add(Community community);
    
    /**
     * 批量新增社区
     * @param list 社区列表
     * @return 影响的行数
     */
    int batchAdd(List<Community> list);
    
    /**
     * 更新社区
     * @param community 社区信息
     * @return 影响的行数
     */
    int update(Community community);
    
    /**
     * 根据ID删除社区
     * @param id 社区ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除社区
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
    
    /**
     * 根据城市查询社区
     * @param city 城市名称
     * @return 社区列表
     */
    List<Community> getByCity(String city);
}
