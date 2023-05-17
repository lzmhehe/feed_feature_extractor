package com.dishan.ffe.demo.two;

import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.*;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class ElasticSearchUtil {

    @Autowired
    ElasticSearchConfig2 client;


    /**
     * 创建索引
     *
     * @param index 索引名
     * @param type 分片
     * @param val
     * @return
     */
    public String createIndex(String index, String type, String val) {
        if (existsIndex(index)){
            throw new RuntimeException("索引已经存在");
        }
        CreateIndexResponse createResponse = null;
        try {
            createResponse = client.elasticsearchClient().indices().create(
                    new CreateIndexRequest.Builder()
                            .index(index)
//                            .aliases("foo",
//                                    new Alias.Builder().isWriteIndex(true).build()
//                            )
                            .build()
            );
            log.info(String.valueOf(createResponse));
            return index;
        } catch (Exception e) {
            throw new RuntimeException("创建索引失败，索引名：" + index + "  信息：" + createResponse);
        }
    }

    //DELETE /book
    // 删除索引
    public boolean deleteIndex(String index){
        if (!existsIndex(index)) {
            throw new RuntimeException("索引不存在");
        }
        try {
            //创建删除索引请求
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(index).build();
            //执行
            return client.elasticsearchClient().indices().delete(deleteIndexRequest).acknowledged();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("索引删除失败");
        }
    }

    /**
     * 判断索引是否存在
     *
     * @param index 索引
     * @return
     */
    public boolean existsIndex(String index) {
        boolean exists = false;
        try {
            exists = client.elasticsearchClient().indices().exists(new co.elastic.clients.elasticsearch.indices.ExistsRequest.Builder().index(index).build()).value();
            //exists = client.elasticsearchClient().indices().existsIndexTemplate(new ExistsIndexTemplateRequest.Builder().name(index).build()).value();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exists;
    }

    /**
     * 查询id数据是否存在，id为主键
     *
     * @param index 索引
     * @param id    主键id
     * @return
     */
    public boolean existsDocumentById(String index, String id) {
        if (!existsIndex(index)) {
            log.info("索引不存在：" + index);
            return false;
        }
        boolean exists = false;
        try {
            exists = client.elasticsearchClient().exists(new ExistsRequest.Builder().index(index).id(id).build()).value();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exists;
    }


    /**
     * 根据主键id删除文档
     *
     * @param index
     * @param id
     */
    public void deleteDocumentById(String index, String id) {
        if (!existsIndex(index)) {
            log.info("索引不存在：" + index);
            throw new RuntimeException("索引不存在：" + index);
        }
        try {
            if (existsDocumentById(index, id)) {
                DeleteResponse delete = client.elasticsearchClient().delete(new DeleteRequest.Builder().index(index).id(id).build());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据id获取文档
     *
     * @throws IOException
     */
    public <T> T getDocumentById(String index, String id, Class<T> clazz) throws IOException {
        // get /index/_doc/1
        GetRequest getRequest = new GetRequest.Builder().index(index).id(id).build();
        GetResponse<T> bookGetResponse = client.elasticsearchClient().get(getRequest, clazz);

        T result = bookGetResponse.source();
        return result;
    }

    /**
     * 根据id更新文档
     */
    public <T> void updateDocument(String index, String id, Class<T> tClass) throws IOException {
        UpdateResponse<T> personUpdateResponse = client.elasticsearchClient().update(
                new UpdateRequest.Builder<T, Object>()
                        .index(index)
                        .id(id)
                        .doc(tClass)
                        .build()
                , tClass);
        // 执行结果
        System.out.println(personUpdateResponse.result());
    }

    /**
     * 新增数据,如果索引下存在主键id数据则会替换
     *
     * @param index  索引
     * @param id     主键id
     * @param tClass 实体类class
     * @param <T>
     * @throws IOException
     */
    public <T> String createDocument(String index, String id, T tClass){
        IndexResponse indexResponse;
        try {
            // 创建添加文档的请求
            IndexRequest<T> indexRequest = new IndexRequest.Builder<T>().index(index).document(tClass).id(id).build();
            // 执行
            indexResponse = client.elasticsearchClient().index(indexRequest);
        }catch (Exception e){
            throw new RuntimeException("查询失败");
        }
        return indexResponse.id();
    }

    /**
     * 批量新增数据,暂时用不了，需要指定主键id，如果有指定的class可以循环遍历新增
     * @param index  索引
     * @param id    主键id
     * @param tClass      实体类class
     * @param <T>
     */
    public <T> void createDocumentAll(String index, String id, List<T> tClass){
        try {
            BulkRequest.Builder br = new BulkRequest.Builder();
            for (T product : tClass) {
                br.operations(op -> op
                        .index(idx -> idx
                                .index(index)
                                .id(id)
                                .document(product)
                        )
                );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
