package org.move.fast.common.entity;

import java.util.Arrays;

public enum ConfKeyEnum {

    vpn_rss_which,

    vpn_rss_repertory,
    ;

    public static boolean check(String str) {
        return Arrays.stream(ConfKeyEnum.values()).anyMatch(s -> s.name().equals(str));
    }
}
