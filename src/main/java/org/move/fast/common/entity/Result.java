package org.move.fast.common.entity;

import org.move.fast.common.Exception.CustomerException;

import java.io.Serializable;

/**
 * @Description 统一返回处理类
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -5007654155748473782L;
    private String code;
    private String msg;
    private T data;

    public Result(T data) {
        this.code = ResponseCode.DEFAULT_CODE;
        this.msg = ResponseCode.DEFAULT_MSG;
        this.data = data;
    }

    public Result() {
        this.code = ResponseCode.DEFAULT_CODE;
        this.msg = ResponseCode.DEFAULT_MSG;
    }

    public Result(String msg) {
        this.code = ResponseCode.DEFAULT_CODE;
        this.msg = msg;
    }

    public Result(String msg, T data) {
        this.code = ResponseCode.DEFAULT_CODE;
        this.msg = msg;
        this.data = data;
    }

    public Result(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(CustomerException customerException) {
        this.code = customerException.getRetCd();
        this.msg = customerException.getMsgDes();
    }

    public Result(Exception exception) {
        this.code = ResponseCode.DEFAULT_SERVER_ERROR_CODE;
        this.msg = exception.getMessage();
    }

    /**
     * 默认创建方法
     */
    public static <T> Result<T> success() {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_CODE);
    }

    /**
     * 默认错误
     */
    public static <T> Result<T> error() {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_SERVER_ERROR_CODE);
    }

    /**
     * 默认抛出异常
     */
    public static <T> Result<T> exception(CustomerException customerException) {
        Result<T> r = new Result<T>();
        return r.setCode(customerException.getRetCd()).setMsg(customerException.getMsgDes());
    }

    public static <T> Result<T> exception(Exception exception) {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_SERVER_ERROR_CODE).setMsg(exception.getMessage());
    }

    public String getCode() {
        return code;
    }

    public Result<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Result<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }

    public interface ResponseCode {

        public static final String DEFAULT_MSG = "success";
        public static final String DEFAULT_CODE = "200";
        public static final String DEFAULT_SERVER_ERROR_CODE = "500";

    }
}
