package com.cutv.mobile.cartoon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
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
import com.cutv.mobile.data.MyProfile;
import com.cutv.mobile.data.WAPI;
import com.cutv.mobile.utils.CategoryPopwindow;
import com.cutv.mobile.utils.CategoryPopwindow.OnPopWindowItemcCick;
import com.cutv.mobile.utils.HttpUrlHelper;
import com.cutv.mobile.utils.MyUtils;

public class AnimationFragmentPage extends BaseFragment implements
		OnItemClickListener, OnClickListener, OnScrollListener,
		OnPopWindowItemcCick {

	private static String TAG = "AnimationPage";

	private int _contentType = ContentInfo.CONTENT_TYPE_CARTOON;

	private ListView _listView;

	private Dialog _loadingDlg = null;

	private MyCustomAdapter _adapter = null;

	private List<ContentInfo> _dataList = new ArrayList<ContentInfo>();
	private String _nextPageUrlString = null;
	private View _moreView = null;

	private boolean _moreLoading = false;
	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();

	private View _rootView = null;
	private View _retryView = null;

	private List<CategoryInfo> _categoryList = new ArrayList<CategoryInfo>();

	private int _currentCategoryId = 1; // 漫画

	public void setContentType(int contentType) {
		_contentType = contentType;
	}

	private CategoryPopwindow popwindow;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_animation, null);

		_rootView = v;

		return v;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		_listView = (ListView) view.findViewById(R.id.listView1);
		_listView.setVisibility(View.INVISIBLE);
		_listView.setOnItemClickListener(this);
		_listView.setOnScrollListener(this);

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

		// if (_contentType == ContentInfo.CONTENT_TYPE_CARTOON)
		setTitle("漫画");
		// else
		// setTitle("动画");

		View titleView = (View) this.getView().findViewById(
				R.id.titlebar_title_layout);
		if (titleView != null) {
			titleView.setOnClickListener(this);
		}

		TextView titleTextView = (TextView) this.getView().findViewById(
				R.id.title_center_txt);
		if (titleTextView != null) {
			titleTextView.setOnClickListener(this);
		}

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

		_nextPageUrlString = WAPI.get_animation_url_string(_contentType,
				_currentCategoryId);
		LoadThread loadThread = new LoadThread();
		loadThread.type = LoadThread.LOAD_TYPE_CATEGORY;
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

		_nextPageUrlString = WAPI.get_animation_url_string(_contentType,
				_currentCategoryId);

		LoadThread loadThread = new LoadThread();
		loadThread.start();
	}

	private void doLoadMore() {
		LoadThread loadThread = new LoadThread();
		loadThread.type = LoadThread.LOAD_TYPE_MORE;
		loadThread.start();
	}

	private class LoadThread extends Thread {
		public static final int LOAD_TYPE_FIRST = 0;
		public static final int LOAD_TYPE_MORE = 1;
		public static final int LOAD_TYPE_CATEGORY = 2;
		public int type = LOAD_TYPE_FIRST;

		public int loadCategory() {
			int ret = 1;
			String api_string = WAPI.get_category_list_url_string();

			// 请求接口
			String response = WAPI.http_get_content(api_string);
			if (response.length() > 0) {
				// 请求成功

				List<CategoryInfo> alist = new ArrayList<CategoryInfo>();
				ret = WAPI.parse_category_detail(response, alist);
				if (ret == 0) {
					_categoryList.clear();
					CategoryInfo ci = new CategoryInfo();
					ci.id = 0;
					ci.title = "全部";
					_categoryList.add(ci);

					for (int i = 0; i < alist.size(); i++) {
						ci = alist.get(i);
						Log.d("", ci.title);
						_categoryList.add(ci);
					}

					alist.clear();
				}
			}

			return ret;
		}

		public int loadData() {
			int ret = 1;

			// 接口URL
			String api_string = _nextPageUrlString;// WAPI.get_animation_url_string();
			// 请求接口
			// String response = WAPI.http_get_content(api_string);
			String response = HttpUrlHelper.getCartoonList(_currentCategoryId);
			if (response.length() > 0) {
				// 请求成功
				ArrayList<ContentInfo> dataList = new ArrayList<ContentInfo>();
				Map<String, Object> userInfo = new HashMap<String, Object>();

				ret = WAPI.parse_content_list_response(response, dataList,
						userInfo);
				if (ret == 0) {
					// 解析成功
					if (type == LOAD_TYPE_FIRST)
						_dataList.clear();

					for (int i = 0; i < dataList.size(); i++) {
						_dataList.add(dataList.get(i));
					}
					String s = (String) userInfo.get("nextpage");
					if (s != null && s.length() > 0)
						_nextPageUrlString = s;
					else
						_nextPageUrlString = null;
				}
				dataList.clear();
			}
			return ret;
		}

		public void run() {
			int ret = 1;
			try {
				if (type == LOAD_TYPE_CATEGORY) {
					ret = loadCategory();
					if (ret == 0) {
						ret = loadData();
					}
				} else if (type == LOAD_TYPE_FIRST || type == LOAD_TYPE_MORE) {
					ret = loadData();
				}
			} catch (Exception e) {
			}

			// 给主线程发消息
			Message msg = new Message();
			msg.what = ret;
			msg.arg1 = this.type;
			handler.sendMessage(msg);
		}

	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// 加载完成
			if (msg.what == 0) {
				if (_nextPageUrlString != null) {
					if (_listView.getFooterViewsCount() < 1) {
						View v = LayoutInflater.from(
								AnimationFragmentPage.this.getActivity())
								.inflate(R.layout.autoloading, null);
						_moreView = v;
						_listView.addFooterView(v);
					}
				} else {
					if (_moreView != null) {
						_listView.removeFooterView(_moreView);
						_moreView = null;
					}
				}

				if (msg.arg1 == LoadThread.LOAD_TYPE_MORE) {
					_adapter.notifyDataSetChanged();
				} else {
					MyCustomAdapter adapter = new MyCustomAdapter(
							AnimationFragmentPage.this.getActivity());
					adapter.notifyDataSetChanged();
					_adapter = adapter;
					_listView.setAdapter(adapter);

					_listView.setVisibility(View.VISIBLE);
					_loadingDlg.dismiss();
					_loadingDlg = null;
				}

			} else {
				if (msg.arg1 == LoadThread.LOAD_TYPE_MORE) {
				} else {
					_loadingDlg.dismiss();
					_loadingDlg = null;

					// 显示加载失败的UI
					_listView.setVisibility(View.GONE);
					View v = LayoutInflater.from(
							AnimationFragmentPage.this.getActivity()).inflate(
							R.layout.loading_failed, null);
					_listView.setVisibility(View.GONE);

					v.setOnClickListener(AnimationFragmentPage.this);

					LinearLayout ll = (LinearLayout) _rootView;
					LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(
							-1, -1);
					ll.addView(v, llp);
					_retryView = v;
				}

			}

			_moreLoading = false;

		}
	};

	private class MyCustomAdapter extends BaseAdapter implements
			OnDownloadTaskListener {
		private LayoutInflater _inflater;

		public MyCustomAdapter(Context context) {
			_inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return _dataList.size();
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

			ContentInfo ci = _dataList.get(position);

			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.list_item, null);

			}

			convertView.setTag(position);

			TextView tv = (TextView) convertView.findViewById(R.id.item_title);
			if (tv != null)
				tv.setText(ci.title);

			TextView txt_price = (TextView) convertView
					.findViewById(R.id.txt_price);
			if (txt_price != null) {
				if (ci.price > 0) {
					txt_price.setVisibility(View.VISIBLE);
					if (ci.price > 1) {
						txt_price.setText("收费(" + (int) ci.price + "元)");
					}
					txt_price.setText("收费(" + ci.price + "元)");

				} else {
					txt_price.setText("免费阅读");
					txt_price.setVisibility(View.VISIBLE);
				}
			}

			tv = (TextView) convertView.findViewById(R.id.item_category);
			if (tv != null)
				tv.setText("分类：" + ci.category);

			tv = (TextView) convertView.findViewById(R.id.item_author);
			if (tv != null)
				tv.setText("作者：" + ci.author);

			tv = (TextView) convertView.findViewById(R.id.item_desc);
			if (tv != null)
				tv.setText(ci.update);

			ImageView imgView = (ImageView) convertView
					.findViewById(R.id.cover_imageView);
			if (imgView != null) {
				Context ctx = AnimationFragmentPage.this.getActivity();

				if (MyUtils.isValidURLString(ci.imgurl)) {
					// ImageLoader.display(imgView, ci.imgurl, getActivity());
					String imgfile = DataModule.getLocalImageFileNameByUrl(
							ci.imgurl, "cover-");
					if (MyUtils.PrivateFileExist(ctx, imgfile)) {
						imgView.setImageDrawable(MyUtils.loadPrivateBitmapFile(
								ctx, imgfile));
					} else {
						imgView.setImageResource(R.drawable.bg_cover);
						// 不存在，去下载
						_downloader.addTask(_downloader.new DownloadTask(ctx,
								ci.imgurl, imgfile, this, convertView,
								position, 0));

					}
				}
			}

			return convertView;
		}

		@Override
		public void onDownloadSuccessfully(DownloadTask downloadTask) {
			int position = downloadTask.mArg1;
			View convertView = downloadTask.contentView;

			Integer key1 = (Integer) convertView.getTag();
			if (key1 != position) {
				return;
			}

			ImageView imgView = (ImageView) convertView
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
	public void onClick(View v) {

		Log.d(TAG, "onClick");

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
				((LinearLayout) _rootView).removeView(_retryView);
				_retryView = null;

				if (_categoryList.size() > 1) {
					doLoadData(true);
				} else {
					doLoadCategory();
				}
			}

		} else if (v.getId() == R.id.titlebar_title_layout
				|| v.getId() == R.id.title_center_txt) {
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
		// TODO Auto-generated method stub

		// Log.d(MyProfile.TAG, "onScroll");
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.d(MyProfile.TAG,
					"last visible:" + _listView.getLastVisiblePosition());
			if (_listView.getLastVisiblePosition() == _listView.getCount() - 1) {
				if (!_moreLoading && _nextPageUrlString != null) {
					_moreLoading = true;
					this.doLoadMore();
				}
				// Toast.makeText(this.getActivity(), "正在获取...", 3).show();
			}
		}

	}

	@Override
	public void onResume() {
		super.onResume();

		// _listView.setLayoutAnimation(_listView.getLayoutAnimation());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		String s = String.format("%d, %d", arg2, arg3);
		Log.d(TAG, s);

		ContentInfo ci = _dataList.get(arg2);

		Intent intent = new Intent(AnimationFragmentPage.this.getActivity(),
				ContentPageActivity.class);

		Bundle b = new Bundle();
		b.putInt("id", ci.id);
		b.putString("title", ci.title);
		intent.putExtras(b);

		startActivity(intent);
		// AnimationFragmentPage.this.getActivity().overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_left_out);

	}

	@Override
	public void onclick(int position) {
		CategoryInfo ci = _categoryList.get(position);
		// if (ci.id != _currentCategoryId) {
		// _currentCategoryId = ci.id;
		setTitle(ci.title);
		if (_moreView != null)
			_listView.removeFooterView(_moreView);
		_dataList.clear();
		_adapter.notifyDataSetChanged();
		doLoadData(true);
		// } 
	}

}
