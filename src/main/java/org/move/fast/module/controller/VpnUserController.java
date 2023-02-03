package org.move.fast.module.controller;


import org.move.fast.common.entity.Result;
import org.move.fast.module.entity.VpnUser;
import org.move.fast.module.mapper.VpnUserMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 代码瞬间移动工程师
 * @since 2023-02-03
 */
@Controller
@RequestMapping("/module/vpn-user")
public class VpnUserController {

    @Resource
    VpnUserMapper vpnUserMapper;

    @GetMapping("/list")
    public Result<Object> getUserInfo() {
        VpnUser vpnUser = new VpnUser();
        vpnUser.setEmail("123");
        vpnUser.setCrtDate(LocalDateTime.now());
        int insert = vpnUserMapper.insert(vpnUser);
        return Result.success().setData(insert);

    }
}
