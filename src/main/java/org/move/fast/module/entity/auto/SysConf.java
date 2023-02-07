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
 * @since 2023-02-07
 */
@TableName("sys_conf")
public class SysConf implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * key
     */
    private String confKey;

    /**
     * war
     */
    private String confVal;

    /**
     * 备注
     */
    private String confRemark;

    private LocalDateTime crtDate;

    private LocalDateTime updDate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getConfKey() {
        return confKey;
    }

    public void setConfKey(String confKey) {
        this.confKey = confKey;
    }
    public String getConfVal() {
        return confVal;
    }

    public void setConfVal(String confVal) {
        this.confVal = confVal;
    }
    public String getConfRemark() {
        return confRemark;
    }

    public void setConfRemark(String confRemark) {
        this.confRemark = confRemark;
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
        return "SysConf{" +
            "id=" + id +
            ", confKey=" + confKey +
            ", confVal=" + confVal +
            ", confRemark=" + confRemark +
            ", crtDate=" + crtDate +
            ", updDate=" + updDate +
        "}";
    }
}
