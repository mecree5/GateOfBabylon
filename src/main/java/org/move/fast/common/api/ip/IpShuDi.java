package org.move.fast.common.api.ip;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.move.fast.common.utils.IP;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 本地使用没问题，服务器使用重定向不返回Location响应头。。。
 */
public class IpShuDi {

    private static final String url = "https://www.ipshudi.com/";

    public static Map<String, String> getIpHomePlace(String ip) {

        HttpResponse execute = HttpRequest.get(url + ip + ".htm").header("x-forwarded-for", IP.getRandomIp()).execute();

        //重定向
        if (execute.getStatus() == 301 || execute.getStatus() == 302) {
            execute = HttpRequest.get(execute.header("Location")).header("x-forwarded-for", IP.getRandomIp()).execute();
        }

        String body = execute.body();
        Map<String, String> ipHomePlace = new LinkedHashMap<>();

        if (StrUtil.isBlank(body) || !(body.contains("<table>"))) {
            return ipHomePlace;
        }


        //根据标签解析
        String table = body.substring(body.indexOf("<table>"), body.indexOf("</table>"));
        String[] split = table.split("<td class=\"th\">");
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            ipHomePlace.put(s.substring(0, s.indexOf("</td>")), s.substring(s.indexOf("<span>"), s.indexOf("</span>")).substring(6));
        }

        return ipHomePlace;

    }

}