package com.dishan.ffe.demo.two;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(indexName = "goods_info", shards = 5, replicas = 1)// myindex 必须全小写
public class GoodsInfo implements Serializable {
    /**
     * @Id 主键id
     * String 分两种：text 可分词，不参加聚合、keyword，不可分词，数据会根据完整匹配，可参与聚合
     * ik_max_word ：会对文本进行最细粒度的拆分，
     * ik_smart：会对文本进行最粗粒度的拆分
     * 索引时使用ik_max_word、搜索时使用ik_smart
     * 分词器：analyzer  1、插入文档时，将text类型字段做分词，然后插入倒排索引。2、在查询时，先对text类型输入做分词，再去倒排索引搜索
     * 分词器：如果想要“索引”和“查询”， 使用不同的分词器，那么 只需要在字段上 使用 search_analyzer。这样，索引只看 analyzer，查询就看 search_analyzer。
     * 此外，如果没有定义，就看有没有 analyzer，再没有就去使用 ES 预设。
     */

    @Id
    private String goodId;

    @Field(type = FieldType.Keyword)
    private String userId;
    @Field(type = FieldType.Keyword)
    private String classId;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String goodTitle;
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String goodIntro;
    @Field(type = FieldType.Keyword)
    private String goodImg;
    @Field(type = FieldType.Keyword)
    private String goodAddress;
    @Field(type = FieldType.Double)
    private Double price;
    @Field(type = FieldType.Integer)
    private int sts;
    @Field(type = FieldType.Date, format = DateFormat.custom, pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private String time;

}
