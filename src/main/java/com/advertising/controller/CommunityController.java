package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.Community;
import com.advertising.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区信息管理控制器
 */
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Tag(name = "社区管理", description = "社区信息的增删改查操作")
public class CommunityController {
    
    private final CommunityService communityService;
    
    /**
     * 根据ID查询社区
     * @param id 社区ID
     * @return 社区信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询社区", description = "根据社区ID获取详细信息")
    public Result<Community> getById(
            @Parameter(description = "社区ID", required = true) @PathVariable Integer id) {
        Community community = communityService.getById(id);
        if (community == null) {
            return Result.notFound("社区不存在");
        }
        return Result.success(community);
    }
    
    /**
     * 根据社区编号查询
     * @param communityNo 社区编号
     * @return 社区信息
     */
    @GetMapping("/no/{communityNo}")
    @Operation(summary = "根据编号查询社区", description = "根据社区编号获取详细信息")
    public Result<Community> getByCommunityNo(
            @Parameter(description = "社区编号", required = true) @PathVariable String communityNo) {
        Community community = communityService.getByCommunityNo(communityNo);
        if (community == null) {
            return Result.notFound("社区不存在");
        }
        return Result.success(community);
    }
    
    /**
     * 查询所有社区
     * @return 社区列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有社区", description = "获取所有社区信息列表")
    public Result<List<Community>> getAll() {
        List<Community> list = communityService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询社区列表
     * @param community 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询社区", description = "根据条件分页查询社区列表")
    public Result<PageResult<Community>> getPage(
            @RequestBody Community community,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Community> pageResult = communityService.getPage(community, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 新增社区
     * @param community 社区信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增社区", description = "创建新的社区信息")
    public Result<Integer> add(
            @Parameter(description = "社区信息", required = true) @RequestBody Community community) {
        if (community.getCommunityNo() == null || community.getCommunityNo().trim().isEmpty()) {
            return Result.badRequest("社区编号不能为空");
        }
        int result = communityService.add(community);
        if (result > 0) {
            return Result.success("新增成功", community.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增社区
     * @param list 社区列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增社区", description = "批量创建社区信息")
    public Result<Integer> batchAdd(
            @Parameter(description = "社区列表", required = true) @RequestBody List<Community> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("社区列表不能为空");
        }
        int result = communityService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新社区
     * @param community 社区信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新社区", description = "更新社区信息")
    public Result<Integer> update(
            @Parameter(description = "社区信息", required = true) @RequestBody Community community) {
        if (community.getId() == null) {
            return Result.badRequest("社区ID不能为空");
        }
        int result = communityService.update(community);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除社区
     * @param id 社区ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除社区", description = "根据ID删除社区信息")
    public Result<Integer> delete(
            @Parameter(description = "社区ID", required = true) @PathVariable Integer id) {
        int result = communityService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除社区
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除社区", description = "根据ID列表批量删除社区")
    public Result<Integer> batchDelete(
            @Parameter(description = "社区ID列表", required = true) @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = communityService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
    
    /**
     * 根据城市查询社区
     * @param city 城市名称
     * @return 社区列表
     */
    @GetMapping("/city/{city}")
    @Operation(summary = "根据城市查询社区", description = "根据城市名称获取社区列表")
    public Result<List<Community>> getByCity(
            @Parameter(description = "城市名称", required = true) @PathVariable String city) {
        List<Community> list = communityService.getByCity(city);
        return Result.success(list);
    }
}
