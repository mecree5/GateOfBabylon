package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.Result;
import org.move.fast.common.entity.SysConfKey;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.common.utils.random.RandomString;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class VpnService {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    @Resource
    SysConfMapper sysConfMapper;

    private static final List<Integer> usedRssUrlId = new ArrayList<>();

    private static String lastGetRssTime = "";


    @Scheduled(cron = "30 10 1 * * ?")
    public void check() {
        List<VpnUser> is_check = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getIsCheck, "1").gt(VpnUser::getCrtDate, DateUtil.lastMonth()));
        for (VpnUser vpnUser : is_check) {
            Map<String, String> login = Vpn.login(vpnUser);
            Vpn.checkIn(login, vpnUser);
        }
    }


    public String getRss() {
        StringBuilder urls = new StringBuilder();

        if (!lastGetRssTime.equals(DateUtil.today())) {
            lastGetRssTime = DateUtil.today();
            usedRssUrlId.clear();
        }

        List<Map<String, Object>> maps = vpnVmessMapper.selectMaps(new QueryWrapper<VpnVmess>().select("COUNT(*) as num").notIn("id", usedRssUrlId));
        int num = Integer.parseInt(String.valueOf(maps.get(0).get("num")));
        if (num < 10) {
            //添加配置表
            //去异步注册go个
            int go = 10 - num;
            if (num < 5) {
                //去同步注册go个
                System.out.println();
            }
        }

        List<VpnVmess> vpnVmesses = vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>().notIn(VpnVmess::getId, usedRssUrlId).last("limit 5"));
        for (VpnVmess vpnVmess : vpnVmesses) {
            usedRssUrlId.add(vpnVmess.getId());
            urls.append(vpnVmess.getVmessUrl()).append("\r\n");
        }
        return Base64.encode(urls.toString());
    }

    public Result<Object> getRssUrl(int num) {

        Random random = new Random();
        String username = RandomString.getRandomString(4);
        String password = RandomString.getRandomString(10);
        String email = username + random.nextInt(1000) + "@qq.com";

        VpnUser vpnUser = Vpn.register(email, username, password, null);
        Map<String, String> cookie = Vpn.login(vpnUser);
        Vpn.buy(cookie);
        Vpn.checkIn(cookie, vpnUser);
        //拿订阅地址
        Map<String, String> rssUrls = Vpn.takeRssUrl(cookie);
        ArrayList<VpnVmess> vpnVmesses = new ArrayList<>();
        for (String type : rssUrls.keySet()) {
            VpnVmess vpnVmess = new VpnVmess();
            String base64_vmess = Requests.downloadToString(rssUrls.get(type));
            String[] vmesses = Base64.decodeStr(base64_vmess).split("vmess");
            SysConf sysConf = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, SysConfKey.sys_conf_vpn_rss_num)).get(0);
            String vmess = vmesses[Integer.parseInt(sysConf.getConfVal())];
            vpnVmess.setClientType(type);
            vpnVmess.setVmessUrl(vmess);
            vpnVmess.setUserId(vpnUser.getId());
            vpnVmess.setCrtDate(LocalDateTimeUtil.now());
            vpnVmess.setUpdDate(LocalDateTimeUtil.now());
            vpnVmesses.add(vpnVmess);
        }
        return Result.success().setData(vpnVmesses);
    }

}
