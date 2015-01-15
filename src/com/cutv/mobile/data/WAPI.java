package com.cutv.mobile.data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.util.Log;

import com.cutv.mobile.utils.MyUtils;

public class WAPI {

	private static WAPI myProfileInstance = null;

	// public final static String WAPI_BASE_URL =
	// "http://192.168.1.104/cartoon";
	// public final static String WAPI_BASE_URL =
	// "http://192.168.1.101/cartoon";
	// public final static String WAPI_BASE_URL =
	// "http://211.148.197.177:2013/cartoon";
	public final static String WAPI_BASE_URL = "http://www.timesyw.com:8001/cartoon";

	// 首页
	public final static String WAPI_HOME_URL = WAPI_BASE_URL + "/home.php";

	// 动画
	public final static String WAPI_ANIMATION_URL = WAPI_BASE_URL
			+ "/animation.php";

	// 图片
	public final static String WAPI_GET_PICTURE_URL = WAPI_BASE_URL
			+ "/picture.php";

	// 排行榜
	public final static String WAPI_GET_RANK_URL = WAPI_BASE_URL + "/rank.php";

	// 获取详情
	public final static String WAPI_GET_CONTENT_DETAIL_URL = WAPI_BASE_URL
			+ "/getcontentdetail.php";

	// 动画分类
	public final static String WAPI_GET_CATEGORY_LIST_URL = WAPI_BASE_URL
			+ "/category.php";

	// 检查升级
	public final static String WAPI_CHECK_VERSION_URL = WAPI_BASE_URL
			+ "/checkversion.php?client=android";

	public static WAPI getInstance() {
		if (myProfileInstance == null)
			myProfileInstance = new WAPI();

		return myProfileInstance;
	}

	public static String http_get_content(String url) {
		HttpGet request = new HttpGet(url);
		// request.setHeader("User-Agent", MyProfile.http_user_agent);

		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setParameter(
					CoreConnectionPNames.CONNECTION_TIMEOUT, 15000);
			httpClient.getParams().setParameter(
					CoreConnectionPNames.SO_TIMEOUT, 15000);
			HttpResponse response = httpClient.execute(request);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String str = EntityUtils.toString(response.getEntity());
				return str;
			} else {
				System.out.println("errorCode::::::::::"
						+ response.getStatusLine().getStatusCode());
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String getValueByTagName(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		if (nodeList != null && nodeList.getLength() > 0) {
			Element e = (Element) nodeList.item(0);
			if (e != null && e.getFirstChild() != null) {
				return e.getFirstChild().getNodeValue().trim();
			}
		}
		return "";
	}

	public static String getAttributeValueByTagName(Element element,
			String tagName) {
		String s = element.getAttribute(tagName);
		return s;
	}

	public static int getIntValueByTagName(Element element, String tagName) {
		String s = getValueByTagName(element, tagName);
		if (s == "")
			return 0;

		return Integer.parseInt(s);
	}

	public static Float getFloatValueByTagName(Element element, String tagName) {
		String s = getValueByTagName(element, tagName);
		if (s == "")
			return 0f;

		return Float.valueOf(s);
	}

	public static int getIntAttributeValueByTagName(Element element,
			String tagName) {
		String s = getAttributeValueByTagName(element, tagName);
		if (s == null || s.length() < 1)
			return 0;
		return Integer.parseInt(s);
	}

	public static String load_content_from_file(Context context, String filename) {
		try {
			FileInputStream fis = context.openFileInput(filename);
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = fis.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
			}
			outStream.close();

			byte[] data = outStream.toByteArray();
			return new String(data);
		} catch (Exception e) {
		}

		return null;
	}

	public static String get_content_from_remote_url(String url) {
		MyUtils.showLog(url);
		try {
			String scontent = http_get_content(url);
			if (scontent == null || scontent == "")
				return null;

			return scontent;
		} catch (Exception e) {

		}

		return null;

	}

	public static int save_to_private_file(Context context, String scontent,
			String filename) {
		int ret = 1;
		try {
			FileOutputStream fos = context.openFileOutput(filename,
					Context.MODE_PRIVATE);
			fos.write(scontent.getBytes());
			fos.close();
			ret = 0;
		} catch (Exception e) {
		}

		return ret;

	}

