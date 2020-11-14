package com.chuang.tauceti.tools.basic;


import com.chuang.tauceti.tools.basic.reflect.ClassKit;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 统一资源定位符相关工具类
 *
 * @author xiaoleilu
 */
public class URLKit {
    private final static Set<String> PublicSuffixSet = new HashSet<>(
            Arrays.asList("com|org|net|gov|edu|co|cc|tv|mobi|info|asia|xxx|onion|cn|com.cn|edu.cn|gov.cn|net.cn|org.cn|jp|kr|tw|com.hk|hk|com.hk|org.hk|se|com.se|org.se"
                    .split("\\|")));
    private static final Pattern IP_PATTERN = Pattern.compile("(\\d{1,3}\\.){3}(\\d{1,3})");

    /**
     * 创建URL对象
     *
     * @param url URL
     * @return URL对象
     */
    public static URL url(String url) throws MalformedURLException {
        return new URL(url);
    }


    /**
     * 获得URL
     *
     * @param pathBaseClassLoader 相对路径（相对于classes）
     * @return URL
     */
    public static URL getURL(String pathBaseClassLoader) {
        return ClassKit.getClassLoader().getResource(pathBaseClassLoader);
    }

    /**
     * 获得URL
     *
     * @param path  相对给定 class所在的路径
     * @param clazz 指定class
     * @return URL
     */
    public static URL getURL(String path, Class<?> clazz) {
        return clazz.getResource(path);
    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param file URL对应的文件对象
     * @return URL
     */
    public static URL getURL(File file) throws MalformedURLException {

        return file.toURI().toURL();

    }

    /**
     * 获得URL，常用于使用绝对路径时的情况
     *
     * @param files URL对应的文件对象
     * @return URL
     */
    public static URL[] getURLs(File... files) throws MalformedURLException {
        final URL[] urls = new URL[files.length];
        for (int i = 0; i < files.length; i++) {
            urls[i] = files[i].toURI().toURL();
        }

        return urls;
    }

    /**
     * 格式化URL链接
     *
     * @param url 需要格式化的URL
     * @return 格式化后的URL，如果提供了null或者空串，返回null
     */
    public static String formatUrl(String url) {

        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        return "http://" + url;
    }

    /**
     * 补全相对路径
     *
     * @param baseUrl      基准URL
     * @param relativePath 相对URL
     * @return 相对路径
     */
    public static String complateUrl(String baseUrl, String relativePath) throws MalformedURLException {
        baseUrl = formatUrl(baseUrl);
        if (StringKit.isBlank(baseUrl)) {
            return null;
        }

        final URL absoluteUrl = new URL(baseUrl);
        final URL parseUrl = new URL(absoluteUrl, relativePath);
        return parseUrl.toString();

    }

    /**
     * 编码URL
     *
     * @param url     URL
     * @param charset 编码
     * @return 编码后的URL
     */
    public static String encode(String url, String charset) throws UnsupportedEncodingException {

        return URLEncoder.encode(url, charset);

    }

    /**
     * 解码URL
     *
     * @param url     URL
     * @param charset 编码
     * @return 解码后的URL
     */
    public static String decode(String url, String charset) throws UnsupportedEncodingException {

        return URLDecoder.decode(url, charset);

    }

    /**
     * 获得path部分<br>
     * URI -> http://www.aaa.bbb/search?scope=ccc&q=ddd
     * PATH -> /search
     *
     * @param uriStr URI路径
     * @return path
     */
    public static String getPath(String uriStr) throws URISyntaxException {
        URI uri = new URI(uriStr);

        return uri.getPath();
    }

    /**
     * 获取url的顶级域名
     */
    public static String getDomainName(URL url) {
        String host = url.getHost();
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        if (IP_PATTERN.matcher(host).matches()) {
            return host;
        }
        int index = 0;
        String candidate = host;
        while (index >= 0) {
            index = candidate.indexOf('.');
            String subCandidate = candidate.substring(index + 1);
            if (PublicSuffixSet.contains(subCandidate)) {
                return candidate;
            }
            candidate = subCandidate;
        }
        return candidate;
    }

    /**
     * 获取url的顶级域名
     *
     */
    public static String getDomainName(String url) throws MalformedURLException {
        return getDomainName(new URL(url));
    }

    /**
     * 判断两个url顶级域名是否相等
     *
     */
    public static boolean isSameDomainName(URL url1, URL url2) {
        return getDomainName(url1).equalsIgnoreCase(getDomainName(url2));
    }

    /**
     * 判断两个url顶级域名是否相等
     */
    public static boolean isSameDomainName(String url1, String url2)
            throws MalformedURLException {
        return isSameDomainName(new URL(url1), new URL(url2));
    }


    public static String getQueryURI (URI uri, Map<String, String> params) {
        String path = uri.getPath();
        String query = uri.getQuery();
        if(StringKit.isEmpty(path)) {
            path = "/";
        }
        if(StringKit.isBlank(query)) {
            query = getQueryString(params);
        } else {
            query += "&" + getQueryString(params);
        }

        return path + "?" + query;
    }

    /**
     * 将map转成字符串
     */
    public static String getQueryString(Map<String, String> params) {

        StringBuilder result = new StringBuilder();
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
        Map.Entry<String, String> entry = iter.next();

        result.append(entry.getKey()).append("=").append(entry.getValue());// 添加第一个参数
        if (params.size() == 1) {
            return result.toString();
        }

        while (iter.hasNext()) {
            entry = iter.next();
            result.append("&").append(entry.getKey()).append("=").append(entry.getValue());// 添加第一个参数
        }
        return result.toString();

    }
}
