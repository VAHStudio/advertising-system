package com.advertising.service;

import com.advertising.common.PageResult;
import com.advertising.entity.BarrierGate;

import java.util.List;

/**
 * 道闸设备服务接口
 */
public interface BarrierGateService {
    
    /**
     * 根据ID查询道闸
     * @param id 道闸ID
     * @return 道闸信息
     */
    BarrierGate getById(Integer id);
    
    /**
     * 根据道闸编号查询
     * @param gateNo 道闸编号
     * @return 道闸信息
     */
    BarrierGate getByGateNo(String gateNo);
    
    /**
     * 查询所有道闸
     * @return 道闸列表
     */
    List<BarrierGate> getAll();
    
    /**
     * 分页查询道闸列表
     * @param barrierGate 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    PageResult<BarrierGate> getPage(BarrierGate barrierGate, Integer pageNum, Integer pageSize);
    
    /**
     * 根据社区ID查询道闸列表
     * @param communityId 社区ID
     * @return 道闸列表
     */
    List<BarrierGate> getByCommunityId(Integer communityId);
    
    /**
     * 新增道闸
     * @param barrierGate 道闸信息
     * @return 影响的行数
     */
    int add(BarrierGate barrierGate);
    
    /**
     * 批量新增道闸
     * @param list 道闸列表
     * @return 影响的行数
     */
    int batchAdd(List<BarrierGate> list);
    
    /**
     * 更新道闸
     * @param barrierGate 道闸信息
     * @return 影响的行数
     */
    int update(BarrierGate barrierGate);
    
    /**
     * 根据ID删除道闸
     * @param id 道闸ID
     * @return 影响的行数
     */
    int delete(Integer id);
    
    /**
     * 批量删除道闸
     * @param ids ID列表
     * @return 影响的行数
     */
    int batchDelete(List<Integer> ids);
}
