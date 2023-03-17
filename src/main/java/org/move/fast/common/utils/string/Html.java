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

            html.append("<table border=\"1\" width=\"100%\" align=\"center\" style=\"border-collapse: collapse; border-width: 3px; border-style: solid; border-radius: 10px; outline: 2px solid white;\">");
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

            html.append("<table border=\"0\" rules=\"rows\" width=\"100%\">");
            for (int i = 0; i < keys.length; i++) {
                String key = keys[i];
                html.append("<tr>");
                html.append("<td width=\"30%\" align=\"center\">").append(key).append("</td>");
                Object val = jsonObject.get(key);
                if (val instanceof JSONArray || val instanceof JSONObject) {
                    html.append("<td width=\"70%\">").append(jsonToHtml(val)).append("</td>");
                } else {
                    html.append("<td width=\"70%\">").append(val).append("</td>");
                }
                //避免出现两个边框
                if (i != keys.length - 1) {
                    html.append("</tr>").append("<tr></tr>");
                }
            }
            html.append("</table>");
        } else if (obj instanceof JSONArray) {
            JSONArray array = (JSONArray) obj;
            array.stream().map(Html::jsonToHtml).forEach(html::append);
        } else {
            return JSON.toJSONString(obj);
        }
        return html.toString();
    }

    public static String getHeadHtml() {
        return "<pre style=\"text-align: center; color: #94a3b8; \">" +
                "                                                                                                                                                              bbbbbbbb                                                                            \n" +
                "        GGGGGGGGGGGGG                          tttt                                   OOOOOOOOO        ffffffffffffffff  BBBBBBBBBBBBBBBBB                    b::::::b                                 lllllll                                    \n" +
                "     GGG::::::::::::G                       ttt:::t                                 OO:::::::::OO     f::::::::::::::::f B::::::::::::::::B                   b::::::b                                 l:::::l                                    \n" +
                "   GG:::::::::::::::G                       t:::::t                               OO:::::::::::::OO  f::::::::::::::::::fB::::::BBBBBB:::::B                  b::::::b                                 l:::::l                                    \n" +
                "  G:::::GGGGGGGG::::G                       t:::::t                              O:::::::OOO:::::::O f::::::fffffff:::::fBB:::::B     B:::::B                  b:::::b                                 l:::::l                                    \n" +
                " G:::::G       GGGGGG  aaaaaaaaaaaaa  ttttttt:::::ttttttt        eeeeeeeeeeee    O::::::O   O::::::O f:::::f       ffffff  B::::B     B:::::B  aaaaaaaaaaaaa   b:::::bbbbbbbbb yyyyyyy           yyyyyyyl::::l    ooooooooooo   nnnn  nnnnnnnn    \n" +
                "G:::::G                a::::::::::::a t:::::::::::::::::t      ee::::::::::::ee  O:::::O     O:::::O f:::::f               B::::B     B:::::B  a::::::::::::a  b::::::::::::::bby:::::y         y:::::y l::::l  oo:::::::::::oo n:::nn::::::::nn  \n" +
                "G:::::G                aaaaaaaaa:::::at:::::::::::::::::t     e::::::eeeee:::::eeO:::::O     O:::::Of:::::::ffffff         B::::BBBBBB:::::B   aaaaaaaaa:::::a b::::::::::::::::by:::::y       y:::::y  l::::l o:::::::::::::::on::::::::::::::nn \n" +
                "G:::::G    GGGGGGGGGG           a::::atttttt:::::::tttttt    e::::::e     e:::::eO:::::O     O:::::Of::::::::::::f         B:::::::::::::BB             a::::a b:::::bbbbb:::::::by:::::y     y:::::y   l::::l o:::::ooooo:::::onn:::::::::::::::n\n" +
                "G:::::G    G::::::::G    aaaaaaa:::::a      t:::::t          e:::::::eeeee::::::eO:::::O     O:::::Of::::::::::::f         B::::BBBBBB:::::B     aaaaaaa:::::a b:::::b    b::::::b y:::::y   y:::::y    l::::l o::::o     o::::o  n:::::nnnn:::::n\n" +
                "G:::::G    GGGGG::::G  aa::::::::::::a      t:::::t          e:::::::::::::::::e O:::::O     O:::::Of:::::::ffffff         B::::B     B:::::B  aa::::::::::::a b:::::b     b:::::b  y:::::y y:::::y     l::::l o::::o     o::::o  n::::n    n::::n\n" +
                "G:::::G        G::::G a::::aaaa::::::a      t:::::t          e::::::eeeeeeeeeee  O:::::O     O:::::O f:::::f               B::::B     B:::::B a::::aaaa::::::a b:::::b     b:::::b   y:::::y:::::y      l::::l o::::o     o::::o  n::::n    n::::n\n" +
                " G:::::G       G::::Ga::::a    a:::::a      t:::::t    tttttte:::::::e           O::::::O   O::::::O f:::::f               B::::B     B:::::Ba::::a    a:::::a b:::::b     b:::::b    y:::::::::y       l::::l o::::o     o::::o  n::::n    n::::n\n" +
                "  G:::::GGGGGGGG::::Ga::::a    a:::::a      t::::::tttt:::::te::::::::e          O:::::::OOO:::::::Of:::::::f            BB:::::BBBBBB::::::Ba::::a    a:::::a b:::::bbbbbb::::::b     y:::::::y       l::::::lo:::::ooooo:::::o  n::::n    n::::n\n" +
                "   GG:::::::::::::::Ga:::::aaaa::::::a      tt::::::::::::::t e::::::::eeeeeeee   OO:::::::::::::OO f:::::::f            B:::::::::::::::::B a:::::aaaa::::::a b::::::::::::::::b       y:::::y        l::::::lo:::::::::::::::o  n::::n    n::::n\n" +
                "     GGG::::::GGG:::G a::::::::::aa:::a       tt:::::::::::tt  ee:::::::::::::e     OO:::::::::OO   f:::::::f            B::::::::::::::::B   a::::::::::aa:::ab:::::::::::::::b       y:::::y         l::::::l oo:::::::::::oo   n::::n    n::::n\n" +
                "        GGGGGG   GGGG  aaaaaaaaaa  aaaa         ttttttttttt      eeeeeeeeeeeeee       OOOOOOOOO     fffffffff            BBBBBBBBBBBBBBBBB     aaaaaaaaaa  aaaabbbbbbbbbbbbbbbb       y:::::y          llllllll   ooooooooooo     nnnnnn    nnnnnn\n" +
                "                                                                                                                                                                                     y:::::y                                                      \n" +
                "                                                                                                                                                                                    y:::::y                                                       \n" +
                "                                                                                                                                                                                   y:::::y                                                        \n" +
                "                                                                                                                                                                                  y:::::y                                                         \n" +
                "                                                                                                                                                                                 yyyyyyy                                                          \n"
                + "</pre>";
    }

}
