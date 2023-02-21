package org.move.fast.common.api.ip;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import org.move.fast.common.utils.IP;

import java.util.LinkedHashMap;
import java.util.Map;

public class IpShuDi {

    private static final String url = "https://www.ipshudi.com/";

    public static Map<String, String> getIpHomePlace(String ip) {

        String body = HttpRequest.get(url + ip + ".htm").header("x-forwarded-for", IP.getRandomIp()).execute().body();

        if (StrUtil.isBlank(body)) {
            return null;
        }

        Map<String, String> ipHomePlace = new LinkedHashMap<>();

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