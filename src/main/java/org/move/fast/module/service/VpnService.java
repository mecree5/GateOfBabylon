package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.VpnEnum;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.common.utils.random.RandomString;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Resource
    private PlatformTransactionManager txm;

    private static String lastGetRssTime = "";

    private static final List<Integer> usedRssUrlId = new ArrayList<>();

    static {
//        VpnService vpnService = new VpnService();
//        List<Map<String, Object>> maps = vpnService.vpnVmessMapper.selectMaps(new QueryWrapper<VpnVmess>().select("COUNT(*) as num").notIn("id", usedRssUrlId));
//        int num = Integer.parseInt(String.valueOf(maps.get(0).get("num")));
//        if (num < 10) {
//            int go = 10 - num;
//
//        }
    }

    @Scheduled(cron = "30 10 1 * * ?")
    public void check() {
        List<VpnUser> is_check = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getIsCheck, "1").gt(VpnUser::getCrtDate, DateUtil.lastMonth()));
        for (VpnUser vpnUser : is_check) {
            Map<String, String> login = Vpn.login(vpnUser);
            Vpn.checkIn(login, vpnUser);
        }
    }


    public String getRss(String clientType) {
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

        }

        List<VpnVmess> vpnVmesses = vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).notIn(VpnVmess::getId, usedRssUrlId).last("limit 5"));
        for (VpnVmess vpnVmess : vpnVmesses) {
            usedRssUrlId.add(vpnVmess.getId());
            urls.append(vpnVmess.getVmessUrl()).append("\r\n");
        }
        return Base64.encode(urls.toString());
    }

    public void getRssUrl(int num) throws InterruptedException {
        for (int i = 0; i < num; i++) {
            getRssUrl();
        }
    }

    @Async("asyncTaskExecutor")
    public void getRssUrlSync(int num) throws InterruptedException {
        for (int i = 0; i < num; i++) {
             getRssUrl();
        }
    }

    private ArrayList<VpnVmess> getRssUrl() {

        Random random = new Random();
        String username = RandomString.getRandomString(4);
        String password = RandomString.getRandomString(10);
        String email = username + random.nextInt(1000) + "@qq.com";

        VpnUser vpnUser = Vpn.register(email, username, password, null);
        Map<String, String> cookie = Vpn.login(vpnUser);
        Vpn.buy(cookie);
        Vpn.checkIn(cookie, vpnUser);

        //拿订阅地址
        Map<VpnEnum, String> rssUrls = Vpn.takeRssUrl(cookie);

        StringBuilder rssUrlStr = new StringBuilder();
        ArrayList<VpnVmess> vpnVmesses = new ArrayList<>();
        for (VpnEnum type : rssUrls.keySet()) {

            String rssUrl = rssUrls.get(type);
            //拼接总的订阅节点
            rssUrlStr.append(type.getValue()).append(rssUrl).append("; ");

            //获取各类型有用节点
            VpnVmess vpnVmess = new VpnVmess();
            String base64_vmess = Requests.downloadToString(rssUrl);


            String[] vmesses = Base64.decodeStr(base64_vmess).split("vmess");
            SysConf sysConf = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, VpnEnum.setting_rss_num.getKey())).get(0);
            String vmess = vmesses[Integer.parseInt(sysConf.getConfVal())];
            vpnVmess.setClientType(type.getKey());
            vpnVmess.setVmessUrl(vmess);
            vpnVmess.setUserId(vpnUser.getId());
            vpnVmess.setCrtDate(LocalDateTimeUtil.now());
            vpnVmess.setUpdDate(LocalDateTimeUtil.now());
            vpnVmesses.add(vpnVmess);
            vpnVmessMapper.insert(vpnVmess);
        }

        vpnUser.setRssUrl(rssUrlStr.toString());
        vpnUserMapper.insert(vpnUser);

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


        return vpnVmesses;
    }

}
