package com.cutv.mobile.cartoon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class TestActivity extends BaseActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LinearLayout rootView = (LinearLayout) LayoutInflater.from(this)
				.inflate(R.layout.test_layout, null);
		this.setContentView(rootView);

		this.setTitle("≤‚ ‘");

		Button btn = this.getLeftButton();
		btn.setBackgroundResource(R.drawable.btn_return_selector);
		btn.setOnClickListener(this);

		btn = this.getRightButton();
		btn.setVisibility(View.INVISIBLE);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.btn_left) {
			finish();
		}

	}
}
