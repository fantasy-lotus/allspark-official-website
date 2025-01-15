package com.allspark.allsparkofficialwebsite.exception;


import com.allspark.allsparkofficialwebsite.common.BaseResponse;
import com.allspark.allsparkofficialwebsite.common.ErrorCode;
import com.allspark.allsparkofficialwebsite.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *

 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler(BusinessException e) {
        log.error("BusinessException", e);
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException");
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, e.getBindingResult().getFieldError().getDefaultMessage());
    }
    @ExceptionHandler(DuplicateKeyException.class)
    public BaseResponse<?> duplicateKeyExceptionHandler(DuplicateKeyException e) {
        log.error("DuplicateKeyException", e);
        return ResultUtils.error(ErrorCode.PARAMS_ERROR, "数据重复");
    }

    @ExceptionHandler(Exception.class)
    public BaseResponse<?> runtimeExceptionHandler(Exception e) {
        log.error("Exception", e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, "系统错误");
    }
}
