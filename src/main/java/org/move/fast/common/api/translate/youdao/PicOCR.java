package org.move.fast.common.api.translate.youdao;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.config.ReadConf;

import java.util.HashMap;
import java.util.Map;


/**
 * 图片扫描,一张一分钱
 *
 * @author : YinShiJie
 * @date : 2022/2/10 16:47
 */
public class PicOCR {

    private static final String URL = "https://openapi.youdao.com/ocrapi";

    private static final String APP_KEY = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr.key");

    private static final String APP_SECRET = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr.secret");

    public static String OCR(String base64) {
        Map<String, Object> params = new HashMap<>();

        String salt = String.valueOf(UUID.randomUUID());
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);

        params.put("img", base64);
        params.put("langType", "auto");
        params.put("detectType", "10012");
        params.put("imageType", "1");
        params.put("appKey", APP_KEY);
        params.put("salt", salt);
        params.put("sign", Common.getDigestSha(APP_KEY + Common.truncate(base64) + salt + curtime + APP_SECRET));
        params.put("docType", "json");
        params.put("signType", "v3");
        params.put("curtime", curtime);

        return parse(HttpRequest.post(URL).form(params).execute().body());
    }

    private static String parse(String rsp) {
        StringBuilder answer = new StringBuilder();
        JSONObject rspJson = JSONObject.parseObject(rsp);
        if (!Common.suc_code.equals(rspJson.getString("errorCode"))) {
            return null;
        }
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.parseObject(rspJson.getString("Result")).getString("regions"));
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONArray array = JSONArray.parseArray(JSONObject.parseObject(jsonArray.getString(i)).getString("lines"));
            for (int j = 0; j < array.size(); j++) {
                String text = JSONObject.parseObject(array.getString(j)).getString("text");
                answer.append(text).append(",");
            }
        }
        return String.valueOf(answer);
    }

}
