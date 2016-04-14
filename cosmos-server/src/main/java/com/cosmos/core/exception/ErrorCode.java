package com.cosmos.core.exception;

/**
 * 错误代码
 *
 * @author David
 */
public enum ErrorCode {

    @Error(code = "00000", message = "")
    success_0,
    @Error(code = "1000", message = "系统错误！")
    system_1000,
    @Error(code = "1021", message = "参数有误！")
    param_1021;

    /**
     * 返回错误码
     */
    public Error getError() {
        Error error;
        try {
            error = this.getClass().getField(this.name()).getAnnotation(Error.class);
        } catch (Exception e) {
            return null;
        }
        return error;
    }
}
