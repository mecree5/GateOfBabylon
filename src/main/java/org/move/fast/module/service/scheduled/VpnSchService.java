package org.move.fast.module.service.scheduled;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.move.fast.common.api.vpn.DaBai;
import org.move.fast.common.entity.DBFieldEnum;
import org.move.fast.common.entity.SysConfKeyEnum;
import org.move.fast.common.utils.Log;
import org.move.fast.module.entity.auto.SysConf;
import org.move.fast.module.entity.auto.VpnUser;
import org.move.fast.module.mapper.auto.SysConfMapper;
import org.move.fast.module.mapper.auto.VpnUserMapper;
import org.move.fast.module.service.RssService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * vpn定时任务处理类
 */
@Service
public class VpnSchService {

    @Resource
    VpnUserMapper vpnUserMapper;

    @Resource
    RssService rssService;

    @Resource
    SysConfMapper sysConfMapper;

    /**
     * 补偿机制去重新获取RssUrl
     */
    @Scheduled(cron = "0 10 0 * * ?")
    public void makeUpForRssUrl() {

        DateTime now = DateTime.now();
        Log.printAndWrite("开始执行VpnService.makeUpForRssUrl定时任务,时间为" + now, this.getClass());

        String which = sysConfMapper.selectOne(new LambdaQueryWrapper<SysConf>()
                .eq(SysConf::getConfKey, SysConfKeyEnum.vpn_rss_which.name())).getConfVal();

        List<VpnUser> vpnUsers = vpnUserMapper.selectList(new LambdaQueryWrapper<VpnUser>()
                .isNotNull(VpnUser::getRssUrl).ne(VpnUser::getRssUrl, "").eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal));

        if (!CollectionUtils.isEmpty(vpnUsers)) {

            for (VpnUser vpnUser : vpnUsers) {

                String cookie = DaBai.login(vpnUser);

                if (StrUtil.isBlank(cookie)) {
                    continue;
                }

                vpnUser.setRssUrl(rssService.getAllRssUrlAndInsertVmess(vpnUser.getId(), DaBai.takeRssUrl(cookie), Integer.parseInt(which)));
                vpnUser.setUpdDate(LocalDateTime.now());
                vpnUserMapper.updateById(vpnUser);

            }
        }

        Log.printAndWrite("完成执行VpnService.makeUpForRssUrl定时任务,处理了" + vpnUsers.size() + "条数据,耗时为" + DateUtil.between(now, DateTime.now(), DateUnit.MS) + "ms.", this.getClass());
    }

    @Scheduled(cron = "0 30 0 * * ?")
    public void expire() {

        DateTime now = DateTime.now();
        Log.printAndWrite("开始执行VpnService.expire定时任务,时间为" + now, this.getClass());
        DateTime lastMonth = DateUtil.lastMonth();

        int count = Integer.parseInt(String.valueOf(vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>()
                .select("COUNT(*) as num").lambda().lt(VpnUser::getLastBuyTime, lastMonth)
                .eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey())).get(0).get("num")));

        if (count != 0) {
            //分页查询(一次1000条)
            int pageSize = count / 1000 + 1;

            for (int i = 1; i <= pageSize; i++) {

                Page<VpnUser> vpnUserPage = new Page<>(i, 1000);

                Page<VpnUser> vpnUsers = vpnUserMapper.selectPage(vpnUserPage, new LambdaQueryWrapper<VpnUser>()
                        .lt(VpnUser::getLastBuyTime, lastMonth).eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_normal.getKey()));

                for (VpnUser vpnUser : vpnUsers.getRecords()) {
                    //账号过期
                    vpnUser.setUpdDate(LocalDateTime.now());
                    vpnUser.setStatus(DBFieldEnum.vpn_user_status_expire.getKey());
                    vpnUserMapper.updateById(vpnUser);
                }
            }
        }

        Log.printAndWrite("完成执行VpnService.expire定时任务,处理了" + count + "条数据,耗时为" + DateUtil.between(now, DateTime.now(), DateUnit.MS) + "ms.", this.getClass());
    }

    @Scheduled(cron = "0 30 2 * * ?")
    public void buy() {

        DateTime now = DateTime.now();
        Log.printAndWrite("开始执行VpnService.buy定时任务,时间为" + now, this.getClass());
        DateTime lastMonth = DateUtil.lastMonth();

        int count = Integer.parseInt(String.valueOf(vpnUserMapper.selectMaps(new QueryWrapper<VpnUser>().select("COUNT(*) as num").lambda()
                .eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_expire.getKey())).get(0).get("num")));

        if (count != 0) {
            //分页查询(一次1000条)
            int pageSize = count / 1000 + 1;

            for (int i = 1; i <= pageSize; i++) {

                Page<VpnUser> vpnUserPage = new Page<>(i, 1000);

                Page<VpnUser> vpnUsers = vpnUserMapper.selectPage(vpnUserPage, new LambdaQueryWrapper<VpnUser>()
                        .eq(VpnUser::getStatus, DBFieldEnum.vpn_user_status_expire.getKey()));

                for (VpnUser vpnUser : vpnUsers.getRecords()) {

                    if (LocalDateTimeUtil.betweenPeriod(vpnUser.getLastBuyTime(), LocalDate.from(LocalDateTimeUtil.of(lastMonth))).getDays() <= 3) {
                        String cookie = DaBai.login(vpnUser);
                        if (StrUtil.isBlank(cookie)) {
                            continue;
                        }
                        if (DaBai.buy(cookie, vpnUser)) {
                            //账号恢复正常
                            vpnUser.setStatus(DBFieldEnum.vpn_user_status_normal.getKey());
                            vpnUser.setUpdDate(LocalDateTime.now());
                            vpnUserMapper.updateById(vpnUser);
                        }
                        continue;
                    }
                    //多次未购买成功的直接当作账号注销处理
                    vpnUser.setUpdDate(LocalDateTime.now());
                    vpnUser.setStatus(DBFieldEnum.vpn_user_status_logout.getKey());
                    vpnUserMapper.updateById(vpnUser);
                }
            }
        }

        Log.printAndWrite("完成执行VpnService.buy定时任务,处理了" + count + "条数据,耗时为" + DateUtil.between(now, DateTime.now(), DateUnit.MS) + "ms.", this.getClass());
    }

}
