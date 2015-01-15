package com.cutv.mobile.cartoon;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BaseFragment extends Fragment {

	protected void showLog(String log) {
		// System.out.println(TAG + ":" + log);

		String tag = this.getClass().getName();

		Log.d(tag, log);
	}

	protected void setTitle(String title) {
		TextView titleTextView = (TextView) this.getView().findViewById(
				R.id.title_center_txt);
		if (titleTextView != null)
			titleTextView.setText(title);
	}

	protected Button getLeftButton() {
		Button button = (Button) this.getView().findViewById(R.id.btn_left);

		return button;
	}

	protected Button getRightButton() {
		Button button = (Button) this.getView().findViewById(R.id.btn_right);

		return button;
	}

	@Override
	public void onAttach(Activity activity) {
		showLog("onAttach");
		super.onAttach(activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		showLog("onCreate");
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		showLog("onCreateView");
		return inflater.inflate(R.layout.fragment_home, null);
	}

	@Override
	public void onStart() {
		showLog("onStart");
		super.onStart();

	}

	@Override
	public void onResume() {
		showLog("onResume");
		super.onResume();
	}

	@Override
	public void onPause() {
		showLog("onPause");
		super.onPause();
	}

	@Override
	public void onDestroy() {
		showLog("onDestroy");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		showLog("onDetach");
		super.onDetach();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		showLog("onActivityCreated");
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		showLog("onDestroyView");
		super.onDestroyView();
	}

	@Override
	public void onStop() {
		showLog("onStop");
		super.onStop();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		showLog("onViewCreated");
		super.onViewCreated(view, savedInstanceState);
	}

}
