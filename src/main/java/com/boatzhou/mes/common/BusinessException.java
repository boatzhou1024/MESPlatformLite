package com.boatzhou.mes.common;

import lombok.Getter;

/**
 * 业务异常（运行时异常）。
 *
 * <p>当你希望“可控地”返回错误码和错误信息时，抛出该异常。
 * 例如：资源不存在、参数不合法、状态不允许、并发冲突等。</p>
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 返回给前端的业务错误码。 */
    private final Integer code;

    /**
     * 仅指定错误信息，默认使用 INTERNAL_ERROR 的 code。
     */
    public BusinessException(String message) {
        super(message);
        this.code = ErrorCode.INTERNAL_ERROR.getCode();
    }

    /**
     * 使用预定义错误码构造异常。
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用预定义错误码 + 自定义信息构造异常。
     */
    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    /**
     * 完全自定义 code + message。
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
