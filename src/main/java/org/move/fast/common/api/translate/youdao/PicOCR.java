package org.move.fast.common.api.translate.youdao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.RetCodeEnum;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.config.ReadConf;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;


/**
 * 图片扫描,一张一分钱
 *
 * @author : YinShiJie
 * @date : 2022/2/10 16:47
 */
public class PicOCR {

    private static final String YOUDAO_URL = "https://openapi.youdao.com/ocrapi";

    private static final String APP_KEY = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr.key");

    private static final String APP_SECRET = ReadConf.getConfValue("gateOfBabylon.api.youdao.ocr.secret");

    public static String OCR(String base64) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        String detectType = "10012";
        String imageType = "1";
        String langType = "auto";
        params.put("detectType", detectType);
        params.put("imageType", imageType);
        params.put("langType", langType);
        params.put("img", base64);
        params.put("docType", "json");
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = APP_KEY + Common.truncate(base64) + salt + curtime + APP_SECRET;
        String sign = Common.getDigest(signStr);
        params.put("appKey", APP_KEY);
        params.put("salt", salt);
        params.put("sign", sign);
        return Requests.sendPost(YOUDAO_URL, params);
    }

    public static String loadAsBase64(String imgFile) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        File file = new File(imgFile);
        if (!file.exists()) {
            throw new CustomerException(RetCodeEnum.file_not_exists, imgFile);
        }

        byte[] data = null;
        //读取图片字节数组
        try (InputStream in = Files.newInputStream(Paths.get(imgFile))) {
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            throw new RuntimeException(imgFile + "读取失败" + e);
        }
        //对字节数组Base64编码
        return Base64.getEncoder().encodeToString(data);//返回Base64编码过的字节数组字符串
    }

    public static String parsingJson(String json) {
        StringBuilder answer = new StringBuilder();
        JSONArray jsonArray = JSONArray.parseArray(JSONObject.parseObject(JSONObject.parseObject(json).getString("Result")).getString("regions"));
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
