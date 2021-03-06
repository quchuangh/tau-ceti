package com.chuang.tauceti.tools.basic.util;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.channels.FileChannel;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * RSA helper 2.0,it can encrypt and decrypt any length character string.<br>
 * use public key encrypt,use private decrypt.<br>
 * use private key sign,use public key verify.<br>
 * input data was encoded by BASE64.<br>
 * output data use BASE64 encode.<br>
 * 
 * @author never
 * 
 */
@SuppressWarnings("unused")
public class RSAUtil {
		
	private static final Integer KEY_SIZE = 1024;

	private static final Integer MAX_ENCRYPT_SIZE = 117;

	private static final Integer MAX_DECRYPT_SIZE = 128;

	public static void generateKeyPair(String path) throws IOException {
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		assert keyPairGenerator != null;
		keyPairGenerator.initialize(KEY_SIZE);

		KeyPair keyPair = keyPairGenerator.genKeyPair();
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();

		byte[] publicKeyBytes = publicKey.getEncoded();
		byte[] privateKeyBytes = privateKey.getEncoded();

		String publicKeyString = base64Encode(publicKeyBytes);
		String privateKeyString = base64Encode(privateKeyBytes);

		byte[] publicKeyBase64Bytes = publicKeyString.getBytes();
		byte[] privateKeyBase64Bytes = privateKeyString.getBytes();

		File publicKeyFile = new File(path + "public.key");
		File privateKeyFile = new File(path + "private.key");

		FileOutputStream publicKeyFos = new FileOutputStream(publicKeyFile);
		FileOutputStream privateKeyFos = new FileOutputStream(privateKeyFile);

		publicKeyFos.write(publicKeyBase64Bytes);
		privateKeyFos.write(privateKeyBase64Bytes);

		publicKeyFos.flush();
		publicKeyFos.close();
		privateKeyFos.flush();
		privateKeyFos.close();

	}

