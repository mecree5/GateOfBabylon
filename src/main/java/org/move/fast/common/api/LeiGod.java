package org.move.fast.common.api;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.utils.IP;
import org.move.fast.common.utils.Log;
import org.move.fast.common.utils.Unicode;
import org.move.fast.config.ReadConf;

import java.util.HashMap;
import java.util.Map;

public class LeiGod {

    private static final String sucRetCode = "0";
    private static final String indexUrl = "https://www.leigod.com/m/mlogin.html?platform=4&code=d1zURMBJu3gKHyVN&state_html=m/mcenterList";

    private static final String loginUrl = "https://webapi.leigod.com/wap/login/bind";

    private static final String infoUrl = "https://webapi.leigod.com/api/user/info";

    private static final String recoverUrl = "https://webapi.leigod.com/api/user/recover";

    private static final String pauseUrl = "https://webapi.leigod.com/api/user/pause";

    private static final String username = ReadConf.getConfValue("gateOfBabylon.api.leiGod.username");

    private static final String password = ReadConf.getConfValue("gateOfBabylon.api.leiGod.password");

    public static String index() {
        HttpResponse execute = HttpRequest.get(indexUrl)
                .header("x-forwarded-for", IP.getRandomIp())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36")
                .execute();
        return execute.body();
    }

    public static String login() {

        Map<String, Object> param = new HashMap<>();
        param.put("code", "");
        param.put("country_code", "86");
        param.put("lang", "zh_CN");
        param.put("password", password);
        param.put("src_channel", "guanwang");
        param.put("user_type", "0");
        param.put("username", username);

        HttpRequest post = HttpRequest.post(loginUrl)
                .header("x-forwarded-for", IP.getRandomIp())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36")
                .form(param);
        String body = post.execute().body();
        Log.infoPro(body, LeiGod.class);

        if (StrUtil.isNotBlank(body)) {
            JSONObject jsonObject = JSONObject.parseObject(body);
            if (sucRetCode.equals(jsonObject.getString("code"))) {
                Object o = JSONObject.parseObject(jsonObject.get("data").toString()).get("login_info");
                return JSONObject.parseObject(o.toString()).get("account_token").toString();
            }
        }
        throw new CustomerException("雷神加速器账号登陆失败");
    }

    public static JSONObject info(String token) {

        HttpRequest post = HttpRequest.post(infoUrl).header("x-forwarded-for", IP.getRandomIp())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36")
                .form("account_token", token).form("lang", "zh_CN");
        String body = post.execute().body();
        Log.infoPro(body, LeiGod.class);

        if (StrUtil.isNotBlank(body)) {
            return JSONObject.parseObject(Unicode.decode(body));
        }
        throw new CustomerException("雷神加速器账号获取信息失败");
    }

    public static boolean recover(String token) {

        HttpRequest post = HttpRequest.post(recoverUrl).header("x-forwarded-for", IP.getRandomIp())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36")
                .form("account_token", token).form("lang", "zh_CN");
        String body = post.execute().body();
        Log.infoPro(body, LeiGod.class);

        if (StrUtil.isNotBlank(body)) {
            return sucRetCode.equals(JSONObject.parseObject(Unicode.decode(body)).get("code"));
        }
        throw new CustomerException("雷神加速器账号恢复失败");
    }

    public static boolean pause(String token) {

        HttpRequest post = HttpRequest.post(pauseUrl).header("x-forwarded-for", IP.getRandomIp())
                .header("User-Agent", "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.164 Mobile Safari/537.36")
                .form("account_token", token).form("lang", "zh_CN");
        String body = post.execute().body();
        Log.infoPro(body, LeiGod.class);

        if (StrUtil.isNotBlank(body)) {
            return sucRetCode.equals(JSONObject.parseObject(Unicode.decode(body)).get("code"));
        }
        throw new CustomerException("雷神加速器账号暂停失败");
    }

}
