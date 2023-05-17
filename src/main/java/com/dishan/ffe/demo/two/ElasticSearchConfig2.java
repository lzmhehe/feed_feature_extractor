package com.dishan.ffe.demo.two;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author Liner
 * todo ES连接配置工具
 * @date 2021/12/9 21:36
 */
@Component
public class ElasticSearchConfig2 {

    @Value("${elasticsearch.url}")
    private String url;
    // @Value("${spring.elasticsearch.index}")
    // private String index;

    @Value("${elasticsearch.port}")
    private int port;

    public static ElasticsearchClient client;

    //http集群
    public ElasticsearchClient elasticsearchClient(){

//        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//        credentialsProvider.setCredentials(
//                AuthScope.ANY, new UsernamePasswordCredentials(account, passWord));//设置账号密码

        RestClientBuilder builder = RestClient.builder(new HttpHost(url,port));
//        RestClientBuilder builder = RestClient.builder(httpHosts)
//                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider));

        // Create the low-level client
        RestClient restClient = builder.build();
        // Create the transport with a Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper());
        // And create the API client
        client = new ElasticsearchClient(transport);//获取连接
        return client;
    }
}
