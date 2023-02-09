package org.move.fast.common.utils;

import cn.hutool.core.codec.Base64Encoder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @description: QrCodeGeneratorUtil
 * @author: YinShiJie
 * @create: 2022-02-23 09:34
 **/
public class QrCode {

    /**
     * @description: crateQRCode 生成base64的二维码 不支持中文
     * @author YinShiJie
     * @Param [content]
     * @Return java.lang.String
     * @date 2022/2/21 16:45
     */
    public static String strToQRCode(String content) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 200, 200);
            BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ImageIO.write(bufferedImage, "png", os);
        } catch (WriterException | IOException e) {
            throw new RuntimeException(e);
        }
        //添加图片标识
        String res = "data:image/png;base64," + Base64Encoder.encode(os.toByteArray());
        return res.replaceAll("\r\n", "");
    }

}
