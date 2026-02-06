package xyz.bx25.demo.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.common.enums.UserTypeEnum;
import xyz.bx25.demo.common.util.UserContext;
import xyz.bx25.demo.model.vo.RepairmanVO;
import xyz.bx25.demo.service.RepairmanService;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairman")
public class RepairmanController {
    @Autowired
    private RepairmanService repairmanService;
    @GetMapping("/free-list")
    public Response<List<RepairmanVO>> listFreeRepairmen(@RequestParam(required = false) String cityName) {
        // 鉴权：只有管理员或老板能看
        String role = UserContext.getRoleKey();
        if (!UserTypeEnum.ADMIN.getCode().equals(role) && !UserTypeEnum.BOSS.getCode().equals(role)) {
            return Response.error("无权访问");
        }

        return Response.success(repairmanService.listFreeRepairmen(cityName));
    }
}