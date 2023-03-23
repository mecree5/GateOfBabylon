package org.move.fast.module.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.api.vpn.VpnTypeEnum;
import org.move.fast.common.utils.Html;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnCrawler;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnCrawlerMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SysService {

    @Resource
    SysConfMapper sysConfMapper;

    @Resource
    VpnCrawlerMapper vpnCrawlerMapper;

    public String getApiList(String url) {

        JSONObject getRssUrl = new JSONObject(true);
        getRssUrl.put("RssUrl", url + "/vpn/stockUp/#{num}");

        JSONObject getVmess = getVmessList(url);
        JSONArray sysConfList = getSysConfList(url);

        JSONObject funcList = new JSONObject(true);
        funcList.put("添加订阅地址", getVmess);
        funcList.put("获取订阅地址", getRssUrl);
        funcList.put("修改设置", sysConfList.get(1));

        JSONObject result = new JSONObject(true);
        result.put("功能列表", funcList);

        return Html.getHeadHtml() + Html.objToHtml(sysConfList.get(0)) + Html.objToHtml(result);
    }

    private JSONObject getVmessList(String url) {
        JSONObject vmessList = new JSONObject(true);
        for (VpnTypeEnum value : VpnTypeEnum.values()) {
            vmessList.put("DABAI-" + value.getName(), url + "/vpn/down/" + value.getName());
        }
        for (VpnCrawler vpnCrawler : vpnCrawlerMapper.selectList(new QueryWrapper<>())) {
            vmessList.put("GITHUB-" + vpnCrawler.getId(), url + "/vpn/cd/" + VpnTypeEnum.checkTypeAndGetName(vpnCrawler.getClientType()) + "/" + vpnCrawler.getId());
        }
        return vmessList;
    }

    private JSONArray getSysConfList(String url) {
        JSONObject nowSysConfList = new JSONObject(true);
        JSONObject upSysConfList = new JSONObject(true);
        JSONObject nowSysConf = new JSONObject(true);
        for (SysConf sysConf : sysConfMapper.selectList(new QueryWrapper<>())) {
            String confKey = sysConf.getConfKey();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(confKey, sysConf.getConfVal());
            nowSysConf.put(sysConf.getConfRemark(), jsonObject);
            upSysConfList.put(sysConf.getConfRemark(), url + "/sys/upset/" + confKey + "/#{num}");
        }
        nowSysConfList.put("当前设置", nowSysConf);

        JSONArray sysConfList = new JSONArray();
        sysConfList.add(nowSysConfList);
        sysConfList.add(upSysConfList);
        return sysConfList;
    }

    public boolean upSet(String key, String value) {

        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);

        return sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key)) == 1;
    }


}
