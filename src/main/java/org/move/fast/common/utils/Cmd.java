package org.move.fast.common.utils;

/**
 * @author : YinShiJie
 * @date : 2022/1/19 14:08
 */
public class Cmd {

    /**
     * @param colour  颜色代号：背景颜色代号(41-46)；前景色代号(31-36)
     * @param type    样式代号：0-无；1-加粗；3-斜体；4-下划线
     * @param content 要打印的内容
     */
    public static String colorString(String content, int colour, int type) {
        boolean hasType = type != 1 && type != 3 && type != 4;
        if (hasType) {
            return String.format("\033[%dm%s\033[0m", colour, content);
        } else {
            return String.format("\033[%d;%dm%s\033[0m", colour, type, content);
        }
    }

//    public static void main(String[] args) {
//        System.out.println(getFormatLogString("[ 红色 ]", 31, 0));
//        System.out.println(getFormatLogString("[ 黄色 ]", 32, 0));
//        System.out.println(getFormatLogString("[ 橙色 ]", 33, 0));
//        System.out.println(getFormatLogString("[ 蓝色 ]", 34, 0));
//        System.out.println(getFormatLogString("[ 紫色 ]", 35, 0));
//        System.out.println(getFormatLogString("[ 绿色 ]", 36, 0));
//    }
}
