package org.move.fast.common.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

public class Log {

    private static final String LOG_PATH = System.getProperty("user.dir") + "\\log\\";

    private static String LAST_WRITE_TIME;

    private static final List<String> exceptionMsgMaskList = Collections.singletonList(
            "Connection reset by peer".trim().toLowerCase() //客户端中断请求,tomcat报错,不影响业务
    );

    public enum Grade {
        INFO,
        DEBUG,
        ERROR,
    }

    static {
        String[] list = new File(LOG_PATH).list();
        if (list != null && list.length > 0) {
            String fileName = list[list.length - 1];
            LAST_WRITE_TIME = fileName.substring(0, fileName.indexOf(".txt"));
        }
    }

    public static void info(String targetStr, Class clazz) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "]"
                + "[" + Grade.INFO.name() + "]" + clazz.getName() + "::" + targetStr);
    }

    public static void debug(String targetStr, Class clazz) {
        System.out.println("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")) + "]"
                + "[" + Grade.DEBUG.name() + "]" + clazz.getName() + "::" + targetStr);
    }

    public static void printAndWrite(String targetStr, Class clazz) {
        printAndWrite(Grade.INFO, targetStr, clazz);
    }

    public static void printAndWrite(Grade grade, String targetStr, Class clazz) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String nowDate = now.replaceAll("-", "").substring(0, 8);
        String path = LOG_PATH + nowDate + ".txt";
        if (!nowDate.equals(LAST_WRITE_TIME)) {
            FileUtil.touch(new File(path));
            LAST_WRITE_TIME = nowDate;
        }
        FileWriter writer = new FileWriter(path);
        targetStr = "[" + now + "]" + "[" + grade.name() + "]" + clazz.getName() + "::" + targetStr;
        System.out.println(targetStr);
        writer.append(targetStr + "\r\n");
    }

    public static void printAndWrite(Throwable exception) {
        printAndWrite("", exception);
    }

    public static void printAndWrite(String str, Throwable exception) {

        if (exceptionMsgMaskList.contains(exception.getMessage().trim().toLowerCase())) {
            return;
        }

        StringBuilder log = new StringBuilder(str);
        log.append(exception.getClass()).append(":").append(exception.getMessage());
        log.append("\r\n");
        StackTraceElement[] trace = exception.getStackTrace();

        for (StackTraceElement traceElement : trace) {
            log.append("    at  ").append(traceElement).append("\r\n");
        }

        Log.printAndWrite(Grade.ERROR, log.toString(), Log.class);
    }

}
