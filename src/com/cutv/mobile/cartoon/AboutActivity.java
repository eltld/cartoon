package com.cutv.mobile.cartoon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.cmdm.phone.ui.CaiYinWapActivity;
import com.cutv.mobile.utils.MyUtils;

public class AboutActivity extends BaseActivity implements OnClickListener {
	private TextView txt_version;
	private Button mbutton_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		initView();
	}

	private void initView() {
		Button btn = this.getLeftButton();
		btn.setBackgroundResource(R.drawable.btn_return_selector);
		btn.setOnClickListener(this);
		btn = this.getRightButton();
		btn.setVisibility(View.INVISIBLE);
		setTitle("����");
		txt_version = (TextView) findViewById(R.id.txt_app_version);
		txt_version.setText("V" + MyUtils.getVersionName(this));
		mbutton_id = (Button) findViewById(R.id.button1);
		mbutton_id.setOnClickListener(this); 

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.button1) {
			Intent intent = new Intent();
			intent.setClass(this, CaiYinWapActivity.class);
			startActivity(intent);
			return;
		}
		finish();
	}
}
