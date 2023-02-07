package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.ConfKeyEnum;
import org.move.fast.common.entity.VpnTypeEnum;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VpnService {

    @Resource
    RssService rssService;

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    @Resource
    SysConfMapper sysConfMapper;

    private static String lastGetRssTime = "";

    private static final List<Integer> usedRssUrlId = new ArrayList<>();

    @PostConstruct
    public void init() {
//        checkRssNum();
    }

    private void checkRssNum() {
        List<Map<String, Object>> maps = vpnVmessMapper.selectMaps(new QueryWrapper<VpnVmess>().select("COUNT(*) as num").notIn("id", usedRssUrlId));
        int num = Integer.parseInt(String.valueOf(maps.get(0).get("num")));

        SysConf sysConf = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, ConfKeyEnum.vpn_rss_repertory.name())).get(0);
        int repertory = Integer.parseInt(sysConf.getConfVal());

        if (num < repertory) {
            rssService.getRssUrlAsync(repertory - num);
        }
    }

    @Scheduled(cron = "30 10 1 * * ?")
    public void checkIn() {
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

        checkRssNum();

        List<VpnVmess> vpnVmesses = vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).notIn(VpnVmess::getId, usedRssUrlId).last("limit 5"));
        for (VpnVmess vpnVmess : vpnVmesses) {
            usedRssUrlId.add(vpnVmess.getId());
            urls.append(vpnVmess.getVmessUrl()).append("\r\n");
        }
        return Base64.encode(urls.toString());
    }

    public boolean upSet(String key, String value) {
        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);
        int conf_key = sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().eq("conf_key", key));
        return conf_key == 1;
    }

}
