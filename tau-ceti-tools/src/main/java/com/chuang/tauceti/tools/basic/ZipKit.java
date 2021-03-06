package com.chuang.tauceti.tools.basic;

import com.chuang.tauceti.support.exception.SystemException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.*;

/**
 * 压缩工具类
 * 
 * @author Looly
 *
 */
public class ZipKit {
	private static final Logger log = LoggerFactory.getLogger(ZipKit.class);

	/**
	 * 打包到当前目录
	 * 
	 * @param srcPath 源文件路径
	 * @return 打包好的压缩文件
	 *
	 */
	public static File zip(String srcPath) throws IOException {
		return zip(FileKit.file(srcPath));
	}

	/**
	 * 打包到当前目录
	 * 
	 * @param srcFile 源文件或目录
	 * @return 打包好的压缩文件
	 *
	 */
	public static File zip(File srcFile) throws IOException {
		File zipFile = FileKit.file(srcFile.getParentFile(), FileKit.mainName(srcFile) + ".zip");
		zip(srcFile, zipFile, false);
		return zipFile;
	}

	/**
	 * 对文件或文件目录进行压缩<br>
	 * 不包含被打包目录
	 * 
	 * @param srcPath 要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
	 * @param zipPath 压缩文件保存的路径，包括文件名。注意：zipPath不能是srcPath路径下的子文件夹
	 * @return 压缩好的Zip文件
	 *
	 */
	public static File zip(String srcPath, String zipPath) throws IOException {
		return zip(srcPath, zipPath, false);
	}

	/**
	 * 对文件或文件目录进行压缩<br>
	 * 
	 * @param srcPath 要压缩的源文件路径。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
	 * @param zipPath 压缩文件保存的路径，包括文件名。注意：zipPath不能是srcPath路径下的子文件夹
	 * @param withSrcDir 是否包含被打包目录
	 *
	 */
	public static File zip(String srcPath, String zipPath, boolean withSrcDir) throws IOException {
		File srcFile = FileKit.file(srcPath);
		File zipFile = FileKit.file(zipPath);
		zip(srcFile, zipFile, withSrcDir);
		return zipFile;
	}

	/**
	 * 对文件或文件目录进行压缩<br>
	 * 
	 * @param srcFile 要压缩的源文件或目录。如果压缩一个文件，则为该文件的全路径；如果压缩一个目录，则为该目录的顶层目录路径
	 * @param zipFile 生成的Zip文件，包括文件名。注意：zipPath不能是srcPath路径下的子文件夹
	 * @param withSrcDir 是否包含被打包目录
	 *
	 */
	public static void zip(File srcFile, File zipFile, boolean withSrcDir) throws IOException {
		validateFile(srcFile, zipFile);

		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new CheckedOutputStream(FileKit.getOutputStream(zipFile), new CRC32()));

