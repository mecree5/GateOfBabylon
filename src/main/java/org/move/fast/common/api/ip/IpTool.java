package org.move.fast.common.api.ip;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import org.move.fast.common.utils.IP;
import org.move.fast.common.utils.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class IpTool {

    private static final String url = "https://ip.tool.chinaz.com/";

    public static Map<String, String> getIpHomePlace(String ip) {

        Map<String, String> ipHomePlace = new LinkedHashMap<>();

        try {
            String body = HttpRequest.get(url + ip).header("x-forwarded-for", IP.getRandomIp()).execute().body();

            String tag = "<em id=\"infoLocation\">";

            if (StrUtil.isBlank(body) || !(body.contains(tag))) {
                return ipHomePlace;
            }

            //根据标签解析
            int i = body.indexOf(tag);
            String result = body.substring(i, body.indexOf("</em>", i)).substring(tag.length());

            String[] split = result.split(" ");

            ipHomePlace.put("IP归属地", split[0]);
            ipHomePlace.put("运营商", split[1]);

            return ipHomePlace;
        } catch (Exception e){
            Log.error("IpTool获取不到ip", IpTool.class);
            return ipHomePlace;
        }

    }

}
