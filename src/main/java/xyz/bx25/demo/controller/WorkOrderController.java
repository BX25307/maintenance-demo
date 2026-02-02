package xyz.bx25.demo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated; // 参数校验
import org.springframework.web.bind.annotation.*;

import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.model.dto.OrderAssignDTO;
import xyz.bx25.demo.model.dto.OrderSubmitDTO;
import xyz.bx25.demo.model.vo.OrderDetailVO;
import xyz.bx25.demo.service.IWorkOrderService;

import java.util.List;

@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private IWorkOrderService workOrderService;

    /**
     * 1. 用户扫码报修
     * POST /api/work-order/submit
     */
    @PostMapping("/submit")
    public Response<String> submitOrder(@RequestBody @Validated OrderSubmitDTO dto) {
        // userId 通常从 Token 获取，这里先模拟直接传或者在 Service 里处理
        String orderId = workOrderService.submitOrder(dto);
        return Response.success(orderId);
    }

    /**
     * 2. 管理员派单
     * POST /api/work-order/assign
     */
    @PostMapping("/assign")
    public Response<Void> assignOrder(@RequestBody @Validated OrderAssignDTO dto) {
        workOrderService.assignOrder(dto);
        return Response.success();
    }

    /**
     * 3. 维修工完结工单
     * POST /api/work-order/finish?orderId=xxx&result=xxx
     */
    @PostMapping("/finish")
    public Response<Void> finishOrder(@RequestParam String orderId,
                                    @RequestParam String repairResult) {
        workOrderService.finishOrder(orderId, repairResult);
        return Response.success();
    }

    /**
     * 4. 查询工单列表 (老板看自己的，管理员看全部)
     * GET /api/work-order/list?userId=xxx
     */
    @GetMapping("/list")
    public Response<List<OrderDetailVO>> listOrders(@RequestParam(required = false) String userId) {
        List<OrderDetailVO> list = workOrderService.queryOrderList(userId);
        return Response.success(list);
    }

    /**
     * 5. 查询单个工单详情
     * GET /api/work-order/detail/12345
     */
    @GetMapping("/detail/{orderId}")
    public Response<OrderDetailVO> getOrderDetail(@PathVariable String orderId) {
        OrderDetailVO vo = workOrderService.getOrderDetail(orderId);
        return Response.success(vo);
    }
}