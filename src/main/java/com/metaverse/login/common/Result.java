package com.metaverse.login.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    // 响应码，1 代表成功; 0 代表失败
    private Integer code;
    // 响应信息 描述字符串
    private String msg;
    // 返回的数据
    private T data;

    // 增删改 成功响应
    public static <T> Result<T> success() {
        ////////  dddddddddddddddddddd
        return new Result<>(1, "success", null);
    }

    // 查询 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(1, "success", data);
    }

    // 失败响应
    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, null);
    }
}