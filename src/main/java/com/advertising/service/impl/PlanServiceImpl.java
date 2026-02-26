package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.Plan;
import com.advertising.mapper.PlanMapper;
import com.advertising.service.PlanService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 投放方案服务实现类
 */
@Service
@RequiredArgsConstructor
public class PlanServiceImpl implements PlanService {
    
    private final PlanMapper planMapper;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getById(Integer id) {
        return planMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getByPlanNo(String planNo) {
        return planMapper.selectByPlanNo(planNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getAll() {
        return planMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Plan> getPage(Plan plan, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Plan> list = planMapper.selectList(plan);
        PageInfo<Plan> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getByCustomer(String customer) {
        return planMapper.selectByCustomer(customer);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getByReleaseStatus(Integer releaseStatus) {
        return planMapper.selectByReleaseStatus(releaseStatus);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Plan plan) {
        return planMapper.insert(plan);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<Plan> list) {
        return planMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Plan plan) {
        return planMapper.update(plan);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return planMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return planMapper.batchDelete(ids);
    }
}
