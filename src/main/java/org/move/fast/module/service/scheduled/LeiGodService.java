package org.move.fast.module.service.scheduled;

import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.api.LeiGod;
import org.move.fast.common.api.push.PushPlus;
import org.move.fast.common.utils.Log;
import org.move.fast.module.service.PushService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LeiGodService {

    @Resource
    PushService pushService;

    private static long startTime = 0;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void pause() {

        long now = System.currentTimeMillis();
        String token = LeiGod.login();

        if (startTime > 0 && now - startTime > 3600000 * 6) {
            boolean pauseSuc = LeiGod.pause(token);
            if (pauseSuc) {
                startTime = 0;
            }
            Log.infoPro("雷神加速器开启超过6小时，自动关闭" + (pauseSuc ? "成功" : "失败"), this.getClass());
            pushService.pushToPerson(PushPlus.Template.txt, "雷神加速器开启超过6小时，自动关闭" + (pauseSuc ? "成功" : "失败"), "雷神加速器开启超过6小时，自动关闭" + (pauseSuc ? "成功" : "失败"));
            return;
        }

        JSONObject info = LeiGod.info(token);
        boolean isPause = "1".equals(String.valueOf(info.get("pause_status_id"))); //0-未暂停 1-暂停

        if (isPause) {
            Log.infoPro("监测到雷神加速器处于暂停状态，结束计时", this.getClass());
            startTime = 0;
            return;
        }
        if (startTime == 0) {
            Log.infoPro("监测到雷神加速器处于开始状态，开始计时", this.getClass());
            startTime = now;
        }
    }

}
