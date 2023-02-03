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
        if (list != null) {
            String fileName = list[list.length - 1];
            LAST_WRITE_TIME = fileName.substring(0, fileName.indexOf(".txt"));
        }
    }

    public static void writeTxt(String targetStr) {
        String now = DateUtil.now();
        String nowDate = now.replaceAll("-", "").substring(0, 8);
        String path = LOG_PATH + nowDate + ".txt";
        if (!nowDate.equals(LAST_WRITE_TIME)) {
            FileUtil.touch(new File(path));
            LAST_WRITE_TIME = nowDate;
        }
        FileWriter writer = new FileWriter(path);
        writer.append("[" + now + "]" + targetStr + "\r\n");
    }

}
