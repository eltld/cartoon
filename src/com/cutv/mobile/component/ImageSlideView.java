package com.cutv.mobile.component;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.cutv.mobile.component.AsynDownloadImageTask.DownloadTask;
import com.cutv.mobile.component.AsynDownloadImageTask.OnDownloadTaskListener;
import com.cutv.mobile.utils.MyUtils;

public class ImageSlideView extends ViewGroup implements
		OnDownloadTaskListener, OnClickListener {
	private static final String TAG = "ImageSlideView";
	private VelocityTracker mVelocityTracker; // 用于判断甩动手势
	private static final int SNAP_VELOCITY = 600;
	private Scroller mScroller; // 滑动控制器
	private int mCurScreen;
	private float mLastMotionX;
	private float _downX;
	private float _downY;

	private Timer _timer = null;

	private int _step = 1;

	private int _moveCnt = 0;
	private final static int ITEM_BASE_TAG = 9000;

	private AsynDownloadImageTask _downloader = new AsynDownloadImageTask();

	// 数据列表
	private ArrayList<SlideImageItem> mImageList = new ArrayList<SlideImageItem>();

	public interface OnItemClickListener {
		/**
		 * Called when a view has been clicked.
		 * 
		 * @param v
		 *            The view that was clicked.
		 */
		void onItemClick(ImageSlideView view, int itemIndex);
	}

	private OnItemClickListener _onItemClickListener = null;

	public ImageSlideView(Context context) {
		super(context);

		mCurScreen = 0;
		mScroller = new Scroller(context);

		this.setOnClickListener(this);
	}

	public void setOnItemClickListener(OnItemClickListener listener) {
		_onItemClickListener = listener;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		Log.d(TAG, "onLayout: child count=" + getChildCount());
		if (true) {
			Log.d(TAG, "onLayout: changed");
			int childLeft = 0;
			final int childCount = getChildCount();
			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					Log.d(TAG, "width=" + childWidth);
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		Log.d(TAG, "onMeasure: width=" + width);

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}

		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		final int screenWidth = getWidth();
		final int destScreen = (getScrollX() + screenWidth / 2) / screenWidth;
		snapToScreen(destScreen);
	}

	public void snapToScreen(int whichScreen) {
		// get the valid layout page
		whichScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
		if (getScrollX() != (whichScreen * getWidth())) {
			final int delta = whichScreen * getWidth() - getScrollX();
			mScroller.startScroll(getScrollX(), 0, delta, 0,
					Math.abs(delta) * 2);

			mCurScreen = whichScreen;
			invalidate(); // Redraw the layout

			/*
			 * if (mOnViewChangeListener != null) {
			 * mOnViewChangeListener.OnViewChange(mCurScreen); }
			 */
		}
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		final int action = event.getAction();
		final float x = event.getX();
		final float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.i("", "onTouchEvent  ACTION_DOWN");
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
				mVelocityTracker.addMovement(event);
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			_moveCnt = 0;

			_downX = x;
			_downY = y;
			stopAutoSlide();

			break;

		case MotionEvent.ACTION_MOVE:
			_moveCnt++;
			// Log.i("", "onTouchEvent  ACTION_MOVE");
			int deltaX = (int) (mLastMotionX - x);
			if (IsCanMove(deltaX)) {
				if (mVelocityTracker != null) {
					mVelocityTracker.addMovement(event);
				}
				mLastMotionX = x;
				scrollBy(deltaX, 0);
			}

			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			Log.i("", "onTouchEvent  ACTION_UP");
			int velocityX = 0;
			if (mVelocityTracker != null) {
				mVelocityTracker.addMovement(event);
				mVelocityTracker.computeCurrentVelocity(1000);
				velocityX = (int) mVelocityTracker.getXVelocity();
			}
			if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
				// Fling enough to move left
				Log.e(TAG, "snap left");
				snapToScreen(mCurScreen - 1);
			} else if (velocityX < -SNAP_VELOCITY
					&& mCurScreen < getChildCount() - 1) {
				// Fling enough to move right
				Log.e(TAG, "snap right");
				snapToScreen(mCurScreen + 1);
			} else {
				snapToDestination();
			}

			if (mVelocityTracker != null) {
				mVelocityTracker.recycle();
				mVelocityTracker = null;
			}

			startAutoSlide();

			float offsetX = Math.abs(_downX - x);
			float offsetY = Math.abs(_downY - y);

			Log.v("", "offsetx=" + offsetX + " offsety=" + offsetY);
			if (_moveCnt <= 5 && offsetX < 15 && offsetY < 15) {
				Log.v("", "x=" + x + " scrollX=" + mScroller.getCurrX());
				onItemClick(x);
			}
			break;
		}
		return true;
	}

	private void stopAutoSlide() {
		if (_timer != null) {
			_timer.cancel();
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			//
			// Log.d("debug", "handleMessage方法所在的线程："
			// + Thread.currentThread().getName());
			//
			// Handler处理消息
			if (msg.what == 1) {
				int cnt = getChildCount();

				int nextScreen = mCurScreen;

				if (mCurScreen == cnt - 1) {
					_step = -1;
				} else if (mCurScreen == 0)
					_step = 1;

				nextScreen += _step;

				snapToScreen(nextScreen);
			}
		}
	};

	private void startAutoSlide() {
		stopAutoSlide();

		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// Log.d("debug", "run方法所在的线程："
				// + Thread.currentThread().getName());

				// 定义一个消息传过去
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
		};

		// Timer在cancel之后不能再次schedule
		// 所以每次都new一个新的
		_timer = new Timer();
		_timer.schedule(timerTask, 5000, 5000);
	}

	private void onItemClick(float touchX) {
		int x = mScroller.getCurrX() + (int) touchX;

		int basex = -1;
		for (int i = 0; i < mImageList.size(); i++) {
			View v = this.findViewWithTag(ITEM_BASE_TAG + i);
			if (v != null) {
				int[] location = new int[2];
				v.getLocationInWindow(location);

				if (basex == -1)
					basex = location[0];

				int itemLeft = location[0] - basex;
				int itemRight = itemLeft + v.getRight() - v.getLeft();

				Log.v("", "x=" + x + " location.left=" + itemLeft
						+ ",location.right=" + itemRight + ")");

				if (x >= itemLeft && x <= itemRight) {
					Log.v("", "item: " + i);

					if (_onItemClickListener != null) {
						_onItemClickListener.onItemClick(this, i);
					}

					break;
				}
			}
		}
	}

	private boolean IsCanMove(int deltaX) {
		if (getScrollX() <= 0 && deltaX < 0) {
			return false;
		}
		if (getScrollX() >= (getChildCount() - 1) * getWidth() && deltaX > 0) {
			return false;
		}
		return true;
	}

	public void reset() {
		stopAutoSlide();

		_downloader.cancelAll();

		this.removeAllViews();

		mCurScreen = 0;

		mImageList.clear();

		mScroller.startScroll(0, 0, 0, 0, 0);
		mScroller.forceFinished(true);
	}

	public void setImageList(ArrayList<SlideImageItem> itemList) {
		reset();

		// 复制数据
		for (int i = 0; i < itemList.size(); i++) {
			SlideImageItem item = itemList.get(i);

			mImageList.add(item);
		}

		// 构造UI

		int pageSize = 1;
		int pageCnt = itemList.size() / pageSize;
		if ((itemList.size() % pageSize) != 0)
			pageCnt++;

		for (int i = 0; i < pageCnt; i++) {
			int itemIndex = i * pageSize;
			SlideImageItem item1 = itemList.get(itemIndex);
			SlideImageItem item2 = null;
			if (itemIndex + 1 < itemList.size())
				item2 = itemList.get(itemIndex + 1);

			LinearLayout ll_item = new LinearLayout(this.getContext());
			ll_item.setOrientation(LinearLayout.HORIZONTAL);
			// ll_item.setBackgroundResource(item.resId);

			LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(-1, -1);
			ll.weight = 1;

			RelativeLayout rlItem = new RelativeLayout(this.getContext());
			rlItem.setBackgroundColor(Color.TRANSPARENT);
			rlItem.setTag(ITEM_BASE_TAG + itemIndex);
			// rlItem.setOnClickListener(this);

			TextView tv = new TextView(this.getContext());
			tv.setText(item1.title);
			tv.setGravity(Gravity.CENTER);
			tv.setTextSize(12);
			tv.setBackgroundColor(Color.argb(120, 0, 0, 0));
			tv.setTextColor(Color.WHITE);

			RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(
					-1, MyUtils.dip2px(this.getContext(), 20));
			rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			// rl.bottomMargin = MyUtils.dip2px(this.getContext(), 14);
			rlItem.addView(tv, rl);

			// ll.rightMargin = MyUtils.dip2px(getContext(), 5);
			ll_item.addView(rlItem, ll);

			if (updateImageDisplay(rlItem, item1) != 0) {
				String imgfile = MyUtils.getLocalImageFileNameByUrl(
						item1.imageUrl, "recommend-");

				_downloader.addTask(_downloader.new DownloadTask(this
						.getContext(), item1.imageUrl, imgfile, this, rlItem,
						0, 0));
			}

			/*
			 * rlItem = new RelativeLayout(this.getContext()); if( item2 != null
			 * ) { rlItem.setBackgroundColor(Color.TRANSPARENT);
			 * rlItem.setTag(ITEM_BASE_TAG + itemIndex + 1); //
			 * rlItem.setOnClickListener(this);
			 * 
			 * tv = new TextView(this.getContext()); tv.setText(item2.title);
			 * tv.setGravity(Gravity.CENTER); tv.setTextSize(12);
			 * tv.setBackgroundColor(Color.argb(120, 0, 0, 0));
			 * tv.setTextColor(Color.WHITE);
			 * 
			 * rl = new
			 * RelativeLayout.LayoutParams(-1,MyUtils.dip2px(this.getContext(),
			 * 20)); rl.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			 * rl.bottomMargin = MyUtils.dip2px(this.getContext(), 14);
			 * rlItem.addView(tv, rl);
			 * 
			 * 
			 * if( updateImageDisplay(rlItem, item2) != 0 ) { String imgfile =
			 * MyUtils.getLocalImageFileNameByUrl(item2.imageUrl, "recommend-");
			 * 
			 * _downloader.addTask(_downloader.new
			 * DownloadTask(this.getContext(), item2.imageUrl, imgfile, this,
			 * rlItem, 0,0)); }
			 * 
			 * } ll.leftMargin = MyUtils.dip2px(getContext(), 5); ll.rightMargin
			 * = 0;
			 * 
			 * ll_item.addView(rlItem, ll);
			 */

			this.addView(ll_item);
		}

		startAutoSlide();
	}

	protected int updateImageDisplay(RelativeLayout flItem,
			SlideImageItem itemInfo) {
		int ret = 0;
		// flItem.setBackgroundResource(itemInfo.resId);
		if (MyUtils.isValidURLString(itemInfo.imageUrl)) {
			String imgfile = MyUtils.getLocalImageFileNameByUrl(
					itemInfo.imageUrl, "recommend-");
			if (MyUtils.PrivateFileExist(this.getContext(), imgfile)) {
				flItem.setBackgroundDrawable(MyUtils.loadPrivateBitmapFile(
						this.getContext(), imgfile));
			} else {
				ret = 1;
			}
		}

		return ret;
	}

	public static class SlideImageItem {
		public String imageUrl;
		public String title;
		public int itemId;

		public int resId;
	}

	@Override
	public void onDownloadSuccessfully(DownloadTask downloadTask) {
		// TODO Auto-generated method stub
		if (downloadTask.contentView != null) {
			downloadTask.contentView.setBackgroundDrawable(downloadTask
					.getDrawable());
		}

	}

	@Override
	public void onDownloadFailed(int errCode, DownloadTask downloadTask) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.v("", "onClick");
		int itemIndex = (Integer) v.getTag();
		if (_onItemClickListener != null) {
			_onItemClickListener.onItemClick(this, itemIndex);
		}

	}
}
