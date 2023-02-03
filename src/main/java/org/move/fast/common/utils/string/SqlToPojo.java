package org.move.fast.common.utils.string;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : YinShiJie
 * @date : 2022/1/19 10:48
 */
public class SqlToPojo {
    /**
     * 下划线命名转驼峰命名
     *
     * @param underscore
     * @return
     */
    public static String underscoreToCamelCase(String underscore) {
        String[] ss = underscore.split("_");
        if (ss.length == 1) {
            return underscore;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(ss[0]);
        for (int i = 1; i < ss.length; i++) {
            sb.append(upperFirstCase(ss[i]));
        }

        return sb.toString();
    }

    /**
     * 驼峰 转下划线
     *
     * @param camelCase
     * @return
     */
    public static String toLine(String camelCase) {
        Pattern humpPattern = Pattern.compile("[A-Z]");
        Matcher matcher = humpPattern.matcher(camelCase);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * 首字母 转小写
     *
     * @param str
     * @return
     */
    private static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    /**
     * 首字母 转大写
     *
     * @param str
     * @return
     */
    private static String upperFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] -= 32;
        return String.valueOf(chars);
    }

}
