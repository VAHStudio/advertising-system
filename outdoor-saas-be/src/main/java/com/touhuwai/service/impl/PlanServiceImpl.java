package com.touhuwai.service.impl;

import com.touhuwai.common.PageResult;
import com.touhuwai.entity.Plan;
import com.touhuwai.mapper.PlanMapper;
import com.touhuwai.mapper.PlanBarrierMapper;
import com.touhuwai.mapper.PlanFrameMapper;
import com.touhuwai.service.PlanService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * 投放方案服务实现类
 */
@Service
public class PlanServiceImpl implements PlanService {

    private final PlanMapper planMapper;
    private final PlanBarrierMapper planBarrierMapper;
    private final PlanFrameMapper planFrameMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PlanServiceImpl(PlanMapper planMapper, PlanBarrierMapper planBarrierMapper, PlanFrameMapper planFrameMapper, JdbcTemplate jdbcTemplate) {
        this.planMapper = planMapper;
        this.planBarrierMapper = planBarrierMapper;
        this.planFrameMapper = planFrameMapper;
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void initDatabase() {
        try {
            // Check if created_at column exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'plan' AND COLUMN_NAME = 'created_at'",
                Integer.class
            );
            if (count == null || count == 0) {
                // Add columns
                jdbcTemplate.execute(
                    "ALTER TABLE plan ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "ADD COLUMN updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                );
                System.out.println("Database columns added successfully");
            }
        } catch (Exception e) {
            System.err.println("Database migration failed: " + e.getMessage());
        }
    }

    /**
     * 填充方案的媒体类型和数量
     * 注意：每个方案只能选择一种媒体类型（1-道闸 或 2-框架）
     * @param plan 方案对象
     */
    private void fillMediaInfo(Plan plan) {
        if (plan == null) {
            return;
        }

        // 统计道闸数量
        int barrierCount = planBarrierMapper.countByPlanId(plan.getId());
        // 统计框架数量
        int frameCount = planFrameMapper.countByPlanId(plan.getId());

        // 判断媒体类型（1-道闸，2-框架）
        // 每个方案只能选择一种媒体类型
        if (barrierCount > 0 && frameCount == 0) {
            plan.setMediaType(1); // 道闸
            plan.setMediaCount(barrierCount);
        } else if (frameCount > 0 && barrierCount == 0) {
            plan.setMediaType(2); // 框架
            plan.setMediaCount(frameCount);
        } else if (barrierCount > 0 && frameCount > 0) {
            // 理论上不应该出现这种情况，每个方案只能有一种媒体类型
            // 优先返回道闸类型（或者可以选择报错）
            plan.setMediaType(1);
            plan.setMediaCount(barrierCount);
        } else {
            plan.setMediaType(null); // 未选择
            plan.setMediaCount(0);
        }
    }

    /**
     * 批量填充方案的媒体类型和数量
     * @param plans 方案列表
     */
    private void fillMediaInfoList(List<Plan> plans) {
        if (plans == null || plans.isEmpty()) {
            return;
        }
        for (Plan plan : plans) {
            fillMediaInfo(plan);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getById(Integer id) {
        Plan plan = planMapper.selectById(id);
        fillMediaInfo(plan);
        return plan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Plan getByPlanNo(String planNo) {
        Plan plan = planMapper.selectByPlanNo(planNo);
        fillMediaInfo(plan);
        return plan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getAll() {
        List<Plan> list = planMapper.selectAll();
        fillMediaInfoList(list);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Plan> getPage(Plan plan, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Plan> list = planMapper.selectList(plan);
        fillMediaInfoList(list);
        PageInfo<Plan> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getByCustomer(String customer) {
        List<Plan> list = planMapper.selectByCustomer(customer);
        fillMediaInfoList(list);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getByReleaseStatus(Integer releaseStatus) {
        List<Plan> list = planMapper.selectByReleaseStatus(releaseStatus);
        fillMediaInfoList(list);
        return list;
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Plan> getListByParam(com.touhuwai.dto.param.PlanQueryParam param) {
        List<Plan> list = planMapper.selectListByParam(param);
        fillMediaInfoList(list);
        return list;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PageResult<Plan> getPageByParam(com.touhuwai.dto.param.PlanQueryParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<Plan> list = planMapper.selectListByParam(param);
        fillMediaInfoList(list);
        PageInfo<Plan> pageInfo = new PageInfo<>(list);
        return PageResult.build(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), list);
    }
}
