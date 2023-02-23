package org.move.fast.common.api.translate.youdao;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.config.ReadConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : YinShiJie
 * @date : 2022/1/25 18:04
 */
public class TextTranslate {

    //文本翻译URL
    private static final String URL = "https://openapi.youdao.com/api";
    //应用ID
    private static final String APP_KEY = ReadConf.getConfValue("gateOfBabylon.api.youdao.translate.key");
    //应用密钥
    private static final String APP_SECRET = ReadConf.getConfValue("gateOfBabylon.api.youdao.translate.secret");

    public static String trans(String str) {

        Map<String, Object> params = new HashMap<>();

        String salt = String.valueOf(UUID.randomUUID());
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);

        params.put("from", "auto");
        params.put("to", "auto");
        params.put("signType", "v3");
        params.put("curtime", curtime);
        params.put("appKey", APP_KEY);
        params.put("q", str);
        params.put("salt", salt);
        params.put("sign", Common.getDigestSha(APP_KEY + Common.truncate(str) + salt + curtime + APP_SECRET));

        JSONObject rspJson = JSON.parseObject(HttpRequest.post(URL).form(params).execute().body());
        if (!Common.suc_code.equals(rspJson.getString("errorCode"))) {
            return null;
        }
        String answer = rspJson.getString("translation");
        return answer.substring(2, answer.length() - 2);
    }

}
