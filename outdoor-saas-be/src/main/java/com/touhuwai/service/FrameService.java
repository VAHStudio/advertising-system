package com.touhuwai.service;

import com.touhuwai.common.PageResult;
import com.touhuwai.entity.Frame;

import java.util.List;

/**
 * 框架媒体服务接口
 */
public interface FrameService {
    
    /**
     * 根据ID查询框架
     * @param id 框架ID
     * @return 框架信息
     */
    Frame getById(Integer id);
    
    /**
     * 根据框架编号查询
     * @param frameNo 框架编号
     * @return 框架信息
     */
    Frame getByFrameNo(String frameNo);
    
    /**
     * 查询所有框架
     * @return 框架列表
     */
    List<Frame> getAll();
    
    /**
     * 分页查询框架列表
     * @param frame 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<Frame> getPage(Frame frame, Integer pageNum, Integer pageSize);
    
    /**
     * 根据社区ID查询框架列表
     * @param communityId 社区ID
     * @return 框架列表
     */
    List<Frame> getByCommunityId(Integer communityId);
    
    /**
     * 新增框架
     * @param frame 框架信息
     * @return 影响的行数
     */
    int add(Frame frame);
    
    /**
     * 批量新增框架
     * @param list 框架列表
     * @return 影响的行数
     */
    int batchAdd(List<Frame> list);
    
    /**
     * 更新框架
     * @param frame 框架信息
     * @return 影响的行数
     */
    int update(Frame frame);
    
    /**
     * 根据ID删除框架
     * @param id 框架ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除框架
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);

    /**
     * 根据查询参数查询框架列表（不分页）
     * @param param 查询参数
     * @return 框架列表
     */
    List<Frame> getListByParam(com.touhuwai.dto.param.FrameQueryParam param);

    /**
     * 根据查询参数分页查询框架列表
     * @param param 查询参数
     * @return 分页结果
     */
    PageResult<Frame> getPageByParam(com.touhuwai.dto.param.FrameQueryParam param);
}
