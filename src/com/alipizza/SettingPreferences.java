package com.alipizza;


import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import com.alipizza.R;

public class SettingPreferences extends PreferenceActivity {

	PackageInfo pInfo;

	// declare view objects
	ProgressDialog mProgress;
	ImageButton imgNavBack;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.setting_preferences);
		setContentView(R.layout.setting);

		// connect view objects and xml id
		imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);

		// event listener to handle back button when pressed
		imgNavBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// bak to previous page
				finish();
			}
		});

	}
}