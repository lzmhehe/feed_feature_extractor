package com.dishan.ffe.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dishan.ffe.dao.es.XwlbES;
import com.dishan.ffe.dao.es.repository.XwlbESRepository;
import com.dishan.ffe.dao.mysql.entity.XwlbText;
import com.dishan.ffe.dao.mysql.mapper.XwlbTextMapper;
import com.dishan.ffe.demo.two.ElasticSearchConfig;
import com.dishan.ffe.demo.two.GoodsInfo;
import com.dishan.ffe.demo.two.GoodsInfoElasticService;
import com.dishan.ffe.vo.XwlbQueryVo;
import com.github.tusharepro.core.entity.StockBasicEntity;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.wltea.analyzer.lucene.IKAnalyzer;

@RestController
public class TestController {
    @Autowired
    EsService esService;
    @Autowired
    GoodsInfoElasticService goodsInfoElasticService;
    @Autowired
    XwlbTextMapper xwlbTextMapper;
    @Autowired
    ElasticSearchConfig elasticSearchConfig;
    @Autowired
    XwlbESRepository xwlbESRepository;

    @RequestMapping(value = "/test/get")
    public String getRec() throws Exception {
        try {
            //调用Service完成搜索
            List<Hotel> hotelList = esService.getHotelFromTitle("再来");
            if (hotelList != null && hotelList.size() > 0) {  //搜索到结果打印到前端
                return hotelList.toString();
            } else {
                return "no data.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/test/add")
    public String add() throws Exception {
        try {
            //调用Service完成搜索
            Hotel hotel1 = new Hotel();
            hotel1.setCity("北京");
            hotel1.setId(UUID.randomUUID().toString());
            hotel1.setTitle("欢迎再来");
            int i = RandomUtils.nextInt(100, 500);
            hotel1.setPrice(i + "");
            Hotel hotel = esService.addHotel(hotel1);
            return JSON.toJSONString(hotel);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/test/v2/get")
    public String getGoodsInfo(@RequestParam String keyword) throws Exception {
        try {
            List<GoodsInfo> goodsInfos = goodsInfoElasticService.queryBuild(keyword, "2023-01-01", "2023-10-10", 0, 100);
            //调用Service完成搜索
            if (CollectionUtils.isNotEmpty(goodsInfos)) {
                return JSON.toJSONString(goodsInfos);
            } else {
                return "no data.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/test/v2/add")
    public String addGoodsInfo() throws Exception {
        try {
            Date date = new Date();
            date = DateUtils.truncate(date, Calendar.DATE);
            date = DateUtils.addDays(date, RandomUtils.nextInt(0, 9) * -1);
            GoodsInfo goodsInfo = GoodsInfo.builder()
                    .goodId(UUID.randomUUID().toString())
                    .time(DateFormatUtils.format(date, "yyyy-MM-dd"))
                    .goodAddress("goodAddress")
                    .goodImg("goodImg")
                    .goodIntro("中韩渔警冲突调查：韩警平均每天扣1艘中国渔船")
                    .goodTitle("中韩渔警冲突调查：韩警平均每天扣1艘中国渔船")
                    .classId("classId")
                    .sts(1).price(0.2d).userId("user_id").build();

            goodsInfo = goodsInfoElasticService.save(goodsInfo);
            return JSON.toJSONString(goodsInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/test/loadXwlb")
    public String loadXwlb() {
        try {
            // 提前 公司名称
            List<StockBasicEntity> stockBasicEntities = loadStock();
            Set<String> companySet = stockBasicEntities.stream().map(item -> {
                Set<String> set = Sets.newHashSet();
                // set.add(item.getName());
                String fullname = item.getFullname();
                String digest = digestCompanyName(fullname);
                set.add(digest);
                return set;
            }).flatMap(set -> set.stream()).collect(Collectors.toSet());

            companySet.stream().forEach(System.out::println);
            System.out.println("companySet size:" + companySet.size());

            // 拉取 xwlb 数据
            QueryWrapper wrapper = new QueryWrapper<>();
            wrapper.gt("DATE", 1);
            List<XwlbText> list = xwlbTextMapper.selectList(wrapper);

            System.out.println("xwlb size:" + list.size());
            List<String> ret = Lists.newArrayList();
            // 分词
            for (XwlbText xwlbText : list) {
                String contentText = xwlbText.getContentText();
                // 创建IK分词器对象
                Analyzer analyzer = new IKAnalyzer();
                Set<String> tokens = Sets.newHashSet();
                TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(contentText));
                CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
                tokenStream.reset();
                while (tokenStream.incrementToken()) {
                    tokens.add(termAttribute.toString());
                }
                // 关闭分词器
                analyzer.close();

                List<String> matchComp = contentTextContain(tokens, companySet);
                if (CollectionUtils.isNotEmpty(matchComp)) {
                    // 将时间戳转换为Instant对象
                    Instant instant = Instant.ofEpochMilli(xwlbText.getDate());
                    // 将Instant对象转换为LocalDate对象
                    LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();
                    String format = String.format("%s,%s,%s", matchComp, xwlbText.getUrlText(), localDate);
                    ret.add(format);
                    System.out.println(format);
                }
            }

            return ret.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {

        }
    }

    @RequestMapping(value = "/test/getEsIndexProperties")
    public String getEsIndexProperties(@RequestParam String index) {

        try {
            // 创建RestHighLevelClient客户端
            RestHighLevelClient client = elasticSearchConfig.restHighLevelClient();
            // 准备获取映射请求
            GetMappingsRequest request = new GetMappingsRequest().indices(index);

            // 执行获取映射请求
            GetMappingsResponse response = client.indices().getMapping(request, RequestOptions.DEFAULT);

            // 获取索引的映射信息
            ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = response.mappings();
            ImmutableOpenMap<String, MappingMetaData> mapping = mappings.get(index);

            // 输出索引的属性信息
            // Map<String, Object> properties = mapping.getSourceAsMap();
            System.out.println(JSON.toJSONString(mapping));

            // 关闭RestHighLevelClient客户端
            client.close();
            return JSON.toJSONString(mapping);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }


    @RequestMapping(value = "/test/xwlb2es")
    public String xwlb2es() {
        try {
            // 拉取 xwlb 数据
            QueryWrapper wrapper = new QueryWrapper<>();
            wrapper.gt("DATE", 1);
            List<XwlbText> list = xwlbTextMapper.selectList(wrapper);
            System.out.println("xwlb size:" + list.size());

            List<XwlbES> collect = list.stream().map(item -> {
                Instant instant = Instant.ofEpochMilli(item.getDate());
                // 将Instant对象转换为LocalDate对象
                LocalDate localDate = instant.atZone(ZoneId.systemDefault()).toLocalDate();

                XwlbES build = XwlbES.builder()
                        .id(item.getId() + "")
                        .content(item.getContentText())
                        .summary(item.getSummaryText())
                        .url(item.getUrlText())
                        .date(localDate.toString()).build();
                return build;
            }).collect(Collectors.toList());

            xwlbESRepository.saveAll(collect);
            return collect.size() + "";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        } finally {

        }
    }

    @RequestMapping(value = "/test/lookup")
    public Map<String, List<XwlbQueryVo>> lookup() {
        try {
            List<StockBasicEntity> stockBasicEntities = loadStock();
            Map<String, List<XwlbQueryVo>> ret = Maps.newHashMap();
            int i = 1;
            for (StockBasicEntity stockBasicEntity : stockBasicEntities) {

                String fullname = stockBasicEntity.getFullname();
                fullname = digestCompanyName(fullname);
                List<XwlbQueryVo> xwlb = searchByEs("xwlb", fullname);
                if (CollectionUtils.isNotEmpty(xwlb)) {
                    ret.put(stockBasicEntity.getFullname(), xwlb);
                }
                System.out.println(i + "\tdone:" + stockBasicEntity.getFullname());
                i++;
            }
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return Maps.newHashMap();
        }
    }

    public List<XwlbQueryVo> searchByEs(String index, String key) {
        RestHighLevelClient client = elasticSearchConfig.restHighLevelClient();
        try {

            // 创建查询请求
            SearchRequest searchRequest = new SearchRequest(index);
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

            // 设置查询条件
            sourceBuilder.query(QueryBuilders.matchQuery("content", key));

            // 设置高亮显示
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("content");
            sourceBuilder.highlighter(highlightBuilder);

            // 设置过滤规则和排序
            sourceBuilder.postFilter(QueryBuilders.existsQuery("content"));
            sourceBuilder.sort("_score", SortOrder.DESC);

            // 设置最大返回结果数
            sourceBuilder.size(10);

            // 将查询请求配置到搜索源中
            searchRequest.source(sourceBuilder);

            // 执行搜索请求
            SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

            // 处理搜索结果
            SearchHit[] hits = searchResponse.getHits().getHits();
            List<XwlbQueryVo> ret = Lists.newArrayList();
            for (SearchHit hit : hits) {
                if (hit.getScore() < 10) {
                    continue;
                }
                // 获取文档数据
                Map<String, Object> sourceAsMap = hit.getSourceAsMap();

                // 获取高亮显示的结果
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField contentHighlight = highlightFields.get("content");
                String highlightedContent = "";
                if (contentHighlight == null) {
                    continue;
                }
                if (contentHighlight != null) {
                    highlightedContent = contentHighlight.fragments()[0].string();
                }
                XwlbQueryVo vo = XwlbQueryVo.builder()
                        .highLight(highlightedContent)
                        .keyword(key)
                        .score(hit.getScore())
                        .date(sourceAsMap.get("date").toString())
                        .url(sourceAsMap.get("url").toString())
                        .build();
                ret.add(vo);
                // 输出结果
                // System.out.println("Highlighted Content: " + highlightedContent);
                // System.out.println("Source Data: " + sourceAsMap);
                // System.out.println("------------------------------------------------------");
            }

            return ret;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    private List<String> contentTextContain(Set<String> tokens, Set<String> companySet) {
        List<String> collect = companySet.stream().filter(item -> {
                    return tokens.contains(item);
                }
        ).collect(Collectors.toList());
        return collect;
    }

    private String digestCompanyName(String fullname) {
        List<String> list = Lists.newArrayList("公司", "有限", "股份", "责任");
        for (String s : list) {
            fullname = fullname.replace(s, "");
        }
        return fullname;
    }

    @SneakyThrows
    public List<StockBasicEntity> loadStock() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("data/company.csv");
        List<String> strings = IOUtils.readLines(inputStream);
        List<StockBasicEntity> collect = strings.stream()
                .filter(StringUtils::isNotBlank)
                .map(item -> {
                    String[] split = item.split(",");
                    StockBasicEntity stockBasicEntity = new StockBasicEntity();
                    stockBasicEntity.setTsCode(split[1].trim());
                    stockBasicEntity.setName(split[2].trim());
                    stockBasicEntity.setFullname(split[3].trim());
                    return stockBasicEntity;
                }).collect(Collectors.toList());
        return collect;
    }
}