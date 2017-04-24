package com.alipizza;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.alipizza.R;

public class ShareMenu extends Activity {

	// declare view objects
	EditText edtMessage;
	Button btnPost;
	ImageButton imgNavBack;

	// declare string variables to store data
	String App_name, Menu_name, Menu_description, Menu_image;
	long Menu_ID;

	String MenuSharingPage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_menu);

		// connect view objects and xml id
		imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);
		edtMessage = (EditText) findViewById(R.id.edtMessage);
		btnPost = (Button) findViewById(R.id.btnPost);

		// get values that sent from previous page and store them to string
		// variables
		Intent i_get = getIntent();
		Menu_ID = i_get.getLongExtra("menu_id", 0);
		Menu_name = i_get.getStringExtra("menu_name");
		Menu_description = i_get.getStringExtra("menu_description");
		Menu_image = i_get.getStringExtra("menu_image");
		App_name = getString(R.string.app_name);

		MenuSharingPage = Utils.MenuSharingAPI + "?id=" + Menu_ID;

		// event listener to handle back button when pressed
		imgNavBack.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// back to previous page
				finish();
			}
		});

		// event listener to handle post button when pressed
		btnPost.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				// get text from edittext and store to string variable
				String review = edtMessage.getText().toString();

				// check to value of review
				if (review.equals("")) {
					review = Menu_name + " at " + getString(R.string.app_name)
							+ " ";
				} else {
					review += " - " + Menu_name + " at "
							+ getString(R.string.app_name) + " ";
				}

			}
		});
	}
}
