package org.move.fast.module.service;

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
            Log.info("PushService.pushToPerson推送:" + title + "内容为" + content, this.getClass());
        }
    }

}
