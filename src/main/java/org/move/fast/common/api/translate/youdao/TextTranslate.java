package org.move.fast.common.api.translate.youdao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.config.ReadConf;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : YinShiJie
 * @date : 2022/1/25 18:04
 */
public class TextTranslate {

    //文本翻译URL
    public static final String TEXT_YOUDAO_URL = "https://openapi.youdao.com/api";
    //应用ID
    public static final String TEXT_APP_KEY = ReadConf.getConfValue("gateOfBabylon.api.youdao.translate.key");
    //应用密钥
    public static final String TEXT_APP_SECRET = ReadConf.getConfValue("gateOfBabylon.api.youdao.translate.secret");
    //源语言
    public static final String TEXT_LAN_FORM = "auto";
    //目标语言
    public static final String TEXT_LAN_TO = "auto";

    public static String textTran(String q) {
        JSONObject jsonObject = JSON.parseObject(Requests.sendPost(TEXT_YOUDAO_URL, paramsMap(q)));
        String answer = jsonObject.getString("translation");
        return answer.substring(2, answer.length() - 2);
    }

    public static Map<String, String> paramsMap(String q) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", TEXT_LAN_FORM);
        params.put("to", TEXT_LAN_TO);
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = TEXT_APP_KEY + Common.truncate(q) + salt + curtime + TEXT_APP_SECRET;
        params.put("appKey", TEXT_APP_KEY);
        params.put("q", q);
        params.put("salt", salt);
        params.put("sign", Common.getDigest(signStr));
        return params;
    }

}
