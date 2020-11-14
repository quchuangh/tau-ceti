package com.chuang.tauceti.httpclient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {

	private static final Logger logger = LoggerFactory.getLogger(Tools.class);

	public static boolean isBlank(@Nullable CharSequence str) {
		int length;
		if ((str == null) || ((length = str.length()) == 0)) {
			return true;
		}
		for (int i = 0; i < length; i++) {
			// 只要有一个非空字符即为非空字符串
			if (!Character.isWhitespace(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}


	public static String entity2str(HttpEntity httpEntity, int frameLen, String charset) throws IOException {
		InputStream input = httpEntity.getContent();
		byte[] buff = new byte[frameLen];
		int len;

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {
			while ((len = input.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
		} catch (EOFException e) {
			logger.warn("Apache HttpEntity读取时意外的打到了流结尾。", e);
		}

		return new String(out.toByteArray(), charset);

	}

	public static String entity2strAndClose(HttpEntity httpEntity, int frameLen, String charset) throws IOException {
		String response = entity2str(httpEntity, frameLen, charset);
		httpEntity.getContent().close();
		return response;
	}

	public static void closeQuietly(CloseableHttpResponse response) {
		try {
			if(null != response) {
				response.close();
			}
		} catch (Exception e) {
			logger.error("无法关闭response", e);
		}
	}

	public static HttpEntity getEntity(Request request, String charset) throws UnsupportedEncodingException {
		if(null != request.getEntity()) {
			return request.getEntity();
		}
		Map<String, String> params = request.getParams();

		if (null != params && !params.isEmpty()) {
			ArrayList<NameValuePair> pairs = new ArrayList<>(params.size());
			for (Entry<String, String> entry : params.entrySet()) {
				String value = entry.getValue();

				if (value == null) {
					pairs.add(new BasicNameValuePair(entry.getKey(), ""));
				} else {
					pairs.add(new BasicNameValuePair(entry.getKey(), value));
				}
			}
			return new UrlEncodedFormEntity(pairs, charset);
		} else {
			return request.getEntity();
		}
	}

	public static void handEntity(Request request, String charset) throws IOException {
		HttpEntity entity = getEntity(request, charset);
		HttpRequestBase requestBase = request.get();
		if (null != entity) {
			//如果是将参数写入entity的
			if(requestBase instanceof HttpEntityEnclosingRequest) {
				((HttpEntityEnclosingRequest)requestBase).setEntity(entity);
			} else {
				requestBase.setURI(URI.create(requestBase.getURI().toString() + "?" + EntityUtils.toString(entity)));
			}
		}
	}

	/**
	 * 获得匹配的字符串
	 *
	 * @param pattern 编译后的正则模式
	 * @param content 被匹配的内容
	 * @param groupIndex 匹配正则的分组序号
	 * @return 匹配后得到的字符串，未匹配返回null
	 */
	public static String regexGet(Pattern pattern, String content, int groupIndex) {
		if(null == content){
			return null;
		}
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			return matcher.group(groupIndex);
		}
		return null;
	}

	/**
	 * 获得匹配的字符串
	 *
	 * @param regex 匹配的正则
	 * @param content 被匹配的内容
	 * @param groupIndex 匹配正则的分组序号
	 * @return 匹配后得到的字符串，未匹配返回null
	 */
	public static String regexGet(String regex, String content, int groupIndex) {
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
		return regexGet(pattern, content, groupIndex);
	}


}
