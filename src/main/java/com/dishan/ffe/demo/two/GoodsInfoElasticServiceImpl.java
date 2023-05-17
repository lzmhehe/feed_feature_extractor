package com.dishan.ffe.demo.two;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GoodsInfoElasticServiceImpl implements GoodsInfoElasticService {
    @Autowired
    ElasticSearchConfig client;
    @Autowired
    GoodsInfoRepository goodsInfoRepository;

    public String index = "goods_info";

    @Override
    public GoodsInfo save(GoodsInfo goodsInfo) {
        try {
            return goodsInfoRepository.save(goodsInfo);
        } catch (Exception e) {
            // es client 版本和 server不匹配造成的，暂时ignore
            return goodsInfo;
        }
    }

    /**
     * QueryBuilders.
     * 精确查询：term、terms 范围查询：range 模糊查询：wildcardQuery
     *      term ：单值查询 类似 =
     *      terms：多值查询，类似in
     *      range：范围查询 类似 between and
     *      wildcardQuery：模糊查询 like
     *
     * 匹配查询：match、multiMatch
     *      match：单字段匹配一个值 根据分词后的结果去查询，如果分词后的结果有命中一个，就会返回，模糊查询
     *      multiMatch：多字段匹配同一个值
     *
     *  复合查询 bool :must、must_not、should 、filter
     *      must：必须满足条件
     *      must_not：必须不满足条件
     *      should：应该满足条件
     *      filter：过滤，关闭评分，提高查询效率
     *
     *  以下聚合查询
     *      统计：max、min、sum、avg、count
     *          stats：一并获取max、min、sum、count、AVG统计
     *          extendedStats：追加方差、标准差等统计指标
     *          ardinality：去重
     *      分组：单字段分组、多字段分组、筛选后分组
     */

    /**
     * 1、SearchRequest 构建查询请求
     * 2、SearchSourceBuilder 构建请求体(查询条件)
     * QueryBuilders 查询条件 --> termQuery()、termsQuery()、rangeQuery() 范围查询 、wildcardQuery()通配符查询（支持* 匹配任何字符序列，包括空 避免*开始，会检索大量内容造成效率缓慢）、idsQuery()只根据id查询
     * 、fuzzyQuery()模糊查询、前缀匹配查询 prefixQuery("name","hello");
     * AggregateBuilders 聚合条件 --> min、max、sum、range、terms、stats、extendedStats
     * @return
     */
    @Override
    public List<GoodsInfo> queryBuild(String keyword, String beginTime, String endTime, int page, int size) {
        try {
            SearchRequest request = new SearchRequest(index);
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            //组建模糊查询 keyword：例 hello world 会拆分存储，加keyword不进行分词搜索
            if (StringUtils.isNotBlank(keyword)) {
                boolQueryBuilder.must(QueryBuilders.wildcardQuery("goodTitle.keyword", "*" + keyword + "*"));

                //多字段匹配 operator: or 表示 只要有一个词在文档中出现则就符合条件，and表示每个词都在文档中出现则才符合条件
//            boolQueryBuilder.must(QueryBuilders.multiMatchQuery("数据", "goodTitle","goodIntro").operator(Operator.OR));
            }
            //组建时间范围查询
            if (StringUtils.isNotBlank(beginTime) && StringUtils.isNotBlank(endTime)) {
                beginTime = StringUtils.join(beginTime, "000000");
                endTime = StringUtils.join(endTime, "999999");
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("time.keyword").gte(beginTime).lte(endTime));
            }
            searchSourceBuilder.query(boolQueryBuilder);
            searchSourceBuilder.from((page - 1) * size);
            searchSourceBuilder.size(size);

            //fetchSource(string[],string[])  第一个数组表明需要哪些字段，第二个是不需要哪些字段 类似 select id,name,age
            searchSourceBuilder.fetchSource(new String[]{"goodTitle", "goodId", "userId", "classId", "goodIntro"}, new String[]{});

            request.source(searchSourceBuilder);
            SearchResponse response = client.restHighLevelClient().search(request, RequestOptions.DEFAULT);
            //搜索结果
            SearchHits hits = response.getHits();
            // 匹配到的总记录数
            long totalHits = hits.getTotalHits().value;
            // 得到匹配度高的文档
            SearchHit[] searchHits = hits.getHits();

            List<GoodsInfo> goodsInfos = new ArrayList<>();
            if (null != searchHits && searchHits.length > 0) {
                for (SearchHit searchHit : searchHits) {
                    String sourceString = searchHit.getSourceAsString();
                    GoodsInfo goodsInfo = JSONObject.parseObject(sourceString, GoodsInfo.class);
                    goodsInfos.add(goodsInfo);
                }
            }
            // for (int i = 0; i < goodsInfos.size(); i++) {
            //     System.out.println(goodsInfos.get(i));
            // }
            return goodsInfos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
