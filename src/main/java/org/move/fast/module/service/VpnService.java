package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.ConfKeyEnum;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @PostConstruct
    public void init() {
        checkRssNum();
    }

    private void checkRssNum() {

        List<Map<String, Object>> maps = vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>().select("COUNT(*) as num").eq("status", "1").ne("last_used_date", LocalDate.now()));
        int num = Integer.parseInt(String.valueOf(maps.get(0).get("num")));

        SysConf sysConf = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, ConfKeyEnum.vpn_rss_repertory.name())).get(0);
        int repertory = Integer.parseInt(sysConf.getConfVal());

        if (num < repertory) {
            rssService.getRssUrlAsync(repertory - num);
        }
    }

    @Scheduled(cron = "0 10 0 * * ?")
    public void checkIn() {
        List<VpnUser> is_check = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().ne(VpnUser::getLastCheckDate, LocalDate.now()).eq(VpnUser::getStatus, "1"));
        for (VpnUser vpnUser : is_check) {
            Map<String, String> login = Vpn.login(vpnUser);
            Vpn.checkIn(login, vpnUser);
            vpnUser.setUpdDate(LocalDateTime.now());
            vpnUserMapper.updateById(vpnUser);
        }
    }

    @Scheduled(cron = "0 10 1 * * ?")
    public void buy() {

        DateTime lastMonth = DateUtil.lastMonth();

        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().lt(VpnUser::getLastBuyTime, lastMonth).eq(VpnUser::getStatus, "1"));

        for (VpnUser vpnUser : vpnUsers) {
            LocalDate lastBuyTime = vpnUser.getLastBuyTime();
            if (LocalDateTimeUtil.betweenPeriod(lastBuyTime, LocalDate.from(LocalDateTimeUtil.of(lastMonth))).getDays() <= 3) {
                Map<String, String> login = Vpn.login(vpnUser);
                Vpn.buy(login, vpnUser);
                vpnUser.setUpdDate(LocalDateTime.now());
                vpnUserMapper.updateById(vpnUser);
            } else {
                vpnUser.setUpdDate(LocalDateTime.now());
                vpnUser.setStatus("0");
                vpnUserMapper.updateById(vpnUser);
            }

        }
    }

    public String getRss(String clientType) {

        StringBuilder urls = new StringBuilder();

        checkRssNum();

        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getStatus, "1").ne(VpnUser::getLastUsedDate, LocalDate.now()).last("limit 5"));

        for (VpnUser vpnUser : vpnUsers) {
            List<VpnVmess> vpnVmesses = vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).eq(VpnVmess::getId, vpnUser.getId()));
            urls.append(vpnVmesses.get(0).getVmessUrl()).append("\r\n");
        }
        return Base64.encode(urls.toString());
    }

    public boolean upSet(String key, String value) {
        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);
        int conf_key = sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key));
        return conf_key == 1;
    }

}
