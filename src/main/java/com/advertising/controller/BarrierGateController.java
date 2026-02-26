package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.BarrierGate;
import com.advertising.service.BarrierGateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 道闸设备管理控制器
 */
@RestController
@RequestMapping("/barrier-gate")
@RequiredArgsConstructor
@Tag(name = "道闸管理", description = "道闸设备的增删改查操作")
public class BarrierGateController {
    
    private final BarrierGateService barrierGateService;
    
    /**
     * 根据ID查询道闸
     * @param id 道闸ID
     * @return 道闸信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询道闸", description = "根据道闸ID获取详细信息")
    public Result<BarrierGate> getById(
            @Parameter(description = "道闸ID", required = true) @PathVariable Integer id) {
        BarrierGate barrierGate = barrierGateService.getById(id);
        if (barrierGate == null) {
            return Result.notFound("道闸不存在");
        }
        return Result.success(barrierGate);
    }
    
    /**
     * 根据道闸编号查询
     * @param gateNo 道闸编号
     * @return 道闸信息
     */
    @GetMapping("/no/{gateNo}")
    @Operation(summary = "根据编号查询道闸", description = "根据道闸编号获取详细信息")
    public Result<BarrierGate> getByGateNo(
            @Parameter(description = "道闸编号", required = true) @PathVariable String gateNo) {
        BarrierGate barrierGate = barrierGateService.getByGateNo(gateNo);
        if (barrierGate == null) {
            return Result.notFound("道闸不存在");
        }
        return Result.success(barrierGate);
    }
    
    /**
     * 查询所有道闸
     * @return 道闸列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有道闸", description = "获取所有道闸信息列表")
    public Result<List<BarrierGate>> getAll() {
        List<BarrierGate> list = barrierGateService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询道闸列表
     * @param barrierGate 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询道闸", description = "根据条件分页查询道闸列表")
    public Result<PageResult<BarrierGate>> getPage(
            @RequestBody BarrierGate barrierGate,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BarrierGate> pageResult = barrierGateService.getPage(barrierGate, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据社区ID查询道闸列表
     * @param communityId 社区ID
     * @return 道闸列表
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "根据社区查询道闸", description = "根据社区ID获取道闸列表")
    public Result<List<BarrierGate>> getByCommunityId(
            @Parameter(description = "社区ID", required = true) @PathVariable Integer communityId) {
        List<BarrierGate> list = barrierGateService.getByCommunityId(communityId);
        return Result.success(list);
    }
    
    /**
     * 新增道闸
     * @param barrierGate 道闸信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增道闸", description = "创建新的道闸信息")
    public Result<Integer> add(
            @Parameter(description = "道闸信息", required = true) @RequestBody BarrierGate barrierGate) {
        if (barrierGate.getGateNo() == null || barrierGate.getGateNo().trim().isEmpty()) {
            return Result.badRequest("道闸编号不能为空");
        }
        if (barrierGate.getCommunityId() == null) {
            return Result.badRequest("社区ID不能为空");
        }
        int result = barrierGateService.add(barrierGate);
        if (result > 0) {
            return Result.success("新增成功", barrierGate.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增道闸
     * @param list 道闸列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增道闸", description = "批量创建道闸信息")
    public Result<Integer> batchAdd(
            @Parameter(description = "道闸列表", required = true) @RequestBody List<BarrierGate> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("道闸列表不能为空");
        }
        int result = barrierGateService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新道闸
     * @param barrierGate 道闸信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新道闸", description = "更新道闸信息")
    public Result<Integer> update(
            @Parameter(description = "道闸信息", required = true) @RequestBody BarrierGate barrierGate) {
        if (barrierGate.getId() == null) {
            return Result.badRequest("道闸ID不能为空");
        }
        int result = barrierGateService.update(barrierGate);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除道闸
     * @param id 道闸ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除道闸", description = "根据ID删除道闸信息")
    public Result<Integer> delete(
            @Parameter(description = "道闸ID", required = true) @PathVariable Integer id) {
        int result = barrierGateService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除道闸
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除道闸", description = "根据ID列表批量删除道闸")
    public Result<Integer> batchDelete(
            @Parameter(description = "道闸ID列表", required = true) @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = barrierGateService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
}
