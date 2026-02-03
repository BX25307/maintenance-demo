package xyz.bx25.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.service.RepairmanService;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairman")
public class RepairmanController {

    @Autowired
    private RepairmanService repairmanService;

    /**
     * 查询所有空闲的维修工 (用于管理员派单下拉框)
     * GET /api/repairman/free
     */
    @GetMapping("/free")
    public Response<List<Map<String, Object>>> getIdleRepairmen() {
        // 返回的数据包含：ID, 名字, 电话
        List<Map<String, Object>> list = repairmanService.getIdleRepairmen();
        return Response.success(list);
    }
}