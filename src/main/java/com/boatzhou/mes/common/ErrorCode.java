package com.boatzhou.mes.common;

import lombok.Getter;

/**
 * 统一业务错误码枚举。
 *
 * <p>注意：这里的 code 是“业务码”，不是 HTTP 状态码。
 * 本项目采用统一返回体，所以前端一般读取该 code 判断业务结果。</p>
 */
@Getter
public enum ErrorCode {

    /** 请求成功。 */
    OK(200, "success"),

    /** 请求参数错误。 */
    BAD_REQUEST(400, "请求参数错误"),

    /** 未登录或登录态失效。 */
    UNAUTHORIZED(401, "未认证或认证已失效"),

    /** 无权限访问。 */
    FORBIDDEN(403, "无权限访问"),

    /** 资源不存在。 */
    NOT_FOUND(404, "资源不存在"),

    /** 数据冲突（如乐观锁冲突、唯一键冲突）。 */
    CONFLICT(409, "数据冲突"),

    /** 服务端内部异常。 */
    INTERNAL_ERROR(500, "系统内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
