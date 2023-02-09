package org.move.fast.common.utils;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * image对象直接转base64
 *
 * @author : YinShiJie
 * @date : 2022/2/11 9:56
 */
public class Image {

    /**
     * 直接输出图片为base64码
     */
    public static String imageToBase64(java.awt.Image image) throws IOException {
        // Image->BufferedImage
        BufferedImage bimg = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics g = bimg.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        // bufferImage->base64
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(bimg, "jpg", outputStream);

        byte[] by = outputStream.toByteArray();
        System.out.println(Arrays.toString(by));

        return "data:image/png;base64," + new BASE64Encoder().encode(outputStream.toByteArray());
    }

}
