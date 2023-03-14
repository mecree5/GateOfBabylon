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
 * @since 2023-03-13
 */
@TableName("vpn_crawler")
public class VpnCrawler implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 客户端类型(1-v2ray,2-kitsunebi,3-clash,4-shadowrocket,5-Quantumult,6-QuantumultX)
     */
    private String clientType;

    /**
     * 爬取url
     */
    private String crawlerUrl;

    /**
     * 爬取类型（1-直接返回base64加密配置）
     */
    private String crawlerType;

    /**
     * 上次爬取时间
     */
    private LocalDate lastCrawlerDate;

    private LocalDateTime crtDate;

    private LocalDateTime updDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }
    public String getCrawlerUrl() {
        return crawlerUrl;
    }

    public void setCrawlerUrl(String crawlerUrl) {
        this.crawlerUrl = crawlerUrl;
    }
    public String getCrawlerType() {
        return crawlerType;
    }

    public void setCrawlerType(String crawlerType) {
        this.crawlerType = crawlerType;
    }
    public LocalDate getLastCrawlerDate() {
        return lastCrawlerDate;
    }

    public void setLastCrawlerDate(LocalDate lastCrawlerDate) {
        this.lastCrawlerDate = lastCrawlerDate;
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
        return "VpnCrawler{" +
            "id=" + id +
            ", clientType=" + clientType +
            ", crawlerUrl=" + crawlerUrl +
            ", crawlerType=" + crawlerType +
            ", lastCrawlerDate=" + lastCrawlerDate +
            ", crtDate=" + crtDate +
            ", updDate=" + updDate +
        "}";
    }
}
