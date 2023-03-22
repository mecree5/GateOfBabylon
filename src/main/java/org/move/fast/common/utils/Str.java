package org.move.fast.common.utils;

import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Str {

    @SafeVarargs
    public static <R> boolean checkObjFiledIsBlank(R rs, Function<R, Object>... function) {
        for (Function<R, Object> rObjectFunction : function) {
            Object apply = rObjectFunction.apply(rs);
            if (apply instanceof String) {
                if (StrUtil.isBlank((String) apply)) {
                    return true;
                }
            } else {
                if (apply == null) {
                    return true;
                }
            }
        }
        return false;
    }

    //正则找出所有url
    public static List<String> takeUrl(String str) {
        Pattern pattern = Pattern.compile("http(s?)://(\\w+\\.)+\\w+([\\w./?%&=]*)?");
        Matcher m = pattern.matcher(str);
        List<String> urls = new ArrayList<>();
        while (m.find()) {
            urls.add(m.group());
        }
        return urls;
    }


    //正则工具
    public static List<String> takeByRegular(String regular, String str) {
        Pattern pattern = Pattern.compile(regular);
        Matcher m = pattern.matcher(str);
        List<String> urls = new ArrayList<>();
        while (m.find()) {
            urls.add(m.group());
        }
        return urls;
    }
}
