package com.cutv.mobile.cartoon;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cutv.mobile.component.AsynDownloadImageTask;
import com.cutv.mobile.component.AsynDownloadImageTask.DownloadTask;
import com.cutv.mobile.component.AsynDownloadImageTask.OnDownloadTaskListener;
import com.cutv.mobile.data.DataModule;
import com.cutv.mobile.photoview.PhotoView;
import com.cutv.mobile.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.cutv.mobile.utils.MyUtils;
import com.cutv.mobile.utils.SaveImageTask;
import com.cutv.mobile.utils.SaveImageTask.SaveImge;
import com.cutv.mobile.utils.SharedUtils;

public class ShowPictureActivity extends BaseActivity implements
		OnClickListener, OnPageChangeListener {
	private ImageView img_back;
	private ImageView img_save;
	private TextView txt_indicator;
	ViewPager _viewPager = null;
	private RelativeLayout lay;

	List<View> _viewList = new ArrayList<View>();

	List<String> _dataList = new ArrayList<String>();

	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();
	private String titile = "";
	private int itemIndex = -1;
	private int selectIndex;
	private MyCustomAdapter adapter;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_slide);
		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();
		_dataList = (ArrayList<String>) b.getSerializable("dataList");
		itemIndex = getIntent().getIntExtra("itemIndex", -1);
		initUI();

	}

	void initUI() {
		img_save = (ImageView) findViewById(R.id.imgSave);
		img_save.setVisibility(View.VISIBLE);
		lay = (RelativeLayout) findViewById(R.id.layTitle);
		lay.getBackground().setAlpha(120);
		img_back = (ImageView) findViewById(R.id.imgBack);
		txt_indicator = (TextView) findViewById(R.id.indicator);
		_viewPager = (ViewPager) this.findViewById(R.id.viewPager);
		adapter = new MyCustomAdapter();
		_viewPager.setAdapter(adapter);
		_viewPager.setCurrentItem(itemIndex);
		txt_indicator.setText(getString(R.string.viewpager_indicator,
				itemIndex + 1, _viewPager.getAdapter().getCount()));

		setListener();
	}

	private void setListener() {
		img_back.setOnClickListener(this);
		_viewPager.setOnPageChangeListener(this);
		img_save.setOnClickListener(this);

	}

	View getItemView(int position) {
		for (int i = 0; i < _viewList.size(); i++) {
			View v = _viewList.get(i);
			Integer tag = (Integer) v.getTag();
			if (tag == position) {
				return v;
			}
		}

		if (position >= 0)
			return null;

		FrameLayout llItem = new FrameLayout(this);
		llItem.setBackgroundColor(Color.BLACK);
		llItem.setTag(-1);
		PhotoView imgView = new PhotoView(this);
		imgView.setTag("img");
		FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		llp.gravity = Gravity.CENTER;
		llItem.addView(imgView, llp);
		// 添加等待框
		llp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		llp.gravity = Gravity.CENTER;
		ProgressBar bar = new ProgressBar(this);
		bar.setTag("bar");
		bar.setVisibility(View.GONE);
		llItem.addView(bar, llp);
		_viewList.add(llItem);
		String s = String.format("new view, size=%d", _viewList.size());
		MyUtils.showLog(s);
		return llItem;

	}

	class MyCustomAdapter extends PagerAdapter implements
			OnDownloadTaskListener {

		@Override
		public int getCount() {
			return _dataList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			String s = String.format("remove %d", position);
			MyUtils.showLog(s);

			View v = getItemView(position);
			if (v != null) {
				container.removeView(v);
				v.setTag(-1);
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) { // 这个方法用来实例化页卡
			String s = String.format("show %d", position);
			MyUtils.showLog(s);

			Context ctx = ShowPictureActivity.this;

			View v = getItemView(-1);
			v.setTag(position);
			ProgressBar bar = (ProgressBar) v.findViewWithTag("bar");
			PhotoView imgView = (PhotoView) v.findViewWithTag("img");
			imgView.setOnPhotoTapListener(new OnPhotoTapListener() {

				@Override
				public void onPhotoTap(View view, float x, float y) {
					if (lay.getVisibility() != View.VISIBLE) {
						lay.setVisibility(View.VISIBLE);
						lay.setAnimation(AnimationUtils.loadAnimation(
								ShowPictureActivity.this, R.anim.up_in));
					} else {
						lay.setVisibility(View.GONE);
						lay.setAnimation(AnimationUtils.loadAnimation(
								ShowPictureActivity.this, R.anim.up_out));
					}
				}
			});
			if (imgView != null) {
				String link = _dataList.get(position);
				if (MyUtils.isValidURLString(link)) {
					String imgfile = DataModule.getLocalImageFileNameByUrl(
							link, "image-");
					if (MyUtils.PrivateFileExist(ctx, imgfile)) {
						imgView.setImageDrawable(MyUtils.loadPrivateBitmapFile(
								ctx, imgfile));
					} else {
						imgView.setImageResource(R.drawable.bg_cover);
						// 不存在，去下载
						_downloader.addTask(_downloader.new DownloadTask(ctx,
								link, imgfile, this, v, position, 0));
						bar.setVisibility(View.VISIBLE);
					}
				}

			}

			container.addView(v, 0);// 添加页卡
			return v;
		}

		@Override
		public void onDownloadSuccessfully(DownloadTask downloadTask) {
			int position = downloadTask.mArg1;
			View convertView = downloadTask.contentView;

			Integer key1 = (Integer) convertView.getTag();
			if (key1 != position) {
				return;
			}
			ProgressBar bar = (ProgressBar) convertView.findViewWithTag("bar");
			if (bar != null) {
				bar.setVisibility(View.GONE);
			}
			ImageView imgView = (ImageView) convertView.findViewWithTag("img");
			if (imgView != null) {
				imgView.setImageDrawable(downloadTask.getDrawable());
			}

		}

		@Override
		public void onDownloadFailed(int errCode, DownloadTask downloadTask) {

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (itemIndex == -1) {
			SharedUtils.setInt(titile + "_position",
					_viewPager.getCurrentItem());
		}
	}

	@SuppressLint("ShowToast")
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imgBack:
			finish();
			break;
		case R.id.imgSave:
			img_save.setEnabled(false);
			String imgfile = DataModule.getLocalImageFileNameByUrl(
					_dataList.get(selectIndex), "image-");
			Bitmap bmp = MyUtils.loadBitmapByFile(this, imgfile);
			if (bmp != null) {
				SaveImageTask task = new SaveImageTask(bmp, this);
				task.setCallBack(new SaveImge() {
					@Override
					public void saveFinish() {
						img_save.setEnabled(true);
						Toast.makeText(ShowPictureActivity.this, "保存成功",
								Toast.LENGTH_SHORT).show();

					}
				});
				task.execute();
			} else {
				Toast.makeText(ShowPictureActivity.this, "保存失败",
						Toast.LENGTH_SHORT).show();
				img_save.setEnabled(true);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int index) {
		txt_indicator.setText(getString(R.string.viewpager_indicator,
				index + 1, _viewPager.getAdapter().getCount()));
		selectIndex = index;
	}

}
