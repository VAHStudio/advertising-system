package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.PlanCommunity;
import com.advertising.mapper.PlanCommunityMapper;
import com.advertising.service.PlanCommunityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 方案社区关联服务实现类
 */
@Service
public class PlanCommunityServiceImpl implements PlanCommunityService {
    
    private final PlanCommunityMapper planCommunityMapper;
    
    @Autowired
    public PlanCommunityServiceImpl(PlanCommunityMapper planCommunityMapper) {
        this.planCommunityMapper = planCommunityMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlanCommunity getById(Integer id) {
        return planCommunityMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PlanCommunity getByPlanAndCommunity(Integer planId, Integer communityId) {
        return planCommunityMapper.selectByPlanAndCommunity(planId, communityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanCommunity> getAll() {
        return planCommunityMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<PlanCommunity> getPage(PlanCommunity planCommunity, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<PlanCommunity> list = planCommunityMapper.selectList(planCommunity);
        PageInfo<PlanCommunity> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanCommunity> getByPlanId(Integer planId) {
        return planCommunityMapper.selectByPlanId(planId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<PlanCommunity> getByCommunityId(Integer communityId) {
        return planCommunityMapper.selectByCommunityId(communityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(PlanCommunity planCommunity) {
        return planCommunityMapper.insert(planCommunity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<PlanCommunity> list) {
        return planCommunityMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(PlanCommunity planCommunity) {
        return planCommunityMapper.update(planCommunity);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return planCommunityMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return planCommunityMapper.batchDelete(ids);
    }
}
