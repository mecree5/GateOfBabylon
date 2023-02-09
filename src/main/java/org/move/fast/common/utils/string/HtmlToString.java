package org.move.fast.common.utils.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : YinShiJie
 * @date : 2022/1/16 14:42
 */
public class HtmlToString {

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
        Pattern pattern = Pattern.compile("http[s]?:\\/\\/([\\w]+\\.)+[\\w]+([\\w./?%&=]*)?");
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
