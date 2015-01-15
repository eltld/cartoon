package com.cutv.mobile.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

/**
 * 有关http辅助操作类
 * 
 * @author teeker_bin
 * 
 */
public class HttpUrlHelper {
	public static final int CONNECTION_TIMEOUT = 10 * 1000;
	public static final int SO_TIMEOUT = 10 * 1000;
	// public static final String DEFAULT_HOST =
	// public static final String DEFAULT_HOST =
	// "http://10.6.7.158:8080/cartoon/servlet/"; // 服务器地址

	public static final String DEFAULT_HOST = "http://www.timesyw.com:8080/cartoon/servlet/"; // 服务器地址

	/**
	 * POST 靖求方式
	 * 
	 * @param url
	 *            URL 链接
	 * @param pairs
	 *            传递的参数
	 * @return
	 */
	public static String postUrlData(String url, List<NameValuePair> pairs) {
		try {
			HttpPost httpPost = new HttpPost(DEFAULT_HOST + url);
			System.out.println("url::::::::::::" + DEFAULT_HOST + url);
			HttpClient httpclient = new DefaultHttpClient();
			// 请求超时
			httpclient.getParams()
					.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
							CONNECTION_TIMEOUT);
			// 读取超时
			httpclient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);

			if (pairs != null) {
				// 请求参数设置
				HttpEntity httpentity = new UrlEncodedFormEntity(pairs, "utf8");
				httpPost.setEntity(httpentity);
			}
			HttpResponse httpResponse = httpclient.execute(httpPost);
			// 判断是否成功
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(httpResponse.getEntity());
				return result;
			} else {
				System.out.println("HttpUrlHelper.postUrlData status code="
						+ httpResponse.getStatusLine().getStatusCode()
						+ " url=" + url);
			}
		} catch (Exception e) {
			System.out.println("HttpUrlHelper.postUrlData" + e.toString());
		}
		return "";
	}

	public static String getCartoonContent(int cartoon_id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("cartoon_id", cartoon_id);
		params.put("user_id", SharedUtils.getUserID());

		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		return postUrlData("FindCartoonContentByCartoonID", nv);
	}

	public static String getCartoonList(int category) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category", category);
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		return postUrlData("GetCartoonListServlet", nv);
	}

	public static String getPictureList(int category) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("category", category);
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		return postUrlData("PictureServlet", nv);
	}

	public static String getRecomment() {
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		return postUrlData("RecommendServlet", nv);
	}

	public static void payCartoon(int cartoon_id) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("cartoon_id", cartoon_id);
		params.put("user_id", SharedUtils.getUserID());
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		postUrlData("PayCartoonServlet", nv);
	}

	public static boolean addNewUser(String deviceid) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("user_id", deviceid);
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		String res = postUrlData("AddNewUserServlet", nv);
		if (res == null || "".equals(res)) {
			return false;
		}
		return Integer.valueOf(res) > 0;
	}

	public static void insertImage(int cartoon_id, int image_id,
			int cartoon_title_id, String cartoon_image_url) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("image_id", image_id);
		params.put("cartoon_id", cartoon_id);
		params.put("cartoon_title_id", cartoon_title_id);
		params.put("cartoon_image_url", cartoon_image_url);
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		postUrlData("InsertCartoonImageServlet", nv);
	}

	public static void insertCartoonContent(int cartoon_id,
			int cartoon_title_id, String cartoon_title) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("cartoon_id", cartoon_id);
		params.put("cartoon_title_id", cartoon_title_id);
		params.put("cartoon_title", cartoon_title);
		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		postUrlData("InsertCartoonContentServlet", nv);
	}

	public static void insertCartoon(int cartoon_id, String cartoon_title,
			String cartoon_category, String cartoon_author,
			String cartoon_cover_url, String cartoon_star,
			String cartoon_update, String cartoon_desc, int cartoon_type) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("cartoon_id", cartoon_id);
		params.put("cartoon_title", cartoon_title);
		params.put("cartoon_category", cartoon_category);
		params.put("cartoon_author", cartoon_author);
		params.put("cartoon_cover_url", cartoon_cover_url);
		params.put("cartoon_star", cartoon_star);
		params.put("cartoon_update", cartoon_update);
		params.put("cartoon_desc", cartoon_desc);
		params.put("cartoon_type", cartoon_type);

		List<NameValuePair> nv = new ArrayList<NameValuePair>();
		Iterator<?> iterator = params.entrySet().iterator();
		while (iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry testDemo = (Map.Entry) iterator.next();
			Object key = testDemo.getKey();
			Object value = testDemo.getValue();
			if (key != null && value != null) {
				nv.add(new BasicNameValuePair(key.toString(), value.toString()));
			}
		}
		postUrlData("InsertCartoonServlet", nv);
	}
}
