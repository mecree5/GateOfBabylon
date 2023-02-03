package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class lisnhi {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    private static final List<Integer> usedRssUrlId = new ArrayList<>();

    private static String lastGetRssTime = "";


    @Scheduled(cron = "30 10 1 * * ?")
    public void check() {
        List<VpnUser> is_check = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getIsCheck, "1").gt(VpnUser::getCrtDate, DateUtil.lastMonth()));
        for (VpnUser vpnUser : is_check) {
            Map<String, String> login = Vpn.login(vpnUser.getEmail(), vpnUser.getPassword(), null);
            Vpn.checkIn(login, vpnUser.getEmail());
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

}
