package org.move.fast.module.controller;

import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.module.service.SysService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/sys")
public class SysController {

    @Resource
    SysService sysService;

    @GetMapping("/api")
    public String api(HttpServletRequest request) {
        return sysService.getApiList("http://" + request.getServerName() + ":" + request.getServerPort());
    }

    @GetMapping("/upset/{key}/{value}")
    public Result<Object> upset(@PathVariable String key, @PathVariable String value) {

        if ((!SysConfKeyEnum.check(key)) || 0 > Integer.parseInt(value) || Integer.parseInt(value) > 12) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }

        return sysService.upSet(key, value) ? Result.success() : Result.error();
    }
}
