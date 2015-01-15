package com.cutv.mobile.cartoon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.cutv.mobile.component.CustomUI;
import com.cutv.mobile.utils.MyUtils;

public class MoviePlayerActivity extends Activity implements
		OnPreparedListener, OnCompletionListener, OnErrorListener {
	private VideoView mVideoView;
	private RelativeLayout mLoadingView;
	private View mContentView;

	private int mDuration = 0;
	private Dialog _loadingDlg = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MyUtils.showLog("MoviePlayerActivity onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		//
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// if( this.getRequestedOrientation() !=
		// ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE )
		// {
		// this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// return;
		// }

		/*
		 * mContentView = new FrameLayout(this);
		 * 
		 * mVideoView = new VideoView(this); mVideoView.setMediaController(new
		 * MediaController(this)); mVideoView.setOnPreparedListener(this);
		 * mVideoView.setOnCompletionListener(this);
		 * mVideoView.setOnErrorListener(this);
		 * 
		 * LinearLayout ll_video = new LinearLayout(this);
		 * ll_video.setOrientation(LinearLayout.VERTICAL);
		 * 
		 * FrameLayout.LayoutParams flp = new FrameLayout.LayoutParams(-1,-1);
		 * mContentView.addView(ll_video, flp);
		 * 
		 * LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(-2,-2);
		 * llp.gravity = Gravity.CENTER; ll_video.addView(mVideoView, llp);
		 * 
		 * 
		 * //==================
		 * 
		 * mLoadingView = new RelativeLayout(this);
		 * mLoadingView.setBackgroundColor(Color.TRANSPARENT);
		 * //mLoadingView.setBackgroundResource(R.drawable.video_loading);
		 * 
		 * RelativeLayout.LayoutParams rlp; //
		 * //===================================================== //
		 * LinearLayout ll = new LinearLayout(this); //
		 * ll.setBackgroundColor(Color.TRANSPARENT); // ll.setId(1); //
		 * RelativeLayout.LayoutParams rlp = new
		 * RelativeLayout.LayoutParams(100,150); //
		 * rlp.addRule(RelativeLayout.CENTER_IN_PARENT); //
		 * mLoadingView.addView(ll, rlp);
		 * 
		 * ProgressBar mProgressBar = new ProgressBar(this);//,
		 * null,R.attr.progressBarStyle); mProgressBar.setIndeterminate(true);
		 * mProgressBar.setId(2); // rlp = new
		 * RelativeLayout.LayoutParams(40,40)
		 * ;//RelativeLayout.LayoutParams.WRAP_CONTENT,
		 * RelativeLayout.LayoutParams.WRAP_CONTENT); //
		 * rlp.addRule(RelativeLayout.CENTER_IN_PARENT); //
		 * rlp.addRule(RelativeLayout.BELOW, 1); //
		 * mLoadingView.addView(mProgressBar, rlp);
		 * 
		 * 
		 * //===================================================== LinearLayout
		 * ll_tips = new LinearLayout(this);
		 * ll_tips.setOrientation(LinearLayout.HORIZONTAL);
		 * 
		 * llp = new LinearLayout.LayoutParams(MyUtils.dip2px(this,
		 * 40),MyUtils.dip2px(this, 40)); llp.gravity = Gravity.CENTER_VERTICAL;
		 * ll_tips.addView(mProgressBar, llp);
		 * 
		 * TextView tv = new TextView(this); tv.setTextColor(Color.WHITE);
		 * tv.setTextSize(16); tv.setText("正在缓冲..."); llp = new
		 * LinearLayout.LayoutParams(-2,-2); llp.gravity =
		 * Gravity.CENTER_VERTICAL; llp.leftMargin = MyUtils.dip2px(this, 10);
		 * 
		 * ll_tips.addView(tv, llp);
		 * 
		 * rlp = new
		 * RelativeLayout.LayoutParams(-2,-2);//RelativeLayout.LayoutParams
		 * .WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		 * rlp.addRule(RelativeLayout.CENTER_IN_PARENT);
		 * mLoadingView.addView(ll_tips, rlp);
		 * 
		 * 
		 * 
		 * flp = new FrameLayout.LayoutParams(-1,-1);
		 * mContentView.addView(mLoadingView, flp);
		 * 
		 * //=====================================================
		 * mContentView.setBackgroundColor(Color.BLACK);
		 * setContentView(mContentView);
		 */

		mContentView = LayoutInflater.from(this).inflate(R.layout.movie_player,
				null);
		setContentView(mContentView);
		_loadingDlg = CustomUI.createLoadingDialog(this, "加载中...");
		_loadingDlg.show();
		mVideoView = (VideoView) this.findViewById(R.id.videoView1);
		mVideoView.setMediaController(new MediaController(this));
		mVideoView.setOnPreparedListener(this);
		mVideoView.setOnCompletionListener(this);
		mVideoView.setOnErrorListener(this);

		Intent intent = this.getIntent();
		// mVideoInfo = (VideoInfo)intent.getSerializableExtra("videoinfo");
		// String videoLink = intent.getStringExtra("link");

		Bundle b = intent.getExtras();

		String videoLink = b.getString("link");

		playOnlineVideo(videoLink);
		// this.finish();
	}

	public void playOnlineVideo(String videoLink) {

		// SimpleDateFormat sDateFormat = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm");
		// String date = sDateFormat.format(new java.util.Date());
		//

		mVideoView.setVideoURI(Uri.parse(videoLink));
		mVideoView.requestFocus();

	}

	// @Override
	public void onPrepared(MediaPlayer mp) {
		MyUtils.showLog("onPrepared");
		_loadingDlg.dismiss();
		if (mDuration > 0)
			mVideoView.seekTo(mDuration);
		mVideoView.start();

		// mContentView.removeView(mLoadingView);

	}

	@Override
	public void onDestroy() {
		Log.d("", "onDestroy");
		super.onDestroy();
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		int duration = mp.getCurrentPosition();
		this.finish();

	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		MyUtils.showLog("onError");
		return true;
	}

	public void onUserBreak() {
		int duration = mVideoView.getCurrentPosition();
		mVideoView.stopPlayback();

		MyUtils.showLog("duration: " + duration);
		this.finish();
	}

	// MyBaseActivity不处理Back键
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onUserBreak();
			return true;

		}

		return super.onKeyDown(keyCode, event);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		super.onConfigurationChanged(newConfig);

		// 检测屏幕的方向：纵向或横向
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {

			// 当前为横屏， 在此处添加额外的处理代码
			Log.d("", "ORIENTATION_LANDSCAPE");

		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// 当前为竖屏， 在此处添加额外的处理代码

			Log.d("", "ORIENTATION_PORTRAIT");
		}
	}

}
