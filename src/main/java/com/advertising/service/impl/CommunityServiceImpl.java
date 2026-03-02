package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.Community;
import com.advertising.mapper.CommunityMapper;
import com.advertising.service.CommunityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 社区信息服务实现类
 */
@Service
public class CommunityServiceImpl implements CommunityService {
    
    private final CommunityMapper communityMapper;
    
    @Autowired
    public CommunityServiceImpl(CommunityMapper communityMapper) {
        this.communityMapper = communityMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Community getById(Integer id) {
        return communityMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Community getByCommunityNo(String communityNo) {
        return communityMapper.selectByCommunityNo(communityNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Community> getAll() {
        return communityMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Community> getPage(Community community, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Community> list = communityMapper.selectList(community);
        PageInfo<Community> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Community community) {
        return communityMapper.insert(community);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<Community> list) {
        return communityMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Community community) {
        return communityMapper.update(community);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return communityMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return communityMapper.batchDelete(ids);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Community> getByCity(String city) {
        return communityMapper.selectByCity(city);
    }
}
