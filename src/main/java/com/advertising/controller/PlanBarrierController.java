package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.PlanBarrier;
import com.advertising.service.PlanBarrierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方案道闸明细管理控制器
 */
@RestController
@RequestMapping("/plan-barrier")
@RequiredArgsConstructor
@Tag(name = "方案道闸明细管理", description = "方案与道闸关联明细的增删改查操作")
public class PlanBarrierController {
    
    private final PlanBarrierService planBarrierService;
    
    /**
     * 根据ID查询方案道闸明细
     * @param id 明细ID
     * @return 方案道闸明细信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询明细", description = "根据明细ID获取详细信息")
    public Result<PlanBarrier> getById(
            @Parameter(description = "明细ID", required = true) @PathVariable Integer id) {
        PlanBarrier planBarrier = planBarrierService.getById(id);
        if (planBarrier == null) {
            return Result.notFound("明细信息不存在");
        }
        return Result.success(planBarrier);
    }
    
    /**
     * 查询所有方案道闸明细
     * @return 方案道闸明细列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有明细", description = "获取所有方案道闸明细列表")
    public Result<List<PlanBarrier>> getAll() {
        List<PlanBarrier> list = planBarrierService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询方案道闸明细列表
     * @param planBarrier 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询明细", description = "根据条件分页查询方案道闸明细列表")
    public Result<PageResult<PlanBarrier>> getPage(
            @RequestBody PlanBarrier planBarrier,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<PlanBarrier> pageResult = planBarrierService.getPage(planBarrier, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据方案ID查询明细列表
     * @param planId 方案ID
     * @return 方案道闸明细列表
     */
    @GetMapping("/plan/{planId}")
    @Operation(summary = "根据方案查询明细", description = "根据方案ID获取道闸明细列表")
    public Result<List<PlanBarrier>> getByPlanId(
            @Parameter(description = "方案ID", required = true) @PathVariable Integer planId) {
        List<PlanBarrier> list = planBarrierService.getByPlanId(planId);
        return Result.success(list);
    }
    
    /**
     * 根据道闸ID查询明细列表
     * @param barrierGateId 道闸ID
     * @return 方案道闸明细列表
     */
    @GetMapping("/barrier/{barrierGateId}")
    @Operation(summary = "根据道闸查询明细", description = "根据道闸ID获取方案明细列表")
    public Result<List<PlanBarrier>> getByBarrierGateId(
            @Parameter(description = "道闸ID", required = true) @PathVariable Integer barrierGateId) {
        List<PlanBarrier> list = planBarrierService.getByBarrierGateId(barrierGateId);
        return Result.success(list);
    }
    
    /**
     * 根据方案社区ID查询明细列表
     * @param planCommunityId 方案社区ID
     * @return 方案道闸明细列表
     */
    @GetMapping("/plan-community/{planCommunityId}")
    @Operation(summary = "根据方案社区查询明细", description = "根据方案社区ID获取道闸明细列表")
    public Result<List<PlanBarrier>> getByPlanCommunityId(
            @Parameter(description = "方案社区ID", required = true) @PathVariable Integer planCommunityId) {
        List<PlanBarrier> list = planBarrierService.getByPlanCommunityId(planCommunityId);
        return Result.success(list);
    }
    
    /**
     * 根据发布状态查询明细列表
     * @param releaseStatus 发布状态
     * @return 方案道闸明细列表
     */
    @GetMapping("/status/{releaseStatus}")
    @Operation(summary = "根据状态查询明细", description = "根据发布状态获取道闸明细列表")
    public Result<List<PlanBarrier>> getByReleaseStatus(
            @Parameter(description = "发布状态：1-意向，2-锁位，3-待刊发，4-刊发中，5-可调，6-到期，7-已下刊", required = true) 
            @PathVariable Integer releaseStatus) {
        List<PlanBarrier> list = planBarrierService.getByReleaseStatus(releaseStatus);
        return Result.success(list);
    }
    
    /**
     * 新增方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增明细", description = "创建方案道闸关联明细")
    public Result<Integer> add(
            @Parameter(description = "明细信息", required = true) @RequestBody PlanBarrier planBarrier) {
        if (planBarrier.getPlanId() == null) {
            return Result.badRequest("方案ID不能为空");
        }
        if (planBarrier.getBarrierGateId() == null) {
            return Result.badRequest("道闸ID不能为空");
        }
        if (planBarrier.getPlanCommunityId() == null) {
            return Result.badRequest("方案社区ID不能为空");
        }
        int result = planBarrierService.add(planBarrier);
        if (result > 0) {
            return Result.success("新增成功", planBarrier.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增方案道闸明细
     * @param list 明细列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增明细", description = "批量创建方案道闸关联明细")
    public Result<Integer> batchAdd(
            @Parameter(description = "明细列表", required = true) @RequestBody List<PlanBarrier> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("明细列表不能为空");
        }
        int result = planBarrierService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新方案道闸明细
     * @param planBarrier 方案道闸明细信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新明细", description = "更新方案道闸关联明细")
    public Result<Integer> update(
            @Parameter(description = "明细信息", required = true) @RequestBody PlanBarrier planBarrier) {
        if (planBarrier.getId() == null) {
            return Result.badRequest("明细ID不能为空");
        }
        int result = planBarrierService.update(planBarrier);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除方案道闸明细
     * @param id 明细ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除明细", description = "根据ID删除方案道闸关联明细")
    public Result<Integer> delete(
            @Parameter(description = "明细ID", required = true) @PathVariable Integer id) {
        int result = planBarrierService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除方案道闸明细
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除明细", description = "根据ID列表批量删除明细")
    public Result<Integer> batchDelete(
            @Parameter(description = "明细ID列表", required = true) @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = planBarrierService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
}
