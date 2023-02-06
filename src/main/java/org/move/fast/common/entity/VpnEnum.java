package org.move.fast.common.entity;

import org.move.fast.common.Exception.CustomerException;

import java.util.Arrays;
import java.util.Optional;

public enum VpnEnum {

    client_v2ray("1", "v2ray"),
    client_kitsunebi("2", "kitsunebi"),
    client_clash("3", "clash"),
    client_shadowrocket("4", "shadowrocket"),
    client_Quantumult("5", "Quantumult"),
    client_QuantumultX("6", "QuantumultX"),

    setting_vpn_rss_which("vpn_rss_which",""),
    setting_vpn_rss_repertory("vpn_rss_repertory", "")
    ;

    public static final String prefix_client = "client";

    public static final String prefix_setting = "setting";

    private final String key;

    private final String value;

    VpnEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public static String getKey(String key, String prefix){
        Optional<VpnEnum> first = Arrays.stream(VpnEnum.values()).filter(s -> s.name().startsWith(prefix)).filter(s -> s.getKey().equals(key)).findFirst();
        if (first.isPresent()){
           return first.get().getKey();
        }
        throw new CustomerException(RetCodeEnum.validated_error);
    }

    public static String getValue(String value, String prefix){
        Optional<VpnEnum> first = Arrays.stream(VpnEnum.values()).filter(s -> s.name().startsWith(prefix)).filter(s -> s.getValue().equals(value)).findFirst();
        if (first.isPresent()){
            return first.get().getValue();
        }
        throw new CustomerException(RetCodeEnum.validated_error);
    }

}
