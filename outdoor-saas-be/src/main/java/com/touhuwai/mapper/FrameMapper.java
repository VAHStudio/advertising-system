package com.touhuwai.mapper;

import com.touhuwai.entity.Frame;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 框架媒体数据访问层
 */
@Mapper
public interface FrameMapper {
    
    /**
     * 根据ID查询框架
     * @param id 框架ID
     * @return 框架信息
     */
    Frame selectById(Integer id);
    
    /**
     * 根据框架编号查询
     * @param frameNo 框架编号
     * @return 框架信息
     */
    Frame selectByFrameNo(String frameNo);
    
    /**
     * 查询所有框架
     * @return 框架列表
     */
    List<Frame> selectAll();
    
    /**
     * 分页查询框架列表（带社区信息）
     * @param frame 查询条件
     * @return 框架列表
     */
    List<Frame> selectList(Frame frame);
    
    /**
     * 根据条件统计数量
     * @param frame 查询条件
     * @return 数量
     */
    Long count(Frame frame);
    
    /**
     * 根据社区ID查询框架列表
     * @param communityId 社区ID
     * @return 框架列表
     */
    List<Frame> selectByCommunityId(Integer communityId);
    
    /**
     * 新增框架
     * @param frame 框架信息
     * @return 影响行数
     */
    int insert(Frame frame);
    
    /**
     * 批量新增框架
     * @param list 框架列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<Frame> list);
    
    /**
     * 更新框架
     * @param frame 框架信息
     * @return 影响行数
     */
    int update(Frame frame);
    
    /**
     * 根据ID删除框架
     * @param id 框架ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除框架
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);
    
    /**
     * 根据社区ID删除框架
     * @param communityId 社区ID
     * @return 影响行数
     */
    int deleteByCommunityId(Integer communityId);

    /**
     * 根据查询参数查询框架列表
     * @param param 查询参数
     * @return 框架列表
     */
    List<Frame> selectListByParam(com.touhuwai.dto.param.FrameQueryParam param);

    /**
     * 根据查询参数统计数量
     * @param param 查询参数
     * @return 数量
     */
    Long countByParam(com.touhuwai.dto.param.FrameQueryParam param);
}
