package org.move.fast.module.entity.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author YinShiJie
 * @since 2023-02-15
 */
@TableName("vpn_vmess")
public class VpnVmess implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * vpn_user.id
     */
    private Integer userId;

    /**
     * vmess配置地址
     */
    private String vmessUrl;

    /**
     * 客户端类型(1-v2ray,2-kitsunebi,3-clash,4-shadowrocket,5-Quantumult,6-QuantumultX)
     */
    private String clientType;

    private LocalDateTime crtDate;

    private LocalDateTime updDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
    public String getVmessUrl() {
        return vmessUrl;
    }

    public void setVmessUrl(String vmessUrl) {
        this.vmessUrl = vmessUrl;
    }
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
    public LocalDateTime getCrtDate() {
        return crtDate;
    }

    public void setCrtDate(LocalDateTime crtDate) {
        this.crtDate = crtDate;
    }
    public LocalDateTime getUpdDate() {
        return updDate;
    }

    public void setUpdDate(LocalDateTime updDate) {
        this.updDate = updDate;
    }

    @Override
    public String toString() {
        return "VpnVmess{" +
            "id=" + id +
            ", userId=" + userId +
            ", vmessUrl=" + vmessUrl +
            ", clientType=" + clientType +
            ", crtDate=" + crtDate +
            ", updDate=" + updDate +
        "}";
    }
}
