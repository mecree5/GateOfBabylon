package org.move.fast.common.api.vpn;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.utils.Cmd;
import org.move.fast.common.utils.IP;
import org.move.fast.common.utils.Log;
import org.move.fast.common.utils.string.HtmlToString;
import org.move.fast.common.utils.string.Unicode;
import org.move.fast.module.entity.auto.VpnUser;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: Vpn
 * @author: YinShiJie
 * @create: 2022-03-15 10:05
 */
public class DaBai {

    private static final String suc_ret_code = "1";

    private static final String vpn_url = "https://b.db01.in/";
    private static final String vpn_register_path = "/auth/register";
    private static final String vpn_emailCode_path = "/auth/send";
    private static final String vpn_login_path = "/auth/login";
    private static final String vpn_user_path = "/user";
    private static final String vpn_buy_path = "/user/buy";
    private static final String vpn_user_check = "/user/checkin";
    //邀请码
    private static final String vpn_user_code = "";

    private static boolean checkRsp(String rsp) {
        if (StrUtil.isBlank(rsp)) {
            return false;
        }
        JSONObject rspJsonObj = JSONObject.parseObject(rsp);
        if (!rspJsonObj.containsKey("ret")) {
            return false;
        }
        return suc_ret_code.equals(rspJsonObj.getString("ret"));
    }

    public static boolean sendEmailCode(String email) {
        //现在需要验证邮箱和ip 使用x-forwarded-for伪装下即可
        String rsp = HttpRequest.post(vpn_url + vpn_emailCode_path).form("email", email).header("x-forwarded-for", IP.getRandomIp()).execute().body();
        if (!checkRsp(rsp)) {
            return false;
        }
        Log.info(Cmd.colorString("邮件发送:" + "响应信息为   " + Unicode.unicodeDecode(rsp), 32, 1), DaBai.class);
        return checkRsp(rsp);
    }

    public static VpnUser register(String email, String name, String passwd, String emailCode) {

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("name", name);
        body.put("passwd", passwd);
        body.put("repasswd", passwd);
        body.put("code", vpn_user_code);
        //body.put("emailcode", emailCode);

        String rsp = HttpRequest.post(vpn_url + vpn_register_path).header("x-forwarded-for", IP.getRandomIp()).body(JSONObject.toJSONString(body)).execute().body();
        if (checkRsp(rsp)) {

            Log.info(Cmd.colorString("账号" + email + "注册成功,响应信息为   " + Unicode.unicodeDecode(rsp), 32, 1), DaBai.class);

            LocalDateTime time = LocalDateTime.now();
            VpnUser vpnUser = new VpnUser();
            vpnUser.setEmail(email);
            vpnUser.setPassword(passwd);
            vpnUser.setCrtDate(time);
            vpnUser.setUpdDate(time);
            return vpnUser;
        }
        return null;
    }

    public static String login(VpnUser vpnUser) {
        Map<String, String> body = new HashMap<>();
        body.put("email", vpnUser.getEmail());
        body.put("passwd", vpnUser.getPassword());
        body.put("code", vpn_user_code);

        List<String> rspHeaders = HttpRequest.post(vpn_url + vpn_login_path).body(JSONObject.toJSONString(body)).execute().headerList("set-cookie");
        if (CollectionUtils.isEmpty(rspHeaders)) {
            return null;
        }

        StringBuilder cookie = new StringBuilder();
        for (String rspHeader : rspHeaders) {
            cookie.append(rspHeader);
        }

        //返回内容
        String expire_in = cookie.substring(cookie.indexOf("expire_in="), cookie.indexOf(";", cookie.indexOf("expire_in=") + 1) + 1);
        String key = cookie.substring(cookie.indexOf("key="), cookie.indexOf(";", cookie.indexOf("key=") + 1) + 1);
        String email = cookie.substring(cookie.indexOf("email="), cookie.indexOf(";", cookie.indexOf("email=") + 1) + 1);
        String uid = cookie.substring(cookie.indexOf("uid="), cookie.indexOf(";", cookie.indexOf("uid=") + 1) + 1);

        Log.info(Cmd.colorString("账号" + vpnUser.getEmail() + "登录成功," + "cookie信息为   " + uid + expire_in + key + email, 32, 1), DaBai.class);

        return uid + expire_in + key + email;
    }


    public static boolean buy(String cookie, VpnUser vpnUser) {

        HashMap<String, String> body = new HashMap<>();
        body.put("coupon", "");
        body.put("shop", "32");
        body.put("autorenew", "0");
        body.put("disableothers", "1");

        String rsp = HttpRequest.post(vpn_url + vpn_buy_path).cookie(cookie).formStr(body).execute().body();

        if (!checkRsp(rsp)) {
            return false;
        }

        Log.info(Cmd.colorString("账号" + vpnUser.getEmail() + "购买成功,响应信息为  " + Unicode.unicodeDecode(rsp), 32, 1), DaBai.class);
        vpnUser.setLastBuyTime(LocalDate.now());
        return true;
    }

