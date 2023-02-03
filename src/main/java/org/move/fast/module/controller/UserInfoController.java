package org.move.fast.module.controller;


import org.move.fast.common.entity.Result;
import org.move.fast.module.entity.UserInfo;
import org.move.fast.module.service.IUserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 代码瞬间移动工程师
 * @since 2022-11-22
 */
@RestController
@RequestMapping("/user-info")
public class UserInfoController {

    @Resource
    private IUserInfoService userInfoService;

    @GetMapping("/list")
    public Result<Object> getUserInfo(){
        List<UserInfo> list = userInfoService.list();
        return new Result<>(list);
    }

    @GetMapping("/detail")
    public Result<Object> getUserDetail(@RequestParam String name){
        return userInfoService.getUserDetail(name);
    }

    @GetMapping("/test")
    public Result<Object> test(){
        return Result.error();
    }
}
