package com.metaverse.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private boolean success;
    private int code;
    private T data;
    private List<String> message;

    // 增删改 成功响应
    public static <T> Result<T> success() {
        return new Result<>(true, 1, null, null);
    }

    // 查询 成功响应
    public static <T> Result<T> success(T data) {
        return new Result<>(true, 1, data, null);
    }

    public static <T> Result<T> modify(boolean data) {
        if (data) {
            return Result.success();
        }
        return new Result<>(false, 0, null, Collections.singletonList("No change required!"));
    }


    // 失败响应
    public static <T> Result<T> error(List<String> msg) {
        return new Result<>(true, 1, null, msg);
    }
}
