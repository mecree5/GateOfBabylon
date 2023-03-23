package org.move.fast.config;

import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.Result;
import org.move.fast.common.utils.Log;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;

@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final List<String> exceptionMaskList = Collections.singletonList(
            "org.apache.catalina.connector.ClientAbortException" //客户端中断请求,tomcat报错,不影响业务
    );

    /*
    处理自定义异常
     */
    @ExceptionHandler(value = CustomerException.class)
    public Result<Object> customExceptionHandler(CustomerException customerException) {

        Log.error(customerException);
        return Result.exception(customerException);
    }

    /*
    处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception exception) {

        if (!exceptionMaskList.contains(exception.getClass().getName())) {
            Log.error(exception);
        }
        return Result.exception(exception);
    }

}
