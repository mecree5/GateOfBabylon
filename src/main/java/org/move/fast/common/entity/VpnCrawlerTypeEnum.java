package org.move.fast.common.entity;

public enum VpnCrawlerTypeEnum {

    TYPE_1("1", "直接返回base64加密配置"),

    ;

    private final String type;

    private final String remarks;

    VpnCrawlerTypeEnum(String type, String remarks) {
        this.type = type;
        this.remarks = remarks;
    }

    public String getType() {
        return type;
    }

    public String getRemarks() {
        return remarks;
    }
}
