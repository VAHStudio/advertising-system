package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.Plan;
import com.advertising.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投放方案管理控制器
 */
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
public class PlanController {
    
    private final PlanService planService;
    
    /**
     * 根据ID查询方案
     * @param id 方案ID
     * @return 方案信息
     */
    @GetMapping("/{id}")
    public Result<Plan> getById(@PathVariable Integer id) {
        Plan plan = planService.getById(id);
        if (plan == null) {
            return Result.notFound("方案不存在");
        }
        return Result.success(plan);
    }
    
    /**
     * 根据方案编号查询
     * @param planNo 方案编号
     * @return 方案信息
     */
    @GetMapping("/no/{planNo}")
    public Result<Plan> getByPlanNo(@PathVariable String planNo) {
        Plan plan = planService.getByPlanNo(planNo);
        if (plan == null) {
            return Result.notFound("方案不存在");
        }
        return Result.success(plan);
    }
    
    /**
     * 查询所有方案
     * @return 方案列表
     */
    @GetMapping("/list")
    public Result<List<Plan>> getAll() {
        List<Plan> list = planService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询方案列表
     * @param plan 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    public Result<PageResult<Plan>> getPage(
            @RequestBody Plan plan,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Plan> pageResult = planService.getPage(plan, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据客户查询方案列表
     * @param customer 客户名称
     * @return 方案列表
     */
    @GetMapping("/customer/{customer}")
    public Result<List<Plan>> getByCustomer(@PathVariable String customer) {
        List<Plan> list = planService.getByCustomer(customer);
        return Result.success(list);
    }
    
    /**
     * 根据发布状态查询方案列表
     * @param releaseStatus 发布状态
     * @return 方案列表
     */
    @GetMapping("/status/{releaseStatus}")
    public Result<List<Plan>> getByReleaseStatus(@PathVariable Integer releaseStatus) {
        List<Plan> list = planService.getByReleaseStatus(releaseStatus);
        return Result.success(list);
    }
    
    /**
     * 新增方案
     * @param plan 方案信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Integer> add(@RequestBody Plan plan) {
        if (plan.getPlanNo() == null || plan.getPlanNo().trim().isEmpty()) {
            return Result.badRequest("方案编号不能为空");
        }
        int result = planService.add(plan);
        if (result > 0) {
            return Result.success("新增成功", plan.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增方案
     * @param list 方案列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    public Result<Integer> batchAdd(@RequestBody List<Plan> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("方案列表不能为空");
        }
        int result = planService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新方案
     * @param plan 方案信息
     * @return 操作结果
     */
    @PutMapping
    public Result<Integer> update(@RequestBody Plan plan) {
        if (plan.getId() == null) {
            return Result.badRequest("方案ID不能为空");
        }
        int result = planService.update(plan);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除方案
     * @param id 方案ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Integer> delete(@PathVariable Integer id) {
        int result = planService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除方案
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    public Result<Integer> batchDelete(@RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = planService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
}
