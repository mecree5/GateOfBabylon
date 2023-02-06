package org.move.fast.common.entity;

public enum VpnEnum {

    client_v2ray("1", "v2ray"),
    client_kitsunebi("2", "kitsunebi"),
    client_clash("3", "clash"),
    client_shadowrocket("4", "shadowrocket"),
    client_Quantumult("5", "Quantumult"),
    client_QuantumultX("6", "QuantumultX"),

    setting_rss_num("vpn_rss_num","")
    ;


    private VpnEnum(String key, String value){
        this.key = key;
        this.value = value;
    }

    private String key = null;

    private String value = null;

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getValue(String key){
        for (VpnEnum o : VpnEnum.values()) {
            if (o.getKey().equals(key)){
                return o.getValue();
            }
        }
        return null;
    }


}
