package org.move.fast.common.entity;

import org.move.fast.common.Exception.CustomerException;

import java.util.Arrays;
import java.util.Optional;

public enum ConfKeyEnum {

    vpn_rss_which,

    vpn_rss_repertory,
    ;

    public static boolean check(String str){
        Optional<ConfKeyEnum> first = Arrays.stream(ConfKeyEnum.values()).filter(s -> s.name().equals(str)).findFirst();
        return first.isPresent();
    }
}
