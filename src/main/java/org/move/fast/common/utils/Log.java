package org.move.fast.common.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;

import java.io.File;

public class Log {

    public static String LOG_PATH = System.getProperty("user.dir") + "\\log\\";

    public static String LAST_WRITE_TIME;

    static {
        String[] list = new File(LOG_PATH).list();
        if (list != null && list.length > 0) {
            String fileName = list[list.length - 1];
            LAST_WRITE_TIME = fileName.substring(0, fileName.indexOf(".txt"));
        }
    }

    public static void printAndWrite(String targetStr) {
        String now = DateUtil.now();
        String nowDate = now.replaceAll("-", "").substring(0, 8);
        String path = LOG_PATH + nowDate + ".txt";
        if (!nowDate.equals(LAST_WRITE_TIME)) {
            FileUtil.touch(new File(path));
            LAST_WRITE_TIME = nowDate;
        }
        FileWriter writer = new FileWriter(path);
        System.err.println(targetStr);
        writer.append("[" + now + "]" + targetStr + "\r\n");
    }

    public static void printAndWrite(Exception exception) {
        StringBuilder log = new StringBuilder(exception.getClass().toString());
        log.append(":").append(exception.getMessage());
        log.append("\r\n");
        StackTraceElement[] trace = exception.getStackTrace();

        for (StackTraceElement traceElement : trace) {
            log.append("    at  ").append(traceElement).append("\r\n");
        }

        Log.printAndWrite(log.toString());
    }

}
