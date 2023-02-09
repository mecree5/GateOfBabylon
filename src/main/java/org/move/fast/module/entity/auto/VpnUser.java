package org.move.fast.module.entity.auto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 *
 * </p>
 *
 * @author YinShiJie
 * @since 2023-02-08
 */
@TableName("vpn_user")
public class VpnUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String email;

    private String password;

    /**
     * 订阅地址
     */
    private String rssUrl;

    /**
     * 状态（0-已注销，1-正常， 2-过期需购买）
     */
    private String status;

    /**
     * 上次更新时获取的第几个节点
     */
    private String lastUpdRssWhich;

    /**
     * 上次使用时间
     */
    private LocalDate lastUsedDate;

    /**
     * 上次签到时间
     */
    private LocalDate lastCheckDate;

    /**
     * 上次购买时间
     */
    private LocalDate lastBuyTime;

    private LocalDateTime crtDate;

    private LocalDateTime updDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRssUrl() {
        return rssUrl;
    }

    public void setRssUrl(String rssUrl) {
        this.rssUrl = rssUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdRssWhich() {
        return lastUpdRssWhich;
    }

    public void setLastUpdRssWhich(String lastUpdRssWhich) {
        this.lastUpdRssWhich = lastUpdRssWhich;
    }

    public LocalDate getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(LocalDate lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }

    public LocalDate getLastCheckDate() {
        return lastCheckDate;
    }

    public void setLastCheckDate(LocalDate lastCheckDate) {
        this.lastCheckDate = lastCheckDate;
    }

    public LocalDate getLastBuyTime() {
        return lastBuyTime;
    }

    public void setLastBuyTime(LocalDate lastBuyTime) {
        this.lastBuyTime = lastBuyTime;
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
        return "VpnUser{" +
                "id=" + id +
                ", email=" + email +
                ", password=" + password +
                ", rssUrl=" + rssUrl +
                ", status=" + status +
                ", lastUpdRssWhich=" + lastUpdRssWhich +
                ", lastUsedDate=" + lastUsedDate +
                ", lastCheckDate=" + lastCheckDate +
                ", lastBuyTime=" + lastBuyTime +
                ", crtDate=" + crtDate +
                ", updDate=" + updDate +
                "}";
    }
}
