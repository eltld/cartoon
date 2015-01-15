package com.cutv.mobile.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;

public class MyUtils {

	public static final int NETWORK_NONE = 0;
	public static final int NETWORK_WIFI = 1;
	public static final int NETWORK_MOBILE = 2;

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static void showLog(String log) {
		System.out.println(log);
	}

	public static String get_duration_string(int duration) {
		if (duration < 0)
			duration = 0;

		String s = "";

		if (duration < 60) // 秒

			s = String.format("%d秒", duration);
		else if (duration < 60 * 60) // 分钟
		{
			int minute = duration / 60;
			int sec = duration % 60;
			s = String.format("%d分%d秒", minute, sec);
		} else {
			int hour = duration / 3600;
			int minute = (duration % 3600) / 60;
			int sec = (duration % 3600) % 60;
			s = String.format("%d小时%d分%d秒", hour, minute, sec);
		}

		return s;
	}

	public static String get_filename_from_url(String url, boolean bHasExt) {
		String[] p1 = url.split("\\/", -1);
		if (p1.length <= 0)
			return "";

		String fname = p1[p1.length - 1];

		if (bHasExt)
			return fname;

		String[] p2 = fname.split("\\.", -1);
		if (p2.length <= 0)
			return "";

		return p2[0];
	}

	public static String get_filename_ext_from_url(String url) {
		String[] p1 = url.split("\\/", -1);
		if (p1.length <= 0)
			return "";

		String fname = p1[p1.length - 1];

		String[] p2 = fname.split("\\.", -1);
		if (p2.length <= 1)
			return "";

		return p2[1];
	}

	public static String getTimeMaskString() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddhhmmssSS");
		String date = sDateFormat.format(new java.util.Date());

