package org.move.fast.config;

import org.move.fast.common.entity.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result<Object> exceptionHandler(HttpServletRequest request, Exception exception) {
        String message = exception.getMessage() + request.getRequestURL().toString();
        return Result.exception().setData(message);
    }
}
