package org.move.fast.common.utils;

/**
 * @author : YinShiJie
 * @date : 2022/1/19 14:08
 */
public class Console {

    /**
     * @param content 要打印的内容
     * @param color  颜色代号：背景颜色代号(41-46)；前景色代号(31-36)
     * @param style    样式代号：0-无；1-加粗；3-斜体；4-下划线
     */
    public static String colorString(String content, Color color, Style style) {
        boolean hasType = style.getCode() != 1 && style.getCode() != 3 && style.getCode() != 4;
        if (hasType) {
            return String.format("\033[%dm%s\033[0m", color.getCode(), content);
        } else {
            return String.format("\033[%d;%dm%s\033[0m", color.getCode(), style.getCode(), content);
        }
    }

    public static String colorString(String content, Color color) {
        return colorString(content, color, Style.NORMAL);
    }

    public enum Color {
        RED(31),
        GREEN(32),
        YELLOW(33),
        BLUE(34),
        PURPLE(35),
        SKYBLUE(36),
        ;
        private final int code;

        Color(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    public enum Style {
        NORMAL(0),
        BOLD(1),
        ITALIC(3),
        UNDERLINE(4),
        ;
        private final int code;

        Style(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
