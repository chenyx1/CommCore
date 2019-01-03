package com.chenyx.http.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpCookie;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.Properties;

public class HttpClientUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);
    private static Integer connnectTimeOut = 30;//连接超时时间


    /**
     * @desc 设置连接超时时间方法，用于提供地势方修改超时时间
     * @param  timeOut 当timeOut为空或小于0时，取默认值
     * @author chenyx
     * @date 2018-12-26
     */
    public static void setConnectTimeOut(Integer timeOut) throws Exception {
        if (timeOut != null && timeOut > 0) {
            connnectTimeOut = timeOut;
        }
        LOGGER.info("connnectTimeOut is {}", connnectTimeOut);
    }

    /**
     * @desc 通过配置设置连接超时时间方法，用于提供地势方修改超时时间(proprefiles)
     * @param  timeOutKey 当timeOut为空或小于0时，取默认值
     * @param  proFileRoot 配置文件路径
     * @desc chenyx
     * @date 2018-12-26
     */
    public static void setConnectTimeOut(String proFileRoot, String timeOutKey) throws Exception {
        if (StringUtils.isBlank(proFileRoot)) {
            throw new RuntimeException("proFileRoot is null");
        }
        if (StringUtils.isBlank(timeOutKey)) {
            throw new RuntimeException("timeOutKey is null");
        }
        LOGGER.info("proFileRoot:{},timeOutKey:{}", proFileRoot,timeOutKey);
        Properties properties = new Properties();
        InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(proFileRoot);
        if (inputStream != null) {
            try {
                properties.load(inputStream);
                String timeOutValue = (String) properties.get(timeOutKey);
                if (StringUtils.isNotBlank(timeOutValue)) {
                    Integer tempTimeOut = Integer.parseInt(timeOutValue);
                    connnectTimeOut = tempTimeOut > 0 ? tempTimeOut : connnectTimeOut;
                }
                LOGGER.info("connnectTimeOut is {}", connnectTimeOut);
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error("parse Properties error");
                throw new RuntimeException("parse Properties error");
            }
        }
    }

    /**
     * @desc 连接超时信息
     * @desc chenyx
     * @date 2018-12-26
     */
    public static Integer getConnnectTimeOut() {
        return  connnectTimeOut;
    }
    /***
     * @desc get请求
     * @author chenyx
     * @date 2018-12-26
     *
     * */
    public static <T> T httpGet(String url, Class clazz, Charset charSet) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url is null");
        }
        LOGGER.info("url:" + url);
        if (charSet == null) {
            charSet = Consts.UTF_8;//默认utf-8编码
        }
        T res = null;//返回结果
        try {
            URI uri = new URI(url);
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);//设置URL

            RequestConfig.Builder builder = RequestConfig.custom();//设置请求参数
            builder.setConnectTimeout(connnectTimeOut);
            httpGet.setConfig(builder.build());

            HttpClient httpClient = HttpClients.createDefault();//默认http请求客户端
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //判断是否正确返回
            if (httpResponse != null){
                HttpEntity httpEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(httpEntity,charSet);
                if (StringUtils.isNotBlank(result)) {
                    res  = (T) ConvertUtils.convert(result,clazz);
                }
            }
        }catch (Exception e) {
            LOGGER.error("请求网络异常,e = {}", JSONObject.toJSONString(e));
            throw new RuntimeException("请求网络异常,e:"+ e);
        }
        return res;
    }


    /***
     * @desc get请求
     * @author chenyx
     * @date 2018-12-26
     *
     * */
    public static <T> T httpGet(String url, Class clazz, Charset charSet,RequestConfig requestConfig) throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url is null");
        }
        LOGGER.info("url:" + url);
        if (charSet == null) {
            charSet = Consts.UTF_8;//默认utf-8编码
        }
        T res = null;//返回结果
        try {
            URI uri = new URI(url);
            HttpGet httpGet = new HttpGet();
            httpGet.setURI(uri);//设置URL

            if (requestConfig == null) {
                requestConfig = RequestConfig.DEFAULT;
            }
            httpGet.setConfig(requestConfig);
            HttpClient httpClient = HttpClients.createDefault();//默认http请求客户端
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //判断是否正确返回
            if (httpResponse != null){
                HttpEntity httpEntity = httpResponse.getEntity();
                String result = EntityUtils.toString(httpEntity,charSet);
                if (StringUtils.isNotBlank(result)) {
                    res  = (T) ConvertUtils.convert(result,clazz);
                }
            }
        }catch (Exception e) {
            LOGGER.error("请求网络异常,e = {}", JSONObject.toJSONString(e));
            throw new RuntimeException("请求网络异常,e:"+ e);
        }
        return res;
    }

    /***
     * @desc post请求
     *@author chenyx
     * @dat  2019-01-03
     * */
    public static<T> T httpPost(String url, Class clazz, Charset charSet, HttpEntity requestBody)throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url is null");
        }
        LOGGER.info("url:" + url);
        if (charSet == null) {
            charSet = Consts.UTF_8;//默认utf-8编码
        }
        T result = null;
        try {
            URI uri = new URI(url);
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);//设置URL
            RequestConfig.Builder builder = RequestConfig.custom();//设置请求参数
            builder.setConnectTimeout(connnectTimeOut);
            httpPost.setConfig(builder.build());
            httpPost.setEntity(requestBody);
            HttpClient httpClient = HttpClients.createDefault();//默认http请求客户端
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //判断是否正确返回
            if (httpResponse != null){
                HttpEntity httpEntity = httpResponse.getEntity();
                String res = EntityUtils.toString(httpEntity,charSet);
                if (StringUtils.isNotBlank(res)) {
                    result  = (T) ConvertUtils.convert(res,clazz);
                }
            }
        }catch (Exception e) {
            LOGGER.error("请求网络异常,e = {}", JSONObject.toJSONString(e));
            throw new RuntimeException("请求网络异常,e:"+ e);
        }
        return  result;
    }



    /***
     * @desc post请求
     *@author chenyx
     * @dat  2019-01-03
     * */
    public static<T> T httpPost(String url, Class clazz, Charset charSet, HttpEntity requestBody,RequestConfig requestConfig)throws Exception {
        if (StringUtils.isBlank(url)) {
            throw new RuntimeException("url is null");
        }
        LOGGER.info("url:" + url);
        if (charSet == null) {
            charSet = Consts.UTF_8;//默认utf-8编码
        }
        T result = null;
        try {
            URI uri = new URI(url);
            HttpPost httpPost = new HttpPost();
            httpPost.setURI(uri);//设置URL
            if (requestConfig == null) {//设置配置参数
                requestConfig = RequestConfig.DEFAULT;
            }
            httpPost.setConfig(requestConfig);
            httpPost.setEntity(requestBody);
            HttpClient httpClient = HttpClients.createDefault();//默认http请求客户端
            HttpResponse httpResponse = httpClient.execute(httpPost);
            //判断是否正确返回
            if (httpResponse != null){
                HttpEntity httpEntity = httpResponse.getEntity();
                String res = EntityUtils.toString(httpEntity,charSet);
                if (StringUtils.isNotBlank(res)) {
                    result  = (T) ConvertUtils.convert(res,clazz);
                }
            }
        }catch (Exception e) {
            LOGGER.error("请求网络异常,e = {}", JSONObject.toJSONString(e));
            throw new RuntimeException("请求网络异常,e:"+ e);
        }
        return  result;
    }

}
