package org.move.fast.module.controller;

import org.apache.commons.io.IOUtils;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.ConfKeyEnum;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.VpnTypeEnum;
import org.move.fast.config.ReadConf;
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
    public Result<Object> getUserInfo() throws Exception {

        System.out.println(123123123);
        System.out.println(ReadConf.getConfValue("server.port"));
        throw new Exception("RetCodeEnum.validated_error");
    }

    @GetMapping("/stockUp/{num}")
    public Result<Object> stockUp(@PathVariable int num) {
        if (num > 10) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }
        return Result.success().setData(rssService.getRssUrl(num));
    }

    @GetMapping("/upset/{key}/{value}")
    public Result<Object> upset(@PathVariable String key, @PathVariable String value) {

        if ((!ConfKeyEnum.check(key)) || 0 > Integer.parseInt(value) || Integer.parseInt(value) > 12) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }

        return vpnService.upSet(key, value) ? Result.success() : Result.error();
    }


    @RequestMapping(value = "/down/{clientName}", method = RequestMethod.GET)
    public void down(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientName) throws IOException {

        String type = VpnTypeEnum.checkNameAndGetType(clientName);

        byte[] bytes = vpnService.getRss(type).getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "rss.txt");
        response.setHeader(headerKey, headerValue);

        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

}
