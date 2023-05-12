package org.move.fast.module.service.scheduled;

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

    private static long startTime;

    @Scheduled(cron = "*/10 * * * *")
    public void pause() {

        long now = System.currentTimeMillis();
        String token = LeiGod.login();

        if (startTime > 0) {
            if (now - startTime > 3600000 * 6) {
                if (LeiGod.pause(token)) {
                    startTime = 0;
                    Log.info("雷神加速器开启超过6小时，已自动关闭", this.getClass());
                    pushService.pushToPerson(PushPlus.Template.txt, "雷神加速器自动关闭成功", "雷神加速器自动关闭成功");
                }
                return;
            }
        }


        if ("0".equals(String.valueOf(LeiGod.info(token).get("pause_status_id")))) {
            //开始中
            startTime = now;
        }

    }


}
