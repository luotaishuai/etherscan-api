package com.test.etherscan.common;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * @author anonymity
 * @create 2018-12-14 18:46
 **/
@Slf4j
public class OkHttpClientHelper {
    private static OkHttpClient mOkHttpClient = new OkHttpClient.Builder().build();

    public static void proxy(String ip, int port) {
        if (null == mOkHttpClient.proxy()) {
            mOkHttpClient = new OkHttpClient.Builder()
                    .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port)))
                    .build();
        }
    }

    public static String get(String url, Map<String, String> header) {
        Request.Builder builder = new Request.Builder();
        if (null != header) {
            for (Map.Entry<String, String> entry : header.entrySet()) {
                // 组装成 OkHttp 的 Header
                builder.header(entry.getKey(), entry.getValue());
            }
        }
        builder.url(url).get();
        Request okRequest = builder.build();
        return execute(okRequest);
    }

    private static String execute(Request request) {
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            log.info(JSON.toJSONString(response));
            if (200 == response.code()) {
                String body = response.body().string();
                return body;
            } else {
                throw new Exception("status code is not 200");
            }
        } catch (IOException e) {
            log.error("OkHttp Request Error: ", e);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}