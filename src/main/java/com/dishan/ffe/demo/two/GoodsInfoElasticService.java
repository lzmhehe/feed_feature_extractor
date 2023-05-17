package com.dishan.ffe.demo.two;

import java.util.List;

public interface GoodsInfoElasticService {
    GoodsInfo save(GoodsInfo goodsInfo);

    List<GoodsInfo> queryBuild(String keyword, String beginTime, String endTime, int page, int size);
}
