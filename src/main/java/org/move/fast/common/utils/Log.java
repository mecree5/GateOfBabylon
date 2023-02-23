package org.move.fast.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Log {

    private static final String LOG_PATH = System.getProperty("user.dir") + "\\log\\";

    private static String LAST_WRITE_TIME;

    private static final List<String> exceptionMsgMaskList = Collections.singletonList(
            "Connection reset by peer" //客户端中断请求,tomcat报错,不影响业务
    );

    private enum Grade{
        INFO,
        ERROR,
    }

    static {
        String[] list = new File(LOG_PATH).list();
        if (list != null && list.length > 0) {
            String fileName = list[list.length - 1];
            LAST_WRITE_TIME = fileName.substring(0, fileName.indexOf(".txt"));
        }
    }

    public static void printAndWrite(String targetStr) {
        printAndWrite(Grade.INFO, targetStr);
    }

    public static void printAndWrite(Grade grade,String targetStr) {
        String now = DateUtil.now();
        String nowDate = now.replaceAll("-", "").substring(0, 8);
        String path = LOG_PATH + nowDate + ".txt";
        if (!nowDate.equals(LAST_WRITE_TIME)) {
            FileUtil.touch(new File(path));
            LAST_WRITE_TIME = nowDate;
        }
        FileWriter writer = new FileWriter(path);
        targetStr = "[" + now + "]" + "[" + grade.name() + "]" + targetStr;
        System.out.println(targetStr);
        writer.append(targetStr + "\r\n");
    }

    public static void printAndWrite(Exception exception) {

        if (exceptionMsgMaskList.contains(exception.getMessage())) {
            return;
        }

        StringBuilder log = new StringBuilder(exception.getClass().toString());
        log.append(":").append(exception.getMessage());
        log.append("\r\n");
        StackTraceElement[] trace = exception.getStackTrace();

        for (StackTraceElement traceElement : trace) {
            log.append("    at  ").append(traceElement).append("\r\n");
        }

        Log.printAndWrite(Grade.ERROR, log.toString());
    }

}
