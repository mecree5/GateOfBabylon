package org.move.fast.common.api.ip;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import org.move.fast.common.utils.IP;

import java.util.LinkedHashMap;
import java.util.Map;

public class IpTool {

    private static final String url = "https://ip.tool.chinaz.com/";

    public static Map<String, String> getIpHomePlace(String ip) {

        String body = HttpRequest.get(url + ip).header("x-forwarded-for", IP.getRandomIp()).execute().body();

        Map<String, String> ipHomePlace = new LinkedHashMap<>();

        if (StrUtil.isBlank(body) || !(body.contains("<div class=\"WhwtdWrap bor-b1s col-gray03\" style=\"height:auto\">"))) {
            return ipHomePlace;
        }

        //根据标签解析
        String cache = body.substring(body.indexOf("<div class=\"WhwtdWrap bor-b1s col-gray03\" style=\"height:auto\">"));
        String result = cache.substring(cache.indexOf("<em>"), cache.indexOf("</em>")).substring(4);

        String[] split = result.split(" ");

        ipHomePlace.put("IP归属地", split[0]);
        ipHomePlace.put("运营商", split[1]);

        return ipHomePlace;

    }

}
