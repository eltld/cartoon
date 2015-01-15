package com.cutv.mobile.utils;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cutv.mobile.cartoon.R;
import com.cutv.mobile.data.CategoryInfo;

/**
 * 标题栏分类弹窗
 */
public class CategoryPopwindow implements OnItemClickListener {
	private PopupWindow popupWindow;
	private Context mContext;
	private View v;
	private View view;
	private ListView listview;
	private MyAdapter adapter;
	private OnPopWindowItemcCick callback;
	private List<CategoryInfo> _categoryList;
	private int popWidth;
	private int popHeight;

	public CategoryPopwindow(Context context, View v,
			List<CategoryInfo> _categoryList) {
		this.mContext = context;
		this.v = v;
		LayoutInflater inflater = LayoutInflater.from(mContext);
		view = inflater.inflate(R.layout.category_popwindow_lalyout, null);
		this._categoryList = _categoryList;
		initView();
		initPopwindow();
	}

	private void initView() {
		listview = (ListView) view.findViewById(R.id.listView1);
		listview.setOnItemClickListener(this);
		adapter = new MyAdapter();
		listview.setAdapter(adapter);
	}

	/**
	 * 初始化popwindow
	 */
	@SuppressWarnings("deprecation")
	private void initPopwindow() {
		popWidth = MyUtils.dip2px(mContext, 200);
		popHeight = MyUtils.dip2px(mContext, 300);
		popupWindow = new PopupWindow(view, popWidth, LayoutParams.WRAP_CONTENT);
		// 这个是为了点击“返回Back”也能使其消失
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
	}

	/**
	 * popwindow的显示
	 */
	public void show() {
		// 屏幕居中显示
		int xOff = (v.getWidth() - popWidth) / 2;
		popupWindow.showAsDropDown(v, xOff, 0);
		// popupWindow.showAsDropDown(v);
		// 使其聚集
		popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		popupWindow.setOutsideTouchable(true);
		// 刷新状态
		popupWindow.update();
	}

	// 隐藏
	public void dismiss() {
		popupWindow.dismiss();
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return _categoryList.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.category_list_item, null);
				holder = new ViewHolder();

				holder.text = (TextView) convertView
						.findViewById(R.id.textView1);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.text.setText(_categoryList.get(position).title);
			return convertView;
		}
	}

	class ViewHolder {
		TextView text;
	}

	public void setOnPopWindowItemcCick(OnPopWindowItemcCick callback) {
		this.callback = callback;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
		dismiss();
		if (callback != null) {
			callback.onclick(position);
		}
	}

	public interface OnPopWindowItemcCick {
		void onclick(int position);
	}
}