		return date;
	}

	public static long getTickCount() {
		Date dt = new Date();
		return dt.getTime();
	}

	public static boolean checkSdCardExists() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;

		return false;
	}

	public static boolean isHttpUrl(String url) {
		if (url != null && url.length() > 10
				&& url.substring(0, 7).compareToIgnoreCase("http://") == 0)
			return true;

		return false;
	}

	public static boolean FileExist(String filename) {
		File file = new File(filename);
		if (file.exists())
			return true;

		return false;
	}

	public static boolean PrivateFileExist(Context context, String filename) {
		if (filename == null || filename.length() < 1)
			return false;

		try {
			FileInputStream fin = context.openFileInput(filename);
			fin.close();
			return true;
		} catch (FileNotFoundException e) {

		} catch (Exception e) {

		}

		return false;
	}

	public static BitmapDrawable loadPrivateBitmapFile(Context context,
			String filename) {
		if (context == null || filename == null)
			return null;
		System.out.println("file::::::::::::" + filename);
		BitmapDrawable bd = null;
		try {
			FileInputStream fin = context.openFileInput(filename);

			BitmapDrawable bd1 = new BitmapDrawable(fin);
			fin.close();

			bd = bd1;
		} catch (Exception e) {

		}

		return bd;
	}

	public static BitmapDrawable loadBitmapFile(String filename) {
		if (filename == null)
			return null;

		BitmapDrawable bd = null;
		try {
			FileInputStream fin = new FileInputStream(filename);

			BitmapDrawable bd1 = new BitmapDrawable(fin);
			fin.close();

			bd = bd1;
		} catch (Exception e) {

		}

		return bd;
	}

	public static boolean copyPrivateFileToSDCard(Context context,
			String src_fname, String dst_fname) {
		try {
			FileInputStream fin = context.openFileInput(src_fname);

			FileOutputStream fout = new FileOutputStream(dst_fname);

			byte[] buf = new byte[1024];
			int len;

			while ((len = fin.read(buf)) > 0) {
				fout.write(buf, 0, len);
			}

			fout.close();
			fin.close();

			return true;
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean makeSureDirExists(String dirname) {
		File file = new File(dirname);
		if (file.exists())
			return true;

		return file.mkdirs();
	}

	public static boolean deleteFile(String filename) {
		File file = new File(filename);
		if (file.exists())
			return file.delete();

		return true;
	}

	public static String getStorageRootPath() {
		return Environment.getExternalStorageDirectory().getPath();
	}

	public static String GetSizeString(long size) {
		DecimalFormat df = new DecimalFormat();
		String style = "0.0";// 定义要显示的数字的格式
		df.applyPattern(style);// 将格式应用于格式化器

		if (size > 1024 * 1024 * 1024) // G
		{
			// sprintf(ch, "%.02fG", (float)size / (float)(1024 * 1024 * 1024));
			double d = (double) size / (double) (1024 * 1024 * 1024);
			df.applyPattern("0.0G");
			return df.format(d);
		} else if (size > 1024 * 1024) // M
		{
			// sprintf(ch, "%.02fM", (float)size / (float)(1024 * 1024));
			double d = (double) size / (double) (1024 * 1024);
			df.applyPattern("0.0M");
			return df.format(d);
		} else if (size > 1024) {

			;// sprintf(ch, "%.02fK", (float)size / (float)1024);
			double d = (double) size / (double) 1024;
			df.applyPattern("0.0K");
			return df.format(d);
		} else {

			;// sprintf(ch, "%.0fB", (float)size);
			double d = size;
			df.applyPattern("0.0B");
			return df.format(d);
		}
	}

	public static int getNetworkType(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况

		if (activeNetInfo != null) {

			if (activeNetInfo.isAvailable()) {
				if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
					// 判断WIFI网
					return NETWORK_WIFI;
				} else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
					// 判断3G网
					return NETWORK_MOBILE;
				}

				return NETWORK_MOBILE;
			}
		}

		return NETWORK_NONE;
	}

	public static boolean isValidURLString(String urlString) {
		if (urlString == null)
			return false;

		String s = urlString.toLowerCase();

		if (s.startsWith("http://"))
			return true;

		return false;
	}

	public static boolean isSDCardMounted() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state))
			return true;

		return false;
	}

	public static String getLocalImageFileNameByUrl(String url, String prefix) {
		String s = String.format("%s%s", prefix, MD5Util.getMD5Encoding(url));

		return s;
	}

	public static String getLocaldeviceId(Context _context) {
		TelephonyManager tm = (TelephonyManager) _context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String deviceId = tm.getDeviceId();
		if (deviceId == null || deviceId.trim().length() == 0) {
			deviceId = String.valueOf(System.currentTimeMillis());
		}
		return deviceId;
	}

	/**
	 * 获取应用的当前版本号
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String getVersionName(Context context) {
		String version = "";
		try {

			// 获取packagemanager的实例
			PackageManager packageManager = context.getPackageManager();
			// getPackageName()是你当前类的包名，0代表是获取版本信息
			PackageInfo packInfo;
			packInfo = packageManager.getPackageInfo(context.getPackageName(),
					0);
			version = packInfo.versionName;

		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return version;
	}

	public static String getImgSavePath() {
		String path = getRootDir() + "/CartoonImgSave/";
		File destDir = new File(path);
		if (!destDir.exists()) {// 创建文件�?
			destDir.mkdirs();
		}
		return path;

	}

	/**
	 * 获取根目�?
	 */
	public static String getRootDir() {
		return Environment.getExternalStorageDirectory().getAbsolutePath();
	}

	/**
	 * 使用当前时间戳拼接一个文件名
	 * 
	 * @param format
	 * @return
	 */
	public static String getFileName() {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SS");
		String fileName = format.format(new Timestamp(System
				.currentTimeMillis()));
		return fileName;
	}// - 通过 Intent.ACTION_MEDIA_SCANNER_SCAN_FILE 扫描某个文件

	public static void fileScan(Context context, String fName) {
		Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		Uri uri = Uri.fromFile(new File(fName));
		intent.setData(uri);
		context.sendBroadcast(intent);
	}

	/**
	 * 将Bitmap文件保存为本地文件
	 * 
	 * @param bmp
	 * @param filename
	 */
	public static void createImgToFile(Bitmap bmp, String filename) {
		FileOutputStream b = null;
		try {
			b = new FileOutputStream(filename);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Bitmap getDiskBitmap(String pathString) {
		Bitmap bitmap = null;
		try {
			File file = new File(pathString);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(pathString);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return bitmap;
	}

	public static Bitmap loadBitmapByFile(Context context, String filename) {
		if (context == null || filename == null)
			return null;
		Bitmap bd = null;
		try {
			FileInputStream fin = context.openFileInput(filename);
			bd = BitmapFactory.decodeStream(fin);
			fin.close();

		} catch (Exception e) {

		}

		return bd;
	}

}
