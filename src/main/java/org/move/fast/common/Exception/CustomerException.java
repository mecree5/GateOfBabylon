package org.move.fast.common.Exception;

import org.move.fast.common.entity.RetCodeEnum;

/**
 * 多数情况下，创建自定义异常需要继承Exception，本例继承Exception的子类RuntimeException
 * @author Mahc
 *
 */
public class CustomerException extends RuntimeException {

    private String retCd ;
    private String msgDes;

    public CustomerException() {
        super();
    }

    public CustomerException(String message) {
        super(message);
        msgDes = message;
    }

    public CustomerException(RetCodeEnum message) {
        super();
        this.retCd = message.getCode();
        this.msgDes = message.getMsg();
    }

    public CustomerException(String retCd, String msgDes) {
        super();
        this.retCd = retCd;
        this.msgDes = msgDes;
    }

    public String getRetCd() {
        return retCd;
    }

    public String getMsgDes() {
        return msgDes;
    }
}