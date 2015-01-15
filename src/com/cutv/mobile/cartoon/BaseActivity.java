package com.cutv.mobile.cartoon;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class BaseActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);

	}

	protected void setTitle(String title) {
		TextView titleTextView = (TextView) this
				.findViewById(R.id.title_center_txt);
		if (titleTextView != null)
			titleTextView.setText(title);
	}

	protected Button getLeftButton() {
		Button button = (Button) findViewById(R.id.btn_left);

		return button;
	}

	protected Button getRightButton() {
		Button button = (Button) findViewById(R.id.btn_right);

		return button;
	}
}
