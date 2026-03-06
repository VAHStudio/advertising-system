package com.touhuwai.controller;

import com.touhuwai.common.PageResult;
import com.touhuwai.common.Result;
import com.touhuwai.entity.Frame;
import com.touhuwai.service.FrameService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 框架媒体管理控制器
 */
@RestController
@RequestMapping("/api/frame")
@RequiredArgsConstructor
public class FrameController {
    
    private final FrameService frameService;
    
    /**
     * 根据ID查询框架
     * @param id 框架ID
     * @return 框架信息
     */
    @GetMapping("/{id}")
    public Result<Frame> getById(
            @PathVariable Integer id) {
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
    public Result<Frame> getByFrameNo(
            @PathVariable String frameNo) {
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
    public Result<PageResult<Frame>> getPage(
            @RequestBody Frame frame,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Frame> pageResult = frameService.getPage(frame, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据社区ID查询框架列表
     * @param communityId 社区ID
     * @return 框架列表
     */
    @GetMapping("/community/{communityId}")
    public Result<List<Frame>> getByCommunityId(
            @PathVariable Integer communityId) {
        List<Frame> list = frameService.getByCommunityId(communityId);
        return Result.success(list);
    }
    
    /**
     * 新增框架
     * @param frame 框架信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Integer> add(
            @RequestBody Frame frame) {
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
    public Result<Integer> batchAdd(
            @RequestBody List<Frame> list) {
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
    public Result<Integer> update(
            @RequestBody Frame frame) {
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
    public Result<Integer> delete(
            @PathVariable Integer id) {
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
    public Result<Integer> batchDelete(
            @RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = frameService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }

    /**
     * 根据查询参数查询框架列表（不分页）
     * @param param 查询参数
     * @return 框架列表
     */
    @PostMapping("/filter")
    public Result<List<Frame>> getListByParam(@RequestBody com.touhuwai.dto.param.FrameQueryParam param) {
        List<Frame> list = frameService.getListByParam(param);
        return Result.success(list);
    }

    /**
     * 根据查询参数分页查询框架列表
     * @param param 查询参数
     * @return 分页结果
     */
    @PostMapping("/filter/page")
    public Result<PageResult<Frame>> getPageByParam(@RequestBody com.touhuwai.dto.param.FrameQueryParam param) {
        PageResult<Frame> pageResult = frameService.getPageByParam(param);
        return Result.success(pageResult);
    }
}
