package com.cutv.mobile.cartoon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cutv.mobile.component.AsynDownloadImageTask;
import com.cutv.mobile.component.AsynDownloadImageTask.DownloadTask;
import com.cutv.mobile.component.AsynDownloadImageTask.OnDownloadTaskListener;
import com.cutv.mobile.component.CustomUI;
import com.cutv.mobile.data.CategoryInfo;
import com.cutv.mobile.data.ContentInfo;
import com.cutv.mobile.data.DataModule;
import com.cutv.mobile.data.WAPI;
import com.cutv.mobile.utils.CategoryPopwindow;
import com.cutv.mobile.utils.CategoryPopwindow.OnPopWindowItemcCick;
import com.cutv.mobile.utils.HttpUrlHelper;
import com.cutv.mobile.utils.MyUtils;

public class PictureFragmentPage extends BaseFragment implements
		OnClickListener, OnScrollListener, OnPopWindowItemcCick {

	private static String TAG = "PicturePage";

	private ListView _listView;

	private Dialog _loadingDlg = null;

	private MyCustomAdapter _adapter = null;

	// private List<ContentInfo> _dataList = new ArrayList<ContentInfo>();
	private String _nextPageUrlString = null;
	private View _moreView = null;

	private boolean _moreLoading = false;
	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();

	private View _retryView = null;

	private List<CategoryInfo> _categoryList = new ArrayList<CategoryInfo>();

	private int _currentCategoryId = 1; // 1壁纸

	private int _pictureWidth = 0;
	private int _pictureHeight = 0;

	private CategoryPopwindow popwindow;

	private ContentInfo contentInfo = new ContentInfo();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_picture, null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		_listView = (ListView) view.findViewById(R.id.listView1);
		_listView.setVisibility(View.INVISIBLE);
		_listView.setOnScrollListener(this);
		_adapter = new MyCustomAdapter(getActivity());
		_listView.setAdapter(_adapter);
		Button btn = getLeftButton();
		if (btn != null) {
			// btn.setOnClickListener(this);
			btn.setVisibility(View.GONE);
		}

		btn = getRightButton();
		if (btn != null) {
			// btn.setOnClickListener(this);
			btn.setVisibility(View.GONE);
		}

		setTitle("壁纸");

		TextView titleTextView = (TextView) this.getView().findViewById(
				R.id.title_center_txt);
		if (titleTextView != null) {
			titleTextView.setOnClickListener(this);
		}

		// 计算图片需要的高度
		DisplayMetrics metric = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metric);

		int screenWidth = metric.widthPixels;

		_pictureWidth = screenWidth / 3;

		_pictureHeight = _pictureWidth * 320 / 213;

		doLoadCategory();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void doLoadCategory() {
		_loadingDlg = CustomUI
				.createLoadingDialog(this.getActivity(), "加载中...");
		_loadingDlg.show();

		_nextPageUrlString = WAPI.get_picture_url_string(_currentCategoryId);

		LoadThread loadThread = new LoadThread();
		loadThread.start();
	}

	private void doLoadData(boolean showLoading) {
		if (showLoading) {
			if (_loadingDlg == null) {
				_loadingDlg = CustomUI.createLoadingDialog(this.getActivity(),
						"加载中...");
				_loadingDlg.show();
			}
		}
		_nextPageUrlString = WAPI.get_picture_url_string(_currentCategoryId);

		LoadThread loadThread = new LoadThread();
		loadThread.start();
	}

	// private void doLoadMore() {
	// LoadThread loadThread = new LoadThread();
	// loadThread.type = LoadThread.LOAD_TYPE_MORE;
	// loadThread.start();
	// }

	private class LoadThread extends Thread {

		public int loadCategory() {
			int ret = 1;
			String api_string = WAPI.get_category_list_url_string();

			// 请求接口
			// String response = WAPI.http_get_content(api_string);
			String response = HttpUrlHelper.getPictureList(_currentCategoryId);

			if (response.length() > 0) {
				// 请求成功
				contentInfo.subList.clear();
				_categoryList.clear();

				ret = WAPI.parse_picture_content(response, _categoryList,
						contentInfo);
			}

			return ret;
		}

		public void run() {
			int ret = 1;
			try {
				ret = loadCategory();

			} catch (Exception e) {
			}

			// 给主线程发消息
			Message msg = new Message();
			msg.what = ret;
			handler.sendMessage(msg);
		}

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 加载完成
			if (msg.what == 0) {
				_adapter.notifyDataSetChanged();
				_listView.setVisibility(View.VISIBLE);
				_loadingDlg.dismiss();
				_loadingDlg = null;
			} else {
				_loadingDlg.dismiss();
				_loadingDlg = null;
				// 显示加载失败的UI
				_listView.setVisibility(View.GONE);
				View v = LayoutInflater.from(
						PictureFragmentPage.this.getActivity()).inflate(
						R.layout.loading_failed, null);
				_listView.setVisibility(View.GONE);

				v.setOnClickListener(PictureFragmentPage.this);

				FrameLayout ll = (FrameLayout) getView();
				FrameLayout.LayoutParams llp = new FrameLayout.LayoutParams(-1,
						-1);
				ll.addView(v, llp);
				_retryView = v;
			}

			_moreLoading = false;

		}
	};

	private class MyCustomAdapter extends BaseAdapter implements
			OnDownloadTaskListener, OnClickListener {
		private LayoutInflater _inflater;

		public MyCustomAdapter(Context context) {
			_inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			int cnt = contentInfo.subList.size() / 3;
			if ((contentInfo.subList.size() % 3) != 0)
				cnt++;

			return cnt;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			Context ctx = PictureFragmentPage.this.getActivity();

			int[] resId = new int[3];
			resId[0] = R.id.imageView1;
			resId[1] = R.id.imageView2;
			resId[2] = R.id.imageView3;

			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.picture_list_item,
						null);

				for (int i = 0; i < 3; i++) {
					ImageView imgView = (ImageView) convertView
							.findViewById(resId[i]);
					if (imgView == null)
						continue;

					imgView.setScaleType(ScaleType.CENTER_CROP);
					imgView.setOnClickListener(this);
					LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) imgView
							.getLayoutParams();
					llp.height = _pictureHeight;
					llp.width = _pictureWidth;
					imgView.setLayoutParams(llp);
				}
			}

			convertView.setTag(position);

			int dataIndex = position * 3;

			for (int i = 0; i < 3; i++) {
				ContentInfo ci = null;
				if (dataIndex + i < contentInfo.subList.size())
					ci = contentInfo.subList.get(dataIndex + i);

				ImageView imgView = (ImageView) convertView
						.findViewById(resId[i]);
				if (imgView == null)
					continue;

				String tag = String.format("%d-%d", position, i);
				imgView.setTag(tag);

				if (ci != null) {
					imgView.setImageResource(R.drawable.bg_cover);
					imgView.setVisibility(View.VISIBLE);
					if (MyUtils.isValidURLString(ci.imgurl)) {
						String imgfile = DataModule.getLocalImageFileNameByUrl(
								ci.imgurl, "cover-");
						if (MyUtils.PrivateFileExist(ctx, imgfile)) {
							imgView.setImageDrawable(MyUtils
									.loadPrivateBitmapFile(ctx, imgfile));
						} else {
							imgView.setImageResource(R.drawable.bg_cover);
							// 不存在，去下载
							_downloader.addTask(_downloader.new DownloadTask(
									ctx, ci.img_smal_url, imgfile, this,
									convertView, position, i));
							System.out
									.println("img:::::::::" + ci.img_smal_url);

						}
					}
				} else {
					imgView.setVisibility(View.INVISIBLE);
				}
			}

			return convertView;
		}

		@Override
		public void onDownloadSuccessfully(DownloadTask downloadTask) {
			int position = downloadTask.mArg1;
			int index = downloadTask.mArg2;

			View convertView = downloadTask.contentView;

			Integer key1 = (Integer) convertView.getTag();
			if (key1 != position) {
				return;
			}

			int[] resId = new int[3];
			resId[0] = R.id.imageView1;
			resId[1] = R.id.imageView2;
			resId[2] = R.id.imageView3;
			System.out.println("aaaaaaaaaaaaaaaaaaaa");
			ImageView imgView = (ImageView) convertView
					.findViewById(resId[index]);
			if (imgView != null) {
				imgView.setImageDrawable(downloadTask.getDrawable());
			}
		}

		@Override
		public void onDownloadFailed(int errCode, DownloadTask downloadTask) {
			System.out.println("img:::::::::===" + errCode);

		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tag = (String) v.getTag();
			String[] ss = tag.split("-");
			if (ss != null && ss.length == 2) {
				int row = Integer.parseInt(ss[0]);
				int idx = Integer.parseInt(ss[1]);

				int dataIndex = row * 3 + idx;
				List<String> dataList = new ArrayList<String>();
				for (int i = 0; i < contentInfo.subList.size(); i++) {
					dataList.add(contentInfo.subList.get(i).imgurl);
				}
				Intent intent = new Intent(
						PictureFragmentPage.this.getActivity(),
						ShowPictureActivity.class);
				Bundle b = new Bundle();
				b.putSerializable("dataList", (Serializable) dataList);
				intent.putExtra("itemIndex", dataIndex);
				intent.putExtras(b);
				startActivity(intent);
			}

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_left) {
			Log.d(TAG, "left button click");
		} else if (v.getId() == R.id.btn_right) {
			Log.d(TAG, "right button click");
		} else if (v.getId() == R.id.btn_showall) {
		} else if (v.getId() == R.id.loading_failed) {
			// 加载失败，点击重新加载

			Log.d(TAG, "retry loading...");

			if (_retryView != null) {
				((FrameLayout) getView()).removeView(_retryView);
				_retryView = null;

				if (_categoryList.size() > 1) {
					doLoadData(true);
				} else {
					doLoadCategory();
				}
			}

		} else if (v.getId() == R.id.title_center_txt) {
			View titleBar = this.getActivity().findViewById(R.id.title);
			popwindow = new CategoryPopwindow(getActivity(), titleBar,
					_categoryList);
			popwindow.setOnPopWindowItemcCick(this);
			popwindow.show();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onclick(int position) {
		CategoryInfo ci = _categoryList.get(position);
		if (ci.id != _currentCategoryId) {
			_currentCategoryId = ci.id;
			setTitle(ci.title);
			if (_moreView != null)
				_listView.removeFooterView(_moreView);
			contentInfo.subList.clear();
			_adapter.notifyDataSetChanged();
			doLoadData(true);
		}
	}
}
