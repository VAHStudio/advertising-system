package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanBarrier;
import com.advertising.mapper.PlanBarrierMapper;
import com.advertising.service.PlanBarrierService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 方案道闸明细服务实现类
 */
@Service
public class PlanBarrierServiceImpl implements PlanBarrierService {
    
    private final PlanBarrierMapper planBarrierMapper;
    
    @Autowired
    public PlanBarrierServiceImpl(PlanBarrierMapper planBarrierMapper) {
        this.planBarrierMapper = planBarrierMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlanBarrier getById(Integer id) {
        return planBarrierMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanBarrier> getAll() {
        return planBarrierMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<PlanBarrier> getPage(PlanBarrier planBarrier, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PlanBarrier> list = planBarrierMapper.selectList(planBarrier);
        PageInfo<PlanBarrier> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanBarrier> getByPlanId(Integer planId) {
        return planBarrierMapper.selectByPlanId(planId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanBarrier> getByBarrierGateId(Integer barrierGateId) {
        return planBarrierMapper.selectByBarrierGateId(barrierGateId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanBarrier> getByPlanCommunityId(Integer planCommunityId) {
        return planBarrierMapper.selectByPlanCommunityId(planCommunityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanBarrier> getByReleaseStatus(Integer releaseStatus) {
        return planBarrierMapper.selectByReleaseStatus(releaseStatus);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(PlanBarrier planBarrier) {
        return planBarrierMapper.insert(planBarrier);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<PlanBarrier> list) {
        return planBarrierMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(PlanBarrier planBarrier) {
        return planBarrierMapper.update(planBarrier);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return planBarrierMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return planBarrierMapper.batchDelete(ids);
    }
}
