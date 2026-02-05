package xyz.bx25.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.common.enums.OrderStatusEnum;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.model.dto.OrderAssignDTO;
import xyz.bx25.demo.model.dto.order.OrderSubmitDTO;
import xyz.bx25.demo.model.entity.WorkOrder;
import xyz.bx25.demo.model.vo.OrderDetailVO;
import xyz.bx25.demo.model.vo.order.OrderListSimpleVO;
import xyz.bx25.demo.service.WorkOrderService;


@RestController
@RequestMapping("/api/work-order")
public class WorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    /**
     * 1.2 提交报修工单
     * 用户填写故障描述、上传图片、输入详细地址后提交。
     *
     * @param submitDTO 提交参数 (含 deviceId, faultDesc, addressDetail 等)
     * @return 成功返回 orderId
     */
    @PostMapping("/submit")
    public Response<String> submitOrder(@RequestBody @Valid OrderSubmitDTO submitDTO) {
        // 服务层逻辑：生成 PENDING 工单，写入详细地址
        String orderId = workOrderService.submitOrder(submitDTO);
        return Response.success(orderId);
    }

    /**
     * 1.3 用户取消工单
     * 仅当工单状态为 PENDING (待接单) 时允许取消。
     * @param orderId 工单ID
     * @return 是否成功
     */
    @PostMapping("/cancel")
    public Response<Void> cancelOrder(@RequestParam("orderId") String orderId) {
        // 服务层逻辑：校验状态，修改状态为 CANCELLED
        workOrderService.cancelOrder(orderId);
        return Response.success();
    }

    /**
     * 1.4 分页查询我的工单列表
     * 用于“我的-报修记录”页面，展示轻量级数据。
     * @param page 分页对象
     * @param status (可选) 筛选状态: 0-待接单, 1-维修中, 3-待支付...
     * @return 分页列表数据
     */
    @GetMapping("/my-list")
    public Response<Page<? extends OrderListSimpleVO>> queryMyOrderList(@ModelAttribute Page<WorkOrder> page,
                                                              @RequestParam(required = false) Integer status) {
        // 服务层逻辑：从 UserContext 获取当前 userId，执行分页查询
        // 返回轻量级 VO (只含 ID、设备名、状态文本、时间)
        return Response.success(workOrderService.queryOrderList(page, status));
    }



    /**
     * 1.5 查询工单详情
     * 点击列表项后，查看完整详情（含进度、详细地址、费用预估等）。
     *
     * @param orderId 工单ID
     * @return 完整详情 VO
     */
    @GetMapping("/detail/{orderId}")
    public Response<OrderDetailVO> getOrderDetail(@PathVariable String orderId) {
        // 服务层逻辑：组装完整数据，包含时间轴日志
        return Response.success(workOrderService.getOrderDetail(orderId));
    }

    /**
     * 2.1 维修工抢单
     * POST /api/work-order/grab?orderId=xxx
     */
    @PostMapping("/grab")
    public Response<Void> grabOrder(@RequestParam("orderId") String orderId) {
        // 鉴权：只有维修工能抢
        if (!UserTypeEnum.REPAIRMAN.getCode().equals(UserContext.getRoleKey())) {
            return Response.error("无权操作");
        }
        workOrderService.grabOrder(orderId);
        return Response.success();
    }

    /**
     * 2.2 管理员派单
     * POST /api/work-order/assign
     */
    @PostMapping("/assign")
    public Response<Void> assignOrder(@RequestBody @Valid OrderAssignDTO dto) {
        // 鉴权：只有管理员/老板能派
        String role = UserContext.getRoleKey();
        if (!UserTypeEnum.ADMIN.getCode().equals(role) && !UserTypeEnum.BOSS.getCode().equals(role)) {
            return Response.error("无权操作");
        }
        workOrderService.assignOrder(dto);
        return Response.success();
    }
    /**
     * 2.3 查询待派单工单列表
     * 用于“工单池”页面，展示轻量级数据。
     * @param page 分页对象
     * @return 分页列表数据
     */
    @GetMapping("/pool")
    public Response<Page<? extends OrderListSimpleVO>> queryPool(@ModelAttribute Page<WorkOrder> page) {
        // 强制只查 status=PENDING
        return Response.success(workOrderService.queryOrderList(page, OrderStatusEnum.PENDING.getCode()));
    }
}