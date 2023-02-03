package org.move.fast.common.utils.string;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;
import org.move.fast.common.utils.CmdColour;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description: DealEnglish 通过导入 stanford 实现
 * @author: YinShiJie
 * @create: 2022-02-14 12:41
 **/
public class DealEnglish {

    /*/**
     * @description: dealWord
     * @author YinShiJie
     * @Param [word]
     * @Return java.lang.String
     * @date 2022/2/14 12:31
     */
    public static String dealWord(String txtWord) {
        String result = null;
        Properties props = new Properties();
        props.put("annotators", "tokenize,ssplit,pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        Annotation document = new Annotation(txtWord);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                result = token.get(CoreAnnotations.LemmaAnnotation.class);
            }
        }
        return result;
    }


    /*/**
     * @description: dealWords 返回带下划线分割的字符串 如：xx_xx_xx
     * @author YinShiJie
     * @Param [words]
     * @Return java.lang.String
     * @date 2022/2/14 13:03
     */
    public static String dealWords(String words) {
        StringBuilder dealWord = new StringBuilder();
        String[] listWord = check(words);
        for (String s : listWord) {
            dealWord.append(dealWord(s)).append("_");
        }
        return dealWord.substring(0, dealWord.toString().length() - 1).trim();
    }

    /*/**
     * @description: dealListWords
     * @author YinShiJie
     * @Param [listWords]
     * @Return java.util.List<java.lang.String>
     * @date 2022/2/14 13:03
     */
    public static List<String> dealListWords(List<String> listWords) {
        List<String> dealWords = new ArrayList<>();
        for (String words : listWords) {
            String[] listWord = check(words);
            StringBuilder dealWord = new StringBuilder();
            for (String word : listWord) {
                dealWord.append(dealWord(word));
            }
            dealWords.add(dealWord.toString());
        }
        return dealWords;
    }

    /*/**
     * @description: check 只能有一种分隔符  " "或者"_"否则抛出异常
     * @author YinShiJie
     * @Param [words]
     * @Return java.lang.String[]
     * @date 2022/2/14 13:47
     */
    private static String[] check(String words) {
        if (words.contains("_") && words.contains(" ")) {
            System.out.println(CmdColour.getFormatLogString("----------------------由于:" + "单词格式有误" + "----------------------", 31, 1));
            try {
                throw new Exception();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        if (words.contains("_")) {
            return words.split("_");
        }
        if (words.contains(" ")) {
            return words.split(" ");
        }
        return new String[]{words};
    }

}