    public static String checkIn(String cookie, VpnUser vpnUser) {
        String rsp = HttpRequest.post(vpn_url + vpn_user_check).cookie(cookie).header("x-forwarded-for", IP.getRandomIp()).execute().body();

        if (StrUtil.isBlank(rsp)) {
            return null;
        }
        Log.info(Cmd.colorString("账号" + vpnUser.getEmail() + "签到成功,响应信息为  " + Unicode.unicodeDecode(rsp), 32, 1), DaBai.class);

        JSONObject rspJsonObj = JSONObject.parseObject(rsp);
        if (!suc_ret_code.equals(rspJsonObj.getString("ret"))) {
            return null;
        }

        return rspJsonObj.getString("traffic");
    }

    public static Map<VpnTypeEnum, String> takeRssUrl(String cookie) {

        HashMap<VpnTypeEnum, String> hashMap = new HashMap<>();

        String result = HttpRequest.get(vpn_url + vpn_user_path).cookie(cookie).execute().body();

        if (StrUtil.isBlank(result)) {
            return null;
        }

        String v2ray = HtmlToString.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(sub=3)+", result).get(0);
        Log.info(Cmd.colorString("v2ray获取订阅成功    " + "订阅信息为" + Unicode.unicodeDecode(v2ray), 32, 1), DaBai.class);
        hashMap.put(VpnTypeEnum.client_v2ray, v2ray);

        String kitsunebi = HtmlToString.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=kitsunebi)+", result).get(0);
        Log.info(Cmd.colorString("kitsunebi获取订阅成功    " + "订阅信息为" + Unicode.unicodeDecode(kitsunebi), 32, 1), DaBai.class);
        hashMap.put(VpnTypeEnum.client_kitsunebi, kitsunebi);

//        //暂不支持clash 更新订阅为 yaml配置
//        String clash = HtmlToStringUtils.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(clash=1)+", result).get(0);
//        Log.info(CmdColour.colorString("clash获取订阅成功" + " 订阅信息为" + UnicodeUtils.unicodeDecode(clash), 32, 1));
//        hashMap.put(VpnEnum.client_clash, clash);

        String shadowrocket = HtmlToString.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=shadowrocket)+", result).get(0);
        Log.info(Cmd.colorString("shadowrocket获取订阅成功    " + "订阅信息为" + Unicode.unicodeDecode(shadowrocket), 32, 1), DaBai.class);
        hashMap.put(VpnTypeEnum.client_shadowrocket, shadowrocket);

        String Quantumult = HtmlToString.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=quantumult)+", result).get(0);
        Log.info(Cmd.colorString("Quantumult获取订阅成功    " + "订阅信息为" + Unicode.unicodeDecode(Quantumult), 32, 1), DaBai.class);
        hashMap.put(VpnTypeEnum.client_Quantumult, Quantumult);

        //不用解密 直接为vmess串
        String QuantumultX = HtmlToString.takeByRegular("[A-Za-z\\u003a\\u002f\\u002d0-9\\u005f\\u002e\\u003f\\u003d\\u0026]+(list=quantumultx)+", result).get(0);
        Log.info(Cmd.colorString("QuantumultX获取订阅成功   " + "订阅信息为" + Unicode.unicodeDecode(QuantumultX), 32, 1), DaBai.class);
        hashMap.put(VpnTypeEnum.client_QuantumultX, QuantumultX);

        return hashMap;
    }

    public static String upVmessName(String clientType, String vmess, String replaceStr) {

        String head = "vmess://";
        JSONObject answerJson = new JSONObject();

        if (VpnTypeEnum.client_v2ray.getType().equals(clientType)) {

            answerJson = JSON.parseObject(Base64.decodeStr(vmess.substring(head.length())));
            answerJson.put("ps", replaceStr);
            answerJson.put("remark", replaceStr);

        } else if (VpnTypeEnum.client_kitsunebi.getType().equals(clientType)) {
            //todo
            return null;
        } else if (VpnTypeEnum.client_shadowrocket.getType().equals(clientType)) {
            //todo
            return null;
        } else if (VpnTypeEnum.client_Quantumult.getType().equals(clientType)) {
            //todo
            return null;
        } else if (VpnTypeEnum.client_QuantumultX.getType().equals(clientType)) {
            //todo
            return null;
        }
        return head + Base64.encode(answerJson.toJSONString());
    }

}

