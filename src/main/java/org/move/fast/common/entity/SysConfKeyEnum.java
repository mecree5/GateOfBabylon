package org.move.fast.common.entity;

import java.util.Arrays;

public enum SysConfKeyEnum {

    vpn_rss_which,

    vpn_rss_repertory,

    vpn_rss_down_num,
    ;

    public static boolean check(String str) {
        return Arrays.stream(SysConfKeyEnum.values()).anyMatch(s -> s.name().equalsIgnoreCase(str));
    }
}