	// 首页接口
	public static String get_home_url_string() {
		String url = String.format("%s", WAPI_HOME_URL);

		Log.d(MyProfile.TAG, url);
		System.out.println("url:::::::::::::::::::" + url);

		return url;
	}

	public static int parse_home_content(String scontent,
			ArrayList<CategoryInfo> categoryList,
			ArrayList<RecommendInfo> recommendList) {
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		int ret = 1;

		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);

			// more
			Element root = doc.getDocumentElement();
			// data

			// 推荐大图
			NodeList recommendsNodeList = root
					.getElementsByTagName("recommends");
			if (recommendsNodeList != null
					&& recommendsNodeList.getLength() > 0) {
				Element e_recommends = (Element) recommendsNodeList.item(0);

				NodeList recommendNodeList = e_recommends
						.getElementsByTagName("recommend");
				if (recommendNodeList != null) {
					for (int i = 0; i < recommendNodeList.getLength(); i++) {
						Element node = (Element) recommendNodeList.item(i);
						RecommendInfo ri = new RecommendInfo();
						ri.id = Integer.parseInt(node.getAttribute("id"));
						ri.title = node.getAttribute("title");
						ri.imageUrl = node.getAttribute("imageurl");
						ri.action = node.getAttribute("action");
						ri.link = node.getAttribute("link");
						ri.param1 = node.getAttribute("param1");
						ri.param2 = node.getAttribute("param2");
						System.out
								.println("title::::::::::::::::::" + ri.title);
						recommendList.add(ri);
					}
				}
			}

			// 数据
			NodeList nodes = root.getElementsByTagName("categorys");
			if (nodes != null && nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				nodes = e.getElementsByTagName("category");
				if (nodes != null) {
					for (int i = 0; i < nodes.getLength(); i++) {
						e = (Element) nodes.item(i);

						CategoryInfo categoryInfo = new CategoryInfo();
						categoryInfo.id = getIntAttributeValueByTagName(e, "id");
						categoryInfo.title = getAttributeValueByTagName(e,
								"title");
						categoryInfo.more = getAttributeValueByTagName(e,
								"more");

						NodeList itemNodes = e.getElementsByTagName("item");
						if (itemNodes != null) {
							for (int j = 0; j < itemNodes.getLength(); j++) {
								Element eItem = (Element) itemNodes.item(j);

								ContentInfo contentInfo = new ContentInfo();
								contentInfo.id = getIntAttributeValueByTagName(
										eItem, "id");
								contentInfo.title = getAttributeValueByTagName(
										eItem, "title");
								contentInfo.update = getAttributeValueByTagName(
										eItem, "update");
								contentInfo.imgurl = getAttributeValueByTagName(
										eItem, "imageurl");

								categoryInfo.contentList.add(contentInfo);
							}
						}

						categoryList.add(categoryInfo);

					}
				}
			}

			ret = 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	// 动画列表接口
	public static String get_picture_url_string(int categoryId) {
		String url;
		if (WAPI_GET_PICTURE_URL.indexOf("?") > 0)
			url = String.format("%s&cid=%d", WAPI_GET_PICTURE_URL, categoryId);
		else
			url = String.format("%s?cid=%d", WAPI_GET_PICTURE_URL, categoryId);

		Log.d(MyProfile.TAG, url);

		return url;
	}

	// 动画列表接口
	public static String get_rank_url_string() {
		String url = String.format("%s", WAPI_GET_RANK_URL);

		Log.d(MyProfile.TAG, url);

		return url;
	}

	// 动画列表接口
	public static String get_animation_url_string(int contentType,
			int categoryId) {
		String url;
		if (WAPI_ANIMATION_URL.indexOf("?") > 0)
			url = String.format("%s&type=%d&cid=%d", WAPI_ANIMATION_URL,
					contentType, categoryId);
		else
			url = String.format("%s?type=%d&cid=%d", WAPI_ANIMATION_URL,
					contentType, categoryId);

		Log.d(MyProfile.TAG, url);
		System.out.println("url:::::::::::::::::::" + url);
		return url;
	}

