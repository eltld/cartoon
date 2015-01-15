package com.cutv.mobile.cartoon;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.cmdm.control.biz.CaiYinPhoneBiz;
import com.cmdm.control.util.ToastUtil;
import com.cmdm.control.util.client.ResultEntity;
import com.cutv.mobile.data.ContentInfo;
import com.cutv.mobile.utils.HttpUrlHelper;
import com.cutv.mobile.utils.MyUtils;
import com.cutv.mobile.utils.SharedUtils;

public class MainTabActivity extends FragmentActivity implements
		RadioGroup.OnCheckedChangeListener {

	private List<Fragment> _fragmentList = new ArrayList<Fragment>();

	private int _currentTabIndex = -1;

	private static MainTabActivity _mainTabActivityInstance = null;

	private static long _firstExitTime = 0;

	public static MainTabActivity sharedInstance() {
		return _mainTabActivityInstance;
	}

	public void onCreate(Bundle savedInstanceState) {
		_mainTabActivityInstance = this;

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_layout);

		// DataModule.removeAllTempImage(this);

		buildUI();
		initCaiYin();
		// ClientUpgrade cu = new ClientUpgrade(this);
		// cu.startCheckVersion();
	}

	private void initCaiYin() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				new CaiYinPhoneBiz(MainTabActivity.this).initSDK(1);
			}
		}).start();
	}

	private void buildUI() {
		_fragmentList.add(new HomeFragmentPage());

		AnimationFragmentPage page = new AnimationFragmentPage();
		// page.setContentType(ContentInfo.CONTENT_TYPE_ANIMATION);
		// _fragmentList.add(page);

		page = new AnimationFragmentPage();
		page.setContentType(ContentInfo.CONTENT_TYPE_CARTOON);
		_fragmentList.add(page);

		_fragmentList.add(new PictureFragmentPage());
		_fragmentList.add(new RankFragmentPage());

		RadioGroup rg = (RadioGroup) this.findViewById(R.id.tabs_rg);

		rg.setOnCheckedChangeListener(this);

		showTab(0);
		regisiterUser();
	}

	private void regisiterUser() {
		if (!"".equals(SharedUtils.getUserID())) {
			return;
		}
		new Thread() {
			public void run() {
				String deviceid = MyUtils
						.getLocaldeviceId(MainTabActivity.this);
				boolean res = HttpUrlHelper.addNewUser(deviceid);
				if (res) {
					SharedUtils.setUserId(deviceid);
				}
			}
		}.start();
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		for (int i = 0; i < group.getChildCount(); i++) {
			if (group.getChildAt(i).getId() == checkedId) {
				showTab(i);
				break;
			}
		}
	}

	public void showTab(int tabIndex) {
		RadioGroup group = (RadioGroup) this.findViewById(R.id.tabs_rg);

		if (tabIndex < 0 || tabIndex >= group.getChildCount())
			return;

		if (_currentTabIndex == tabIndex)
			return;

		if (_currentTabIndex >= 0) {
			_fragmentList.get(_currentTabIndex).onPause();
		}

		FragmentTransaction ft = this.getSupportFragmentManager()
				.beginTransaction();

		for (int i = 0; i < group.getChildCount(); i++) {
			Fragment fg = _fragmentList.get(i);
			RadioButton tabItem = (RadioButton) group.getChildAt(i);

			if (i == tabIndex) {

				// if( i > _currentTabIndex )
				// {
				// ft.setCustomAnimations(R.anim.slide_left_in,
				// R.anim.slide_left_out);
				// }
				// else
				// {
				// ft.setCustomAnimations(R.anim.slide_right_in,
				// R.anim.slide_right_out);
				// }

				if (fg.isAdded()) {
					fg.onResume();
				} else {
					ft.add(R.id.realtabcontent, fg);
				}

				ft.show(fg);

				tabItem.setTextColor(Color.rgb(255, 34, 34));
			} else {
				ft.hide(fg);
				tabItem.setTextColor(Color.rgb(108, 79, 34));
			}
		}
		ft.commit();

		_currentTabIndex = tabIndex;

		RadioButton rb = (RadioButton) group.getChildAt(tabIndex);
		if (!rb.isChecked())
			rb.setChecked(true);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - _firstExitTime <= 2000) {
				System.exit(0);
				// this.finish();
			} else {
				Toast toast = Toast.makeText(this, "再按一次退出程序！",
						Toast.LENGTH_SHORT);
				// toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				_firstExitTime = secondTime;
			}
			return true;
		}

		return false;
	}

}
