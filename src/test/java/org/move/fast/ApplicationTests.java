package org.move.fast;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.time.LocalDate;

@SpringBootTest
class ApplicationTests {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Test
    void contextLoads() {

        int num = Integer.parseInt(String.valueOf(vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>().select("COUNT(*) as num").lambda().eq(VpnUser::getStatus, "1").ne(VpnUser::getLastUsedDate, LocalDate.now())).get(0).get("num")));

    }

}
