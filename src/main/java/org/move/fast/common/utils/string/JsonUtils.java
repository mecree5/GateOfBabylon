package org.move.fast.common.utils.string;

import com.alibaba.fastjson.JSONObject;

/**
 * @author : YinShiJie
 * @date : 2022/2/11 15:55
 */
public class JsonUtils {

    /**
     * 递归从json串中获取key对应的value,可能是多层结构,多级key按xx.xx.xx传递
     * 这里使用的是alibaba.fastjson
     *
     * @param str json串
     * @param key 要获取value对应的key
     * @return
     */
    public static String getString(String str, String key) {
        String[] split = key.split("\\.");
        if (split.length > 1) {
            for (String s : split) {
                String targetKey = key.substring(key.indexOf(".") + 1);
                JSONObject jsonObject = JSONObject.parseObject(str);
                String string = jsonObject.getString(s);
                return getString(string, targetKey);
            }
        }
        // 没有"." 直接取
        JSONObject jsonObject = JSONObject.parseObject(str);
        return jsonObject.getString(key);
    }

}
