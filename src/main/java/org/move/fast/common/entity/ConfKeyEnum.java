package org.move.fast.common.entity;

import org.move.fast.common.Exception.CustomerException;

import java.util.Arrays;
import java.util.Optional;

public enum ConfKeyEnum {

    vpn_rss_which,

    vpn_rss_repertory,
    ;

    public static boolean check(String str){
        return Arrays.stream(ConfKeyEnum.values()).anyMatch(s -> s.name().equals(str));
    }
}
