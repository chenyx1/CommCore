package com.chenyx.http.util;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author chenyx
 * @desc 该工具类采用单例模式
 * @date 2019-01--3
 */
public class OkHttpUtil {

    //初始化OkHttpClient,OkHttpClient将采用单例模式
    private static OkHttpClient okHttpClient = new OkHttpClient();
    private static Logger LOGGER = LoggerFactory.getLogger(OkHttpUtil.class);
    //发送请求消息为Json类型
    private static final MediaType JSONTYPE = MediaType.parse("application/*; charset=utf-8");


    //采用类方法获得单一的OkHttpClient实例
    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }


    /*
    * 获得get方法Request的请求信息
    * */
    private Request getRequest(String url) {
        return getRequest(url, null);
    }


    /*
    * 获取响应头
    * */
    public void getHeader(String url) {
        getHeader(url, null);
    }

    /*
    * 获取响应头
    * */
    public void getHeader(String url, String requestContent) {
        //初始化Request对象用于封装请求消息
        Request request = this.getRequest(url, requestContent);
        try {
            //访问网络，获得响应信息
            Response response = this.execute(request);

            //获得响应头
            Headers headers = response.headers();

            //遍历响应头信息
            for (int i = 0; i < headers.size(); i++) {
                LOGGER.info("tag: {}", headers.name(i) + "--->" + headers.value(i));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * call.execute()访问网络，返回响应报文信息
    * */
    private Response execute(Request request) throws IOException {
        //初始化Call对象，用于访问网络
        Call call = okHttpClient.newCall(request);
        //初始化Response对象，用于显示访问网络的响应头信息
        Response response = call.execute();
        return response;
    }





   /*
    * 采用异步方法访问网络，返回响应报文信息
    * */

    public void enqueue(String url, Callback callback) {


        enqueue(url, callback, null);
    }


    /*
    * 采用异步方法访问网络，返回响应报文信息
    * */


    public void enqueue(String url, Callback callback, String requestContent) {

        if (requestContent == null || "".equals(requestContent)) {
            //采用异步get方法访问网络
            Request request = this.getRequest(url);
            //初始化Call对象，用于网络连接
            Call call = okHttpClient.newCall(request);
            //采用异步访问网络
            call.enqueue(callback);

        } else {
            //采用异步post方法访问网络
            Request request = this.getRequest(url, requestContent);

            //初始化Call对象，用于网络连接
            Call call = okHttpClient.newCall(request);
            call.enqueue(callback);
        }

    }


    /*
    * 获得post方法的Request请求信息，当requestContent信息为null时，默认为get请求
    * */
    private Request getRequest(String url, String requestContent) {

        if ("".equals(requestContent) || requestContent == null) {

            //定义Rquest对象，封装访问网络的请求信息
            //new Request.Builder()创建Request的内部类Builder的对象，默认通过get方法访问网络
            Request.Builder builder = new Request.Builder();
            //关联访问网络的url,并将其转换为Request对象
            Request request = builder.url(url).build();
            return request;
        } else {

            //初始化RequestBody，用于保存我访问网络的请求信息
            RequestBody requestBody = RequestBody.create(JSONTYPE, requestContent);
            //初始化Request，用于封装请求报头
            Request request = new Request.Builder().url(url).post(requestBody).build();
            return request;
        }


    }


    //OkHttpClient的get方法
    public Object get(String url) throws IOException {


        Object result = null;
        //关联访问网络的url,并将其转换为Request对象
        Request request = this.getRequest(url);
        Response response = this.execute(request);
        //检测网络是否连接成功
        if (response.isSuccessful()) {

            //获取网连接信息
            String mess = response.message();
            LOGGER.info("tag:{}", "mess = " + mess);
            //初始化ResponseBody对象，用于封装网络资源，该实体用许多访问网络资源的方法
            ResponseBody body = response.body();
            //以字符串的形式获得网络
            result = body.string();
            //以字符流的形式获得网络
            //body.charStream();

            //以字节的形式获得网络
            // body.bytes();
            //以字节流的形式获得网络
            //body.byteStream();
        } else {
            //如果没有连接成功则抛出io异常，答应出错误码
            throw new IOException("Unexcepted code = " + response.code());
        }
        return result;

    }


    //OkHttpClient的get方法
    public Object post(String url, String requestContent) throws IOException {

        String result = null;
        //初始化Request，用于封装请求报头
        Request request = this.getRequest(url, requestContent);
        //访问网络，返回相应报文
        Response response = this.execute(request);
        //检测网络是否连接成功
        if (response.isSuccessful()) {

            //获取网连接信息
            String mess = response.message();
            LOGGER.info("tag:{}", "mess = " + mess);
            //初始化ResponseBody对象，用于封装网络资源，该实体用许多访问网络资源的方法
            ResponseBody body = response.body();
            //以字符串的形式获得网络
            result = body.string();
            //以字符流的形式获得网络
            //body.charStream();

            //以字节的形式获得网络
            // body.bytes();
            //以字节流的形式获得网络
            //body.byteStream();
        } else {
            //如果没有连接成功则抛出io异常，答应出错误码
            throw new IOException("Unexcepted code = " + response.code());
        }
        return result;
    }
}
