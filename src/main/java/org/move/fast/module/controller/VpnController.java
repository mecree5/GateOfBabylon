package org.move.fast.module.controller;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.api.vpn.VpnTypeEnum;
import org.move.fast.common.entity.RetCodeEnum;
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

    @GetMapping("/stockUp/{num}")
    public String stockUp(@PathVariable int num) {
        if (num > 20) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }
        return rssService.getRssUrl(num);
    }

    @RequestMapping(value = "/cd/my", method = RequestMethod.GET)
    public void my(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientName, @PathVariable String id) throws IOException {
        byte[] bytes = HttpRequest.get("https://yysw.acyun.tk/api/v1/client/subscribe?token=00d3734775f6b474371008ed43a2d4fc").execute().body().getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "rss.txt"));

        IoUtil.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/down/{clientName}", method = RequestMethod.GET)
    public void down(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientName) throws IOException {

        String type = VpnTypeEnum.checkNameAndGetType(clientName);

        byte[] bytes = vpnService.getRss(type, VpnTypeEnum.checkTypeAndGetName(type), request.getRemoteAddr()).getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "rss.txt"));

        IoUtil.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

    @RequestMapping(value = "/cd/{clientName}/{id}", method = RequestMethod.GET)
    public void crawlerToDown(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientName, @PathVariable String id) throws IOException {

        byte[] bytes = vpnService.crawler(VpnTypeEnum.checkNameAndGetType(clientName), id).getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", "rss.txt"));

        IoUtil.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
        response.flushBuffer();
    }

}
