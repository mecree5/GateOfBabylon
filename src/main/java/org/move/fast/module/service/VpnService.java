package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.api.crawler.dabai.Vpn;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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

    @Scheduled(cron = "0 10 1 * * ?")
    public void buy() {

        DateTime lastMonth = DateUtil.lastMonth();

        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().lt(VpnUser::getLastBuyTime, lastMonth).eq(VpnUser::getStatus, "1"));

        for (VpnUser vpnUser : vpnUsers) {

            if (LocalDateTimeUtil.betweenPeriod(vpnUser.getLastBuyTime(), LocalDate.from(LocalDateTimeUtil.of(lastMonth))).getDays() <= 3) {
                String cookie = Vpn.login(vpnUser);

                if (StrUtil.isBlank(cookie)) {
                    continue;
                }

                if (Vpn.buy(cookie, vpnUser)) {
                    vpnUser.setUpdDate(LocalDateTime.now());
                    vpnUserMapper.updateById(vpnUser);
                }

                continue;

            }

            vpnUser.setUpdDate(LocalDateTime.now());
            vpnUser.setStatus("0");
            vpnUserMapper.updateById(vpnUser);

        }
    }

    public String getRss(String clientType) {

        StringBuilder urls = new StringBuilder();
        LocalDate now = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now();

        checkRssNum();

        String which = sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, ConfKeyEnum.vpn_rss_which.name())).get(0).getConfVal();

        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getStatus, "1").ne(VpnUser::getLastUsedDate, LocalDate.now()).last("limit 5"));

        //签到为懒加载 异步方式
        rssService.checkInAsync(vpnUsers);

        for (VpnUser vpnUser : vpnUsers) {

            if (StrUtil.isNotBlank(which) && which.equals(vpnUser.getLastUpdRssWhich())) {

                urls.append(vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).eq(VpnVmess::getUserId, vpnUser.getId())).get(0).getVmessUrl()).append("\r\n");

                vpnUser.setLastUsedDate(now);
                vpnUser.setUpdDate(nowTime);
                vpnUserMapper.updateById(vpnUser);
                continue;

            }

            //获取节点变化 懒加载方式
            String rssUrls = vpnUser.getRssUrl();
            String clientName = VpnTypeEnum.checkTypeAndGetName(clientType);
            String strCache = rssUrls.substring(rssUrls.indexOf(clientName + ":"));
            String vmess = rssService.getVmessByRssUrl(Integer.parseInt(which), clientType, strCache.substring(clientName.length() + 1, strCache.indexOf(";")));
            urls.append(vmess).append("\r\n");

            vpnUser.setLastUsedDate(now);
            vpnUser.setUpdDate(nowTime);
            vpnUser.setLastUpdRssWhich(clientType);
            vpnUserMapper.updateById(vpnUser);
            VpnVmess vpnVmess = new VpnVmess();
            vpnVmess.setUpdDate(nowTime);
            vpnVmess.setVmessUrl(vmess);
            vpnVmessMapper.update(vpnVmess, new UpdateWrapper<VpnVmess>().lambda().eq(VpnVmess::getUserId, vpnUser.getId()).eq(VpnVmess::getClientType, clientType));

        }
        return Base64.encode(urls.toString());
    }

    public boolean upSet(String key, String value) {

        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);

        return sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key)) == 1;
    }

    private void checkRssNum() {

        int num = Integer.parseInt(String.valueOf(vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>().select("COUNT(*) as num").eq("status", "1").ne("last_used_date", LocalDate.now())).get(0).get("num")));

        int repertory = Integer.parseInt(sysConfMapper.selectList(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, ConfKeyEnum.vpn_rss_repertory.name())).get(0).getConfVal());

        if (num < repertory) {
            rssService.getRssUrlAsync(repertory - num);
        }
    }

}
