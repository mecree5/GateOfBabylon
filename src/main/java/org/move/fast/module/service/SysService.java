package org.move.fast.module.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import org.move.fast.common.utils.string.Html;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class SysService {

    @Resource
    SysConfMapper sysConfMapper;

    public String getApiList(String url) {

        JSONObject getVmess = new JSONObject(true);
        getVmess.put("DABAI", url + "/vpn/down/v2ray");
        getVmess.put("GITHUB1", url + "/vpn/cd/v2ray/1");
        getVmess.put("GITHUB2", url + "/vpn/cd/v2ray/2");

        JSONObject getRssUrl = new JSONObject(true);
        getRssUrl.put("RssUrl", url + "/vpn/stockUp/#{num}");

        JSONObject funcList = new JSONObject(true);
        funcList.put("添加订阅地址", getVmess);
        funcList.put("获取订阅地址", getRssUrl);

        JSONObject result = new JSONObject(true);
        result.put("功能列表", funcList);
        result.put("系统设置", getSysConfList(url));

        return Html.getHeadHtml() + Html.objToHtml(result);
    }

    private JSONObject getSysConfList(String url) {
        JSONObject nowSysConfList = new JSONObject(true);
        JSONObject upSysConfList = new JSONObject();
        for (SysConf sysConf : sysConfMapper.selectList(new QueryWrapper<>())) {
            String confKey = sysConf.getConfKey();
            nowSysConfList.put(sysConf.getConfRemark(), confKey + "-->" + sysConf.getConfVal());
            upSysConfList.put(sysConf.getConfRemark(), url + "/sys/upset/" + confKey + "/#{num}");
        }
        JSONObject sysConfList = new JSONObject(true);
        sysConfList.put("当前设置", nowSysConfList);
        sysConfList.put("修改设置", upSysConfList);
        return sysConfList;
    }

    public boolean upSet(String key, String value) {

        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);

        return sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key)) == 1;
    }


}
