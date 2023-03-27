package org.move.fast.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import org.move.fast.config.ReadConf;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class Log {

    private static final String LOG_ROOT_PATH = System.getProperty("user.dir") + "\\log\\";

    private static final int LOG_WRITE_GRADE;

    private static String LOG_PATH;

    private static String LAST_WRITE_TIME;

    static {
        //文件相关初始化
        String[] list = new File(LOG_ROOT_PATH).list();
        if (list != null && list.length > 0) {
            String fileName = list[list.length - 1];
            LAST_WRITE_TIME = fileName.substring(0, fileName.indexOf(".log"));
            LOG_PATH = LOG_ROOT_PATH + LAST_WRITE_TIME + ".log";
        } else {
            LAST_WRITE_TIME = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            LOG_PATH = LOG_ROOT_PATH + LAST_WRITE_TIME + ".log";
            FileUtil.touch(new File(LOG_PATH));
        }

        //写入等级初始化 默认为info
        LOG_WRITE_GRADE = Arrays.stream(Grade.values()).filter(s -> s.name().equalsIgnoreCase(ReadConf.getConfValue("gateOfBabylon.log.write-grade")))
                .findFirst().map(Grade::getCode).orElse(2);
    }

    public static <T> void debug(String targetStr, Class<T> clazz) {
        printAndWrite(Grade.DEBUG, getHeaderStr(Grade.DEBUG, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), clazz.getName()) + targetStr, null);
    }

    public static <T> void infoNotWrite(String targetStr, Class<T> clazz) {
        System.out.println(getHeaderStr(Grade.INFO, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), clazz.getName()) + targetStr);
    }

    public static <T> void info(String targetStr, Class<T> clazz) {
        printAndWrite(Grade.INFO, getHeaderStr(Grade.INFO, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), clazz.getName()) + targetStr, null);
    }

    public static <T> void infoPro(String targetStr, Class<T> clazz) {
        printAndWrite(Grade.INFO, getHeaderStr(Grade.INFO, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), clazz.getName()) + targetStr, Console.Color.SKYBLUE);
    }

    public static <T> void error(String targetStr, Class<T> clazz) {
        printAndWrite(Grade.ERROR, getHeaderStr(Grade.INFO, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")), clazz.getName()) + targetStr, Console.Color.RED);
    }

    public static void error(Exception exception) {

        StringBuilder log = new StringBuilder();
        log.append(exception.getClass().toString().substring(6)).append(":").append(exception.getMessage()).append("\r\n");

        StackTraceElement[] trace = exception.getStackTrace();

        for (StackTraceElement traceElement : trace) {
            log.append("    at  ").append(traceElement).append("\r\n");
        }

        Log.printAndWrite(Grade.ERROR, log.toString(), Console.Color.RED);
    }

    private static void printAndWrite(Grade grade, String targetStr, Console.Color color) {

        if (!isWrite(grade)) {
            return;
        }

        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String nowDate = now.replaceAll("-", "").substring(0, 8);

        if (!nowDate.equals(LAST_WRITE_TIME)) {
            LAST_WRITE_TIME = nowDate;
            LOG_PATH = LOG_ROOT_PATH + nowDate + ".log";
            FileUtil.touch(new File(LOG_PATH));
        }

        String printStr = targetStr;
        if (color != null) {
            printStr = Console.colorString(targetStr, color);
        }
        System.out.println(printStr);

        FileWriter writer = new FileWriter(LOG_PATH);
        writer.append(targetStr + "\r\n");
    }

    private static boolean isWrite(Grade grade) {
        return grade.getCode() >= LOG_WRITE_GRADE;
    }

    private static String getHeaderStr(Grade grade, String datetime, String clazzName) {
        return "[" + datetime + "]" + "[" + grade.name() + "]" + clazzName + "::";
    }

    public enum Grade {
        DEBUG(1),
        INFO(2),
        ERROR(3),
        ;
        private final int code;

        Grade(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

}
