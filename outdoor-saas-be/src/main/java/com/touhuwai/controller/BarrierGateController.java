package com.touhuwai.controller;

import com.touhuwai.common.PageResult;
import com.touhuwai.common.Result;
import com.touhuwai.entity.BarrierGate;
import com.touhuwai.service.BarrierGateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 道闸设备管理控制器
 */
@RestController
@RequestMapping("/api/barrier-gate")
@RequiredArgsConstructor
public class BarrierGateController {
    
    private final BarrierGateService barrierGateService;
    
    /**
     * 根据ID查询道闸
     * @param id 道闸ID
     * @return 道闸信息
     */
    @GetMapping("/{id}")
    public Result<BarrierGate> getById(@PathVariable Integer id) {
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
    public Result<BarrierGate> getByGateNo(@PathVariable String gateNo) {
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
    public Result<PageResult<BarrierGate>> getPage(
            @RequestBody BarrierGate barrierGate,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<BarrierGate> pageResult = barrierGateService.getPage(barrierGate, pageNum, pageSize);
        return Result.success(pageResult);
    }
    
    /**
     * 根据社区ID查询道闸列表
     * @param communityId 社区ID
     * @return 道闸列表
     */
    @GetMapping("/community/{communityId}")
    public Result<List<BarrierGate>> getByCommunityId(@PathVariable Integer communityId) {
        List<BarrierGate> list = barrierGateService.getByCommunityId(communityId);
        return Result.success(list);
    }
    
    /**
     * 新增道闸
     * @param barrierGate 道闸信息
     * @return 操作结果
     */
    @PostMapping
    public Result<Integer> add(@RequestBody BarrierGate barrierGate) {
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
    public Result<Integer> batchAdd(@RequestBody List<BarrierGate> list) {
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
    public Result<Integer> update(@RequestBody BarrierGate barrierGate) {
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
    public Result<Integer> delete(@PathVariable Integer id) {
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
    public Result<Integer> batchDelete(@RequestParam List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.badRequest("ID列表不能为空");
        }
        int result = barrierGateService.batchDelete(ids);
        if (result > 0) {
            return Result.success("批量删除成功", result);
        }
        return Result.error("批量删除失败");
    }

    /**
     * 根据查询参数查询道闸列表（不分页）
     * @param param 查询参数
     * @return 道闸列表
     */
    @PostMapping("/filter")
    public Result<List<BarrierGate>> getListByParam(@RequestBody com.touhuwai.dto.param.BarrierGateQueryParam param) {
        List<BarrierGate> list = barrierGateService.getListByParam(param);
        return Result.success(list);
    }

    /**
     * 根据查询参数分页查询道闸列表
     * @param param 查询参数
     * @return 分页结果
     */
    @PostMapping("/filter/page")
    public Result<PageResult<BarrierGate>> getPageByParam(@RequestBody com.touhuwai.dto.param.BarrierGateQueryParam param) {
        PageResult<BarrierGate> pageResult = barrierGateService.getPageByParam(param);
        return Result.success(pageResult);
    }
}
