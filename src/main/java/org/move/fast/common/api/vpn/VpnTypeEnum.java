package org.move.fast.common.api.vpn;

import org.move.fast.common.Exception.CustomerException;
import org.move.fast.common.entity.RetCodeEnum;

import java.util.Arrays;
import java.util.Optional;

public enum VpnTypeEnum {

    client_v2ray("1", "v2ray"),
    client_kitsunebi("2", "kitsunebi"),
    //    client_clash("3", "clash"),
    client_shadowrocket("4", "shadowrocket"),
    client_Quantumult("5", "Quantumult"),
    client_QuantumultX("6", "QuantumultX"),
    ;

    private final String type;

    private final String name;

    VpnTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String checkTypeAndGetName(String type) {
        Optional<VpnTypeEnum> first = Arrays.stream(VpnTypeEnum.values()).filter(s -> s.getType().equals(type)).findFirst();
        if (first.isPresent()) {
            return first.get().getName();
        }
        throw new CustomerException(RetCodeEnum.validated_error);
    }

    public static String checkNameAndGetType(String name) {
        Optional<VpnTypeEnum> first = Arrays.stream(VpnTypeEnum.values()).filter(s -> s.getName().equalsIgnoreCase(name)).findFirst();
        if (first.isPresent()) {
            return first.get().getType();
        }
        throw new CustomerException(RetCodeEnum.validated_error);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

}