	public static int parse_content_list_response(String scontent,
			ArrayList<ContentInfo> dataList, Map<String, Object> userInfo) {
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		int ret = 1;

		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			Element root = doc.getDocumentElement();

			// 数据
			NodeList nodes = root.getElementsByTagName("list");

			if (nodes != null && nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				String nextpage = getAttributeValueByTagName(e, "nextpage");
				if (nextpage != null && nextpage.length() > 0)
					userInfo.put("nextpage", nextpage);
				nodes = e.getElementsByTagName("item");
				if (nodes != null) {
					for (int i = 0; i < nodes.getLength(); i++) {
						e = (Element) nodes.item(i);
						ContentInfo ci = new ContentInfo();
						ci.id = getIntValueByTagName(e, "id");
						ci.title = getValueByTagName(e, "title");
						ci.contentType = getIntValueByTagName(e, "type");
						ci.category = getValueByTagName(e, "category");
						ci.price = getFloatValueByTagName(e, "price");
						ci.author = getValueByTagName(e, "author");
						ci.update = getValueByTagName(e, "update");
						ci.imgurl = getValueByTagName(e, "imageurl");
						dataList.add(ci);
					}
				}
			}

			ret = 0;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	// 详情页接口
	public static String get_content_detail_url_string(int contentId) {
		String url;
		if (WAPI_GET_CONTENT_DETAIL_URL.indexOf("?") > 0)
			url = String.format("%s&id=%d", WAPI_GET_CONTENT_DETAIL_URL,
					contentId);
		else
			url = String.format("%s?id=%d", WAPI_GET_CONTENT_DETAIL_URL,
					contentId);

		Log.d(MyProfile.TAG, url);
		System.out.println("url:::::::::::::::::::" + url);
		return url;
	}

	public static int parse_content_detail(String scontent,
			ContentInfo contentInfo) {
		System.out.println(scontent);
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		int ret = 1;

		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);

			Element root = doc.getDocumentElement();

			// 数据
			NodeList nodes = root.getElementsByTagName("content");
			if (nodes != null && nodes.getLength() > 0) {
				ret = 0;

				Element e = (Element) nodes.item(0);

				contentInfo.id = getIntValueByTagName(e, "id");
				contentInfo.title = getValueByTagName(e, "title");
				contentInfo.contentType = getIntValueByTagName(e, "type");
				contentInfo.imgurl = getValueByTagName(e, "imageurl");
				contentInfo.author = getValueByTagName(e, "author");
				contentInfo.desc = getValueByTagName(e, "desc");
				contentInfo.update = getValueByTagName(e, "update");
				contentInfo.star = getIntValueByTagName(e, "star");
				contentInfo.category = getValueByTagName(e, "category");
				contentInfo.pay_code = getValueByTagName(e, "pay_code");
				contentInfo.pay_state = getIntValueByTagName(e, "pay_state");
				contentInfo.price = getFloatValueByTagName(e, "price");
				// HttpUrlHelper.insertCartoon(contentInfo.id,
				// contentInfo.title,
				// contentInfo.category, contentInfo.author,
				// contentInfo.imgurl, contentInfo.star + "",
				// contentInfo.update, contentInfo.desc,
				// contentInfo.contentType);
				nodes = e.getElementsByTagName("contentlist");
				if (nodes != null && nodes.getLength() > 0) {
					e = (Element) nodes.item(0);
					nodes = e.getElementsByTagName("episode");
					if (nodes != null && nodes.getLength() > 0) {
						for (int i = 0; i < nodes.getLength(); i++) {
							e = (Element) nodes.item(i);

							ContentInfo episodeInfo = new ContentInfo();
							episodeInfo.id = getIntAttributeValueByTagName(e,
									"id");
							episodeInfo.title = getAttributeValueByTagName(e,
									"title");
							episodeInfo.link = getAttributeValueByTagName(e,
									"link");
							// HttpUrlHelper.insertCartoonContent(contentInfo.id,
							// episodeInfo.id, episodeInfo.title);
							NodeList itemList = e.getElementsByTagName("item");
							if (itemList != null && itemList.getLength() > 0) {
								for (int j = 0; j < itemList.getLength(); j++) {
									e = (Element) itemList.item(j);

									ContentInfo itemInfo = new ContentInfo();
									itemInfo.id = getIntAttributeValueByTagName(
											e, "id");
									itemInfo.title = getAttributeValueByTagName(
											e, "title");
									itemInfo.link = getAttributeValueByTagName(
											e, "link");
									// HttpUrlHelper.insertImage(contentInfo.id,
									// itemInfo.id, episodeInfo.id,
									// itemInfo.link);
									episodeInfo.subList.add(itemInfo);

								}
							}

							contentInfo.subList.add(episodeInfo);
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	// 首页接口
	public static String get_category_list_url_string() {
		String url = String.format("%s", WAPI_GET_CATEGORY_LIST_URL);

		Log.d(MyProfile.TAG, url);

		return url;
	}

	public static int parse_category_detail(String scontent,
			List<CategoryInfo> categoryList) {
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		int ret = 1;
		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			Element root = doc.getDocumentElement();
			// 数据
			NodeList nodes = root.getElementsByTagName("list");
			if (nodes != null && nodes.getLength() > 0) {
				Element e = (Element) nodes.item(0);
				nodes = e.getElementsByTagName("category");
				if (nodes != null && nodes.getLength() > 0) {
					ret = 0;
					for (int i = 0; i < nodes.getLength(); i++) {
						e = (Element) nodes.item(i);

						CategoryInfo ci = new CategoryInfo();
						ci.id = getIntAttributeValueByTagName(e, "id");
						ci.title = getAttributeValueByTagName(e, "title");

						categoryList.add(ci);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}

	/*
	 * <response> <result><code>0</code><message/></result> <versioninfo>
	 * <version>1.0</version> <force>yes</force>
	 * <desc>有新版本了，更多精彩内容，快来体验吧！</desc>
	 * <downloadurl>http://itunes.apple.com/cn/app
	 * /cutv/id492810917?l=en%26mt=8</downloadurl> </versioninfo> </response>
	 */

	public static int parse_client_version_info(String scontent,
			ArrayList<String> fieldList) {
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		int ret = 1;

		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);

			// more
			Element root = doc.getDocumentElement();
			// data

			NodeList listNode = root.getElementsByTagName("versioninfo");
			if (listNode != null && listNode.getLength() > 0) {
				Element e = (Element) listNode.item(0);
				String version = getValueByTagName(e, "version");
				String desc = getValueByTagName(e, "desc");
				String downloadurl = getValueByTagName(e, "downloadurl");
				String force = getValueByTagName(e, "force");
				if (force == null || force.length() < 1)
					force = "no";

				fieldList.add(version);
				fieldList.add(desc);
				fieldList.add(downloadurl);
				fieldList.add(force);

				ret = 0;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static int parse_picture_content(String scontent,
			List<CategoryInfo> categoryList, ContentInfo contentinfo) {
		InputStream inputStream = new ByteArrayInputStream(scontent.getBytes());

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;

		int ret = 1;

		try {
			db = dbf.newDocumentBuilder();
			Document doc = db.parse(inputStream);
			Element root = doc.getDocumentElement();
			// 分类列表
			NodeList categoryNodeList = root
					.getElementsByTagName("category_list");
			if (categoryNodeList != null && categoryNodeList.getLength() > 0) {
				Element e = (Element) categoryNodeList.item(0);
				NodeList itemList = e.getElementsByTagName("item");
				if (itemList != null && itemList.getLength() > 0) {
					for (int j = 0; j < itemList.getLength(); j++) {
						e = (Element) itemList.item(j);
						CategoryInfo itemInfo = new CategoryInfo();
						itemInfo.id = getIntAttributeValueByTagName(e,
								"category_id");
						itemInfo.title = getAttributeValueByTagName(e,
								"category_name");
						categoryList.add(itemInfo);
					}
				}
			}

			// 数据
			NodeList pictureNodeList = root.getElementsByTagName("list");
			if (pictureNodeList != null && pictureNodeList.getLength() > 0) {
				Element e = (Element) pictureNodeList.item(0);
				NodeList itemList = e.getElementsByTagName("item");
				if (itemList != null && itemList.getLength() > 0) {
					for (int j = 0; j < itemList.getLength(); j++) {
						e = (Element) itemList.item(j);
						ContentInfo itemInfo = new ContentInfo();
						itemInfo.imgurl = getAttributeValueByTagName(e,
								"picture_url");
						itemInfo.img_smal_url = getAttributeValueByTagName(e,
								"picture_smal_url");

						contentinfo.subList.add(itemInfo);
					}
				}
			}
			ret = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret;
	}
}
