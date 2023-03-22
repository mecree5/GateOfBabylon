package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.api.vpn.DaBai;
import org.move.fast.common.api.vpn.VpnTypeEnum;
import org.move.fast.common.entity.DBFieldEnum;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.common.utils.HttpReq;
import org.move.fast.common.utils.Log;
import org.move.fast.common.utils.RandomStr;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @author YinShiJie
 * @description: 作为服务工具类被调用  切记不要导致循环依赖
 * @date 2023/2/6 19:13
 */
@Service
public class RssService {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    @Resource
    SysConfMapper sysConfMapper;

    @Async("asyncTaskExecutor")
    public void checkInAsyncAndPutTrafficToResult(VpnUser vpnUser, String resultKey, Integer itemKey, Map<String, LinkedHashMap<Integer, String>> result) {
        Log.info(Thread.currentThread().getName() + "正在签到" + vpnUser.getEmail(), this.getClass());
        result.get(resultKey).put(itemKey, checkInAndGetTraffic(vpnUser));
    }

    public String checkInAndGetTraffic(VpnUser vpnUser) {

        String cookie = DaBai.login(vpnUser);

        if (StrUtil.isBlank(cookie)) {
            return null;
        }

        return DaBai.checkIn(cookie, vpnUser);
    }

    /**
     * @description: 异步获取订阅地址
     * @author YinShiJie
     * @Param [num]
     * @Return void
     * @date 2023/2/6 19:13
     */
    @Async("asyncTaskExecutor")
    public void getRssUrlAsync(int num) {
        for (int i = 0; i < num; i++) {
            getRssUrl(false);
        }
    }

    /**
     * @description: 同步获取订阅地址
     * @author YinShiJie
     * @Param [num]
     * @Return java.util.ArrayList<org.move.fast.module.entity.auto.VpnVmess>
     * @date 2023/2/6 19:14
     */
    public String getRssUrl(int num) {
        StringBuilder urls = new StringBuilder();
        for (int i = 0; i < num; i++) {
            urls.append(getRssUrl(true));
        }
        return urls.toString().replaceAll("; ", "\r\n");
    }

    private String getRssUrl(boolean isUse) {

        String which = sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>()
                .eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_which.name())).getConfVal();

        String username = RandomStr.getRandomString(4);
        String password = RandomStr.getRandomString(10);
        String email = username + new Random().nextInt(1000) + "@qq.com";

        VpnUser vpnUser = DaBai.register(email, username, password, null);

        if (vpnUser == null) {
            throw new CustomerException(RetCodeEnum.api_error);
        }

        String cookie = DaBai.login(vpnUser);

        if (StrUtil.isBlank(cookie)) {
            throw new CustomerException(RetCodeEnum.api_error);
        }

        if (!DaBai.buy(cookie, vpnUser)) {
            throw new CustomerException(RetCodeEnum.api_error);
        }

        if (isUse) {
            DaBai.checkIn(cookie, vpnUser);
        }

        vpnUser.setStatus(DBFieldEnum.vpn_user_status_normal.getKey());
        vpnUser.setLastUpdRssWhich(which);
        //避免今天申请的账号用不了
        vpnUser.setLastUsedDate(isUse ? LocalDate.now() : LocalDate.now().plusDays(-1));

        vpnUserMapper.insert(vpnUser);

        VpnUser vpnUserId = vpnUserMapper.selectOne(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getEmail, vpnUser.getEmail()));

        String rssUrlStr = getAllRssUrlAndInsertVmess(vpnUserId.getId(), DaBai.takeRssUrl(cookie), Integer.parseInt(which));

        vpnUserId.setRssUrl(rssUrlStr);
        vpnUserId.setUpdDate(LocalDateTime.now());
        vpnUserMapper.updateById(vpnUserId);
        return rssUrlStr;
    }

    /**
     * @Param rssUrls
     * @Param userId
     * @Param which
     * @Return
     */
    public String getAllRssUrlAndInsertVmess(int userId, Map<VpnTypeEnum, String> rssUrls, int which) {

        if (CollectionUtils.isEmpty(rssUrls)) {
            return null;
        }

        StringBuilder rssUrlStr = new StringBuilder();

        for (VpnTypeEnum vpnTypeEnum : rssUrls.keySet()) {

            String rssUrl = rssUrls.get(vpnTypeEnum);
            //拼接总的订阅节点
            rssUrlStr.append(vpnTypeEnum.getName()).append(":").append(rssUrl).append("; ");

            //获取各类型有用节点
            VpnVmess vpnVmess = new VpnVmess();
            String vmess = getVmessByRssUrl(which, vpnTypeEnum.getType(), rssUrl);

            vpnVmess.setClientType(vpnTypeEnum.getType());
            vpnVmess.setVmessUrl(vmess);
            vpnVmess.setUserId(userId);
            vpnVmess.setCrtDate(LocalDateTimeUtil.now());
            vpnVmess.setUpdDate(LocalDateTimeUtil.now());
            vpnVmessMapper.insert(vpnVmess);
        }

        return rssUrlStr.toString();
    }

    /**
     * @Param which 第几个
     * @Param clientType
     * @Param rssUrl
     * @Return
     */
    public String getVmessByRssUrl(int which, String clientType, String rssUrl) {

        String base64_vmess = HttpReq.downloadToString(rssUrl);
        String[] vmesses;

        //QuantumultX不用解密 直接为vmess串
        if (VpnTypeEnum.client_QuantumultX.getType().equals(clientType)) {
            vmesses = base64_vmess.split("vmess");
        } else {
            vmesses = Base64.decodeStr(base64_vmess).split("vmess");
        }
        //由于 split() 切割会删掉 匹配字段 和 数组index=0为空正好从1开始算
        return "vmess" + vmesses[which];

    }
}
