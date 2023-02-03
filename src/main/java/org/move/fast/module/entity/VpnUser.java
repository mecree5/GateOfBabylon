package org.move.fast.module.entity;

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
 * @author 代码瞬间移动工程师
 * @since 2023-02-03
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
     * 是否签到0-否1-是
     */
    private String isCheck;

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

    public String getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(String isCheck) {
        this.isCheck = isCheck;
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
                ", isCheck=" + isCheck +
                ", crtDate=" + crtDate +
                ", updDate=" + updDate +
                "}";
    }
}
