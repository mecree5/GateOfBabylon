package org.move.fast.module.service;

import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.api.ip.IpShuDi;
import org.move.fast.common.api.push.PushPlus;
import org.move.fast.common.utils.Log;
import org.move.fast.config.ReadConf;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * @author YinShiJie
 * @description: 作为服务工具类被调用  切记不要导致循环依赖
 * @date 2023/2/14 14:42
 */
@Service
public class PushService {

    private static final String token = ReadConf.getConfValue("gateOfBabylon.api.notice.push-plus-token");

    @Async("asyncTaskExecutor")
    public void pushToPerson(PushPlus.Template template, String title, String content) {

        if (PushPlus.pushToPerson(token, template, title, content)) {
            Log.printAndWrite("PushService.pushToPerson推送:" + title + "内容为" + content);
        }
    }


    @Async("asyncTaskExecutor")
    public void getIpInfoAndPushToPerson(String remoteAdd, String title, JSONObject content) {
        content.fluentPutAll(IpShuDi.getIpHomePlace(remoteAdd));
        String contentStr = content.toJSONString();

        if (PushPlus.pushToPerson(token, PushPlus.Template.json, title, contentStr)) {
            Log.printAndWrite("PushService.getIpInfoAndPushToPerson推送:" + title + "内容为" + contentStr);
        }
    }

}
