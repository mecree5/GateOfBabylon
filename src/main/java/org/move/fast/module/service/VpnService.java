package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.api.dabai.Vpn;
import org.move.fast.common.entity.DBFieldEnum;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.config.ReadConf;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
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
    PushService pushService;

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    @Resource
    SysConfMapper sysConfMapper;


    @PostConstruct
    public void init() {
        //设置本地代理
        if ("true".equalsIgnoreCase(ReadConf.getConfValue("gateOfBabylon.proxy.is-start"))) {
            System.out.println("开启代理...");
            System.setProperty("socksProxyHost", ReadConf.getConfValue("gateOfBabylon.proxy.host"));
            System.setProperty("socksProxyPort", ReadConf.getConfValue("gateOfBabylon.proxy.port"));
        }
        checkRssNumAndGetDownNum(0);
    }

    public String getRss(String clientType, String clientName, String remoteAdd) {

        int downNum = checkRssNumAndGetDownNum(Integer.parseInt(sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>()
                .eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_down_num.name())).getConfVal()));
        if (downNum == 0) {
            throw new CustomerException(RetCodeEnum.too_much_req);
        }

        LocalDateTime nowTime = LocalDateTime.now();
        StringBuilder urls = new StringBuilder();
        LocalDate now = LocalDate.now();

        String which = sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_which.name())).getConfVal();

        //避免因为并发情况下导致的数据未初始化完成而被使用
        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey())
                .isNotNull(VpnUser::getRssUrl).ne(VpnUser::getRssUrl, "")
                .eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey())
                .ne(VpnUser::getLastUsedDate, LocalDate.now()).last("limit " + downNum));

        for (VpnUser vpnUser : vpnUsers) {

            //签到改为同步签到，获取剩余流量
            String traffic = rssService.checkInAndGetTraffic(vpnUser);

            if (StrUtil.isNotBlank(which) && which.equals(vpnUser.getLastUpdRssWhich())) {

                urls.append(Vpn.upVmessName(clientType, vpnVmessMapper.selectList(new LambdaQueryWrapper<VpnVmess>()
                        .eq(VpnVmess::getClientType, clientType)
                        .eq(VpnVmess::getUserId, vpnUser.getId())).get(0).getVmessUrl(), "剩余流量:" + traffic)).append("\r\n");

                vpnUser.setLastUsedDate(now);
                vpnUser.setUpdDate(nowTime);
                vpnUserMapper.updateById(vpnUser);
                continue;

            }

            //获取节点变化 懒加载方式
            String rssUrls = vpnUser.getRssUrl();
            String strCache = rssUrls.substring(rssUrls.indexOf(clientName + ":"));
            String vmess = rssService.getVmessByRssUrl(Integer.parseInt(which), clientType, strCache.substring(clientName.length() + 1, strCache.indexOf(";")));

            vpnUser.setLastUsedDate(now);
            vpnUser.setUpdDate(nowTime);
            vpnUser.setLastUpdRssWhich(which);
            vpnUserMapper.updateById(vpnUser);
            VpnVmess vpnVmess = new VpnVmess();
            vpnVmess.setUpdDate(nowTime);
            vpnVmess.setVmessUrl(vmess);
            vpnVmessMapper.update(vpnVmess, new UpdateWrapper<VpnVmess>().lambda()
                    .eq(VpnVmess::getUserId, vpnUser.getId()).eq(VpnVmess::getClientType, clientType));

            urls.append(Vpn.upVmessName(clientType, vmess, "剩余流量:" + traffic)).append("\r\n");
        }

        JSONObject pushMsg = new JSONObject(true);
        pushMsg.put("请求时间", nowTime);
        pushMsg.put("花费时间", LocalDateTimeUtil.between(nowTime, LocalDateTime.now()));
        pushMsg.put("下载个数", downNum);
        pushMsg.put("获取类型", clientName);
        pushMsg.put("请求地址", remoteAdd);

        pushService.getIpInfoAndPushToPerson(remoteAdd, "GateOfBabylon订阅提醒", pushMsg);

        return Base64.encode(urls.toString());
    }

    public boolean upSet(String key, String value) {

        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);

        return sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key)) == 1;
    }

    private int checkRssNumAndGetDownNum(int downNum) {

        int haveNum = Integer.parseInt(String.valueOf(vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>().select("COUNT(*) as num").lambda()
                .isNotNull(VpnUser::getRssUrl).ne(VpnUser::getRssUrl, "")
                .eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey()).ne(VpnUser::getLastUsedDate, LocalDate.now())).get(0).get("num")));

        int repertory = Integer.parseInt(sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>()
                .eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_repertory.name())).getConfVal());

        if (haveNum > repertory + downNum) {
            return downNum;
        }

        if (haveNum <= downNum) {
            rssService.getRssUrlAsync(repertory + haveNum);
            return haveNum;
        }

        rssService.getRssUrlAsync(downNum + repertory - haveNum);
        return downNum;
    }

}
