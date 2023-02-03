package org.move.fast.module.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.move.fast.common.entity.Result;
import org.move.fast.module.entity.UserInfo;
import org.move.fast.module.mapper.UserInfoMapper;
import org.move.fast.module.service.IUserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 代码瞬间移动工程师
 * @since 2022-11-22
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements IUserInfoService {

    @Override
    public Result<Object> getUserDetail(String name) {
        if (!StringUtils.hasText(name)) {
            return Result.error();
        }

        QueryWrapper<UserInfo> qw = new QueryWrapper<>();
        qw.lambda().eq(UserInfo::getName, name);
        UserInfo userInfo = baseMapper.selectOne(qw);

        if (Objects.isNull(userInfo)) {
            return Result.error();
        }

        return new Result<>(userInfo);
    }
}
