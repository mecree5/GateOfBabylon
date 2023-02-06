package org.move.fast.module.controller;

import org.apache.commons.io.IOUtils;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.VpnEnum;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.service.VpnService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/vpn")
public class VpnController {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnService vpnService;

    @GetMapping("/test")
    public Result<Object> getUserInfo() throws InterruptedException {

        System.out.println("1111111111111111111");
        vpnService.getRssUrlSync(10);
        System.out.println("2222222222222222222");
        return Result.success();

    }

    @GetMapping("/upset/{key}/{value}")
    public Result<Object> getUserInfo(@PathVariable String key, @PathVariable String value) {

        System.out.println(key);
        System.out.println(value);
        VpnUser vpnUser = new VpnUser();
        vpnUser.setEmail("123");
        vpnUser.setCrtDate(LocalDateTime.now());
        int insert = vpnUserMapper.insert(vpnUser);
        return Result.success();

    }


    @RequestMapping(value = "/down/{clientType}", method = RequestMethod.GET)
    public void getDownload(HttpServletRequest request, HttpServletResponse response, @PathVariable String clientType) {

        String type = "";

        if (VpnEnum.client_v2ray.getValue().equals(clientType)){
            type = VpnEnum.client_v2ray.getKey();
        } else if (VpnEnum.client_kitsunebi.getValue().equals(clientType)){
            type = VpnEnum.client_kitsunebi.getKey();
        } else if (VpnEnum.client_clash.getValue().equals(clientType)){
            type = VpnEnum.client_clash.getKey();
        } else if (VpnEnum.client_shadowrocket.getValue().equals(clientType)){
            type = VpnEnum.client_shadowrocket.getKey();
        } else if (VpnEnum.client_Quantumult.getValue().equals(clientType)){
            type = VpnEnum.client_Quantumult.getKey();
        } else if (VpnEnum.client_QuantumultX.getValue().equals(clientType)){
            type = VpnEnum.client_QuantumultX.getKey();
        } else {
            return;
        }

        byte[] bytes = vpnService.getRss(type).getBytes();

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
