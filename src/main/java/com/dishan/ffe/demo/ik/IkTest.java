package com.dishan.ffe.demo.ik;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class IkTest {
    public static void main(String[] args) throws IOException {
        String article = "IK分词器是一个开源的中文分词工具，可以对中文文本进行分词。";

        // 创建IK分词器对象
        Analyzer analyzer = new IKAnalyzer();

        // 分词
        List<String> tokens = new ArrayList<>();
        TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(article));
        CharTermAttribute termAttribute = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();

        while (tokenStream.incrementToken()) {
            tokens.add(termAttribute.toString());
        }

        // 输出分词结果
        System.out.println(tokens);

        // 关闭分词器
        analyzer.close();
    }

}
