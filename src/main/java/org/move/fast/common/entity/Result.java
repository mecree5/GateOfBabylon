package org.move.fast.common.entity;

import java.io.Serializable;

/**
 * @Description 统一返回处理类
 */
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -5007654155748473782L;
    private Integer code;
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

    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 默认创建方法
     */
    public static <T> Result<T> success() {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_CODE);
    }

    /**
     * 默认抛出异常
     */
    public static <T> Result<T> exception() {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_SERVER_ERROR_CODE).setMsg("服务器发生异常，请联系管理员！");
    }

    /**
     * 默认错误
     */
    public static <T> Result<T> error() {
        Result<T> r = new Result<T>();
        return r.setCode(ResponseCode.DEFAULT_PARAM_ERROR_CODE);
    }

    public Integer getCode() {
        return code;
    }

    public Result<T> setCode(Integer code) {
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
        public static final Integer DEFAULT_CODE = 200;
        public static final Integer DEFAULT_SERVER_ERROR_CODE = 500;
        public static final Integer DEFAULT_PARAM_ERROR_CODE = 400;

    }
}
