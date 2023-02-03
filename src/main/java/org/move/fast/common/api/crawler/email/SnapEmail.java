package org.move.fast.common.api.crawler.email;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YinShiJie
 * @description: 临时邮箱工具
 * @date 2022/11/20 2:19
 */
public class SnapEmail {

    private static final String url = "https://linshiyou.com/";

    public static Map<String, String> getCookie() {
        HttpCookie httpCookie = HttpRequest.get(url).execute().getCookies().get(0);
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("cookie", httpCookie.getName() + "=" + httpCookie.getValue());
        return stringStringHashMap;
    }

    public static String generateEmail(Map<String, String> heads) {
        String path = "/user.php?user=";
        return HttpRequest.get(url + path).headerMap(heads, true).execute().body();
    }

    public static String getEmailInfo(Map<String, String> cookie, int listenNum) {
        String path = "mail.php?unseen=1";
        String body = "";
        for (int i = 0; i < listenNum; i++) {
//            body = HttpRequest.get(url + path).headerMap(cookie, true).execute().body();
            HttpResponse execute = HttpRequest.get(url + path).headerMap(cookie, true).execute();
            body = execute.body();
            if (StrUtil.isNotBlank(body)) {
                return body;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
