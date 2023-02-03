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
        File file = isExist(pathName);
        try {
            ImageIO.write((RenderedImage) image, pathName.substring(pathName.length() - 3), file);
        } catch (IOException e) {
            e.printStackTrace();
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
        File file = isExist(pathName);
        FileWriter fw = null;
        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            assert fw != null;
            fw.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        return file;
    }
}
