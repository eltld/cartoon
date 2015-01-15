package com.cutv.mobile.cartoon;

import android.app.Application;

public class CartoonApplation extends Application {
	private static CartoonApplation instance;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}

	public static CartoonApplation getInstance() {
		return instance;
	}
}
