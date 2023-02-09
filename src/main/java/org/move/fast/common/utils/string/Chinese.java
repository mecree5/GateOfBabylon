package org.move.fast.common.utils.string;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : YinShiJie
 * @date : 2022/1/26 14:06
 */
public class Chinese {

    /**
     * @description: takeHanZi  拿出汉字
     * @author YinShiJie
     * @Param [str]
     * @Return java.util.List<java.lang.String>
     * @date 2022/2/10 10:10
     */
    public static List<String> takeHanZi(String str) {
        //加上了括号的正则，防止出现用括号释义的存在
        Pattern pattern = Pattern.compile("[\\u4e00-\\u9fa5\\uff08-\\uff09]+");
        Matcher m = pattern.matcher(str);
        List<String> urls = new ArrayList<>();
        while (m.find()) {
            //去除括号，防止生成sql报错
            String group = m.group();
            if (group.contains("("))
                urls.add(clearBracket(group, '(', ')'));
        }
        return urls;
    }

    /**
     * 去除两符号间内容
     *
     * @param context
     * @param left
     * @param right
     * @return String
     */
    private static String clearBracket(String context, char left, char right) {
        int head = context.indexOf(left);
        if (head == -1) {
            return context;
        } else {
            int next = head + 1;
            int count = 1;
            do {
                if (context.charAt(next) == left) {
                    count++;
                } else if (context.charAt(next) == right) {
                    count--;
                }
                next++;
                if (count == 0) {
                    String temp = context.substring(head, next);
                    context = context.replace(temp, "");
                    head = context.indexOf(left);
                    next = head + 1;
                    count = 1;
                }
            } while (head != -1);
        }
        return context;
    }

    public static String replace(String str) // 识别括号并将括号内容替换的函数
    {
        int head = str.indexOf('('); // 标记第一个使用左括号的位置
        if (head != -1) {
            int next = head + 1; // 从head+1起检查每个字符
            int count = 1; // 记录括号情况
            do {
                if (str.charAt(next) == '(')
                    count++;
                else if (str.charAt(next) == ')')
                    count--;
                next++; // 更新即将读取的下一个字符的位置
                if (count == 0) // 已经找到匹配的括号
                {
                    String temp = str.substring(head, next); // 将两括号之间的内容及括号提取到temp中
                    str = str.replace(temp, ""); // 用空内容替换，复制给str
                    head = str.indexOf('('); // 找寻下一个左括号
                    next = head + 1; // 标记下一个左括号后的字符位置
                    count = 1; // count的值还原成1
                }
            } while (head != -1); // 如果在该段落中找不到左括号了，就终止循环
        }
        return str; // 返回更新后的str
    }

}
