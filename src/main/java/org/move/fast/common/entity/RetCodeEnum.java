package org.move.fast.common.entity;

public enum RetCodeEnum {

    validated_error("5011", "参数检验错误"),
    file_not_exists("5012", "文件不存在"),
    api_error("5013", "api调用出错"),
    too_much_req("5014", "请求的太频繁了..."),
    async_wait_error("5015", "异步响应等待时间过长"),
    ;

    private final String code;

    private final String msg;

    RetCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
