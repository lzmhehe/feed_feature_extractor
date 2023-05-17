package com.dishan.ffe.demo.two;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class ElasticSearchConfig {
	//请求地址 http://ip
    @Value("${elasticsearch.url}")
    private String url;
    //索引
    // @Value("${spring.elasticsearch.index}")
    // private String index;
	//请求端口 9200
    @Value("${elasticsearch.port}")
    private int port;

    @Bean
    public RestHighLevelClient  restHighLevelClient() {
        RestHighLevelClient  client = new RestHighLevelClient(
                RestClient.builder(
                		//http://127.0.0.1:9200
                        new HttpHost(url,port)
                )
        );
        return client;
    }
}

