package com.dishan.ffe.dao.mysql.entity;

import java.time.LocalDateTime;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class BaseEntity {

    /**
     * 自增主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    // /**
    //  * 创建时间
    //  */
    // @TableField("create_time")
    // @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    // @JsonFormat(
    //         pattern = "yyyy-MM-dd HH:mm:ss",
    //         timezone = "GMT+8"
    // )
    // private LocalDateTime createTime;
    //
    // /**
    //  * 更新时间
    //  */
    // @TableField("update_time")
    // @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    // @JsonFormat(
    //         pattern = "yyyy-MM-dd HH:mm:ss",
    //         timezone = "GMT+8"
    // )
    // private LocalDateTime updateTime;
    //
    // public void setGmtNow() {
    //     createTime = LocalDateTime.now();
    //     updateTime = LocalDateTime.now();
    // }

}
