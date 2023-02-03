package org.move.fast.common.utils.json;

import com.alibaba.fastjson.JSON;

import java.util.Map;

/**
 * 根据库来生成json
 *
 * @author : YinShiJie
 * @date : 2022/1/14 10:50
 */
public class JsonGenerator {

    public static String mapToJson(Map<String, Object> nameAndDataType) {
        return JSON.toJSONString(nameAndDataType);
    }

}
