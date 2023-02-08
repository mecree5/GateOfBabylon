package org.move.fast.config;

import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.Result;
import org.move.fast.common.utils.Log;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {

    /*
    处理自定义异常
     */
    @ExceptionHandler(value = CustomerException.class)
    public Result<Object> customExceptionHandler(CustomerException customerException) {

        writeToLog(customerException);
        return Result.exception(customerException);
    }

    /*
    处理系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(Exception exception) {

        writeToLog(exception);
        return Result.exception(exception);
    }

    private static void writeToLog(Exception exception) {

        StringBuilder log = new StringBuilder(exception.getClass().toString());
        StackTraceElement[] trace = exception.getStackTrace();

        for (StackTraceElement traceElement : trace) {
            log.append("    at").append(traceElement).append("\r\n");
        }

        Log.writeTxt(log.toString());
    }
}
