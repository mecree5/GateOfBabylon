package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.api.ip.IpTool;
import org.move.fast.common.api.push.PushPlus;
import org.move.fast.common.api.vpn.DaBai;
import org.move.fast.common.api.vpn.VpnTypeEnum;
import org.move.fast.common.entity.DBFieldEnum;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.common.utils.Log;
import org.move.fast.config.ReadConf;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnCrawler;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnCrawlerMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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

    @Resource
    VpnCrawlerMapper vpnCrawlerMapper;

    static volatile Map<String, LinkedHashMap<Integer, String>> result = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        //设置本地代理
        if ("true".equalsIgnoreCase(ReadConf.getConfValue("gateOfBabylon.proxy.is-start"))) {
            Log.infoPro("开启代理...", this.getClass());
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
        LocalDate now = LocalDate.now();

        String which = sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>().eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_which.name())).getConfVal();

        //避免因为并发情况下导致的数据未初始化完成而被使用
        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>().eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey())
                .isNotNull(VpnUser::getRssUrl).ne(VpnUser::getRssUrl, "").eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey())
                .ne(VpnUser::getLastUsedDate, LocalDate.now()).last("limit " + downNum));

        //随便取一条监听代理地址是否变化
        String vmessToCompare = rssService.getVmessByRssUrl(Integer.parseInt(which), clientType, getClientRssUrl(clientName, vpnUsers.get(0).getRssUrl()));
        boolean addAndPortIsNotChange = checkAddAndPortIsNotChange(clientType,
                vpnVmessMapper.selectOne(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).eq(VpnVmess::getUserId, vpnUsers.get(0).getId())).getVmessUrl(), vmessToCompare);

        String resultKey = UUID.randomUUID().toString(true);
        result.put(resultKey, new LinkedHashMap<>());
        ArrayList<VpnVmess> vpnVmesses = new ArrayList<>();

        for (int i = 0; i < vpnUsers.size(); i++) {

            VpnUser vpnUser = vpnUsers.get(i);
            //签到改为异步签到，获取剩余流量
            rssService.checkInAsyncAndPutTrafficToResult(vpnUser, resultKey, i, result);

            if (addAndPortIsNotChange && StrUtil.isNotBlank(which) && which.equals(vpnUser.getLastUpdRssWhich())) {

                VpnVmess vpnVmess = vpnVmessMapper.selectOne(new LambdaQueryWrapper<VpnVmess>().eq(VpnVmess::getClientType, clientType).eq(VpnVmess::getUserId, vpnUser.getId()));

                vpnVmesses.add(vpnVmess);

                vpnUser.setLastUsedDate(now);
                vpnUser.setUpdDate(nowTime);
                vpnUserMapper.updateById(vpnUser);
                continue;
            }

            Log.infoPro(vpnUser.getEmail() + "由于节点信息变化，重新获取节点信息", this.getClass());
            String vmess = rssService.getVmessByRssUrl(Integer.parseInt(which), clientType, getClientRssUrl(clientName, vpnUser.getRssUrl()));

            vpnUser.setLastUsedDate(now);
            vpnUser.setUpdDate(nowTime);
            vpnUser.setLastUpdRssWhich(which);
            vpnUserMapper.updateById(vpnUser);

            VpnVmess vpnVmess = new VpnVmess();
            vpnVmess.setUpdDate(nowTime);
            vpnVmess.setVmessUrl(vmess);
            vpnVmessMapper.update(vpnVmess, new UpdateWrapper<VpnVmess>().lambda().eq(VpnVmess::getUserId, vpnUser.getId()).eq(VpnVmess::getClientType, clientType));

            vpnVmesses.add(vpnVmess);
        }

        //监听是否完成签到
        while (result.get(resultKey).size() < downNum) {
            Log.infoPro(resultKey + "监听中...", this.getClass());
            if (LocalDateTimeUtil.between(nowTime, LocalDateTime.now(), ChronoUnit.SECONDS) > 30) {
                throw new CustomerException(RetCodeEnum.async_wait_error);
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        StringBuilder urls = new StringBuilder();
        //取出异步签到信息
        for (int i = 0; i < vpnVmesses.size(); i++) {
            urls.append(DaBai.upVmessName(clientType, vpnVmesses.get(i).getVmessUrl(), "剩余流量:" + result.get(resultKey).get(i))).append("\r\n");
        }
        //避免内存溢出
        result.remove(resultKey);

//        pushMsg(nowTime, downNum, clientName, remoteAdd);

        return Base64.encode(urls.toString());
    }

    public static boolean checkAddAndPortIsNotChange(String clientType, String vmess, String vmessToCompare) {
        String head = "vmess://";

        if (VpnTypeEnum.client_v2ray.getType().equals(clientType)) {

            JSONObject vmessJson = JSON.parseObject(Base64.decodeStr(vmess.substring(head.length())));
            JSONObject vmessToCompareJson = JSON.parseObject(Base64.decodeStr(vmessToCompare.substring(head.length())));
            return vmessToCompareJson.getString("add").equals(vmessJson.getString("add")) && vmessToCompareJson.getString("port").equals(vmessJson.getString("port"));

        } else if (VpnTypeEnum.client_kitsunebi.getType().equals(clientType)) {
            //todo
            return true;
        } else if (VpnTypeEnum.client_shadowrocket.getType().equals(clientType)) {
            //todo
            return true;
        } else if (VpnTypeEnum.client_Quantumult.getType().equals(clientType)) {
            //todo
            return true;
        } else if (VpnTypeEnum.client_QuantumultX.getType().equals(clientType)) {
            //todo
            return true;
        }
        return false;
    }

    public static String getClientRssUrl(String clientName, String allRssUrl) {
        String strCache = allRssUrl.substring(allRssUrl.indexOf(clientName + ":"));
        return strCache.substring(clientName.length() + 1, strCache.indexOf(";"));
    }

    private void pushMsg(LocalDateTime startTime, int downNum, String clientName, String remoteAdd) {
        JSONObject pushMsg = new JSONObject(true);
        pushMsg.put("请求时间", startTime);
        pushMsg.put("花费时间", LocalDateTimeUtil.between(startTime, LocalDateTime.now()));
        pushMsg.put("下载个数", downNum);
        pushMsg.put("获取类型", clientName);
        pushMsg.put("请求地址", remoteAdd);
        pushMsg.fluentPutAll(IpTool.getIpHomePlace(remoteAdd));
        pushService.pushToPerson(PushPlus.Template.json, "GateOfBabylon订阅提醒", pushMsg.toJSONString());
    }

    public int checkRssNumAndGetDownNum(int downNum) {

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

    public String crawler(String clientType, String id) {
        VpnCrawler vpnCrawler = vpnCrawlerMapper.selectById(id);

        if (!clientType.equals(vpnCrawler.getClientType())) {
            throw new CustomerException(RetCodeEnum.validated_error);
        }

        if (DBFieldEnum.vpn_crawler_type_direct.getKey().equals(vpnCrawler.getCrawlerType())) {
            //todo 验证可用性
            return HttpRequest.get(vpnCrawler.getCrawlerUrl()).execute().body();
        } else {
            //todo
            return null;
        }
    }

}
