package com.cutv.mobile.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {

    public static String getMD5Encoding(String s) {
	byte[] input = s.getBytes();
	String output = null;
	char[] hexChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };
	try {
	    MessageDigest md = MessageDigest.getInstance("MD5");
	    md.update(input);
	    byte[] tmp = md.digest();
	    char[] str = new char[32];
	    byte b = 0;
	    for (int i = 0; i < 16; i++) {
		b = tmp[i];
		str[2 * i] = hexChar[b >>> 4 & 0xf];
		str[2 * i + 1] = hexChar[b & 0xf];
	    }
	    output = new String(str);
	} catch (NoSuchAlgorithmException e) {
	    e.printStackTrace();
	}
	return output;
    }
}
