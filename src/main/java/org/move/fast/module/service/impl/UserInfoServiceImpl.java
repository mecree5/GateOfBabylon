package org.move.fast.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.move.fast.module.mapper.UserInfoMapper;
import org.move.fast.module.service.IUserInfoService;
import org.move.fast.common.constant.GlobalConstant;
import org.move.fast.common.entity.ResponseBody;
import org.move.fast.module.entity.UserInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 代码瞬间移动工程师
 * @since 2022-11-22
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Override
    public ResponseBody<Object> getUserDetail(String name) {
        if (!StringUtils.hasText(name)){
            return new ResponseBody<>(GlobalConstant.DEFAULT_PARAM_ERROR_CODE,"参数不能为空");
        }

        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.lambda().eq(UserInfo::getName,name);
        UserInfo userInfo = baseMapper.selectOne(qw);

        if (Objects.isNull(userInfo)){
            return new ResponseBody<>(GlobalConstant.DEFAULT_SERVER_ERROR_CODE,"该用户不存在,请输入正确的用户信息");
        }

        return new ResponseBody<>(userInfo);
    }
}
