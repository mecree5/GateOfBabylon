package org.move.fast.module.controller;

import cn.hutool.core.io.IoUtil;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.api.vpn.VpnTypeEnum;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.module.service.RssService;
import org.move.fast.module.service.VpnService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/vpn")
public class VpnController {

    @Resource
    VpnService vpnService;

    @Resource
    RssService rssService;

    @GetMapping("/test")
    public Result<Object> test() {

        System.out.println(123123123);
        return Result.success();
    }

    @GetMapping("/stockUp/{num}")
    public String stockUp(@PathVariable int num) {
        if (num > 20) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }
        return rssService.getRssUrl(num);
    }

    @GetMapping("/upset/{key}/{value}")
    public Result<Object> upset(@PathVariable String key, @PathVariable String value) {

        if ((!SysConfKeyEnum.check(key)) || 0 > Integer.parseInt(value) || Integer.parseInt(value) > 12) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }

        return vpnService.upSet(key, value) ? Result.success() : Result.error();
    }


    @RequestMapping(value = "/down/{clientName}", method = RequestMethod.GET)
    public void down(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientName) throws IOException {

        String type = VpnTypeEnum.checkNameAndGetType(clientName);

        byte[] bytes = vpnService.getRss(type, VpnTypeEnum.checkTypeAndGetName(type), request.getRemoteAddr()).getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "rss.txt");
        response.setHeader(headerKey, headerValue);

        IoUtil.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

}
