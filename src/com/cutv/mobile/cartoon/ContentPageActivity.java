package com.cutv.mobile.cartoon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sms.purchasesdk.cartoon.SMSPaycode;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.cutv.mobile.component.AsynDownloadImageTask;
import com.cutv.mobile.component.AsynDownloadImageTask.DownloadTask;
import com.cutv.mobile.component.AsynDownloadImageTask.OnDownloadTaskListener;
import com.cutv.mobile.component.CustomUI;
import com.cutv.mobile.data.ContentInfo;
import com.cutv.mobile.data.DataModule;
import com.cutv.mobile.data.MyProfile;
import com.cutv.mobile.data.WAPI;
import com.cutv.mobile.pay.IAPListener;
import com.cutv.mobile.utils.HttpUrlHelper;
import com.cutv.mobile.utils.MyUtils;
import com.cutv.mobile.utils.SharedUtils;

import fynn.app.PromptDialog;

public class ContentPageActivity extends BaseActivity implements
		OnClickListener, OnDownloadTaskListener {

	private Dialog _loadingDlg = null;

	private ContentInfo _contentInfo = new ContentInfo();

	private ScrollView _scrollView = null;
	private GridView _gridView = null;
	private TextView txt_pay_state = null;
	private boolean _expand = false;

	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();
	private String title = "";

	private String APPID = "300000006181";
	private String APPKEY = "457AA96D7771572D";
	private String channlid = ""; // 默认渠道ID

	private SMSPaycode msmsPaycode;
	private IAPListener mListener;

	private Handler payHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:// 订购成功
					// Toast.makeText(ContentPageActivity.this, (String)
					// msg.obj,
					// Toast.LENGTH_SHORT).show();
					// SharedUtils.setBoolean(_contentInfo.id + "", true);
				// _contentInfo.pay_state = 1;
				SharedUtils.setPayState(_contentInfo.id, 1);
				// upDatePayState();
				txt_pay_state.setText("已订购");

				break;
			case 2:// 订购失败
					// Toast.makeText(ContentPageActivity.this, (String)
					// msg.obj,
					// Toast.LENGTH_SHORT).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();
		_contentInfo.id = b.getInt("id");
		title = b.getString("title");
		LinearLayout rootView = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.content_page, null);
		this.setContentView(rootView);
		_scrollView = (ScrollView) findViewById(R.id.scrollView);
		_scrollView.setVisibility(View.INVISIBLE);

		_gridView = (GridView) findViewById(R.id.gridView1);

		Button btn = (Button) findViewById(R.id.btn_expand);
		if (btn != null) {
			btn.setOnClickListener(this);
		}

		btn = (Button) findViewById(R.id.btn_read);
		if (btn != null)
			btn.setOnClickListener(this);

		if (title != null)
			this.setTitle(title);

		btn = this.getLeftButton();
		btn.setBackgroundResource(R.drawable.btn_return_selector);
		btn.setOnClickListener(this);

		btn = this.getRightButton();
		btn.setVisibility(View.INVISIBLE);
		doLoad();
		initPay();
	}

	private void initPay() {
		mListener = new IAPListener(this, payHandler);
		msmsPaycode = SMSPaycode.getInstance();
		try {
			msmsPaycode.setAppInfo(APPID, APPKEY);
			msmsPaycode.smsInit(this, mListener);
		} catch (Exception e) {
		}

	}

	private void upDatePayState() {
		new Thread() {
			public void run() {
				HttpUrlHelper.payCartoon(_contentInfo.id);
			}
		}.start();
	}

	private void doLoad() {
		_loadingDlg = CustomUI.createLoadingDialog(this, "加载中...");
		_loadingDlg.show();

		LoadThread thread = new LoadThread();
		thread.start();
	}

	private class LoadThread extends Thread {
		public void run() {
			int ret = 1;
			try {
				// 接口URL
				String api_string = WAPI
						.get_content_detail_url_string(_contentInfo.id);

				// 请求接口
				// String response = WAPI.http_get_content(api_string);
				String response = HttpUrlHelper
						.getCartoonContent(_contentInfo.id);
				if (response.length() > 0) {
					// 请求成功

					ret = WAPI.parse_content_detail(response, _contentInfo);
					if (ret == 0) {
						// 解析成功
					}
				}
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
				TextView textView = (TextView) _scrollView
						.findViewById(R.id.title_textView);
				if (textView != null)
					textView.setText(_contentInfo.title);
				textView = (TextView) _scrollView
						.findViewById(R.id.category_textView);
				if (textView != null)
					textView.setText(_contentInfo.category);
				txt_pay_state = (TextView) _scrollView
						.findViewById(R.id.txt_pay_state);
				if (_contentInfo.price > 0) {
					if (txt_pay_state != null) {
						if (SharedUtils.getPayState(_contentInfo.id) == 1) {
							txt_pay_state.setText("已订购");
						} else {
							txt_pay_state.setText("前两节免费阅读,后续章节需要订购才能阅读");

						}
					}
				} else {
					txt_pay_state.setVisibility(View.GONE);
				}
				textView = (TextView) _scrollView
						.findViewById(R.id.author_textView);
				if (textView != null) {
					textView.setText("作者：" + _contentInfo.author);
				}

				textView = (TextView) _scrollView
						.findViewById(R.id.category_textView);
				if (textView != null)
					textView.setText("类型：" + _contentInfo.category);
				textView.setVisibility(View.GONE);

				textView = (TextView) _scrollView
						.findViewById(R.id.update_textView);
				if (textView != null)
					textView.setText(_contentInfo.update);

				textView = (TextView) _scrollView
						.findViewById(R.id.desc_textView);
				if (textView != null) {
					textView.setEllipsize(TruncateAt.END);
					textView.setText(_contentInfo.desc);
				}

				ImageView imgView = (ImageView) _scrollView
						.findViewById(R.id.cover_imageView);
				if (imgView != null) {
					Context ctx = ContentPageActivity.this;

					if (MyUtils.isValidURLString(_contentInfo.imgurl)) {
						String imgfile = DataModule.getLocalImageFileNameByUrl(
								_contentInfo.imgurl, "cover-");
						if (MyUtils.PrivateFileExist(ctx, imgfile)) {
							imgView.setImageDrawable(MyUtils
									.loadPrivateBitmapFile(ctx, imgfile));
						} else {
							imgView.setImageResource(R.drawable.bg_cover);
							// 不存在，去下载
							_downloader.addTask(_downloader.new DownloadTask(
									ctx, _contentInfo.imgurl, imgfile,
									ContentPageActivity.this, 0, 0));

						}
					}
				}

				GridAdapter adapter = new GridAdapter(ContentPageActivity.this);
				_gridView.setAdapter(adapter);

				// 直接调用这个是无效的，scrollview初始化时很多信息获取不完整，所以无效
				// _scrollView.scrollTo(0, 0);
				// _scrollView.post(new Runnable(){
				//
				// @Override
				// public void run() {
				// _scrollView.scrollTo(0,0);
				// }
				//
				// });

				/*
				 * // 直接计算gridView高度 GridView gridView =
				 * (GridView)_scrollView.findViewById(R.id.gridView1); if(
				 * gridView != null ) { int lineHeight =
				 * MyUtils.dip2px(ContentPageActivity.this,42);
				 * 
				 * // 计算需要多少行 int lines = _contentInfo.subList.size() / 4; if(
				 * (_contentInfo.subList.size() % 4 ) != 0 ) lines++;
				 * 
				 * int totalHeight = lineHeight * lines;
				 * 
				 * LinearLayout.LayoutParams llp =
				 * (LinearLayout.LayoutParams)gridView.getLayoutParams();
				 * llp.height = totalHeight; gridView.setLayoutParams(llp); }
				 */

				_scrollView.setVisibility(View.VISIBLE);
			} else {
				Toast.makeText(ContentPageActivity.this, "加载失败",
						Toast.LENGTH_SHORT);
				finish();
			}
			_loadingDlg.dismiss();
		}
	};

	@Override
	public void onResume() {
		if (_scrollView != null)
			_scrollView.smoothScrollTo(0, 0);
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_expand) {
			TextView textView = (TextView) findViewById(R.id.desc_textView);
			if (textView != null) {
				if (_expand) {
					textView.setMaxLines(2);
					v.setBackgroundResource(R.drawable.btn_expand_selector);
				} else {
					textView.setMaxLines(100);
					v.setBackgroundResource(R.drawable.btn_unexpand_selector);
				}

				_expand = !_expand;
			}
		} else if (v.getId() == R.id.btn_set) {
			int index = (Integer) v.getTag();
			playItem(index);
		} else if (v.getId() == R.id.btn_read) {
			if (_contentInfo.subList.size() > 0) {
				// 从第1集开始读
				playItem(-1);
			}
		}

	}

	private void addList(ContentInfo episodeInfo, List<String> dataList) {
		for (int j = 0; j < episodeInfo.subList.size(); j++) {
			ContentInfo itemInfo = episodeInfo.subList.get(j);
			// ContentInfo contentInfo = new ContentInfo();
			// contentInfo.id = itemInfo.id;
			// contentInfo.title = episodeInfo.title;
			// contentInfo.link = itemInfo.link;
			// Log.d("", contentInfo.link);
			dataList.add(itemInfo.link);
		}
	}

	private void playItem(final int itemIndex) {
		if (itemIndex > 1 && SharedUtils.getPayState(_contentInfo.id) == 0
				&& _contentInfo.price > 0) {
			new PromptDialog.Builder(this)
					.setTitle("温馨提示")
					.setViewStyle(PromptDialog.VIEW_STYLE_TITLEBAR)
					.setMessage(
							"订购 '" + _contentInfo.title + "' 整部作品仅需"
									+ _contentInfo.price + "元,是否订购?")
					.setButton1("确定", new PromptDialog.OnClickListener() {

						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
							// read(itemIndex);
							orderPpay();
						}
					}).setButton2("取消", new PromptDialog.OnClickListener() {

						@Override
						public void onClick(Dialog dialog, int which) {
							dialog.dismiss();
						}
					}).show();
		} else {
			read(itemIndex);

		}

	}

	private void orderPpay() {
		try {
			msmsPaycode.smsOrder(this, _contentInfo.pay_code, channlid,
					mListener, "0123456789abcdef");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void read(int itemIndex) {
		if (_contentInfo.contentType == ContentInfo.CONTENT_TYPE_ANIMATION) {
			ContentInfo ci = _contentInfo.subList.get(itemIndex == -1 ? 0
					: itemIndex);
			// 视频，调用播放器播放
			Intent intent = new Intent(this, MoviePlayerActivity.class);
			Bundle b = new Bundle();
			b.putString("link", ci.link);
			intent.putExtras(b);
			startActivity(intent);
		} else if (_contentInfo.contentType == ContentInfo.CONTENT_TYPE_CARTOON) {
			// 漫画
			List<String> dataList = new ArrayList<String>();
			List<String> needPayList = new ArrayList<String>();

			if (itemIndex == -1) {
				int index = _contentInfo.subList.size() > 2 ? 2
						: _contentInfo.subList.size();

				if (!SharedUtils.getBoolean(_contentInfo.id + "", false)) {
					for (int i = 0; i < index; i++) {
						ContentInfo episodeInfo = _contentInfo.subList.get(i);
						addList(episodeInfo, needPayList);
					}
				}
				for (int i = 0; i < _contentInfo.subList.size(); i++) {
					ContentInfo episodeInfo = _contentInfo.subList.get(i);
					addList(episodeInfo, dataList);
				}
			} else {
				ContentInfo episodeInfo = _contentInfo.subList.get(itemIndex);
				addList(episodeInfo, dataList);
			}
			Intent intent = new Intent(this, PictureViewActivity.class);
			Bundle b = new Bundle();
			b.putSerializable("dataList", (Serializable) dataList);
			b.putSerializable("needPayList", (Serializable) needPayList);
			intent.putExtra("title", title);
			intent.putExtra("itemIndex", itemIndex);
			intent.putExtra("contentInfo_id", _contentInfo.id);
			intent.putExtra("pay_state", _contentInfo.pay_state);
			intent.putExtra("price", _contentInfo.price);
			intent.putExtra("pay_code", _contentInfo.pay_code);
			intent.putExtras(b);
			startActivity(intent);

		}
	}

	@Override
	public void onDownloadSuccessfully(DownloadTask downloadTask) {

		ImageView imgView = (ImageView) _scrollView
				.findViewById(R.id.cover_imageView);
		if (imgView != null) {
			imgView.setImageDrawable(downloadTask.getDrawable());
		}

	}

	@Override
	public void onDownloadFailed(int errCode, DownloadTask downloadTask) {

	}

	private class GridAdapter extends BaseAdapter {

		private LayoutInflater _inflater = null;
		private Context _context = null;

		public GridAdapter(Context c) {
			super();
			_context = c;
			_inflater = LayoutInflater.from(c);
		}

		public int getCount() {
			return _contentInfo.subList.size();
		}

		@Override
		public Object getItem(int index) {

			return null;
		}

		@Override
		public long getItemId(int index) {
			return index;
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = _inflater.inflate(R.layout.grid_item, null);

				Button btn = (Button) convertView.findViewById(R.id.btn_set);
				btn.setOnClickListener(ContentPageActivity.this);
			}

			ContentInfo ci = _contentInfo.subList.get(index);

			Button btn = (Button) convertView.findViewById(R.id.btn_set);
			if (btn != null) {
				btn.setTag(index);
				btn.setText(ci.title);
			}

			return convertView;
		}

	}
}
