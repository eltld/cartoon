package com.cutv.mobile.data;

import java.io.Serializable;

import android.view.View;

public class RecommendInfo implements Serializable, Cloneable {
	private static final long serialVersionUID = -7060210544600464481L;

	public int id;
	public String title;
	public String imageUrl;
	public String action;
	public String link;
	public String param1;
	public String param2;
	public int model;

	// 程序内使用，非协议字段
	View contentView;

	public RecommendInfo() {
		contentView = null;
	}

	public Object clone() {
		try {
			return super.clone();
		} catch (Exception e) {
			return null;
		}
	}

}
