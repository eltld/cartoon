﻿package com.cutv.mobile.component;

import java.text.SimpleDateFormat;
import java.util.Date;
//Download by http://www.codefans.net

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ExpandableListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RefreshExpandableListView extends ExpandableListView implements OnScrollListener {

	private static final String TAG = "refresh-listview";

	private final static int RELEASE_To_REFRESH = 0;
	private final static int PULL_To_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;
	
	private final static int id_head_contentLayout = 1;
	private final static int id_head_arrowImageView = 2;
	private final static int id_head_progressBar = 3;
	private final static int id_head_tipsTextView = 4;
	private final static int id_head_lastUpdatedTextView = 5;

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

//	private LayoutInflater inflater;

	private LinearLayout headView;

	private TextView tipsTextview;
	private TextView lastUpdatedTextView;
	private ImageView arrowImageView;
	private ProgressBar progressBar;


	private RotateAnimation animation;
	private RotateAnimation reverseAnimation;

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;

	private int headContentWidth;
	private int headContentHeight;

	private int startY;
	private int firstItemIndex;

	private int state;

	private boolean isBack;

	private OnRefreshListener refreshListener;

	private boolean isRefreshable;

	public RefreshExpandableListView(Context context) {
		super(context);
		init(context);
	}

	public RefreshExpandableListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	
	public int dip2px(Context context, float dipValue){ 
        final float scale = context.getResources().getDisplayMetrics().density; 
        return (int)(dipValue * scale + 0.5f); 
	} 	
	
	private void buildHeadView(Context context)
	{
		LinearLayout.LayoutParams llp;
		LinearLayout ll = new LinearLayout(context);
		ll.setBackgroundColor(Color.TRANSPARENT);
		
		
		// 内容
		RelativeLayout.LayoutParams rlp;
		RelativeLayout rl = new RelativeLayout(context);
		rl.setId(id_head_contentLayout);
		rl.setPadding(dip2px(context, 30), 0, 0, 0);
		llp = new LinearLayout.LayoutParams(-1, -2);
		
		ll.addView(rl, llp);
		

		FrameLayout.LayoutParams flp;
		FrameLayout fl = new FrameLayout(context);
		rlp = new RelativeLayout.LayoutParams(-2, -2);
		rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		rlp.addRule(RelativeLayout.CENTER_VERTICAL);
		rl.addView(fl, rlp);
		
		
		ImageView imgView = new ImageView(context);
		imgView.setId(id_head_arrowImageView);
		// todo: ...
//		imgView.setImageResource(R.drawable.bg_arrow_down);
		flp = new FrameLayout.LayoutParams(-2, -2);
		flp.gravity = Gravity.CENTER;
		fl.addView(imgView, flp);

		
		ProgressBar pb = new ProgressBar(context);
		pb.setIndeterminate(true);
		pb.setVisibility(View.GONE);
		pb.setId(id_head_progressBar);
		flp = new FrameLayout.LayoutParams(dip2px(context, 30),dip2px(context, 30));
		flp.gravity = Gravity.CENTER;
		fl.addView(pb, flp);
		
		LinearLayout ll_tips = new LinearLayout(context);
		ll_tips.setOrientation(LinearLayout.VERTICAL);
		ll_tips.setGravity(Gravity.CENTER_HORIZONTAL);
		rlp = new RelativeLayout.LayoutParams(-2, -2);
		rlp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		rl.addView(ll_tips, rlp);
		
		TextView tv1 = new TextView(context);
		tv1.setText("下拉刷新");
		tv1.setTextSize(15);
		tv1.setId(id_head_tipsTextView);
		llp = new LinearLayout.LayoutParams(-2, -2);
		ll_tips.addView(tv1, llp);
		
		TextView tv2 = new TextView(context);
		tv2.setText("上次更新");
		tv2.setTextSize(12);
		tv2.setId(id_head_lastUpdatedTextView);
		llp = new LinearLayout.LayoutParams(-2, -2);
		ll_tips.addView(tv2, llp);
		
		headView = ll;
	}

	private void init(Context context) {
		//setCacheColorHint(context.getResources().getColor(R.color.transparent));
//		inflater = LayoutInflater.from(context);

		//headView = (LinearLayout) inflater.inflate(R.layout.head, null);
		buildHeadView(context);

		arrowImageView = (ImageView) headView
				.findViewById(id_head_arrowImageView);
		arrowImageView.setMinimumWidth(70);
		arrowImageView.setMinimumHeight(50);
		progressBar = (ProgressBar) headView
				.findViewById(id_head_progressBar);
		tipsTextview = (TextView) headView.findViewById(id_head_tipsTextView);
		lastUpdatedTextView = (TextView) headView
				.findViewById(id_head_lastUpdatedTextView);

		measureView(headView);
		headContentHeight = headView.getMeasuredHeight();
		headContentWidth = headView.getMeasuredWidth();

		headView.setPadding(0, -1 * headContentHeight, 0, 0);
		headView.invalidate();

		Log.v("size", "width:" + headContentWidth + " height:"
				+ headContentHeight);

		addHeaderView(headView, null, false);
		setOnScrollListener(this);

		animation = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new LinearInterpolator());
		animation.setDuration(250);
		animation.setFillAfter(true);

		reverseAnimation = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		reverseAnimation.setInterpolator(new LinearInterpolator());
		reverseAnimation.setDuration(200);
		reverseAnimation.setFillAfter(true);

		state = DONE;
		isRefreshable = false;
	}

	public void onScroll(AbsListView arg0, int firstVisiableItem, int arg2,
			int arg3) {
		firstItemIndex = firstVisiableItem;
	}

	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	public boolean onTouchEvent(MotionEvent event) {

		if (isRefreshable) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItemIndex == 0 && !isRecored) {
					isRecored = true;
					startY = (int) event.getY();
					Log.v(TAG, "在down时候记录当前位置‘");
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						changeHeaderViewByState();

						Log.v(TAG, "由下拉刷新状态，到done状态");
					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						changeHeaderViewByState();
						onRefresh();

						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();

				if (!isRecored && firstItemIndex == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headContentHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headContentHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							changeHeaderViewByState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							changeHeaderViewByState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							changeHeaderViewByState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						headView.setPadding(0, -1 * headContentHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						headView.setPadding(0, (tempY - startY) / RATIO
								- headContentHeight, 0, 0);
					}

				}

				break;
			}
		}

		return super.onTouchEvent(event);
	}

	// 当状态改变时候，调用该方法，以更新界面
	private void changeHeaderViewByState() {
		switch (state) {
		case RELEASE_To_REFRESH:
			arrowImageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			arrowImageView.clearAnimation();
			arrowImageView.startAnimation(animation);

			tipsTextview.setText("松开刷新");

			Log.v(TAG, "当前状态，松开刷新");
			break;
		case PULL_To_REFRESH:
			progressBar.setVisibility(View.GONE);
			tipsTextview.setVisibility(View.VISIBLE);
			lastUpdatedTextView.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (isBack) {
				isBack = false;
				arrowImageView.clearAnimation();
				arrowImageView.startAnimation(reverseAnimation);

				tipsTextview.setText("下拉刷新");
			} else {
				tipsTextview.setText("下拉刷新");
			}
			Log.v(TAG, "当前状态，下拉刷新");
			break;

		case REFRESHING:

			headView.setPadding(0, 0, 0, 0);

			progressBar.setVisibility(View.VISIBLE);
			arrowImageView.clearAnimation();
			arrowImageView.setVisibility(View.GONE);
			tipsTextview.setText("正在刷新...");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态,正在刷新...");
			break;
		case DONE:
			headView.setPadding(0, -1 * headContentHeight, 0, 0);

			progressBar.setVisibility(View.GONE);
			arrowImageView.clearAnimation();
			//todo: ...
//			arrowImageView.setImageResource(R.drawable.bg_arrow_down);
			tipsTextview.setText("下拉刷新");
			lastUpdatedTextView.setVisibility(View.VISIBLE);

			Log.v(TAG, "当前状态，done");
			break;
		}
	}

	public void setonRefreshListener(OnRefreshListener refreshListener) {
		this.refreshListener = refreshListener;
		isRefreshable = true;
	}

	public interface OnRefreshListener {
		public void onRefresh();
	}

	public void onRefreshComplete() {
		state = DONE;
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText("最近更新:" + date);
		changeHeaderViewByState();
	}

	private void onRefresh() {
		if (refreshListener != null) {
			refreshListener.onRefresh();
		}
	}

	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	private void measureView(View child) {
		 ViewGroup.LayoutParams p = child.getLayoutParams();
	        if (p == null) {
	            p = new ViewGroup.LayoutParams(
	                    ViewGroup.LayoutParams.FILL_PARENT,
	                    ViewGroup.LayoutParams.WRAP_CONTENT);
	        }

	        int childWidthSpec = ViewGroup.getChildMeasureSpec(0,
	                0 + 0, p.width);
	        int lpHeight = p.height;
	        int childHeightSpec;
	        if (lpHeight > 0) {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
	        } else {
	            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
	        }
	        child.measure(childWidthSpec, childHeightSpec);
	}

	public void setAdapter(BaseAdapter adapter) {
		SimpleDateFormat format=new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		String date=format.format(new Date());
		lastUpdatedTextView.setText("最近更新:" + date);
		super.setAdapter(adapter);
	}
	

}