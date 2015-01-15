package com.cutv.mobile.component;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AsynDownloadImageTask {

	private int mMaxConcurrentTaskCount = 1;
	private final ArrayList<DownloadTask> mTaskList = new ArrayList<DownloadTask>();
	private final ReentrantLock mLock = new ReentrantLock();

	public AsynDownloadImageTask() {
	}

	public static BitmapDrawable getBitmapDrawable(Context ctx, String image_url) {
		Bitmap bmp = getBitmap(image_url);
		if (bmp == null)
			return null;

		return new BitmapDrawable(ctx.getResources(), bmp);
	}

	public static Drawable loadDrawableFromFile(Context context, String filename) {
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

			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Drawable frame = new BitmapDrawable(context.getResources(), bmp);

			// Bitmap bmp = BitmapFactory.decodeFile("filename”) ;
			// Drawable frame = new BitmapDrawable(bmp);

			return frame;

		} catch (Exception e) {

		}

		return null;
	}

	public static int downloadFile(String remote_file, Context context,
			String local_file) {

		try {
			URL url = new URL(remote_file);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				FileOutputStream fos = context.openFileOutput(local_file,
						Context.MODE_PRIVATE);

				byte[] buffer = new byte[1024];
				int len = 0;

				while ((len = is.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}

				fos.close();

				return 0;
			}

		} catch (Exception e) {
			System.out.println("e:::::::::::::" + e.toString());
		}

		return 1;
	}

	public static Bitmap getBitmap(String image_url) {
		try {
			URL url = new URL(image_url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(1000);
			conn.setRequestMethod("GET");
			conn.connect();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				InputStream is = conn.getInputStream();

				Bitmap bmp = BitmapFactory.decodeStream(is, null, null);
				if (is != null) {
					is.close();
					is = null;
				}

				if (conn != null) {
					conn.disconnect();
					conn = null;
				}

				return bmp;
			}
		} catch (Exception e) {

		}

		return null;
	}

	private class DownloadThread extends Thread {
		public DownloadTask mTaskObject = null;

		public void run() {

			int ret = 1;

			String imgUrl = mTaskObject.getImageUrl();

			System.out.println("downloading " + imgUrl);

			for (int i = 0; i < 2; i++) {
				if (mTaskObject.mSaveFilename == null
						|| mTaskObject.mContext == null)
					break;

				int iret = downloadFile(imgUrl, mTaskObject.mContext,
						mTaskObject.mSaveFilename);
				System.out.println("ret::::::::::::::" + iret);
				if (iret == 0) {
					Drawable bd = loadDrawableFromFile(mTaskObject.mContext,
							mTaskObject.mSaveFilename);
					if (bd != null) {
						mTaskObject.setDrawable(bd);
						ret = 0;
						break;
					}
				}
			}
			// BitmapDrawable bd = getBitmapDrawable(imgUrl);
			// if( bd != null )
			// {
			// // 保存到本地
			// if( mTaskObject.mSaveFilename != null && mTaskObject.mContext !=
			// null )
			// {
			// //================
			// try{
			// FileOutputStream fos =
			// mTaskObject.mContext.openFileOutput(mTaskObject.mSaveFilename,
			// Context.MODE_PRIVATE);
			// Bitmap bmp = bd.getBitmap();
			// if( bmp.compress(Bitmap.CompressFormat.JPEG, 100,fos) )
			// {
			// fos.flush();
			// }
			// fos.close();
			// mTaskObject.setDrawable(bd);
			// ret = 0;
			// }
			// catch(Exception e){
			// e.printStackTrace();
			// }
			// }
			// else
			// {
			// mTaskObject.setDrawable(bd);
			// ret = 0;
			// }
			//
			// break;
			// }
			// }

			// try{
			// Thread.sleep(5000);
			// }catch(Exception e){}
			//

			// 执行完毕
			Message msg = new Message();
			msg.what = ret;
			msg.obj = mTaskObject;
			handler.sendMessage(msg);

		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.obj != null) {
				DownloadTask taskObject = (DownloadTask) msg.obj;

				// 从队列中删除
				mLock.lock();

				mTaskList.remove(taskObject);
				for (int i = 0; i < mTaskList.size(); i++) {
					DownloadTask t = mTaskList.get(i);
					if (t != null && !t.isRunning() && !t.isCanceled()) {
						t.start();
						break;
					}
				}

				mLock.unlock();

				if (!taskObject.mbCanceled) {
					OnDownloadTaskListener listener = taskObject
							.getOnDownloadTaskListener();
					if (listener != null) {
						if (msg.what == 0)
							listener.onDownloadSuccessfully(taskObject);
						else
							listener.onDownloadFailed(msg.what, taskObject);
					}
				}
			}

		}
	};

	public void setMaxConcurrentTaskCount(int nMax) {
		mMaxConcurrentTaskCount = nMax;
	}

	public void cancelAll() {
		mLock.lock();

		for (int i = 0; i < mTaskList.size(); i++) {
			DownloadTask t = mTaskList.get(i);
			if (t != null) {
				t.cancel();
			}
		}

		mLock.unlock();
	}

	public int addTask(DownloadTask task) {
		mLock.lock();
		int nCnt = 0;
		for (int i = 0; i < mTaskList.size(); i++) {
			DownloadTask t = mTaskList.get(i);
			if (t != null && t.isRunning())
				nCnt++;
		}

		mTaskList.add(task);
		if (nCnt < mMaxConcurrentTaskCount) {
			task.start();
		}

		mLock.unlock();

		return 0;
	}

	public interface OnDownloadTaskListener {
		void onDownloadSuccessfully(DownloadTask downloadTask);

		void onDownloadFailed(int errCode, DownloadTask downloadTask);
	}

	public class DownloadTask {
		private boolean mbCanceled = false;
		private boolean mIsRunning = false;
		private DownloadThread mThread = null;
		private OnDownloadTaskListener onDownloadTaskListener = null;
		private String mImageUrl = null;
		private String mSaveFilename = null;
		private Drawable mDrawable = null;
		private Context mContext = null;

		public View contentView = null;

		public int mArg1 = 0;
		public int mArg2 = 0;

		public Map<String, Object> mMap = null;

		/*
		 * public DownloadTask(Context context,String url, String save_filename,
		 * int arg1, int arg2) { mImageUrl = url; mSaveFilename = save_filename;
		 * mArg1 = arg1; mArg2 = arg2; }
		 */
		public void setDrawable(Drawable d) {
			mDrawable = d;
		}

		public Drawable getDrawable() {
			return mDrawable;
		}

		public DownloadTask(Context context, String url, String save_filename,
				OnDownloadTaskListener l, Map<String, Object> map) {
			mContext = context;
			mImageUrl = url;
			mSaveFilename = save_filename;
			onDownloadTaskListener = l;

			mMap = map;
		}

		public DownloadTask(Context context, String url, String save_filename,
				OnDownloadTaskListener l, int arg1, int arg2) {
			mContext = context;
			mImageUrl = url;
			mSaveFilename = save_filename;
			onDownloadTaskListener = l;
			mArg1 = arg1;
			mArg2 = arg2;
		}

		public DownloadTask(Context context, String url, String save_filename,
				OnDownloadTaskListener l, View aContentView, int arg1, int arg2) {
			mContext = context;
			mImageUrl = url;
			mSaveFilename = save_filename;
			onDownloadTaskListener = l;

			mArg1 = arg1;
			mArg2 = arg2;

			contentView = aContentView;
		}

		public String getImageUrl() {
			return mImageUrl;
		}

		public boolean isRunning() {
			return mIsRunning;
		}

		public void start() {
			mbCanceled = false;
			mIsRunning = true;
			mThread = new DownloadThread();
			mThread.mTaskObject = this;
			mThread.start();
		}

		public void cancel() {
			mbCanceled = true;
			if (mThread != null)
				mThread.interrupt();
		}

		public boolean isCanceled() {
			return mbCanceled;
		}

		public void setOnDownloadTaskListener(OnDownloadTaskListener l) {
			onDownloadTaskListener = l;
		}

		public OnDownloadTaskListener getOnDownloadTaskListener() {
			return onDownloadTaskListener;
		}

	}

}
