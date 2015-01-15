package com.cutv.mobile.cartoon;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class WebViewActivity extends BaseActivity implements OnClickListener {

	private String _linkUrl = null;
	private String _htmlString = null;

	private String _from = "view";

	private WebView _webView = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Intent intent = this.getIntent();
		Bundle b = intent.getExtras();

		String title = b.getString("title");

		_linkUrl = b.getString("link");
		_htmlString = b.getString("html");

		LinearLayout rootView = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.general_webview, null);
		this.setContentView(rootView);

		if (title != null)
			this.setTitle(title);

		Button btn = this.getLeftButton();
		btn.setBackgroundResource(R.drawable.btn_return_selector);
		btn.setOnClickListener(this);

		btn = this.getRightButton();
		btn.setVisibility(View.INVISIBLE);

		_webView = (WebView) rootView.findViewById(R.id.webView);
		_webView.setBackgroundColor(Color.WHITE);
		_webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		_webView.clearCache(true);
		_webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

		_webView.getSettings().setJavaScriptEnabled(true);

		String s = b.getString("zoom");
		if (s != null && s.equalsIgnoreCase("yes"))
			_webView.getSettings().setBuiltInZoomControls(true);

		_webView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});
		if (_linkUrl != null) {
			_webView.loadUrl(_linkUrl);
		} else {

			_webView.getSettings().setDefaultTextEncodingName("utf-8");
			// _webView.loadData(html, "text/html",HTTP.UTF_8); // 用这个会乱码
			_webView.loadDataWithBaseURL("", _htmlString, "text/html",
					HTTP.UTF_8, "");
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// webview.getContentDescription()
			if (_webView.canGoBack()) {
				_webView.goBack(); // goBack()表示返回WebView的上一页面
			} else {
				finish();
			}
			return true;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			if (_webView.canGoBack()) {
				_webView.goBack(); // goBack()表示返回WebView的上一页面
			} else {
				finish();
			}
		}

	}
}
