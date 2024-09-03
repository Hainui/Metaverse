package com.metaverse.login.exception;


import com.metaverse.login.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
//全局异常处理器
public class GlobalException {

    @ExceptionHandler(Exception.class)//指定捕获的异常类型
    public Result ex (Exception ex){
        ex.printStackTrace();
        return Result.error("操作失败,请联系管理员");
    }
}
