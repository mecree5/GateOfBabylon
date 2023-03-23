package org.move.fast.common.utils;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author YinShiJie
 * @date 2022/1/19 17:20
 */
public class HttpReq {

    /**
     * @description: download 通过url下载
     * @author YinShiJie
     * @Param [urlList, path]  path需精确到后缀名
     * @Return void
     * @date 2022/2/25 17:30
     */
    public static void download(String reqUrl, String path) {
        URL url;
        HttpURLConnection conn;

        try (FileOutputStream fileOutputStream = new FileOutputStream(path)) {
            url = new URL(reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            //“ByteArrayOutputStream或ByteArrayInputStream是内存读写流 不需要手动关闭
            final ByteArrayOutputStream outPut = new ByteArrayOutputStream();

            IOUtils.copy(conn.getInputStream(), outPut);

            fileOutputStream.write(outPut.toByteArray());

        } catch (IOException e) {
            Log.error(e);
        }
    }

    public static String downloadToString(String reqUrl) {
        URL url;
        HttpURLConnection conn;
        String result = null;

        try {

            url = new URL(reqUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            //“ByteArrayOutputStream或ByteArrayInputStream是内存读写流 不需要手动关闭
            final ByteArrayOutputStream outPut = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), outPut);

            result = String.valueOf(outPut);

        } catch (IOException e) {
            Log.error(e);
        }

        return result;
    }

}


