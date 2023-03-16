package org.move.fast.common.utils.string;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : YinShiJie
 * @date : 2022/1/16 14:42
 */
public class Html {

    public static String removeTags(String realResp) {
        realResp = realResp.replaceAll("&[a-zA-Z]{1,10};", "").replaceAll("<[^>]*>", "");
        realResp = realResp.replaceAll("[(/>)<]", "");
        realResp = realResp.replaceAll("\r\n", "@").replaceAll("\\s*", "");
        realResp = realResp.replaceAll("@@@", "@");
        realResp = realResp.replaceAll("@@", "@");
        return realResp;
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

    public static String objToHtml(Object obj) {

        StringBuilder html = new StringBuilder();
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            String[] keys = jsonObject.keySet().toArray(new String[0]);

            html.append("<table border=\"1\" width=\"100%\">");
            Arrays.stream(keys).forEach(key -> {
                html.append("<tr>");
                html.append("<td width=\"10%\" align=\"center\">").append(key).append("</td>");
                Object val = jsonObject.get(key);
                if (val instanceof JSONArray || val instanceof JSONObject) {
                    html.append("<td width=\"90%\">").append(jsonToHtml(val)).append("</td>");
                } else {
                    html.append("<td width=\"90%\">").append(val).append("</td>");
                }
                html.append("</tr>");
            });
            html.append("</table>");
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            array.stream().map(Html::jsonToHtml).forEach(html::append);
        } else {
            return JSON.toJSONString(obj);
        }
        return html.toString();

    }

    public static String jsonToHtml(Object obj) {
        StringBuilder html = new StringBuilder();
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            String[] keys = jsonObject.keySet().toArray(new String[0]);

            html.append("<table border=\"1\" width=\"100%\">");
            Arrays.stream(keys).forEach(key -> {
                html.append("<tr>");
                html.append("<td width=\"30%\" align=\"center\">").append(key).append("</td>");
                Object val = jsonObject.get(key);
                if (val instanceof JSONArray || val instanceof JSONObject) {
                    html.append("<td width=\"70%\">").append(jsonToHtml(val)).append("</td>");
                } else {
                    html.append("<td width=\"70%\">").append(val).append("</td>");
                }
                html.append("</tr>");
            });
            html.append("</table>");
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            array.stream().map(Html::jsonToHtml).forEach(html::append);
        } else {
            return JSON.toJSONString(obj);
        }
        return html.toString();
    }

}
