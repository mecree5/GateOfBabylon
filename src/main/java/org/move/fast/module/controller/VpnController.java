package org.move.fast.module.controller;

import org.apache.commons.io.IOUtils;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.VpnEnum;
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
    public Result<Object> getUserInfo() {

        return null;
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

        VpnEnum.getKey(key, VpnEnum.prefix_setting);

        if (0 > Integer.parseInt(value) || Integer.parseInt(value) < 12) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }

        return vpnService.upSet(key, value) ? Result.success() : Result.error();
    }


    @RequestMapping(value = "/down/{clientType}", method = RequestMethod.GET)
    public void down(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientType) throws IOException {

        String key = VpnEnum.getKey(clientType, VpnEnum.prefix_client);

        byte[] bytes = vpnService.getRss(key).getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "rss.txt");
        response.setHeader(headerKey, headerValue);

        IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

}
