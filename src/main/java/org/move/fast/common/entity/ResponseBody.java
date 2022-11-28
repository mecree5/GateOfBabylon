package org.move.fast.common.entity;

import lombok.Data;
import org.move.fast.common.constant.GlobalConstant;

/**
 * @author 代码瞬间移动工程师
 * @Description
 * @time 2022/11/22 9:12 下午
 */
@Data
public class ResponseBody<T> {
    private Integer code;
    private String msg;
    private T data;

    public ResponseBody(T data){
        this.code = GlobalConstant.DEFAULT_CODE;
        this.msg = GlobalConstant.DEFAULT_MSG;
        this.data = data;
    }

    public ResponseBody(){
        this.code = GlobalConstant.DEFAULT_CODE;
        this.msg = GlobalConstant.DEFAULT_MSG;
    }

    public ResponseBody(String msg){
        this.code = GlobalConstant.DEFAULT_CODE;
        this.msg = msg;
    }

    public ResponseBody(String msg,T data){
        this.code = GlobalConstant.DEFAULT_CODE;
        this.msg = msg;
        this.data = data;
    }

    public ResponseBody(Integer code,String msg){
        this.code = code;
        this.msg = msg;
    }
}
