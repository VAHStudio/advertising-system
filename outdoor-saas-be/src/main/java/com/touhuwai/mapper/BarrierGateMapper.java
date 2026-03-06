package com.touhuwai.mapper;

import com.touhuwai.entity.BarrierGate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 道闸设备数据访问层
 */
@Mapper
public interface BarrierGateMapper {
    
    /**
     * 根据ID查询道闸
     * @param id 道闸ID
     * @return 道闸信息
     */
    BarrierGate selectById(Integer id);
    
    /**
     * 根据道闸编号查询
     * @param gateNo 道闸编号
     * @return 道闸信息
     */
    BarrierGate selectByGateNo(String gateNo);
    
    /**
     * 查询所有道闸
     * @return 道闸列表
     */
    List<BarrierGate> selectAll();
    
    /**
     * 分页查询道闸列表（带社区信息）
     * @param barrierGate 查询条件
     * @return 道闸列表
     */
    List<BarrierGate> selectList(BarrierGate barrierGate);
    
    /**
     * 根据条件统计数量
     * @param barrierGate 查询条件
     * @return 数量
     */
    Long count(BarrierGate barrierGate);
    
    /**
     * 根据社区ID查询道闸列表
     * @param communityId 社区ID
     * @return 道闸列表
     */
    List<BarrierGate> selectByCommunityId(Integer communityId);
    
    /**
     * 新增道闸
     * @param barrierGate 道闸信息
     * @return 影响行数
     */
    int insert(BarrierGate barrierGate);
    
    /**
     * 批量新增道闸
     * @param list 道闸列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<BarrierGate> list);
    
    /**
     * 更新道闸
     * @param barrierGate 道闸信息
     * @return 影响行数
     */
    int update(BarrierGate barrierGate);
    
    /**
     * 根据ID删除道闸
     * @param id 道闸ID
     * @return 影响行数
     */
    int deleteById(Integer id);
    
    /**
     * 批量删除道闸
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Integer> ids);
    
    /**
     * 根据社区ID删除道闸
     * @param communityId 社区ID
     * @return 影响行数
     */
    int deleteByCommunityId(Integer communityId);
    
    /**
     * 查询空闲道闸：在指定时间段内没有被占用的道闸
     * @param city 城市（可选）
     * @param communityId 社区ID（可选）
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @param limit 限制数量（可选）
     * @return 空闲道闸列表
     */
    List<BarrierGate> selectAvailableBarriers(
            @Param("city") String city,
            @Param("communityId") Integer communityId,
            @Param("beginDate") java.time.LocalDate beginDate,
            @Param("endDate") java.time.LocalDate endDate,
            @Param("limit") Integer limit);
    
    /**
     * 统计空闲道闸数量
     * @param city 城市（可选）
     * @param beginDate 开始日期
     * @param endDate 结束日期
     * @return 空闲道闸数量
     */
    Integer countAvailableBarriers(
            @Param("city") String city,
            @Param("beginDate") java.time.LocalDate beginDate,
            @Param("endDate") java.time.LocalDate endDate);

    /**
     * 根据查询参数查询道闸列表
     * @param param 查询参数
     * @return 道闸列表
     */
    List<BarrierGate> selectListByParam(com.touhuwai.dto.param.BarrierGateQueryParam param);

    /**
     * 根据查询参数统计数量
     * @param param 查询参数
     * @return 数量
     */
    Long countByParam(com.touhuwai.dto.param.BarrierGateQueryParam param);
}
