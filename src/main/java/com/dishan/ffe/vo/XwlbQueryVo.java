package com.dishan.ffe.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XwlbQueryVo {

    String keyword;
    double score;
    String date;
    String highLight;
    String url;
}
