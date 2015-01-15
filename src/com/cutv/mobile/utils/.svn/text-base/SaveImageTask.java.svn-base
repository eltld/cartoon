package com.cutv.mobile.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;

public class SaveImageTask extends AsyncTask<Void, Void, Void> {
	private SaveImge callBack;
	private Bitmap bmp;
	private Context mContext;

	public void setCallBack(SaveImge callBack) {
		this.callBack = callBack;
	}

	public SaveImageTask(Bitmap bmp, Context mContext) {
		this.bmp = bmp;
		this.mContext = mContext;

	}

	@Override
	protected Void doInBackground(Void... params) {
		String name = MyUtils.getFileName() + ".jpg";
		String fileName = MyUtils.getImgSavePath() + name;
		MyUtils.createImgToFile(bmp, fileName);
		bmp.recycle();
		MyUtils.fileScan(mContext, fileName);

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		callBack.saveFinish();
	}

	public interface SaveImge {
		void saveFinish();
	}
}
