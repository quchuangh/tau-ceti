/*
 * Copyright (c) 2015-2016, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chuang.tauceti.tools.third.servlet;

import com.alibaba.fastjson.JSONObject;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.RegexKit;
import com.chuang.tauceti.tools.basic.StringKit;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.*;

public class HttpKit {

    public static Optional<String> getIpAddress(){
        return getRequest().map(HttpKit::getIpAddress);
    }

    /**
     * 获取所有请求的值
     */
    public static Map<String, String> getRequestParameters() {
        Optional<Map<String,String>> result = HttpKit.getRequest().map(HttpKit::getRequestParameters);
        return result.orElse(Collections.emptyMap());

    }

    public static boolean isSpider() {
        return getRequest().map(HttpKit::isSpider).orElse(false);
    }

    public static boolean isSpider(HttpServletRequest request) {
        if(null == request) {
            return false;
        }
        String ua = request.getHeader("User-Agent");
        if(null == ua) {
            return false;
        }
        return ua.contains("compatible") || ua.toLowerCase().contains("spider");
    }

    public static String getSpiderName() {
        return getRequest().map(request -> {
            if(isSpider(request)) {
                String ua = request.getHeader("User-Agent");
                String name = RegexKit.get("(compatible;(.*);)", ua, 2);
                if(null == name) {
                    return ua;
                } else {
                    return name;
                }
            } else {
                return "";
            }
        }).orElse("");
    }

    public static Map<String, String> getRequestParameters(HttpServletRequest request) {
        Enumeration enums = request.getParameterNames();
        HashMap<String, String> values = new HashMap<>();
        while ( enums.hasMoreElements()){
            String paramName = (String) enums.nextElement();
            String paramValue = request.getParameter(paramName);
            values.put(paramName, paramValue);
        }
        return values;
    }

    public static void printText(String contentType, String charset, String text) {
        getResponse().ifPresent(response -> {
            response.setCharacterEncoding(charset);
            response.setContentType(contentType);
            try {
                response.getWriter().println(text);
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                throw new SystemException("", e);
            }

        });
    }

    public static void printJson(Object obj) {
        printText("text/javascript;charset=UTF-8", "UTF-8", JSONObject.toJSONString(obj));
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-real-ip");
        if(StringKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        } else {
            int index = ip.lastIndexOf(",");
            if (index != -1) {
                ip = ip.substring(index + 1);
            }
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.trim();
    }

    /**
     * 获取 HttpServletRequest
     */
    public static Optional<HttpServletResponse> getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        if(null == attributes) {
            return Optional.empty();
        }
        return Optional.ofNullable(attributes.getResponse());
    }

    /**
     * 获取 包装防Xss Sql注入的 HttpServletRequest
     * @return request
     */
    public static Optional<HttpServletRequest> getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(null == attributes) {
            return Optional.empty();
        }
        HttpServletRequest request = attributes.getRequest();
        return Optional.of(new WafRequestWrapper(request));
    }

    public static Optional<String> requestDebugText() {
        return getRequest().map(request ->
            String.format("remote-ip:%s, \r\nurl:%s, \r\nqueryString:%s, \r\nparams:%s, \r\nheader:%s",
                    request.getRemoteAddr(),
                    request.getRequestURL(),
                    request.getQueryString(),
                    getRequestParameters(request),
                    getRequestHeaders(request)
            )
        );
    }

    public static Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> result = new HashMap<>();
        Enumeration<String> e = request.getHeaderNames();
        while(e.hasMoreElements()) {
            String key = e.nextElement();
            result.put(key, request.getHeader(key));
        }
        return result;
    }

    public static Map<String, String> getRequestHeaders() {
        return getRequest().map(HttpKit::getRequestHeaders).orElse(Collections.emptyMap());
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @param param 请求参数
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String, String> param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            StringBuilder query = new StringBuilder();

            for (Map.Entry<String, String> kv : param.entrySet()) {
                query.append(URLEncoder.encode(kv.getKey(), "UTF-8")).append("=");
                query.append(URLEncoder.encode(kv.getValue(), "UTF-8")).append("&");
            }
            if (query.lastIndexOf("&") > 0) {
                query.deleteCharAt(query.length() - 1);
            }

            String urlNameString = url + "?" + query.toString();
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @param param  请求参数
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, String> param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            StringBuilder para = new StringBuilder();
            for (String key : param.keySet()) {
                para.append(key).append("=").append(param.get(key)).append("&");
            }
            if (para.lastIndexOf("&") > 0) {
                para = new StringBuilder(para.substring(0, para.length() - 1));
            }
            String urlNameString = url + "?" + para;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            System.out.println("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

}
