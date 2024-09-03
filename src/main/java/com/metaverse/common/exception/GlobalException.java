package com.metaverse.common.exception;


import com.metaverse.common.model.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
//全局异常处理器
public class GlobalException {

    @ExceptionHandler(Exception.class)//指定捕获的异常类型
    public Result<?> ex (Exception ex){
        ex.printStackTrace();
        return Result.error(ex.getMessage());
    }
}
