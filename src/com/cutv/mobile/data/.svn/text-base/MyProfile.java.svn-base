package com.cutv.mobile.data;


import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;

public class MyProfile {
	
	public static String TAG = "CartoonLog";
	
	private static MyProfile myProfileInstance = null;
	private static final String PROFILE_NAME = "my.config";
	
	public static final String APK_DIRNAME = Environment.getExternalStorageDirectory().getPath() + "/cartoon/apk/";
	
	public final static boolean USE_ENCRYPT_API = false;
	
	public final static int max_download_task_count = 10;
	
	
	public final static String HTTP_USERAGENT = "CUTV,Android";
	
	
	public final static String client_version = "1.0";
	public static String client_token = "";
	
	
	public final static String http_user_agent = "Mozilla/5.0 (Android; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/1.0 Mobile/EC99E CUTV/A312 Safari/525.13";
	
	public static MyProfile getInstance()
	{
		if( myProfileInstance == null )
			myProfileInstance = new MyProfile();
		
		return myProfileInstance;
	}
	
	
	public static int get_network_wifi_config(Context context)
	{
		String key = "network_wifi";
		
		SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
		
		return pre.getInt(key, 1);
	}
	
	public static void save_network_wifi_config(Context context, int allow)
	{
		String key = "network_wifi";
		
		SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
		
		
		editor.putInt(key, allow);
		
		editor.commit();
	}	
	
	public static int get_network_edge_tips_config(Context context)
	{
		String key = "network_mobile";
		
		SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
		
		return pre.getInt(key, 1);
	}
	
	public static void save_network_edge_tips_config(Context context, int allow)
	{
		String key = "network_mobile";
		
		SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
		
		
		editor.putInt(key, allow);
		
		editor.commit();
	}
	
	
	public static int get_player_install_tips_config(Context context)
	{
		String key = "player_install_tips";
		
		SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
		
		return pre.getInt(key, 1);
	}
	
	public static void save_player_install_tips_config(Context context, int tips)
	{
		String key = "player_install_tips";
		
		SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
		
		
		editor.putInt(key, tips);
		
		editor.commit();
	}	
	
	
	public static void save_token(Context context, String token)
	{
		String key = "token";
		SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
		
		editor.putString(key, token);
		
		editor.commit();
	}
	
	public static String get_token(Context context)
	{
		String key = "token";
		
		SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
		
		return pre.getString(key, null);		
	}
	
	public static void save_hotkey_string(Context context, String s)
	{
		String key = "hotkey_string";
		SharedPreferences.Editor editor = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE).edit();
		
		editor.putString(key, s);
		
		editor.commit();
	}
	
	public static String get_hotkey_string(Context context)
	{
		String key = "hotkey_string";
		
		SharedPreferences pre = context.getSharedPreferences(PROFILE_NAME, Context.MODE_PRIVATE);
		
		return pre.getString(key, null);		
	}
	
}
