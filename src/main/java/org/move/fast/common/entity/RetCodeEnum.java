package org.move.fast.common.entity;

public enum RetCodeEnum {

    validated_error("511", "参数检验错误"),
    file_not_exists("512", "文件不存在"),
    api_error("513", "api调用出错"),
    too_much_req("514", "请求的太频繁了..."),
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
