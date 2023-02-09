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

    public CustomerException(RetCodeEnum message) {
        super();
        this.code = message.getCode();
        this.msg = message.getMsg();
    }

    public CustomerException(RetCodeEnum message, String fill) {
        super();
        this.code = message.getCode();
        this.msg = message.getMsg() + fill;
    }

    public CustomerException(String retCd, String msgDes) {
        super();
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