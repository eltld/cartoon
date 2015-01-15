package com.cutv.mobile.cartoon;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cutv.mobile.component.AsynDownloadImageTask;
import com.cutv.mobile.component.AsynDownloadImageTask.DownloadTask;
import com.cutv.mobile.component.AsynDownloadImageTask.OnDownloadTaskListener;
import com.cutv.mobile.component.CustomUI;
import com.cutv.mobile.component.ImageSlideView;
import com.cutv.mobile.data.CategoryInfo;
import com.cutv.mobile.data.ContentInfo;
import com.cutv.mobile.data.DataModule;
import com.cutv.mobile.data.MyProfile;
import com.cutv.mobile.data.RecommendInfo;
import com.cutv.mobile.data.WAPI;
import com.cutv.mobile.utils.HttpUrlHelper;
import com.cutv.mobile.utils.MyUtils;

public class HomeFragmentPage extends BaseFragment implements
		OnGroupClickListener, OnClickListener,
		ImageSlideView.OnItemClickListener {

	private static String TAG = "HomePage";

	private ExpandableListView _listView;

	private LinearLayout _slideViewFrame = null;
	private ImageSlideView _slideView = null;

	private Dialog _loadingDlg = null;

	private List<CategoryInfo> _categoryList = new ArrayList<CategoryInfo>();
	private List<RecommendInfo> _recommendList = new ArrayList<RecommendInfo>();

	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();

	private View _rootView = null;
	private View _retryView = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_home, null);

		_rootView = v;

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_listView = (ExpandableListView) view
				.findViewById(R.id.expandableListView1);
		_listView.setVisibility(View.INVISIBLE);
		_listView.setGroupIndicator(null);
		_listView.setOnGroupClickListener(this);

		Button btn = getLeftButton();
		if (btn != null)
			// btn.setOnClickListener(this);
			btn.setVisibility(View.GONE);

		btn = getRightButton();
		if (btn != null)
			btn.setOnClickListener(this);

		setTitle("精品推荐");

		doLoad(false, true);
	}

	void buildUI() {
		// buildTestData();

		ArrayList<ImageSlideView.SlideImageItem> recommendList = null;
		if (_recommendList.size() > 0) {

			recommendList = new ArrayList<ImageSlideView.SlideImageItem>();
			for (int i = 0; i < _recommendList.size(); i++) {
				RecommendInfo ri = _recommendList.get(i);
				ImageSlideView.SlideImageItem sii = new ImageSlideView.SlideImageItem();
				sii.title = ri.title;
				sii.imageUrl = ri.imageUrl;
				recommendList.add(sii);
			}

			buildSlideView();

			_listView.addHeaderView(_slideViewFrame);
		}

		MyCustomAdapter adapter = new MyCustomAdapter(this.getActivity());
		adapter.notifyDataSetChanged();
		_listView.setAdapter(adapter);

		if (recommendList != null)
			_slideView.setImageList(recommendList);

		int groupCount = _categoryList.size();
		for (int i = 0; i < groupCount; i++) {
			_listView.expandGroup(i);
		}
		;

	}

	@Override
	public void onStart() {
		super.onStart();

	}

	void buildSlideView() {
		if (_slideViewFrame != null)
			return;

		LinearLayout ll_main = new LinearLayout(this.getActivity());
		_slideViewFrame = ll_main;

		ll_main.setBackgroundColor(Color.BLACK);// TRANSPARENT);

		FrameLayout.LayoutParams fl;

		DisplayMetrics metric = new DisplayMetrics();
		this.getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metric);

		int screenWidth = metric.widthPixels;
		int nSlideViewHeight = 300 * screenWidth / 640;

		FrameLayout fl_slideview = new FrameLayout(this.getActivity());
		fl_slideview.setBackgroundColor(Color.TRANSPARENT);
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(-1,
				nSlideViewHeight);
		ll_main.addView(fl_slideview, ll);

		ImageSlideView slideView = new ImageSlideView(this.getActivity());
		slideView.setOnItemClickListener(this);
		_slideView = slideView;

		fl = new FrameLayout.LayoutParams(-1, nSlideViewHeight);
		fl_slideview.addView(slideView, fl);

		ImageView imgView = new ImageView(this.getActivity());
		// imgView.setImageResource(R.drawable.slideview_frame);
		fl = new FrameLayout.LayoutParams(-1, nSlideViewHeight);
		fl.leftMargin = 0;
		fl.rightMargin = 0;
		fl_slideview.addView(imgView, fl);
	}

	private void buildTestData() {
		ArrayList<ImageSlideView.SlideImageItem> dataList = new ArrayList<ImageSlideView.SlideImageItem>();
		for (int i = 0; i < 3; i++) {
			ImageSlideView.SlideImageItem item = new ImageSlideView.SlideImageItem();
			item.imageUrl = "";// ri.imageUrl;
			item.title = "标题";// ri.title;
			item.itemId = 0;// ri.id;

			dataList.add(item);
		}

		_slideView.setImageList(dataList);

		CategoryInfo ci;

		ci = new CategoryInfo();
		ci.id = 1;
		ci.title = "热门动画";
		_categoryList.add(ci);

		ci = new CategoryInfo();
		ci.id = 2;
		ci.title = "漫画推荐";
		_categoryList.add(ci);

	}

	private void doLoad(boolean bCacheEnable, boolean showTips) {
		if (showTips) {
			_loadingDlg = CustomUI.createLoadingDialog(this.getActivity(),
					"加载中...");
			_loadingDlg.show();
		}
		LoadThread loadThread = new LoadThread();
		loadThread.bCacheEnable = bCacheEnable;
		loadThread.bShowTips = showTips;
		loadThread.start();
	}

	private class LoadThread extends Thread {
		public boolean bCacheEnable = true;
		public boolean bShowTips = true;
		private int _fromCacheFlag = 0;

		public void run() {
			int ret = 1;
			try {
				// 接口URL
				String api_string = WAPI.get_home_url_string();
				// 请求接口
				// String response = WAPI.http_get_content(api_string);
				String response = HttpUrlHelper.getRecomment();
				if (response.length() > 0) {
					// 请求成功
					ArrayList<CategoryInfo> categoryList = new ArrayList<CategoryInfo>();
					ArrayList<RecommendInfo> recommendList = new ArrayList<RecommendInfo>();
					ret = WAPI.parse_home_content(response, categoryList,
							recommendList);
					if (ret == 0) {
						// 解析成功
						for (int i = 0; i < recommendList.size(); i++) {
							_recommendList.add(recommendList.get(i));
						}
						for (int i = 0; i < categoryList.size(); i++) {
							_categoryList.add(categoryList.get(i));
						}
					}
					categoryList.clear();
					recommendList.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 给主线程发消息
			Message msg = new Message();
			msg.what = ret;
			msg.arg1 = _fromCacheFlag;
			msg.arg2 = (bShowTips ? 1 : 0);
			handler.sendMessage(msg);
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 加载完成

			if (msg.what == 0) {
				buildUI();
				_listView.setVisibility(View.VISIBLE);
				_loadingDlg.dismiss();
			} else {
				_loadingDlg.dismiss();

				View v = LayoutInflater.from(
						HomeFragmentPage.this.getActivity()).inflate(
						R.layout.loading_failed, null);
				_listView.setVisibility(View.GONE);

				v.setOnClickListener(HomeFragmentPage.this);

				LinearLayout ll = (LinearLayout) _rootView;
				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
						-1, -1);
				ll.addView(v, llp);
				_retryView = v;

				// Toast toast =
				// Toast.makeText(HomeFragmentPage.this.getActivity(),
				// "加载失败！",Toast.LENGTH_SHORT);
				// toast.setGravity(Gravity.CENTER, 0, 0);
				// toast.show();
			}

		}
	};

	private class MyCustomAdapter extends BaseExpandableListAdapter implements
			OnDownloadTaskListener {
		private LayoutInflater _inflater;

		public MyCustomAdapter(Context context) {
			_inflater = LayoutInflater.from(context);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			Log.d(TAG, "getChildrenCount: groupPosition=" + groupPosition);
			return 1;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getGroupCount() {
			int cnt = _categoryList.size();
			Log.d(TAG, "getGroupCount=" + cnt);
			return cnt;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			Log.d(TAG, "getGroupView at: " + groupPosition);

			CategoryInfo categoryInfo = _categoryList.get(groupPosition);

			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.home_groupbar, null);
			}

			TextView tv = (TextView) convertView
					.findViewById(R.id.groupbar_title);
			if (tv != null) {
				tv.setText(categoryInfo.title);
			}

			ImageView btn = (ImageView) convertView
					.findViewById(R.id.btn_showall);
			if (btn != null) {
				btn.setTag(groupPosition);
				btn.setOnClickListener(HomeFragmentPage.this);
			}

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			CategoryInfo categoryInfo = _categoryList.get(groupPosition);

			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.home_groupcontent,
						null);
			}

			String oldKey = (String) convertView.getTag();

			String key = String.format("group-%d", groupPosition);
			if (key.equals(oldKey))
				return convertView;

			convertView.setTag(key);

			LinearLayout llContainer = (LinearLayout) convertView
					.findViewById(R.id.ll_container);

			llContainer.removeAllViews();

			for (int i = 0; i < categoryInfo.contentList.size(); i++) {
				ContentInfo contentInfo = categoryInfo.contentList.get(i);

				View v = _inflater.inflate(R.layout.content_item, null);
				ImageView imgView = (ImageView) v
						.findViewById(R.id.cover_imageView);
				// imgView.setImageResource(R.drawable.cover1 + i);

				LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
						-2, -2);
				llp.leftMargin = MyUtils.dip2px(
						HomeFragmentPage.this.getActivity(), 5);
				llp.rightMargin = MyUtils.dip2px(
						HomeFragmentPage.this.getActivity(), 5);
				llp.topMargin = MyUtils.dip2px(
						HomeFragmentPage.this.getActivity(), 5);
				llContainer.addView(v, llp);

				key = String.format("%d-%d", groupPosition, i);
				v.setTag(key);

				TextView titleTextView = (TextView) v
						.findViewById(R.id.title_textView);
				if (titleTextView != null) {
					titleTextView.setText(contentInfo.title);
				}

				TextView descTextView = (TextView) v
						.findViewById(R.id.desc_textView);
				if (descTextView != null) {
					descTextView.setText(contentInfo.update);
				}

				Context ctx = HomeFragmentPage.this.getActivity();

				if (MyUtils.isValidURLString(contentInfo.imgurl)) {
					String imgfile = DataModule.getLocalImageFileNameByUrl(
							contentInfo.imgurl, "cover-");
					if (MyUtils.PrivateFileExist(ctx, imgfile)) {
						imgView.setImageDrawable(MyUtils.loadPrivateBitmapFile(
								ctx, imgfile));
					} else {
						// 不存在，去下载
						_downloader.addTask(_downloader.new DownloadTask(ctx,
								contentInfo.imgurl, imgfile, this, convertView,
								groupPosition, i));

					}
				}

				v.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String key = (String) v.getTag();
						String[] ss = key.split("-");
						if (ss.length == 2) {
							int groupPosition = Integer.parseInt(ss[0]);
							int childPosition = Integer.parseInt(ss[1]);

							CategoryInfo categoryInfo = _categoryList
									.get(groupPosition);
							ContentInfo ci = categoryInfo.contentList
									.get(childPosition);

							Log.d(TAG, ci.title);

							Intent intent = new Intent(HomeFragmentPage.this
									.getActivity(), ContentPageActivity.class);

							Bundle b = new Bundle();
							b.putInt("id", ci.id);
							b.putString("title", ci.title);
							intent.putExtras(b);

							startActivity(intent);
							// HomeFragmentPage.this.getActivity().overridePendingTransition(R.anim.push_right_in,
							// R.anim.push_left_out);

						}
					}
				});

			}

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onDownloadSuccessfully(DownloadTask downloadTask) {
			// TODO Auto-generated method stub
			int groupPosition = downloadTask.mArg1;
			int childPosition = downloadTask.mArg2;
			View convertView = downloadTask.contentView;

			String key = String.format("group-%d", groupPosition);
			String key1 = (String) convertView.getTag();
			if (!key.equals(key1)) {
				return;
			}

			key = String.format("%d-%d", groupPosition, childPosition);
			View v = convertView.findViewWithTag(key);
			if (v == null)
				return;

			ImageView imgView = (ImageView) v
					.findViewById(R.id.cover_imageView);
			if (imgView != null) {
				imgView.setImageDrawable(downloadTask.getDrawable());
			}
		}

		@Override
		public void onDownloadFailed(int errCode, DownloadTask downloadTask) {
			// TODO Auto-generated method stub

		}

	}

	@Override
	public boolean onGroupClick(ExpandableListView parent, View v,
			int groupPosition, long id) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btn_left) {
			Log.d(TAG, "left button click");

			Intent intent = new Intent(this.getActivity(), TestActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.btn_right) {
			// Log.d(TAG, "right button click");
			startActivity(new Intent(getActivity(), AboutActivity.class));
		} else if (v.getId() == R.id.btn_showall) {
			Integer groupPosition = (Integer) v.getTag();
			CategoryInfo ci = _categoryList.get(groupPosition);
			Log.d(TAG, "showall button click:" + ci.title);

			if (ci.more.equals("animation")) {
				MainTabActivity.sharedInstance().showTab(1);
			} else if (ci.more.equals("cartoon")) {
				MainTabActivity.sharedInstance().showTab(1);

			} else if (ci.more.equals("picture")) {
				MainTabActivity.sharedInstance().showTab(2);

			}
		} else if (v.getId() == R.id.loading_failed) {
			if (_retryView != null) {
				((LinearLayout) _rootView).removeView(_retryView);
				_retryView = null;

				doLoad(false, true);
			}

		}

	}

	@Override
	public void onItemClick(ImageSlideView view, int itemIndex) {
		// TODO Auto-generated method stub
		RecommendInfo ri = _recommendList.get(itemIndex);
		Intent intent = new Intent(HomeFragmentPage.this.getActivity(),
				ContentPageActivity.class);
		Bundle b = new Bundle();
		b.putInt("id", ri.id);
		b.putString("title", ri.title);
		intent.putExtras(b);
		startActivity(intent);
		// if (ri.action.equals("webpage") || ri.action.equals("page")) {
		// if (ri.link != null && ri.link.length() > 0) {
		// Log.d(MyProfile.TAG, ri.link);
		// Intent intent = new Intent(this.getActivity(),
		// WebViewActivity.class);
		//
		// Bundle b = new Bundle();
		// b.putString("link", ri.link);
		// intent.putExtras(b);
		//
		// startActivity(intent);
		// }
		// }
	}

}
