package com.dishan.ffe.dao.mysql.entity;

import java.io.IOException;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import sun.misc.BASE64Decoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("xwlb_text")
public class XwlbText extends BaseEntity {
    private static final BASE64Decoder DECODER = new BASE64Decoder();
    private Long id;
    private String title;
    private Long date;
    private String summary;
    private String content;
    private String url;
    private Long timeCreated;
    private Long timeUpdated;
    private String segmented;

    @TableField(exist = false)
    private String summaryText;
    @TableField(exist = false)
    private String contentText;
    @TableField(exist = false)
    private String urlText;

    public String getSummaryText() {
        try {
            return new String(DECODER.decodeBuffer(summary));
        } catch (IOException e) {
            e.printStackTrace();
            return summary;
        }
    }

    public String getContentText() {
        try {
            return new String(DECODER.decodeBuffer(content));
        } catch (IOException e) {
            e.printStackTrace();
            return content;
        }
    }

    public String getUrlText() {
        try {
            return new String(DECODER.decodeBuffer(url));
        } catch (IOException e) {
            e.printStackTrace();
            return url;
        }
    }
}