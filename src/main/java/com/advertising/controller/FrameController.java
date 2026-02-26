package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.Frame;
import com.advertising.service.FrameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 框架媒体管理控制器
 */
@RestController
@RequestMapping("/frame")
@RequiredArgsConstructor
@Tag(name = "框架管理", description = "框架媒体的增删改查操作")
public class FrameController {
    
    private final FrameService frameService;
    
    /**
     * 根据ID查询框架
     * @param id 框架ID
     * @return 框架信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询框架", description = "根据框架ID获取详细信息")
    public Result<Frame> getById(
            @Parameter(description = "框架ID", required = true) @PathVariable Integer id) {
        Frame frame = frameService.getById(id);
        if (frame == null) {
            return Result.notFound("框架不存在");
        }
        return Result.success(frame);
    }
    
    /**
     * 根据框架编号查询
     * @param frameNo 框架编号
     * @return 框架信息
     */
    @GetMapping("/no/{frameNo}")
    @Operation(summary = "根据编号查询框架", description = "根据框架编号获取详细信息")
    public Result<Frame> getByFrameNo(
            @Parameter(description = "框架编号", required = true) @PathVariable String frameNo) {
        Frame frame = frameService.getByFrameNo(frameNo);
        if (frame == null) {
            return Result.notFound("框架不存在");
        }
        return Result.success(frame);
    }
    
    /**
     * 查询所有框架
     * @return 框架列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询所有框架", description = "获取所有框架信息列表")
    public Result<List<Frame>> getAll() {
        List<Frame> list = frameService.getAll();
        return Result.success(list);
    }
    
    /**
     * 分页查询框架列表
     * @param frame 查询条件
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页结果
     */
    @PostMapping("/page")
    @Operation(summary = "分页查询框架", description = "根据条件分页查询框架列表")
    public Result<PageResult<Frame>> getPage(
            @RequestBody Frame frame,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Frame> pageResult = frameService.getPage(frame, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据社区ID查询框架列表
     * @param communityId 社区ID
     * @return 框架列表
     */
    @GetMapping("/community/{communityId}")
    @Operation(summary = "根据社区查询框架", description = "根据社区ID获取框架列表")
    public Result<List<Frame>> getByCommunityId(
            @Parameter(description = "社区ID", required = true) @PathVariable Integer communityId) {
        List<Frame> list = frameService.getByCommunityId(communityId);
        return Result.success(list);
    }
    
    /**
     * 新增框架
     * @param frame 框架信息
     * @return 操作结果
     */
    @PostMapping
    @Operation(summary = "新增框架", description = "创建新的框架信息")
    public Result<Integer> add(
            @Parameter(description = "框架信息", required = true) @RequestBody Frame frame) {
        if (frame.getFrameNo() == null || frame.getFrameNo().trim().isEmpty()) {
            return Result.badRequest("框架编号不能为空");
        }
        if (frame.getCommunityId() == null) {
            return Result.badRequest("社区ID不能为空");
        }
        int result = frameService.add(frame);
        if (result > 0) {
            return Result.success("新增成功", frame.getId());
        }
        return Result.error("新增失败");
    }
    
    /**
     * 批量新增框架
     * @param list 框架列表
     * @return 操作结果
     */
    @PostMapping("/batch")
    @Operation(summary = "批量新增框架", description = "批量创建框架信息")
    public Result<Integer> batchAdd(
            @Parameter(description = "框架列表", required = true) @RequestBody List<Frame> list) {
        if (list == null || list.isEmpty()) {
            return Result.badRequest("框架列表不能为空");
        }
        int result = frameService.batchAdd(list);
        if (result > 0) {
            return Result.success("批量新增成功", result);
        }
        return Result.error("批量新增失败");
    }
    
    /**
     * 更新框架
     * @param frame 框架信息
     * @return 操作结果
     */
    @PutMapping
    @Operation(summary = "更新框架", description = "更新框架信息")
    public Result<Integer> update(
            @Parameter(description = "框架信息", required = true) @RequestBody Frame frame) {
        if (frame.getId() == null) {
            return Result.badRequest("框架ID不能为空");
        }
        int result = frameService.update(frame);
        if (result > 0) {
            return Result.success("更新成功", result);
        }
        return Result.error("更新失败");
    }
    
    /**
     * 根据ID删除框架
     * @param id 框架ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除框架", description = "根据ID删除框架信息")
    public Result<Integer> delete(
            @Parameter(description = "框架ID", required = true) @PathVariable Integer id) {
        int result = frameService.delete(id);
        if (result > 0) {
            return Result.success("删除成功", result);
        }
        return Result.error("删除失败");
    }
    
    /**
     * 批量删除框架
     * @param ids ID列表
     * @return 操作结果
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除框架", description = "根据ID列表批量删除框架")
    public Result<Integer> batchDelete(
            @Parameter(description = "框架ID列表", required = true) @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = frameService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }
}
