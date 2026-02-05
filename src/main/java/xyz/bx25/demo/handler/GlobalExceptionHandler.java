package xyz.bx25.demo.handler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import xyz.bx25.demo.common.Response;
import xyz.bx25.demo.common.exception.BusinessException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 捕获业务异常
    @ExceptionHandler(BusinessException.class)
    public Response<Void> handleBusinessException(BusinessException e) {
        log.error("业务异常: {}", e.getMessage());
        return Response.error(e.getMessage());
    }

    // 捕获所有 RuntimeException（除BusinessException外）
    @ExceptionHandler(RuntimeException.class)
    public Response<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常: {}", e.getMessage(), e);
        return Response.error(e.getMessage());
    }

    // 捕获参数校验异常 (LoginDTO 里的 @NotBlank 触发的)
    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public Response<Void> handleValidationException(org.springframework.web.bind.MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return Response.error(msg);
    }

    // 兜底捕获 Exception
    @ExceptionHandler(Exception.class)
    public Response<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Response.error("系统繁忙，请稍后再试");
    }
}