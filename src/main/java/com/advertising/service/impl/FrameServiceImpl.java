package com.advertising.service.impl;

import com.advertising.common.PageResult;
import com.advertising.entity.Frame;
import com.advertising.mapper.FrameMapper;
import com.advertising.service.FrameService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 框架媒体服务实现类
 */
@Service
public class FrameServiceImpl implements FrameService {
    
    private final FrameMapper frameMapper;
    
    @Autowired
    public FrameServiceImpl(FrameMapper frameMapper) {
        this.frameMapper = frameMapper;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Frame getById(Integer id) {
        return frameMapper.selectById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Frame getByFrameNo(String frameNo) {
        return frameMapper.selectByFrameNo(frameNo);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Frame> getAll() {
        return frameMapper.selectAll();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Frame> getPage(Frame frame, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Frame> list = frameMapper.selectList(frame);
        PageInfo<Frame> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Frame> getByCommunityId(Integer communityId) {
        return frameMapper.selectByCommunityId(communityId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(Frame frame) {
        return frameMapper.insert(frame);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchAdd(List<Frame> list) {
        return frameMapper.batchInsert(list);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int update(Frame frame) {
        return frameMapper.update(frame);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int delete(Integer id) {
        return frameMapper.deleteById(id);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDelete(List<Integer> ids) {
        return frameMapper.batchDelete(ids);
    }
}
