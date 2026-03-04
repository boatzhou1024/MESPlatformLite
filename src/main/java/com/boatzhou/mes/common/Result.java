package com.boatzhou.mes.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一接口返回体。
 *
 * @param <T> 业务数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    /** 业务状态码（非 HTTP 状态码）。 */
    private Integer code;

    /** 返回信息，供前端展示。 */
    private String message;

    /** 业务数据，无数据时为 null。 */
    private T data;

    /**
     * 构建成功返回（带数据）。
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.OK.getCode(), ErrorCode.OK.getMessage(), data);
    }

    /**
     * 构建成功返回（无数据）。
     */
    public static <T> Result<T> success() {
        return success(null);
    }

    /**
     * 构建失败返回：默认 INTERNAL_ERROR code + 自定义 message。
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ErrorCode.INTERNAL_ERROR.getCode(), message, null);
    }

    /**
     * 构建失败返回：使用预定义错误码。
     */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 构建失败返回：自定义 code + message。
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
