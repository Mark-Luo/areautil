package com.area.areaUtil.po;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 地区表。
 *
 * @author guohao.yang
 * @version v1.0.0
 */
@Data
@Accessors(chain = true)
@TableName("sys_area")
public class TSysArea extends Model<TSysArea> {

    private static final long serialVersionUID = 1L;

    /**
     * id。
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 区域行政编码。
     */
    @TableField("area_code")
    private String areaCode;

    /**
     * 区域行政名称。
     */
    @TableField("area_value")
    private String areaValue;

    /**
     * 上级地区编码。
     */
    @TableField("parent_code")
    private String parentCode;

    /**
     * 层级。
     */
    @TableField("area_level")
    private Integer areaLevel;

    /**
     * 创建时间。
     */
    @TableField("gmt_create")
    private Date gmtCreate;

    /**
     * 修改时间。
     */
    @TableField("gmt_modified")
    private Date gmtModified;

    /**
     * 创建人。
     */
    @TableField("create_id")
    private Integer createId;

    /**
     * 修改人。
     */
    @TableField("modified_id")
    private Integer modifiedId;

    /**
     * 备注。
     */
    @TableField("note")
    private String note;

    /**
     * 版本。
     */
    @TableField("version")
    private Integer version;

    /**
     * 0：正常 1：已删除。
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 1：正常 0：不可用。
     */
    @TableField("is_can_use")
    private Integer isCanUse;
    
    @Override
    protected Serializable pkVal() {
        return this.id;

    }

}