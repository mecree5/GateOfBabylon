package org.move.fast.common.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author : YinShiJie
 * @date : 2022/2/11 14:26
 */
public class IO {

    public static void saveImg(Image image, String pathName) {
        File file = null;
        try {
            file = isExist(pathName);
            ImageIO.write((RenderedImage) image, pathName.substring(pathName.length() - 3), file);
        } catch (IOException e) {
            throw new RuntimeException(pathName + "路径不存在或文件写入出错");
        }
    }

    /*/**
     * @description: saveTxt
     * @author YinShiJie
     * @Param [content, type]
     * @Return
     * @date 2022/2/11 14:46
     */
    public static void saveTxt(String content, String pathName) {
        File file = null;
        file = isExist(pathName);

        try (FileWriter fw = new FileWriter(file);) {
            fw.write(content);
        } catch (IOException e) {
            throw new RuntimeException(pathName + "文件写入出错");
        }

    }

    private static File isExist(String pathName) {
        File file = new File(pathName);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {
            fileParent.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(pathName + "创建文件出错");
            }
        }
        return file;
    }
}
