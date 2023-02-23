package org.move.fast.common.api.translate.youdao;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.config.ReadConf;

import java.util.HashMap;
import java.util.Map;

public class PicTranslate {

    //文本翻译URL
    private static final String URL = "https://openapi.youdao.com/ocrtransapi";
    //应用ID
    private static final String APP_KEY = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr-trans.key");
    //应用密钥
    private static final String APP_SECRET = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr-trans.secret");


    public static String trans(String base64) {

        Map<String, Object> params = new HashMap<>();

        String salt = String.valueOf(UUID.randomUUID());

        params.put("type", "1");
        params.put("from", "auto");
        params.put("to", "auto");
        params.put("appKey", APP_KEY);
        params.put("salt", salt);
        params.put("sign", Common.getDigestMd5(APP_KEY + base64 + salt + APP_SECRET));
        params.put("q", base64);

        JSONObject rspJson = JSON.parseObject(HttpRequest.post(URL).form(params).execute().body());
        if (!Common.suc_code.equals(rspJson.getString("errorCode"))) {
            return null;
        }
        return rspJson.getJSONArray("resRegions").getJSONObject(0).getString("tranContent");
    }

}
