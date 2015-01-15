package com.cutv.mobile.data;

import java.io.Serializable;
import java.util.ArrayList;

public class ContentInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = -7060210544600464482L;

	public static final int CONTENT_TYPE_ANIMATION = 0; // ¶¯»­
	public static final int CONTENT_TYPE_CARTOON = 1; // Âþ»­

	public int id;
	public int contentType;
	public String title;
	public String imgurl;
	public String img_smal_url;
	public float price;
	public String category;
	public String author;
	public String update;
	public int star;
	public String desc;
	public String pay_code = "";
	public int pay_state;
	public String link;

	public ArrayList<ContentInfo> subList = new ArrayList<ContentInfo>();

	public ContentInfo() {
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}

}
