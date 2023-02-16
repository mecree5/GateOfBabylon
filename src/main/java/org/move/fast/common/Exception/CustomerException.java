package org.move.fast.common.Exception;

import org.move.fast.common.entity.RetCodeEnum;

/**
 * 多数情况下，创建自定义异常需要继承Exception，本例继承Exception的子类RuntimeException
 *
 * @author Mahc
 */
public class CustomerException extends RuntimeException {

    private String code;
    private String msg;

    public CustomerException() {
        super();
    }

    public CustomerException(String message) {
        super(message);
        msg = message;
    }

    public CustomerException(RetCodeEnum retCodeEnum) {
        super(retCodeEnum.getMsg());
        this.code = retCodeEnum.getCode();
        this.msg = retCodeEnum.getMsg();
    }

    public CustomerException(RetCodeEnum retCodeEnum, String fill) {
        super(retCodeEnum.getMsg() + fill);
        this.code = retCodeEnum.getCode();
        this.msg = retCodeEnum.getMsg() + fill;
    }

    public CustomerException(String retCd, String msgDes) {
        super(msgDes);
        this.code = retCd;
        this.msg = msgDes;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}