package com.touhuwai.controller;

import com.touhuwai.common.PageResult;
import com.touhuwai.common.Result;
import com.touhuwai.entity.Community;
import com.touhuwai.service.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 社区信息管理控制器
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
@Tag(name = "社区管理", description = "社区信息的增删改查接口")
@SecurityRequirement(name = "bearerAuth")
public class CommunityController {
    
    private final CommunityService communityService;
    
    /**
     * 根据ID查询社区
     * @param id 社区ID
     * @return 社区信息
     */
    @Operation(summary = "根据ID查询社区", description = "获取指定ID的社区详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "404", description = "社区不存在")
    })
    @GetMapping("/{id}")
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

    /**
     * 根据查询参数查询社区列表（不分页）
     * @param param 查询参数
     * @return 社区列表
     */
    @PostMapping("/filter")
    public Result<List<Community>> getListByParam(@RequestBody com.touhuwai.dto.param.CommunityQueryParam param) {
        List<Community> list = communityService.getListByParam(param);
        return Result.success(list);
    }

    /**
     * 根据查询参数分页查询社区列表
     * @param param 查询参数
     * @return 分页结果
     */
    @PostMapping("/filter/page")
    public Result<PageResult<Community>> getPageByParam(@RequestBody com.touhuwai.dto.param.CommunityQueryParam param) {
        PageResult<Community> pageResult = communityService.getPageByParam(param);
        return Result.success(pageResult);
    }
}
