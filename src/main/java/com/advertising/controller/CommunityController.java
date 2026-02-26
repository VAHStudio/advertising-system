package com.advertising.controller;

import com.advertising.common.PageResult;
import com.advertising.common.Result;
import com.advertising.entity.Community;
import com.advertising.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区信息管理控制器
 */
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    
    private final CommunityService communityService;
    
    /**
     * 根据ID查询社区
     * @param id 社区ID
     * @return 社区信息
     */
    @GetMapping("/{id}")
    public Result<Community> getById(@PathVariable Integer id) {
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
    public Result<Community> getByCommunityNo(@PathVariable String communityNo) {
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
    public Result<PageResult<Community>> getPage(
            @RequestBody Community community,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<Community> pageResult = communityService.getPage(community, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 新增社区
     * @param community 社区信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Integer> add(@RequestBody Community community) {
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
    public Result<Integer> batchAdd(@RequestBody List<Community> list) {
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
    public Result<Integer> update(@RequestBody Community community) {
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
    public Result<Integer> delete(@PathVariable Integer id) {
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
    public Result<Integer> batchDelete(@RequestParam List<Integer> ids) {
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
    public Result<List<Community>> getByCity(@PathVariable String city) {
        List<Community> list = communityService.getByCity(city);
        return Result.success(list);
    }
}
