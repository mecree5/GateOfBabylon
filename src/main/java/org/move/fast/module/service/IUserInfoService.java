package org.move.fast.module.service;

import org.move.fast.common.entity.ResponseBody;
import org.move.fast.module.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 代码瞬间移动工程师
 * @since 2022-11-22
 */
public interface IUserInfoService extends IService<UserInfo> {

    /**
     * 获取用户信息
     * @param name
     * @return
     */
    ResponseBody<Object> getUserDetail(String name);
}
