package org.move.fast.module.service;


import com.alibaba.fastjson.JSONArray;
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
        getVmess.put("DABAI", url + "vpn/down/v2ray");
        getVmess.put("GITHUB1", url + "vpn/cd/v2ray/1");
        getVmess.put("GITHUB2", url + "vpn/cd/v2ray/2");

        JSONObject getRssUrl = new JSONObject(true);
        getRssUrl.put("RssUrl", url + "vpn/stockUp/#{num}");

        JSONObject sys = new JSONObject(true);
        for (SysConf sysConf : sysConfMapper.selectList(new QueryWrapper<>())) {
            String confKey = sysConf.getConfKey();
            sys.put(confKey + "当前设置", sysConf.getConfVal());
            sys.put("UPSET " + confKey, url + "sys/upset/" + confKey + "/#{num}");
        }

        JSONArray list = new JSONArray();
        list.add(getVmess);
        list.add(getRssUrl);
        list.add(sys);

        JSONObject result = new JSONObject(true);
        result.put("功能列表", list);

        return Html.objToHtml(result);
    }

    public boolean upSet(String key, String value) {

        SysConf sysConf = new SysConf();
        sysConf.setUpdDate(LocalDateTime.now());
        sysConf.setConfVal(value);

        return sysConfMapper.update(sysConf, new UpdateWrapper<SysConf>().lambda().eq(SysConf::getConfKey, key)) == 1;
    }


}
