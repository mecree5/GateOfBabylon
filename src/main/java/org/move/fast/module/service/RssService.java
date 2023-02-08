package org.move.fast.module.service;


import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.ConfKeyEnum;
import org.move.fast.common.entity.VpnTypeEnum;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.common.utils.random.RandomString;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

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

    @Resource
    private PlatformTransactionManager txm;

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
            getRssUrl();
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
            urls.append(getRssUrl());
        }
        return urls.toString().replaceAll("; ", "\r\n");
    }

    private String getRssUrl() {

        SysConf sysConf = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, ConfKeyEnum.vpn_rss_which.name())).get(0);
        String which = sysConf.getConfVal();

        Random random = new Random();
        String username = RandomString.getRandomString(4);
        String password = RandomString.getRandomString(10);
        String email = username + random.nextInt(1000) + "@qq.com";

        VpnUser vpnUser = Vpn.register(email, username, password, null);
        Map<String, String> cookie = Vpn.login(vpnUser);
        Vpn.buy(cookie, vpnUser);
        Vpn.checkIn(cookie, vpnUser);
        vpnUser.setStatus("1");
        vpnUser.setLastUsedDate(LocalDate.now());
        vpnUser.setLastUpdRssWhich(which);

        vpnUserMapper.insert(vpnUser);

        VpnUser vpnUserId = vpnUserMapper.selectOne(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getEmail, vpnUser.getEmail()));

        //拿订阅地址
        Map<VpnTypeEnum, String> rssUrls = Vpn.takeRssUrl(cookie);

        StringBuilder rssUrlStr = new StringBuilder();

        for (VpnTypeEnum vpnTypeEnum : rssUrls.keySet()) {

            String rssUrl = rssUrls.get(vpnTypeEnum);
            //拼接总的订阅节点
            rssUrlStr.append(vpnTypeEnum.getName()).append(":").append(rssUrl).append("; ");

            //获取各类型有用节点
            VpnVmess vpnVmess = new VpnVmess();
            String vmess = getVmessByRssUrl(Integer.parseInt(which), vpnTypeEnum.getType(), rssUrl);

            vpnVmess.setClientType(vpnTypeEnum.getType());
            vpnVmess.setVmessUrl(vmess);
            vpnVmess.setUserId(vpnUserId.getId());
            vpnVmess.setCrtDate(LocalDateTimeUtil.now());
            vpnVmess.setUpdDate(LocalDateTimeUtil.now());
            vpnVmessMapper.insert(vpnVmess);
        }

        vpnUserId.setRssUrl(rssUrlStr.toString());
        vpnUserId.setUpdDate(LocalDateTime.now());
        vpnUserMapper.updateById(vpnUserId);

//        //可以考虑用事务
//        Object obj;
//        TransactionTemplate txt = new TransactionTemplate(txm);
//        obj = txt.execute((TransactionCallback<Object>) transactionStatus -> {
//            try {
//
//                return null;
//            } catch (Exception e){
//
//                return null;
//            }
//        });


        return rssUrlStr.toString();
    }

    /**
     * @Param which 第几个
     * @Param clientType
     * @Param rssUrl
     * @Return
     */
    public String getVmessByRssUrl(int which, String clientType, String rssUrl) {

        String base64_vmess = Requests.downloadToString(rssUrl);
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
