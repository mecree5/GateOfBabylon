package org.move.fast.common.api.crawler.dabaivpn;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.move.fast.common.entity.VpnTypeEnum;
import org.move.fast.common.utils.CmdColour;
import org.move.fast.common.utils.IP;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.common.utils.string.HtmlToStringUtils;
import org.move.fast.common.utils.string.UnicodeUtils;
import org.move.fast.module.entity.auto.VpnUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @description: Vpn
 * @author: YinShiJie
 * @create: 2022-03-15 10:05
 */
public class Vpn {

    //vpn的配置
    public static final String vpn_url = "https://www.dabai.in";
    public static final String vpn_register_path = "/auth/register";
    public static final String vpn_emailCode_path = "/auth/send";
    public static final String vpn_login_path = "/auth/login";
    public static final String vpn_user_path = "/user";
    public static final String vpn_buy_path = "/user/buy";
    public static final String vpn_user_check = "/user/checkin";
    //邀请码
    public static final String vpn_user_code = "";


    public static void sendEmailCode(String email) {
        //现在需要验证邮箱和ip 使用x-forwarded-for伪装下即可
        HttpResponse rsp = HttpRequest.post(vpn_url + vpn_emailCode_path).form("email", email).header("x-forwarded-for", IP.getRandomIp()).execute();
        String body = rsp.body();
        System.out.println(CmdColour.getFormatLogString("邮件发送成功" + " 响应信息为" + UnicodeUtils.unicodeDecode(body), 32, 1));
    }

    public static VpnUser register(String email, String name, String passwd, String emailCode) {
        Map<String, String> head = new HashMap<>();
        head.put("x-forwarded-for", IP.getRandomIp());

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("name", name);
        body.put("passwd", passwd);
        body.put("repasswd", passwd);
        body.put("code", vpn_user_code);
//        body.put("emailcode", emailCode);

        String answer = Requests.sendPost(vpn_url + vpn_register_path, head, body);
        String targetStr = "注册成功" + " 账号:" + email + " 密码:" + passwd + " 响应信息为" + UnicodeUtils.unicodeDecode(answer);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));

        VpnUser vpnUser = new VpnUser();
        vpnUser.setEmail(email);
        vpnUser.setPassword(passwd);
        vpnUser.setCrtDate(LocalDateTime.now());
        vpnUser.setUpdDate(LocalDateTime.now());
        return vpnUser;
    }

    public static Map<String, String> login(VpnUser vpnUser) {
        Map<String, String> head = new HashMap<>();
        Map<String, String> body = new HashMap<>();
//        head.put("content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        body.put("email", vpnUser.getEmail());
        body.put("passwd", vpnUser.getPassword());
        body.put("code", vpn_user_code);
        return takeCookie(head, body);
    }

    public static Map<VpnTypeEnum, String> takeRssUrl(Map<String, String> cookie) {
        String result = "";
        HashMap<VpnTypeEnum, String> hashMap = new HashMap<>();
        for (String s : cookie.keySet()) {
            result = HttpRequest.get(vpn_url + vpn_user_path).header(s, cookie.get(s)).execute().body();
        }

        String v2ray = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(sub=3)+", result).get(0);
        String targetStr = "v2ray获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(v2ray);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
        hashMap.put(VpnTypeEnum.client_v2ray, targetStr);

        String kitsunebi = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=kitsunebi)+", result).get(0);
        targetStr = "kitsunebi获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(kitsunebi);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
        hashMap.put(VpnTypeEnum.client_kitsunebi, targetStr);

//        //暂不支持clash 更新订阅为 yaml配置
//        String clash = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(clash=1)+", result).get(0);
//        targetStr = "clash获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(clash);
//        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
//        hashMap.put(VpnEnum.client_clash, targetStr);

        String shadowrocket = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=shadowrocket)+", result).get(0);
        targetStr = "shadowrocket获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(shadowrocket);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
        hashMap.put(VpnTypeEnum.client_shadowrocket, targetStr);

        String Quantumult = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=quantumult)+", result).get(0);
        targetStr = "Quantumult获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(Quantumult);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
        hashMap.put(VpnTypeEnum.client_Quantumult, targetStr);

        //不用解密 直接为vmess串
        String QuantumultX = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=quantumultx)+", result).get(0);
        targetStr = "QuantumultX获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(QuantumultX);
        System.out.println(CmdColour.getFormatLogString(targetStr, 32, 1));
        hashMap.put(VpnTypeEnum.client_QuantumultX, targetStr);

        return hashMap;
    }

    public static void buy(Map<String, String> cookie, VpnUser vpnUser) {
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("coupon", "");
        stringStringHashMap.put("shop", "32");
        stringStringHashMap.put("autorenew", "0");
        stringStringHashMap.put("disableothers", "1");
        String body = "";
        for (String s : cookie.keySet()) {
            body = HttpRequest.post(vpn_url + vpn_buy_path).header(s, cookie.get(s)).formStr(stringStringHashMap).execute().body();
        }
        System.out.println(CmdColour.getFormatLogString("购买成功" + " 响应信息为" + UnicodeUtils.unicodeDecode(body), 32, 1));
        vpnUser.setLastBuyTime(LocalDate.now());
    }

    public static void checkIn(Map<String, String> cookie, VpnUser vpnUser) {
        String body = "";
        for (String s : cookie.keySet()) {
            body = HttpRequest.post(vpn_url + vpn_user_check).header(s, cookie.get(s)).header("x-forwarded-for", IP.getRandomIp()).execute().body();
        }
        System.out.println(CmdColour.getFormatLogString("签到成功了,账号为:" + vpnUser.getEmail() + " 响应信息为" + UnicodeUtils.unicodeDecode(body), 32, 1));
        vpnUser.setLastCheckDate(LocalDate.now());
    }

    private static Map<String, String> takeCookie(Map<String, String> headers, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        //创建post请求对象
        HttpPost post = new HttpPost(vpn_url + vpn_login_path);
        try {
            //创建参数集合
            List<BasicNameValuePair> list = new ArrayList<>();
            //添加参数
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            //把参数放入请求对象，post发送的参数list，指定格式
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            //添加请求头参数
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    post.addHeader(header.getKey(), header.getValue());
                }
            }
            CloseableHttpClient client = HttpClients.createDefault();
            //启动执行请求，并获得返回值
            CloseableHttpResponse response = client.execute(post);
            Header[] headers1 = response.getHeaders("set-cookie");
            //转换为string
            String answer = Arrays.toString(headers1);
            //返回内容
            String expire_in = answer.substring(answer.indexOf("expire_in="), answer.indexOf(";", answer.indexOf("expire_in=") + 1) + 1);
            String key = answer.substring(answer.indexOf("key="), answer.indexOf(";", answer.indexOf("key=") + 1) + 1);
            String email = answer.substring(answer.indexOf("email="), answer.indexOf(";", answer.indexOf("email=") + 1) + 1);
            String uid = answer.substring(answer.indexOf("uid="), answer.indexOf(";", answer.indexOf("uid=") + 1) + 1);

            System.out.println(CmdColour.getFormatLogString("登录成功" + " 登录信息为" + "Cookie:" + uid + expire_in + key + email, 32, 1));
            result.put("Cookie", uid + expire_in + key + email);
        } catch (Exception e) {
            System.out.println("POST请求异常" + e);
        }
        return result;
    }

}

