package org.move.fast.module.controller;

import org.move.fast.common.entity.Result;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/test")
public class TestController {

    @Resource
    VpnUserMapper vpnUserMapper;

    @GetMapping("/test")
    public Result<Object> getUserInfo() {
        VpnUser vpnUser = new VpnUser();
        vpnUser.setEmail("123");
        vpnUser.setCrtDate(LocalDateTime.now());
        int insert = vpnUserMapper.insert(vpnUser);
        return Result.success().setData(insert);

    }
}