			// 如果只是压缩一个文件，则需要截取该文件的父目录
			String srcRootDir = srcFile.getCanonicalPath();
			if (srcFile.isFile() || withSrcDir) {
				srcRootDir = srcFile.getParent();
			}
			// 调用递归压缩方法进行目录或文件压缩
			zip(srcRootDir, srcFile, out);
			out.flush();
		} finally {
			IOKit.close(out);
		}
	}

	/**
	 * 解压到文件名相同的目录中
	 * 
	 * @param zipFile 压缩文件
	 * @return 解压的目录
	 *
	 */
	public static File unzip(File zipFile) throws IOException {
		return unzip(zipFile, FileKit.file(zipFile.getParentFile(), FileKit.mainName(zipFile)));
	}

	/**
	 * 解压到文件名相同的目录中
	 * 
	 * @param zipFilePath 压缩文件路径
	 * @return 解压的目录
	 *
	 */
	public static File unzip(String zipFilePath) throws IOException {
		return unzip(FileKit.file(zipFilePath));
	}

	/**
	 * 解压
	 * 
	 * @param zipFilePath 压缩文件的路径
	 * @param outFileDir 解压到的目录
	 * @return 解压的目录
	 *
	 */
	public static File unzip(String zipFilePath, String outFileDir) throws IOException {
		return unzip(FileKit.file(zipFilePath), FileKit.mkdir(outFileDir));
	}

	/**
	 * 解压
	 * 
	 * @param zipFile zip文件
	 * @param outFile 解压到的目录
	 * @return 解压的目录
	 *
	 */
	@SuppressWarnings("unchecked")
	public static File unzip(File zipFile, File outFile) throws IOException {
		final ZipFile zipFileObj = new ZipFile(zipFile);
		final Enumeration<ZipEntry> em = (Enumeration<ZipEntry>) zipFileObj.entries();
		ZipEntry zipEntry ;
		File outItemFile ;
		while (em.hasMoreElements()) {
			zipEntry = em.nextElement();
			outItemFile = new File(outFile, zipEntry.getName());
			log.debug("UNZIP {}", outItemFile.getPath());
			if (zipEntry.isDirectory()) {
				outItemFile.mkdirs();
			} else {
				FileKit.touch(outItemFile);
				copy(zipFileObj, zipEntry, outItemFile);
			}
		}
		IOKit.close(zipFileObj);
		return outFile;
	}
	
	/**
	 * Gzip压缩处理
	 * 
	 * @param content 被压缩的字符串
	 * @param charset 编码
	 * @return 压缩后的字节流
	 */
	public static byte[] gzip(String content, String charset) throws IOException {
		return gzip(StringKit.bytes(content, charset));
	}
	
	/**
	 * Gzip压缩处理
	 * 
	 * @param val 被压缩的字节流
	 * @return 压缩后的字节流
	 *
	 */
	public static byte[] gzip(byte[] val) throws IOException {
		FastByteArrayOutputStream bos = new FastByteArrayOutputStream(val.length);
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(bos);
			gos.write(val, 0, val.length);
			gos.finish();
			gos.flush();
			val = bos.toByteArray();
		} finally {
			IOKit.close(gos);
		}
		return val;
	}
	
	/**
	 * Gzip压缩文件
	 * 
	 * @param file 被压缩的文件
	 * @return 压缩后的字节流
	 *
	 */
	public static byte[] gzip(File file) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
		GZIPOutputStream gos = null;
		BufferedInputStream in;
		try {
			gos = new GZIPOutputStream(bos);
			in = FileKit.getInputStream(file);
			IOKit.copy(in, gos);
			return bos.toByteArray();
		} finally {
			IOKit.close(gos);
		}
	}
	
	/**
	 * Gzip解压缩处理
	 * 
	 * @param buf 压缩过的字节流
	 * @param charset 编码
	 * @return 解压后的字符串
	 *
	 */
	public static String unGzip(byte[] buf, String charset) throws IOException {
		return StringKit.str(unGzip(buf), Charset.forName(charset));
	}
	
	/**
	 * Gzip解压处理
	 * 
	 * @param buf buf
	 * @return bytes
	 *
	 */
	public static byte[] unGzip(byte[] buf) throws IOException {
		GZIPInputStream gzi = null;
		ByteArrayOutputStream bos ;
		try {
			gzi = new GZIPInputStream(new ByteArrayInputStream(buf));
			bos = new ByteArrayOutputStream(buf.length);
			IOKit.copy(gzi, bos);
			buf = bos.toByteArray();
		} finally {
			IOKit.close(gzi);
		}
		return buf;
	}

	// ---------------------------------------------------------------------------------------------- Private method start
	/**
	 * 递归压缩文件夹
	 * 
	 * @param srcRootDir 压缩文件夹根目录的子路径
	 * @param file 当前递归压缩的文件或目录对象
	 * @param out 压缩文件存储对象
	 */
	private static void zip(String srcRootDir, File file, ZipOutputStream out) throws IOException {

		if (file.isFile()) {// 如果是文件，则直接压缩该文件
			final String subPath = FileKit.subPath(srcRootDir, file); // 获取文件相对于压缩文件夹根目录的子路径
			log.debug("ZIP {}", subPath);
			BufferedInputStream in = null;
			try {
				out.putNextEntry(new ZipEntry(subPath));
				in = FileKit.getInputStream(file);
				IOKit.copy(in, out);
			} catch (IOException e) {
				throw new SystemException("", e);
			} finally {
				IOKit.close(in);
				IOKit.close(out);
			}
		} else {// 如果是目录，则压缩压缩目录中的文件或子目录
            File l[] = file.listFiles();
            if(null == l) {
                return;
            }
			for (File childFile : l) {
				zip(srcRootDir, childFile, out);
			}
		}
	}

	/**
	 * 判断压缩文件保存的路径是否为源文件路径的子文件夹，如果是，则抛出异常（防止无限递归压缩的发生）
	 * 
	 * @param srcFile 被压缩的文件或目录
	 * @param zipFile 压缩后的产生的文件路径
	 */
	private static void validateFile(File srcFile, File zipFile) {
		if (!srcFile.exists()) {
			throw new SystemException(StringKit.format("File [{}] not exist!", srcFile.getAbsolutePath()));
		}

		try {
			// 压缩文件不能位于被压缩的目录内
			if (srcFile.isDirectory() && zipFile.getParent().contains(srcFile.getCanonicalPath())) {
				throw new SystemException("[zipPath] must not be the child directory of [srcPath]!");
			}

			if (!zipFile.exists()) {
				FileKit.touch(zipFile);
			}
		} catch (IOException e) {
			throw new SystemException("", e);
		}
	}


	/**
	 * 从Zip文件流中拷贝文件出来
	 * 
	 * @param zipFile Zip文件
	 * @param zipEntry zip文件中的子文件
	 * @param outItemFile 输出到的文件
	 *
	 */
	private static void copy(ZipFile zipFile, ZipEntry zipEntry, File outItemFile) throws IOException {
		InputStream in = null;
		OutputStream out = null;
		try {
			in = zipFile.getInputStream(zipEntry);
			out = FileKit.getOutputStream(outItemFile);
			IOKit.copy(in, out);
		} finally {
			IOKit.close(out);
			IOKit.close(in);
		}
	}
	// ---------------------------------------------------------------------------------------------- Private method end

}
