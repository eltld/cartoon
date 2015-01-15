package com.cutv.mobile.data;

import android.content.Context;

import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.ClientProtocolException;

import com.cutv.mobile.utils.*;

public class DataModule {
	public static DataModule g_DataModule = null;

	public static DataModule getInstance() {
		if (g_DataModule == null)
			g_DataModule = new DataModule();

		return g_DataModule;
	}

	public int load(Context context) {
		int ret = 0;

		// test(context);
		// 打开数据库
		if (MyDB.getInstance().db_open(context) != 0)
			return 1;

		// MyDB.getInstance().test();

		/*
		 * // 注册token if( register_token(context) != 0 ) return 2;
		 * 
		 * 
		 * load_subscribe_info();
		 * 
		 * removeAllTempImage(context);
		 */

		return ret;

	}

	public String http_get_content(String url) {
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", MyProfile.http_user_agent);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				if (MyProfile.USE_ENCRYPT_API) {
					HttpEntity he = response.getEntity();
					byte[] s1 = EntityUtils.toByteArray(he);
					byte[] ss = MyDes3.decrypt(s1);
					String s = new String(ss);
					return s;
				} else {
					String str = EntityUtils.toString(response.getEntity());
					return str;
				}
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String http_get_content_without_encrypt(String url) {
		HttpGet request = new HttpGet(url);
		request.setHeader("User-Agent", MyProfile.http_user_agent);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String str = EntityUtils.toString(response.getEntity());
				return str;
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getValueByTagName(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		if (nodeList != null && nodeList.getLength() > 0) {
			Element e = (Element) nodeList.item(0);
			if (e != null && e.getFirstChild() != null) {
				return e.getFirstChild().getNodeValue().trim();
			}
		}
		return "";
	}

	public int getIntValueByTagName(Element element, String tagName) {
		String s = getValueByTagName(element, tagName);
		if (s == "")
			return 0;

		return Integer.parseInt(s);
	}

	public static String getLocalImageFileNameByUrl(String url, String prefix) {
		if (url == null)
			return null;

		String s = String.format("%s%s", prefix, MD5Util.getMD5Encoding(url));

		return s;
	}

	public static void removeAllTempImage(Context context) {

		String[] files = context.fileList();
		for (int i = 0; i < files.length; i++) {
			if (files[i].startsWith("cover-")
					|| files[i].startsWith("recommend-")
					|| files[i].endsWith(".apk")) {
				Log.d("", "delete file: " + files[i]);
				context.deleteFile(files[i]);
			}
		}
	}

}
