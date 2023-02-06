package org.move.fast.module.controller;

import org.apache.commons.io.IOUtils;
import org.move.fast.common.entity.Result;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.service.VpnService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnService vpnService;

    @GetMapping("/test")
    public Result<Object> getUserInfo() {
        VpnUser vpnUser = new VpnUser();
        vpnUser.setEmail("123");
        vpnUser.setCrtDate(LocalDateTime.now());
        int insert = vpnUserMapper.insert(vpnUser);
        return Result.success().setData(insert);

    }


    @RequestMapping(value = "/down", method = RequestMethod.GET)
    public void getDownload(HttpServletRequest request, HttpServletResponse response) {

        byte[] bytes = vpnService.getRss().getBytes();

        response.setContentType("text/plain");
        response.setContentLength(bytes.length);

        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"", "rss.txt");
        response.setHeader(headerKey, headerValue);

        try {
            IOUtils.copy(new ByteArrayInputStream(bytes), response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
