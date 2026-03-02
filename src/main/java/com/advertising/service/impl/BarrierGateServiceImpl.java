package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.BarrierGate;
import com.advertising.mapper.BarrierGateMapper;
import com.advertising.service.BarrierGateService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 道闸设备服务实现类
 */
@Service
public class BarrierGateServiceImpl implements BarrierGateService {

    private final BarrierGateMapper barrierGateMapper;

    @Autowired
    public BarrierGateServiceImpl(BarrierGateMapper barrierGateMapper) {
        this.barrierGateMapper = barrierGateMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BarrierGate getById(Integer id) {
        return barrierGateMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BarrierGate getByGateNo(String gateNo) {
        return barrierGateMapper.selectByGateNo(gateNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<BarrierGate> getAll() {
        return barrierGateMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<BarrierGate> getPage(BarrierGate barrierGate, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<BarrierGate> list = barrierGateMapper.selectList(barrierGate);
        PageInfo<BarrierGate> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<BarrierGate> getByCommunityId(Integer communityId) {
        return barrierGateMapper.selectByCommunityId(communityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(BarrierGate barrierGate) {
        return barrierGateMapper.insert(barrierGate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<BarrierGate> list) {
        return barrierGateMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(BarrierGate barrierGate) {
        return barrierGateMapper.update(barrierGate);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return barrierGateMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return barrierGateMapper.batchDelete(ids);
    }
}
