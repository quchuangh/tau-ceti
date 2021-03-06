package com.chuang.tauceti.tools.basic;


import com.chuang.tauceti.support.Result;
import com.chuang.tauceti.support.exception.SystemException;
import com.chuang.tauceti.tools.basic.reflect.ConvertKit;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;

/**
 * IO工具类
 * @author xiaoleilu
 *
 */
public class IOKit {

	/** 默认缓存大小 */
	public static final int DEFAULT_BUFFER_SIZE = 1024;
	/** 数据流末尾 */
	public static final int EOF = -1;


	public interface StreamProgress {

		default void start() {
			// 预留

		}

		default void progress(long size) {
			// 预留

		}

		default void finish() {
			// 预留

		}

	}

	//-------------------------------------------------------------------------------------- Copy start
	/**
	 * 将Reader中的内容复制到Writer中
	 * 使用默认缓存大小
	 * @param reader Reader
	 * @param writer Writer
	 * @return 拷贝的字节数
	 * 
	 */
	public static long copy(Reader reader, Writer writer) throws IOException {
		return copy(reader, writer, DEFAULT_BUFFER_SIZE);
	}
	
	/**
	 * 将Reader中的内容复制到Writer中
	 * @param reader Reader
	 * @param writer Writer
	 * @param bufferSize 缓存大小
	 * @return 传输的byte数
	 * 
	 */
	public static long copy(Reader reader, Writer writer, int bufferSize) throws IOException {
		return copy(reader, writer, bufferSize, null);
	}
	
	/**
	 * 将Reader中的内容复制到Writer中
	 * @param reader Reader
	 * @param writer Writer
	 * @param bufferSize 缓存大小
	 * @return 传输的byte数
	 * 
	 */
	public static long copy(Reader reader, Writer writer, int bufferSize, StreamProgress streamProgress) throws IOException {
		char[] buffer = new char[bufferSize];
		long size = 0;
		int readSize;
		if(null != streamProgress){
			streamProgress.start();
		}
		while ((readSize = reader.read(buffer, 0, bufferSize)) != EOF) {
			writer.write(buffer, 0, readSize);
			size += readSize;
			writer.flush();
			if(null != streamProgress){
				streamProgress.progress(size);
			}
		}
		if(null != streamProgress){
			streamProgress.finish();
		}
		return size;
	}
	
