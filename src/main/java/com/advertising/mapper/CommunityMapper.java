package com.advertising.mapper;

import com.advertising.entity.Community;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 社区信息数据访问层
 */
@Mapper
public interface CommunityMapper {
    
    /**
     * 根据ID查询社区
     * @param id 社区ID
     * @return 社区信息
     */
    Community selectById(Integer id);
    
    /**
     * 根据社区编号查询
     * @param communityNo 社区编号
     * @return 社区信息
     */
    Community selectByCommunityNo(String communityNo);
    
    /**
     * 查询所有社区
     * @return 社区列表
     */
    List<Community> selectAll();
    
    /**
     * 分页查询社区列表
     * @param community 查询条件
     * @return 社区列表
     */
    List<Community> selectList(Community community);
    
    /**
     * 根据条件统计数量
     * @param community 查询条件
     * @return 数量
     */
    Long count(Community community);
    
    /**
     * 新增社区
     * @param community 社区信息
     * @return 影响行数
     */
    int insert(Community community);
    
    /**
     * 批量新增社区
     * @param list 社区列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Community> list);
    
    /**
     * 更新社区
     * @param community 社区信息
     * @return 影响行数
     */
    int update(Community community);
    
    /**
     * 根据ID删除社区
     * @param id 社区ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除社区
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);
    
    /**
     * 根据城市查询社区
     * @param city 城市名称
     * @return 社区列表
     */
    List<Community> selectByCity(String city);
}
