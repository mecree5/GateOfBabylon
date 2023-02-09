package org.move.fast.common.utils.http;

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
import org.move.fast.common.utils.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author YinShiJie
 * @date 2022/1/19 17:20
 */
public class Requests {

    public static String sendPost(String url, Map<String, String> headers, Map<String, String> params) {
        String result = null;
        //创建post请求对象
        HttpPost post = new HttpPost(url);
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
            Log.writeTxt(e);
        }
        return result;
    }

    public static String sendPost(String url) {
        return sendPost(url, null, null);
    }

    public static String sendPost(Map<String, String> headers, String url) {
        return sendPost(url, headers, null);
    }

    public static String sendPost(String url, Map<String, String> params) {
        return sendPost(url, null, params);
    }

    public static String sendGet(String url, String param) {
        StringBuilder result = new StringBuilder();
        String urlName;
        try {
            if (StrUtil.isBlank(param)) {
                urlName = url;
            } else {
                urlName = url + "?" + param;
            }
            URL U = new URL(urlName);
            URLConnection connection = U.openConnection();
            connection.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
            in.close();
        } catch (Exception e) {
            Log.writeTxt(e);
        }
        return result.toString();
    }

    public static String sendGet(String url) {
        return sendGet(url, null);
    }

    public static String sendGet(String url, String param, Map<String, String> header) {
        StringBuilder result = new StringBuilder();
        String urlNameString = url + "?" + param;

        URL realUrl;
        URLConnection connection = null;

        try {

            realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            connection = realUrl.openConnection();
            //设置超时时间
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(15000);
            // 设置通用的请求属性
            if (header != null) {
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
            Log.writeTxt(e);
        }

        // 定义 BufferedReader输入流来读取URL的响应，设置utf8防止中文乱码
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Objects.requireNonNull(connection).getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            Log.writeTxt(e);
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
    public static void download(String urlList, String path) {
        URL url;
        HttpURLConnection conn;
        try {
            url = new URL(urlList);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            final ByteArrayOutputStream outPut = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), outPut);
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outPut.toByteArray()));

            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
        } catch (IOException e) {
            Log.writeTxt(e);
        }
    }

    public static String downloadToString(String urlList) {
        URL url;
        HttpURLConnection conn;
        String result = "";
        try {
            url = new URL(urlList);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(20 * 1000);
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            final ByteArrayOutputStream outPut = new ByteArrayOutputStream();
            IOUtils.copy(conn.getInputStream(), outPut);
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(outPut.toByteArray()));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            result = String.valueOf(output);
            dataInputStream.close();
        } catch (IOException e) {
            Log.writeTxt(e);
        }
        return result;
    }

    /**
     * @description: download 通过url下载
     * @author YinShiJie
     * @Param [urlList, path]  path需精确到后缀名
     * @Return void
     * @date 2022/2/25 17:30
     */
    public static String download(String urlList) {
        try {
            URL url = new URL(urlList);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            return new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            Log.writeTxt(e);
        }

        return null;
    }

}


