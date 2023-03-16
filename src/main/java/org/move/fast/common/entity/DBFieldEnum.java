package org.move.fast.common.entity;

public enum DBFieldEnum {

    //status 0-已注销，1-正常， 2-过期需购买
    vpn_user_status_logout("status", "0", "已注销"),
    vpn_user_status_normal("status", "1", "正常"),
    vpn_user_status_expire("status", "2", "过期需购买"),
    vpn_crawler_type_direct("crawler_type", "1", "直接返回base64加密配置"),
    ;

    private final String name;

    private final String key;

    private final String msg;

    DBFieldEnum(String name, String key, String msg) {
        this.name = name;
        this.key = key;
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getMsg() {
        return msg;
    }
}