	/**
	 * encrypt<br>
	 * result use BASE64 encode
	 *
	 */
	public static byte[] encrypt(String publicKeyStr, byte[] needEncrypt) {
		byte[] encrypt = null;
		try {
			PublicKey publicKey = getPublicKey(publicKeyStr);
			//encrypt = encrypt(publicKey, needEncrypt);
			encrypt = encryptByPublicKey(publicKey, needEncrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypt;
	}

	/**
	 * 
	 * decrypt<br>
	 * input was BASE64 encode
	 *
	 */
	public static byte[] decrypt(String privateKeyStr, String needStr) {
		byte[] decrypt = null;

		try {
			byte[] needDecrypt = base64Decode(needStr);
			PrivateKey privateKey = getPrivateKey(privateKeyStr);
			//decrypt = decrypt(privateKey, needDecrypt);
			decrypt = decryptByPrivateKey(privateKey, needDecrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}

	/**
	 * 
	 * decrypt<br>
	 * input was BASE64 encode
	 *
	 */
	public static byte[] decryptPub(String privateKeyStr, String needStr) {
		byte[] decrypt = null;

		try {
			byte[] needDecrypt = base64Decode(needStr);
			PublicKey publicKey = getPublicKey(privateKeyStr);
			decrypt = decrypt(publicKey, needDecrypt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypt;
	}

	/**
	 * sign
	 *
	 */
	public static byte[] sign(String privateKeyStr, byte[] needSign) {
		byte[] encrypt = null;
		try {
			PrivateKey privateKey = getPrivateKey(privateKeyStr);
			encrypt = encrypt(privateKey, needSign);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encrypt;
	}

	/**
	 * verify
	 * ????????????1024
	 */
	public static byte[] verify(String publicKeyStr, byte[] needVerify) {
		byte[] decrypt;
		try {
			PublicKey publicKey = getPublicKey(publicKeyStr);
			decrypt = decrypt(publicKey, needVerify);
		} catch (Exception e) {
			throw new RuntimeException("??????,"+e.getMessage());
		}
		return decrypt;
	}
	
	/**
	 * verify
	 * ????????????(2048)
	 */
	public static byte[] verify2048(String publicKeyStr, byte[] encryptedData) throws Exception {
		//byte[] decrypt = null;
		//try {
		//	PublicKey publicKey = getPublicKey(publicKeyStr);
		//	decrypt = decrypt(publicKey, needVerify);
		//} catch (Exception e) {
		//	logger.info("??????,"+e.getMessage());
		//}
		//return decrypt;
		byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // ?????????????????????
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > 256) {
                cache = cipher.doFinal(encryptedData, offSet, 256);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * 256;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
	}
	private static byte[] encrypt(Key key, byte[] needEncryptBytes)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException, IOException {

		Cipher cipher = Cipher.getInstance("RSA");

		// encrypt
		cipher.init(Cipher.ENCRYPT_MODE, key);

		ByteArrayInputStream iis = new ByteArrayInputStream(needEncryptBytes);
		ByteArrayOutputStream oos = new ByteArrayOutputStream();
		int restLength = needEncryptBytes.length;
		while (restLength > 0) {
			int readLength = restLength < MAX_ENCRYPT_SIZE ? restLength
					: MAX_ENCRYPT_SIZE;
			restLength = restLength - readLength;

			byte[] readBytes = new byte[readLength];
			iis.read(readBytes);

			byte[] append = cipher.doFinal(readBytes);
			oos.write(append);
		}

        return oos.toByteArray();
	}

	private static byte[] decrypt(Key key, byte[] needDecryptBytes)
			throws InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		if (needDecryptBytes == null) {
			return null;
		}
		Cipher cipher = Cipher.getInstance("RSA");
		// decrypt
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(needDecryptBytes);
//		ByteArrayInputStream iis = new ByteArrayInputStream(needDecryptBytes);
//		ByteArrayOutputStream oos = new ByteArrayOutputStream();
//		int restLength = needDecryptBytes.length;
//		while (restLength > 0) {
//			int readLength = restLength < MAX_DECRYPT_SIZE ? restLength
//					: MAX_DECRYPT_SIZE;
//			restLength = restLength - readLength;
//
//			byte[] readBytes = new byte[readLength];
//			iis.read(readBytes);
//
//			byte[] append = cipher.doFinal(readBytes);
//			oos.write(append);
//		}
//		byte[] decryptedBytes = oos.toByteArray();
//
//		return decryptedBytes;
	}

	public static PublicKey getPublicKey(String publicKeyStr)
			throws InvalidKeySpecException,
			NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		byte[] publicKeyBytes = base64Decode(publicKeyStr);
		KeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);

        return keyFactory.generatePublic(keySpec);
	}

	private static PrivateKey getPrivateKey(String privateKeyStr)
			throws InvalidKeySpecException,
			NoSuchAlgorithmException {
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		byte[] privateKeyBytes = base64Decode(privateKeyStr);
		KeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);

        return keyFactory.generatePrivate(keySpec);
	}

	/**
	 * BASE64 encode
	 */
	public static String base64Encode(byte[] needEncode) {
		String encoded = null;
		if (needEncode != null) {
			encoded = Base64.getEncoder().encodeToString(needEncode);
		}
		return encoded;
	}

	/**
	 * BASE64 decode
	 *
	 */
	private static byte[] base64Decode(String needDecode) {
		byte[] decoded = null;
		if (needDecode != null) {
			decoded = Base64.getDecoder().decode(needDecode);
		}
		return decoded;
	}

	public static String readFile(String filePath, String charSet)
			throws Exception {
		// ByteBuffer byteBuffer = null;
		byte[] bytes;
		try (FileInputStream fileInputStream = new FileInputStream(filePath); FileChannel fileChannel = fileInputStream.getChannel()) {
			bytes = new byte[(int) fileChannel.size()];
			fileInputStream.read(bytes);
			return new String(bytes, charSet);
		}

	}
	
	
	
	public static byte[] decryptByPrivateKey(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(2, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 256) {
            byte[] cache;
            if(inputLen - offSet > 256) {
                cache = cipher.doFinal(encryptedData, offSet, 256);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    private static byte[] encryptByPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(1, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 244) {
            byte[] cache;
            if(inputLen - offSet > 244) {
                cache = cipher.doFinal(data, offSet, 244);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

}
