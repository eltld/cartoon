package com.cutv.mobile.cartoon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

import com.cutv.mobile.component.CustomUI;
import com.cutv.mobile.data.MyProfile;
import com.cutv.mobile.data.WAPI;
import com.cutv.mobile.utils.MyUtils;

public class ClientUpgrade {

	private Context _context;
	private Thread _download_thread;

	private String _downloadApkUrl = null;
	private String _savePath = null;
	private String _saveFilename = null;
	private boolean _interceptFlag = false;
	private int _progress = 0;
	private ProgressBar _progressBar;

	private AlertDialog _downloadDialog;

	private final static int DOWN_UPDATE = 1;
	private final static int DOWN_OVER = 2;
	private final static int DOWN_ERROR = 10;

	public ClientUpgrade(Context context) {
		_context = context;
	}

	private class CheckVersionAsyncTask extends
			AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return checkVersion();
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null)
				return;

			String[] ss = result.split("\\|");
			Log.i("", "result=" + ss.length);
			if (ss == null || ss.length != 4)
				return;

			boolean force = false;
			if (ss[3].equalsIgnoreCase("yes"))
				force = true;

			_downloadApkUrl = ss[2];
			// _downloadApkUrl =
			// "http://a.apk.anzhi.com/apk/201209/28/cn.goapk.market_49302800_0.apk";
			String[] tt = _downloadApkUrl.split("/");
			if (tt.length < 2)
				return;

			if (MyUtils.checkSdCardExists()) {
				_savePath = MyProfile.APK_DIRNAME;
				_saveFilename = String.format("%s%s", _savePath,
						tt[tt.length - 1]);
			} else {
				_savePath = "";
				_saveFilename = String.format("%s/%s", _context.getFilesDir(),
						tt[tt.length - 1]);
			}

			Log.d("", "path=" + _savePath);
			Log.d("", "filename=" + _saveFilename);

			String msg = ss[1];
			if (msg.length() <= 0) {
				msg = String.format("新版本%s出来了，赶快升级吧！", ss[0]);
			}

			Dialog dlg;

			if (force) {
				dlg = new AlertDialog.Builder(_context)
						.setTitle("提示")
						.setMessage(msg)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downloadApk();
									}
								}).create();

			} else {
				dlg = new AlertDialog.Builder(_context)
						.setTitle("提示")
						.setMessage(msg)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										downloadApk();

									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
									}
								}).create();
			}
			dlg.show();

			// if( force )
			// MainActivity.showAlert("force-upgrade-alert", "有新版本了",
			// msg,"现在升级_black,取消_black", _context, ClientUpgrade.this);
			// else
			// MainActivity.showAlert("upgrade-alert", "有新版本了",
			// msg,"现在升级_black,以后再说_black", _context, ClientUpgrade.this);
		}

	}

	public void startCheckVersion() {
		CheckVersionAsyncTask ct = new CheckVersionAsyncTask();
		ct.execute(null, null, null);
	}

	private void downloadApk() {
		showDownloadDialog();

		_download_thread = new Thread(mDownloadRunnable);
		_download_thread.start();
	}

	private Runnable mDownloadRunnable = new Runnable() {

		@Override
		public void run() {
			try {
				URL url = new URL(_downloadApkUrl);

				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();

				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();

				if (_savePath.length() > 0) {
					File file = new File(_savePath);
					if (!file.exists())
						file.mkdirs();
				}

				String apkFilename = _saveFilename;
				File apkFile = new File(apkFilename);
				FileOutputStream fos = new FileOutputStream(apkFile);

				// FileOutputStream fos = _context.openFileOutput(_saveFilename,
				// Context.MODE_PRIVATE);
				int count = 0;
				byte buf[] = new byte[1024];

				do {
					int numread = is.read(buf);
					count += numread;

					Log.i("", "download: " + count + " total: " + length);
					_progress = (int) (((float) count / length) * 100);

					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}

					fos.write(buf, 0, numread);
				} while (!_interceptFlag);

				fos.close();
				is.close();

			} catch (Exception e) {
				e.printStackTrace();
				mHandler.sendEmptyMessage(DOWN_ERROR);
			}
		}

	};

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				_progressBar.setProgress(_progress);
				_downloadDialog.setTitle(String.format("正在下载：%d%%", _progress));
				break;
			case DOWN_OVER:
				_progress = 100;
				_progressBar.setProgress(_progress);
				_downloadDialog.setTitle(String.format("正在下载：%d%%", _progress));
				_downloadDialog.dismiss();
				// MainActivity.getInstance().finish();
				installApk();
				break;
			case DOWN_ERROR:
				_downloadDialog.dismiss();
				CustomUI.showAlertDialog(_context, "提示", "下载失败！");
				break;
			default:
				break;
			}
		}
	};

	private int installApk() {
		File file = new File(_saveFilename);
		if (!file.exists())
			return 1;

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.parse("file://" + file.toString()),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// String s = String.format("%s/%s", _context.getFilesDir(),
		// _saveFilename);
		// Log.d("", s);
		// intent.setDataAndType(Uri.parse("file://" + s),
		// "application/vnd.android.package-archive");
		_context.startActivity(intent);

		return 0;
	}

	private void showDownloadDialog() {
		AlertDialog.Builder builder = new Builder(_context);

		builder.setTitle("正在下载");

		/*
		 * LinearLayout ll = new LinearLayout(_context);
		 * ll.setOrientation(LinearLayout.VERTICAL);
		 * ll.setGravity(Gravity.CENTER);
		 * 
		 * _progressBar = new ProgressBar(_context, null,
		 * android.R.attr.progressBarStyleHorizontal);
		 * _progressBar.setIndeterminate(false);
		 * _progressBar.setMinimumHeight(MyUtils.dip2px(_context, 20));
		 * 
		 * LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-1,
		 * -1); llp.setMargins(MyUtils.dip2px(_context,10), 0,
		 * MyUtils.dip2px(_context,10), 0); ll.addView(_progressBar, llp);
		 */
		View ll = LayoutInflater.from(_context).inflate(
				R.layout.client_upgrade, null);
		_progressBar = (ProgressBar) ll.findViewById(R.id.progressBar1);
		builder.setView(ll);

		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				_interceptFlag = true;
			}

		});

		_downloadDialog = builder.create();
		_downloadDialog.show();

	}

	public static boolean hasNewVersion(String local_version_string,
			String server_version_string) {
		String[] local_version = local_version_string.split("\\.");
		String[] server_version = server_version_string.split("\\.");

		Log.i("", "local=" + local_version.length);
		Log.i("", "server=" + server_version.length);

		for (int i = 0; i < local_version.length || i < server_version.length; i++) {
			int cur_code, new_code;
			if (i >= local_version.length)
				cur_code = 0;
			else
				cur_code = Integer.parseInt(local_version[i]);

			if (i >= server_version.length)
				new_code = 0;
			else
				new_code = Integer.parseInt(server_version[i]);

			if (new_code > cur_code)
				return true;
			else if (new_code < cur_code)
				return false;

		}

		return false;
	}

	public String checkVersion() {
		try {
			String urlString = String.format("%s&platform=android&version=%s",
					WAPI.WAPI_CHECK_VERSION_URL, MyProfile.client_version);

			String content = WAPI.get_content_from_remote_url(urlString);
			if (content == null)
				return null;

			ArrayList<String> fieldList = new ArrayList<String>();
			int iret = WAPI.parse_client_version_info(content, fieldList);
			if (iret == 0 && fieldList.size() == 4) {
				String version = fieldList.get(0);
				String desc = fieldList.get(1);
				String downloadurl = fieldList.get(2);
				String force = fieldList.get(3);

				if (hasNewVersion(MyProfile.client_version, version)) {
					;//
					String result = String.format("%s|%s|%s|%s", version, desc,
							downloadurl, force);
					return result;
				}
			}

		} catch (Exception e) {

		}

		return null;

	}
}