	/**
	 * 拷贝流，使用默认Buffer大小
	 * @param in 输入流
	 * @param out 输出流
	 * @return 传输的byte数
	 * 
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException {
		return copy(in, out, DEFAULT_BUFFER_SIZE);
	}

	/**
	 * 拷贝流
	 * @param in 输入流
	 * @param out 输出流
	 * @param bufferSize 缓存大小
	 * @return 传输的byte数
	 * 
	 */
	public static long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
		return copy(in, out, bufferSize, null);
	}
	
	/**
	 * 拷贝流
	 * @param in 输入流
	 * @param out 输出流
	 * @param bufferSize 缓存大小
	 * @param streamProgress 进度条
	 * @return 传输的byte数
	 * 
	 */
	public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IOException {
		if(null == in){
			throw new NullPointerException("InputStream is null!");
		}
		if(null == out){
			throw new NullPointerException("OutputStream is null!");
		}
		if(bufferSize <= 0){
			bufferSize = DEFAULT_BUFFER_SIZE;
		}
		
		byte[] buffer = new byte[bufferSize];
		long size = 0;
		if(null != streamProgress){
			streamProgress.start();
		}
		for (int readSize; (readSize = in.read(buffer)) != EOF;) {
			out.write(buffer, 0, readSize);
			size += readSize;
			out.flush();
			if(null != streamProgress){
				streamProgress.progress(size);
			}
		}
		if(null != streamProgress){
			streamProgress.finish();
		}
		return size;
	}
	
	/**
	 * 拷贝文件流，使用NIO
	 * @param in 输入
	 * @param out 输出
	 * @return 拷贝的字节数
	 * 
	 */
	public static long copy(FileInputStream in, FileOutputStream out) throws IOException {
		if(null == in){
			throw new NullPointerException("FileInputStream is null!");
		}
		if(null == out){
			throw new NullPointerException("FileOutputStream is null!");
		}
		
		FileChannel inChannel = in.getChannel();
		FileChannel outChannel = out.getChannel();
		
		return inChannel.transferTo(0, inChannel.size(), outChannel);
	}
	//-------------------------------------------------------------------------------------- Copy end
	
	/**
	 * 获得一个文件读取器
	 * @param in 输入流
	 * @param charsetName 字符集名称
	 * @return BufferedReader对象
	 * 
	 */
	public static BufferedReader getReader(InputStream in, String charsetName) {
		return getReader(in, Charset.forName(charsetName));
	}
	
	/**
	 * 获得一个文件读取器
	 * @param in 输入流
	 * @param charset 字符集
	 * @return BufferedReader对象
	 */
	public static BufferedReader getReader(InputStream in, Charset charset) {
		if(null == in){
			return null;
		}
		
		InputStreamReader reader;
		if(null == charset) {
			reader = new InputStreamReader(in);
		}else {
			reader = new InputStreamReader(in, charset);
		}
		
		return new BufferedReader(reader);
	}
	
	/**
	 * 从流中读取bytes
	 * 
	 * @param in 输入流
	 * @return bytes
	 * 
	 */
	public static byte[] readBytes(InputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}
	
	/**
	 * 从流中读取内容
	 * 
	 * @param in 输入流
	 * @param charset 字符集
	 * @return 内容
	 * 
	 */
	public static String read(InputStream in, String charset) throws IOException {
		ByteArrayOutputStream out = read(in);
		return StringKit.isBlank(charset) ? out.toString() : new String(out.toByteArray(), charset);
	}

	/**
	 * 从流中读取内容
	 * 
	 * @param in 输入流
	 * @param charset 字符集
	 * @return 内容
	 * 
	 */
	public static String read(InputStream in, Charset charset) throws IOException {
		ByteArrayOutputStream out = read(in);
		return null == charset ? out.toString() : new String(out.toByteArray(), charset);
	}
	
	/**
	 * 从流中读取内容，读到输出流中
	 * 
	 * @param in 输入流
	 * @return 输出流
	 * 
	 */
	public static ByteArrayOutputStream read(InputStream in) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out;
	}
	
	/**
	 * 从Reader中读取String
	 * @param reader Reader
	 * @return String
	 * 
	 */
	public static String read(Reader reader) throws IOException{
		final StringBuilder builder = StringKit.builder();
		final CharBuffer buffer = CharBuffer.allocate(DEFAULT_BUFFER_SIZE);
		while(-1 != reader.read(buffer)){
			builder.append(buffer.flip().toString());
		}
		return builder.toString();
	}
	
	/**
	 * 从FileChannel中读取内容
	 * @param fileChannel 文件管道
	 * @param charset 字符集
	 * @return 内容
	 * 
	 */
	public static String read(FileChannel fileChannel, String charset) throws IOException {
		final MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size()).load();
		return StringKit.str(buffer, charset);
	}
	
	/**
	 * 从流中读取内容
	 * 
	 * @param in 输入流
	 * @param charset 字符集
	 * @param collection 返回集合
	 * @return 内容
	 * 
	 */
	public static <T extends Collection<String>> T readLines(InputStream in, String charset, T collection) throws IOException {
		// 从返回的内容中读取所需内容
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset));
		String line;
		while ((line = reader.readLine()) != null) {
			collection.add(line);
		}

		return collection;
	}
	
	/**
	 * String 转为 流
	 * @param content 内容
	 * @param charset 编码
	 * @return 字节流
	 */
	public static ByteArrayInputStream toStream(String content, String charset) {
		if(content == null) {
			return null;
		}
		
		byte[] data ;
		try {
			data = StringKit.isBlank(charset) ? content.getBytes() : content.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new SystemException(StringKit.format("Invalid charset [{}] !", charset), e);
		}
		
		return new ByteArrayInputStream(data);
	}
	
	/**
	 * 将多部分内容写到流中，自动转换为字符串
	 * @param out 输出流
	 * @param charset 写出的内容的字符集
	 * @param isCloseOut 写入完毕是否关闭输出流
	 * @param contents 写入的内容，调用toString()方法，不包括不会自动换行
	 * 
	 */
	public static void write(OutputStream out, String charset, boolean isCloseOut, Object... contents) throws IOException {
		OutputStreamWriter osw = null;
		try {
			osw = new OutputStreamWriter(out, charset);
			for (Object content : contents) {
				if(content != null) {
					osw.write(ConvertKit.toStr(content, StringKit.EMPTY));
					osw.flush();
				}
			}
		} catch (Exception e) {
			throw new IOException("Write content to OutputStream error!", e);
		}finally {
			if(isCloseOut) {
				close(osw);
			}
		}
	}
	
	/**
	 * 打印内容，调用系统的System.out.println方法
	 * @param content 内容，会调用toString方法， 当内容中有 {} 表示变量占位符
	 * @param param 参数
	 */
	public static void echo(Object content, Object... param) {
		System.out.println(StringKit.format(content.toString(), param));
	}

	/**
	 * 关闭
	 * @param closeable 被关闭的对象
	 */
	public static void close(@Nullable Closeable closeable) {
		if (closeable == null) return;
		try {
			closeable.close();
		} catch (Exception ignore) {
		}
	}

	/**
	 * 关闭
	 * @param closeable 被关闭的对象
	 * @since 1.7
	 */
	public static void close(@Nullable AutoCloseable closeable) {
		if (closeable == null) return;
		try {
			closeable.close();
		} catch (Exception ignore) {
		}
	}
}
