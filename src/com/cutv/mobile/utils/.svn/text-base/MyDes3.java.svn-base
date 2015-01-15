package com.cutv.mobile.utils;

import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
//import org.bouncycastle.util.encoders.Base64Encoder;
//import sun.misc.BASE64Decoder;
//import sun.misc.BASE64Encoder;

public class MyDes3 {
	private static final Cipher cipher = initCipher();

	// private static final BASE64Encoder base64 = new BASE64Encoder();

	private static final Cipher initCipher() {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			String algorithm = "DESede/ECB/PKCS7Padding";
			String sKey = "hanbaohanbaohanbaohanbao";
			SecretKey desKey = new SecretKeySpec(sKey.getBytes(), algorithm);
			Cipher tcipher = Cipher.getInstance(algorithm);
			tcipher.init(Cipher.DECRYPT_MODE, desKey);
			return tcipher;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/*
	 * public static String encrypt(String src) {
	 * 
	 * return base64.encode(encrypt(src.getBytes()));
	 * 
	 * }
	 */
	public static byte[] decrypt(byte[] src) {
		try {
			return cipher.doFinal(src);
		} catch (Exception e) {

		}
		return null;
	}

	public static byte[] encrypt(byte[] src) {

		try {

			return cipher.doFinal(src);

		} catch (Exception e) {

			e.printStackTrace();

		}

		return null;

	}

}
