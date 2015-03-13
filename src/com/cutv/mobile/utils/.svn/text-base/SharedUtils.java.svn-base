package com.cutv.mobile.utils;

import com.cutv.mobile.cartoon.CartoonApplation;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedUtils {
	private static final String SP_NAME = "cartoon";
	private static SharedPreferences sharedPreferences = CartoonApplation
			.getInstance().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	private static Editor editor = sharedPreferences.edit();

	public static String getString(String key, String defaultValue) {
		return sharedPreferences.getString(key, defaultValue);
	}

	public static int getInt(String key, int defaultValue) {
		return sharedPreferences.getInt(key, defaultValue);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return sharedPreferences.getBoolean(key, defaultValue);
	}

	public static void setString(String key, String value) {
		editor.putString(key, value);
		editor.commit();

	}

	public static void setInt(String key, int value) {
		editor.putInt(key, value);
		editor.commit();
	}

	public static void setBoolean(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static String getUserID() {
		return sharedPreferences.getString("user_id", "");

	}

	public static void setUserId(String id) {
		editor.putString("user_id", id);
		editor.commit();

	}

	public static void setPayState(int cartoon_id, int state) {
		editor.putInt("pay_state_" + cartoon_id, state);
		editor.commit();
	}

	public static int getPayState(int cartoon_id) {
		return sharedPreferences.getInt("pay_state_" + cartoon_id, 0);

	}
}
