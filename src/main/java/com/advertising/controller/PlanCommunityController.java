package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.PlanCommunity;
import com.advertising.service.PlanCommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方案社区关联管理控制器
 */
@RestController
@RequestMapping("/plan-community")
@RequiredArgsConstructor
@Tag(name = "方案社区关联管理", description = "方案与社区关联关系的增删改查操作")
public class PlanCommunityController {
    
    private final PlanCommunityService planCommunityService;
    
    /**
     * 根据ID查询方案社区关联
     * @param id 关联ID
     * @return 方案社区关联信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询关联", description = "根据关联ID获取详细信息")
    public Result<PlanCommunity> getById(
            @Parameter(description = "关联ID", required = true) @PathVariable Integer id) {
        PlanCommunity planCommunity = planCommunityService.getById(id);
        if (planCommunity == null) {
            return Result.notFound("关联信息不存在");
        }
        return Result.success(planCommunity);
    }
    
    /**
     * 根据方案ID和社区ID查询
     * @param planId 方案ID
     * @param communityId 社区ID
     * @return 方案社区关联信息
     */
    @GetMapping("/query")
    @Operation(summary = "根据方案和社区查询", description = "根据方案ID和社区ID获取关联信息")
    public Result<PlanCommunity> getByPlanAndCommunity(
            @Parameter(description = "方案ID", required = true) @RequestParam Integer planId,
            @Parameter(description = "社区ID", required = true) @RequestParam Integer communityId) {
        PlanCommunity planCommunity = planCommunityService.getByPlanAndCommunity(planId, communityId);
        if (planCommunity == null) {
            return Result.notFound("关联信息不存在");
        }
        return Result.success(planCommunity);
    }
    
    /**
     * 查询所有方案社区关联
     * @return 方案社区关联列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有关联", description = "获取所有方案社区关联列表")
    public Result<List<PlanCommunity>> getAll() {
        List<PlanCommunity> list = planCommunityService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询方案社区关联列表
     * @param planCommunity 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询关联", description = "根据条件分页查询方案社区关联列表")
    public Result<PageResult<PlanCommunity>> getPage(
            @RequestBody PlanCommunity planCommunity,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<PlanCommunity> pageResult = planCommunityService.getPage(planCommunity, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据方案ID查询关联列表
     * @param planId 方案ID
     * @return 方案社区关联列表
     */
    @GetMapping("/plan/{planId}")
    @Operation(summary = "根据方案查询关联", description = "根据方案ID获取关联列表")
    public Result<List<PlanCommunity>> getByPlanId(
            @Parameter(description = "方案ID", required = true) @PathVariable Integer planId) {
        List<PlanCommunity> list = planCommunityService.getByPlanId(planId);
        return Result.success(list);
    }
    
    /**
     * 根据社区ID查询关联列表
     * @param communityId 社区ID
     * @return 方案社区关联列表
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "根据社区查询关联", description = "根据社区ID获取关联列表")
    public Result<List<PlanCommunity>> getByCommunityId(
            @Parameter(description = "社区ID", required = true) @PathVariable Integer communityId) {
        List<PlanCommunity> list = planCommunityService.getByCommunityId(communityId);
        return Result.success(list);
    }
    
    /**
     * 新增方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增关联", description = "创建方案与社区的关联关系")
    public Result<Integer> add(
            @Parameter(description = "关联信息", required = true) @RequestBody PlanCommunity planCommunity) {
        if (planCommunity.getPlanId() == null) {
            return Result.badRequest("方案ID不能为空");
        }
        if (planCommunity.getCommunityId() == null) {
            return Result.badRequest("社区ID不能为空");
        }
        int result = planCommunityService.add(planCommunity);
        if (result > 0) {
            return Result.success("新增成功", planCommunity.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增方案社区关联
     * @param list 关联列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增关联", description = "批量创建方案与社区的关联关系")
    public Result<Integer> batchAdd(
            @Parameter(description = "关联列表", required = true) @RequestBody List<PlanCommunity> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("关联列表不能为空");
        }
        int result = planCommunityService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新方案社区关联
     * @param planCommunity 方案社区关联信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新关联", description = "更新方案与社区的关联关系")
    public Result<Integer> update(
            @Parameter(description = "关联信息", required = true) @RequestBody PlanCommunity planCommunity) {
        if (planCommunity.getId() == null) {
            return Result.badRequest("关联ID不能为空");
        }
        int result = planCommunityService.update(planCommunity);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除方案社区关联
     * @param id 关联ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除关联", description = "根据ID删除方案与社区的关联关系")
    public Result<Integer> delete(
            @Parameter(description = "关联ID", required = true) @PathVariable Integer id) {
        int result = planCommunityService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除方案社区关联
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除关联", description = "根据ID列表批量删除关联关系")
    public Result<Integer> batchDelete(
            @Parameter(description = "关联ID列表", required = true) @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = planCommunityService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
}
