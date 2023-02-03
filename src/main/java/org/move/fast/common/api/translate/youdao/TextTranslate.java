package org.move.fast.common.api.translate.youdao;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.move.fast.common.utils.http.Requests;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : YinShiJie
 * @date : 2022/1/25 18:04
 */
public class TextTranslate {

    //文本翻译URL
    public static final String TEXT_YOUDAO_URL = "https://openapi.youdao.com/api";
    //应用ID
    public static final String TEXT_APP_KEY = "1e73084594483506";
    //应用密钥
    public static final String TEXT_APP_SECRET = "Z9mxRDzD4TrnrhYVtNfBH9xBJWN9KiNr";
    //源语言
    public static final String TEXT_LAN_FORM = "auto";
    //目标语言
    public static final String TEXT_LAN_TO = "auto";

    public static String textTran(String q) {
        JSONObject jsonObject = JSON.parseObject(Requests.sendPost(TEXT_YOUDAO_URL, paramsMap(q)));
        String answer = jsonObject.getString("translation");
        return answer.substring(2, answer.length() - 2);
    }

    public static Map<String, String> paramsMap(String q) {
        Map<String, String> params = new HashMap<>();
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("from", TEXT_LAN_FORM);
        params.put("to", TEXT_LAN_TO);
        params.put("signType", "v3");
        String curtime = String.valueOf(System.currentTimeMillis() / 1000);
        params.put("curtime", curtime);
        String signStr = TEXT_APP_KEY + Common.truncate(q) + salt + curtime + TEXT_APP_SECRET;
        params.put("appKey", TEXT_APP_KEY);
        params.put("q", q);
        params.put("salt", salt);
        params.put("sign", Common.getDigest(signStr));
        return params;
    }


    /*
      请求 获取文件流 暂时用不上
     */
//    public static void requestForHttp(String url, Map<String, String> params) throws IOException {
//
//        /* 创建HttpClient */
//        CloseableHttpClient httpClient = HttpClients.createDefault();
//
//        /* httpPost */
//        HttpPost httpPost = new HttpPost(url);
//        List<NameValuePair> paramsList = new ArrayList<>();
//        Iterator<Map.Entry<String, String>> it = params.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry<String, String> en = it.next();
//            String key = en.getKey();
//            String value = en.getValue();
//            paramsList.add(new BasicNameValuePair(key, value));
//        }
//        httpPost.setEntity(new UrlEncodedFormEntity(paramsList, "UTF-8"));
//        CloseableHttpResponse httpResponse = httpClient.execute(httpPost);
//        try {
//            Header[] contentType = httpResponse.getHeaders("Content-Type");
//            System.out.println("Content-Type:" + contentType[0].getValue());
//            if ("audio/mp3".equals(contentType[0].getValue())) {
//                //如果响应是wav
//                HttpEntity httpEntity = httpResponse.getEntity();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                httpResponse.getEntity().writeTo(baos);
//                byte[] result = baos.toByteArray();
//                EntityUtils.consume(httpEntity);
//                //合成成功
//                String file = "合成的音频存储路径" + System.currentTimeMillis() + ".mp3";
//                byte2File(result, file);
//            } else {
//                /* 响应不是音频流，直接显示结果 */
//                HttpEntity httpEntity = httpResponse.getEntity();
//                String json = EntityUtils.toString(httpEntity, "UTF-8");
//                EntityUtils.consume(httpEntity);
//                System.out.println(json);
//            }
//        } finally {
//            try {
//                if (httpResponse != null) {
//                    httpResponse.close();
//                }
//            } catch (IOException e) {
//                System.out.println("## release resource error ##" + e);
//            }
//        }
//    }

    /*
      @param result 音频字节流
     * @param file   存储路径
     */
//    private static void byte2File(byte[] result, String file) {
//        File audioFile = new File(file);
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(audioFile);
//            fos.write(result);
//
//        } catch (Exception e) {
//            System.out.println(e);
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

}
