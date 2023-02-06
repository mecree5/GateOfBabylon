package org.move.fast.common.entity;

public enum RetCodeEnum {

    validated_error("511", "参数检验错误");

    private final String code;

    private final String msg;

    RetCodeEnum(String code, String msg){
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
