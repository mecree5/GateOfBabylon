package org.move.fast.common.api.push;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.utils.Log;

import java.util.HashMap;

public class PushPlus {

    private static final String push_post_url = "http://www.pushplus.plus/send";
    private static final String suc_code = "200";

    public enum Template {
        html,  //支持html文本。为空默认使用html模板
        txt,   //纯文本内容,不转义html内容,换行使用
        json,    //可视化展示json格式内容
        markdown,  //内容基于markdown格式展示
        cloudMonitor,    //阿里云监控报警定制模板
        jenkins,    //jenkins插件定制模板
        route,    //路由器插件定制模板
        pay,   //支付成功通知模板
        ;
    }

    public static boolean pushToPerson(String token, Template template, String title, String content) {

        HashMap<String, String> body = new HashMap<>();
        body.put("token", token);
        body.put("title", title);
        body.put("content", content);
        body.put("template", template.name());

        String rsp = HttpRequest.post(push_post_url).contentType("application/json").body(JSONObject.toJSONString(body)).execute().body();

        if (StrUtil.isNotBlank(rsp)) {

            JSONObject rspJsonObj = JSONObject.parseObject(rsp);

            if (rspJsonObj.containsKey("code")) {
                return rspJsonObj.getString("code").equals(suc_code);
            }
            Log.info("推送失败,响应信息：" + rsp, PushPlus.class);
        }
        return false;
    }

}
