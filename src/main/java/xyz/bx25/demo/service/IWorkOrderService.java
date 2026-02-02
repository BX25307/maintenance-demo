package xyz.bx25.demo.service;

import org.springframework.stereotype.Service;
import xyz.bx25.demo.model.dto.OrderAssignDTO;
import xyz.bx25.demo.model.dto.OrderSubmitDTO;
import xyz.bx25.demo.model.vo.OrderDetailVO;

import java.util.List;

public interface IWorkOrderService {
    String submitOrder(OrderSubmitDTO dto);

    void assignOrder(OrderAssignDTO dto);

    void finishOrder(String orderId, String repairResult);

    List<OrderDetailVO> queryOrderList(String userId);

    OrderDetailVO getOrderDetail(String orderId);
}
