package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanFrame;
import com.advertising.mapper.PlanFrameMapper;
import com.advertising.service.PlanFrameService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 方案框架明细服务实现类
 */
@Service
public class PlanFrameServiceImpl implements PlanFrameService {
    
    private final PlanFrameMapper planFrameMapper;
    
    @Autowired
    public PlanFrameServiceImpl(PlanFrameMapper planFrameMapper) {
        this.planFrameMapper = planFrameMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlanFrame getById(Integer id) {
        return planFrameMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanFrame> getAll() {
        return planFrameMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<PlanFrame> getPage(PlanFrame planFrame, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PlanFrame> list = planFrameMapper.selectList(planFrame);
        PageInfo<PlanFrame> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanFrame> getByPlanId(Integer planId) {
        return planFrameMapper.selectByPlanId(planId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanFrame> getByFrameId(Integer frameId) {
        return planFrameMapper.selectByFrameId(frameId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanFrame> getByPlanCommunityId(Integer planCommunityId) {
        return planFrameMapper.selectByPlanCommunityId(planCommunityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanFrame> getByReleaseStatus(Integer releaseStatus) {
        return planFrameMapper.selectByReleaseStatus(releaseStatus);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(PlanFrame planFrame) {
        return planFrameMapper.insert(planFrame);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<PlanFrame> list) {
        return planFrameMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(PlanFrame planFrame) {
        return planFrameMapper.update(planFrame);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return planFrameMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return planFrameMapper.batchDelete(ids);
    }
}
