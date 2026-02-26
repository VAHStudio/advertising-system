package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.Plan;
import com.advertising.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 投放方案管理控制器
 */
@RestController
@RequestMapping("/plan")
@RequiredArgsConstructor
@Tag(name = "方案管理", description = "投放方案的增删改查操作")
public class PlanController {
    
    private final PlanService planService;
    
    /**
     * 根据ID查询方案
     * @param id 方案ID
     * @return 方案信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询方案", description = "根据方案ID获取详细信息")
    public Result<Plan> getById(
            @Parameter(description = "方案ID", required = true) @PathVariable Integer id) {
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
    @Operation(summary = "根据编号查询方案", description = "根据方案编号获取详细信息")
    public Result<Plan> getByPlanNo(
            @Parameter(description = "方案编号", required = true) @PathVariable String planNo) {
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
    @Operation(summary = "查询所有方案", description = "获取所有方案信息列表")
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
    @Operation(summary = "分页查询方案", description = "根据条件分页查询方案列表")
    public Result<PageResult<Plan>> getPage(
            @RequestBody Plan plan,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Plan> pageResult = planService.getPage(plan, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据客户查询方案列表
     * @param customer 客户名称
     * @return 方案列表
     */
    @GetMapping("/customer/{customer}")
    @Operation(summary = "根据客户查询方案", description = "根据客户名称获取方案列表")
    public Result<List<Plan>> getByCustomer(
            @Parameter(description = "客户名称", required = true) @PathVariable String customer) {
        List<Plan> list = planService.getByCustomer(customer);
        return Result.success(list);
    }
    
    /**
     * 根据发布状态查询方案列表
     * @param releaseStatus 发布状态
     * @return 方案列表
     */
    @GetMapping("/status/{releaseStatus}")
    @Operation(summary = "根据状态查询方案", description = "根据发布状态获取方案列表")
    public Result<List<Plan>> getByReleaseStatus(
            @Parameter(description = "发布状态：1-意向，2-锁位，3-执行中，4-执行完毕，5-档", required = true) 
            @PathVariable Integer releaseStatus) {
        List<Plan> list = planService.getByReleaseStatus(releaseStatus);
        return Result.success(list);
    }
    
    /**
     * 新增方案
     * @param plan 方案信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增方案", description = "创建新的方案信息")
    public Result<Integer> add(
            @Parameter(description = "方案信息", required = true) @RequestBody Plan plan) {
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
    @Operation(summary = "批量新增方案", description = "批量创建方案信息")
    public Result<Integer> batchAdd(
            @Parameter(description = "方案列表", required = true) @RequestBody List<Plan> list) {
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
    @Operation(summary = "更新方案", description = "更新方案信息")
    public Result<Integer> update(
            @Parameter(description = "方案信息", required = true) @RequestBody Plan plan) {
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
    @Operation(summary = "删除方案", description = "根据ID删除方案信息")
    public Result<Integer> delete(
            @Parameter(description = "方案ID", required = true) @PathVariable Integer id) {
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
    @Operation(summary = "批量删除方案", description = "根据ID列表批量删除方案")
    public Result<Integer> batchDelete(
            @Parameter(description = "方案ID列表", required = true) @RequestParam List<Integer> ids) {
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
