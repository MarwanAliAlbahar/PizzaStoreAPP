package com.alipizza;

import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.alipizza.R;

public class MenuCustomization extends Activity {

	// declare view objects
	EditText edtQuantity, edtComment;
	Button btnPost;
	ImageButton imgNavBack;
	CheckBox chkExtraOnion, chkExtraOlives, chkExtraBacon, chkExtraPinaple,
			chkExtraCheese;
	RadioButton rdbSizeSmall, rdbSizeMedium, rdbSizeLarge, rdbSizeXXL;

	// declare dbhelper object
	static DBHelper dbhelper;

	// declare string variables to store data
	String App_name, Menu_name, Menu_description, Menu_image;
	long Menu_ID;
	String user_id;
	long quantity;
	String Comment;
	long extra_onion, extra_olives, extra_bacon, extra_pinaple, extra_cheese;
	int size = 1;
	double line_price;
	double price_s, price_m, price_l, price_xxl;

	String Result;
	String AddOrderAPI;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.customize_menu);

		// connect view objects and xml id
		imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);
		btnPost = (Button) findViewById(R.id.btnPost);
		edtQuantity = (EditText) findViewById(R.id.edtQuantity);
		chkExtraBacon = (CheckBox) findViewById(R.id.extra_bacon);
		chkExtraCheese = (CheckBox) findViewById(R.id.extra_cheese);
		chkExtraOlives = (CheckBox) findViewById(R.id.extra_olives);
		chkExtraOnion = (CheckBox) findViewById(R.id.extra_onion);
		chkExtraPinaple = (CheckBox) findViewById(R.id.extra_pinaples);

		rdbSizeSmall = (RadioButton) findViewById(R.id.rdbSizeSmall);
		rdbSizeMedium = (RadioButton) findViewById(R.id.rdbSizeMedium);
		rdbSizeLarge = (RadioButton) findViewById(R.id.rdbSizeLarge);
		rdbSizeXXL = (RadioButton) findViewById(R.id.rdbSizeXXL);
		

		dbhelper = new DBHelper(this);

		// get values that sent from previous page and store them to string
		// variables
		Intent i_get = getIntent();
		Menu_ID = i_get.getLongExtra("menu_id", 0);
		Menu_name = i_get.getStringExtra("menu_name");
		Menu_description = i_get.getStringExtra("menu_description");
		Menu_image = i_get.getStringExtra("menu_image");
		App_name = getString(R.string.app_name);

		price_s = i_get.getDoubleExtra("price_s", -1);
		price_m = i_get.getDoubleExtra("price_m", -1);
		price_l = i_get.getDoubleExtra("price_l", -1);
		price_xxl = i_get.getDoubleExtra("price_xxl", -1);
		
		user_id = LogedUser.getInstance().getID();

		AddOrderAPI = Utils.AddOrderAPI + "?menu_id=" + Menu_ID + "&user_id="
				+ user_id;
		
		// customize view
		if(price_s == -1) rdbSizeSmall.setVisibility(View.GONE);
		if(price_m == -1) rdbSizeMedium.setVisibility(View.GONE);
		
		if(price_l == -1) rdbSizeLarge.setVisibility(View.GONE);
		if(price_xxl == -1) rdbSizeXXL.setVisibility(View.GONE);

		// event listener to handle back button when pressed
		imgNavBack.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// close database and back to previous page
				dbhelper.close();
				finish();
			}
		});
		// event listener to handle check box when checked
		chkExtraBacon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				extra_bacon = ((CheckBox) v).isChecked() ? 1 : 0;
			}
		});
		chkExtraCheese.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				extra_cheese = ((CheckBox) v).isChecked() ? 1 : 0;
			}
		});
		chkExtraOlives.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				extra_olives = ((CheckBox) v).isChecked() ? 1 : 0;
			}
		});
		chkExtraOnion.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				extra_onion = ((CheckBox) v).isChecked() ? 1 : 0;
			}
		});
		chkExtraPinaple.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				extra_pinaple = ((CheckBox) v).isChecked() ? 1 : 0;
			}
		});
		
		rdbSizeSmall.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				size = 1;
			}
		});
		
		rdbSizeMedium.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				size = 2;
			}
		});
		rdbSizeLarge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				size = 3;
			}
		});
		rdbSizeLarge.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				size = 4;
			}
		});

		// event listener to handle send button when pressed
		btnPost.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// get data from all forms and send to server
				quantity = Integer.parseInt(edtQuantity.getText().toString());
				
				// open database first
				try {
					dbhelper.openDataBase();
				} catch (SQLException sqle) {
					throw sqle;
				}
		    	
		    	// calculate line price :
		    	line_price = 0;
		    	
		    	double item_price = 0;

		    	double price_onion = 3; // Double.valueOf(R.string.price_extra_onion);
		    	double price_olives= 1.5; // Double.valueOf(R.string.price_extra_olives);
		    	double price_bacon= 4; //Double.valueOf(R.string.price_extra_bacon);
		    	double price_pinaple = 1.5; // Double.valueOf(R.string.price_extra_pinaple);
		    	double price_cheese = 2; // Double.valueOf(R.string.price_extra_cheese);
		    				    	
		    	
		    	switch (size) {
				case 1:
					item_price = price_s;
					break;
				case 2:
					item_price = price_m;
					break;
				case 3:
					item_price = price_l;
					break;
				case 4:
					item_price = price_xxl;
					break;
				default:
					break;
				}
		    	
		    	
		    	
		    	line_price = item_price * quantity;
		    	line_price += extra_onion * price_onion;
		    	line_price += extra_olives * price_olives;
		    	line_price += extra_bacon * price_bacon;
		    	line_price += extra_pinaple * price_pinaple;
		    	line_price += extra_cheese * price_cheese;
		    	
		    	dbhelper.addOrderDetail(Menu_ID, Menu_name, size , Menu_ID, quantity, extra_onion, extra_olives, extra_bacon, extra_pinaple, extra_cheese, line_price);
    			
    			Toast.makeText(MenuCustomization.this, R.string.toast_order_detail_ok, Toast.LENGTH_LONG).show();
    			
				dbhelper.close();
				finish();
	    			

			}
		});
	}
}
