package org.move.fast.module.service;

import cn.hutool.core.codec.Base64;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader;
import org.apache.catalina.startup.UserConfig;
import org.move.fast.common.api.crawler.dabaivpn.Vpn;
import org.move.fast.common.entity.Result;
import org.move.fast.common.utils.http.Requests;
import org.move.fast.common.utils.random.RandomString;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.entity.auto.VpnVmess;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.mapper.auto.VpnVmessMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class VpnService {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    VpnVmessMapper vpnVmessMapper;

    public Result<Object> getRssUrl(int num){

        Random random = new Random();
        String username = RandomString.getRandomString(4);
        String password = RandomString.getRandomString(10);
        String email = username + random.nextInt(1000) + "@qq.com";

        VpnUser vpnUser = Vpn.register(email, username, password, null);
        Map<String, String> cookie = Vpn.login(vpnUser);
        Vpn.buy(cookie);
        Vpn.checkIn(cookie, vpnUser);
        //拿订阅地址
        Map<String, String> rssUrl = Vpn.takeRssUrl(cookie);
        ArrayList<VpnVmess> vpnVmesses = new ArrayList<>();
        for (String type : rssUrl.keySet()) {
            VpnVmess vpnVmess = new VpnVmess();
            vpnVmess.setClientType(type);
            String base64_vmess = Requests.downloadToString(rssUrl.get(type));
            String[] vmesses = Base64.decodeStr(base64_vmess).split("vmess");
            for (int i = 0; i < vmesses.length; i++) {
                String vmess = vmesses[i];

            }
            vpnVmess.setVmessUrl();
        }
        return null;
    }

}
