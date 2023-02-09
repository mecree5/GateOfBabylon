package org.move.fast.common.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author YinShiJie
 * @date 2022/1/19 17:20
 */
public class HttpReq {

    public static String sendPost(String reqUrl, Map<String, String> headers, Map<String, String> params) {
        String result = null;
        //创建post请求对象
        HttpPost post = new HttpPost(reqUrl);
        try {
            //创建参数集合
            List<BasicNameValuePair> list = new ArrayList<>();
            //添加参数
            if (params != null && !params.isEmpty()) {
                for (Map.Entry<String, String> param : params.entrySet()) {
                    list.add(new BasicNameValuePair(param.getKey(), param.getValue()));
                }
            }
            //把参数放入请求对象，post发送的参数list，指定格式
            post.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
            //添加请求头参数
            if (headers != null && !headers.isEmpty()) {
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    post.addHeader(header.getKey(), header.getValue());
                }
            }
            CloseableHttpClient client = HttpClients.createDefault();
            //启动执行请求，并获得返回值
            CloseableHttpResponse response = client.execute(post);
            //得到返回的entity对象
            HttpEntity entity = response.getEntity();
            //把实体对象转换为string
            result = EntityUtils.toString(entity);
            //返回内容
        } catch (Exception e) {
            Log.printAndWrite(e);
        }
        return result;
    }

    public static String sendPost(String reqUrl) {
        return sendPost(reqUrl, null, null);
    }

    public static String sendPost(Map<String, String> headers, String reqUrl) {
        return sendPost(reqUrl, headers, null);
    }

    public static String sendPost(String reqUrl, Map<String, String> params) {
        return sendPost(reqUrl, null, params);
    }


    public static String sendGet(String reqUrl, Map<String, String> param) {
        return sendGet(reqUrl,param,null);
    }

    public static String sendGet(Map<String, String> header, String reqUrl) {
        return sendGet(reqUrl,null,header);
    }

    public static String sendGet(String reqUrl) {
        return sendGet(reqUrl, null);
    }

    public static String sendGet(String reqUrl, Map<String, String> param, Map<String, String> header) {

        StringBuilder result = new StringBuilder();

        StringBuilder url = new StringBuilder(reqUrl);

        if (!CollectionUtils.isEmpty(param)){
            url.append("?");
            for (Map.Entry<String, String> entry : param.entrySet()) {
                url.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }

        URL realUrl;
        URLConnection connection = null;

        try {

            realUrl = new URL(url.substring(0,url.length()-1));
            // 打开和URL之间的连接
            connection = realUrl.openConnection();
            //设置超时时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);

            // 设置通用的请求属性
            if (!CollectionUtils.isEmpty(header)) {
                for (Map.Entry<String, String> entry : header.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }

            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();

        } catch (IOException e) {
            Log.printAndWrite(e);
        }

        // 定义 BufferedReader输入流来读取URL的响应，设置utf8防止中文乱码
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(connection).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            Log.printAndWrite(e);
        }
        return result.toString();
    }

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
            Log.printAndWrite(e);
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
            Log.printAndWrite(e);
        }

        return result;
    }

}


