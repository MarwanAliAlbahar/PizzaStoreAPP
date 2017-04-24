package com.alipizza;

import java.io.IOException;

import com.alipizza.R;
import com.alipizza.Login;
import com.alipizza.library.UserFunctions;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Home extends Activity {
	
	// declare view objects
	ImageButton imgNews, imgContact, imgSetting;
	ListView listMainMenu;
	ImageView imgCover;
	// declare dbhelper and adapter object
	static DBHelper dbhelper;
	MainMenuAdapter mma;
	int user_id;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		
		TextView Logoutxt = (TextView) findViewById(R.id.textView1);
		Logoutxt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
            	LogedUser.getInstance().setID("");
                Intent myIntent = new Intent(view.getContext(), Login.class);
                startActivity(myIntent);
                //finish();
            }

        });
		
		
		// connect view objects and xml id
		imgNews = (ImageButton) findViewById(R.id.imgNews);
		imgContact = (ImageButton) findViewById(R.id.imgContact);
		imgSetting = (ImageButton) findViewById(R.id.imgSetting);
		listMainMenu = (ListView) findViewById(R.id.listMainMenu);
		imgCover = (ImageView) findViewById(R.id.imgCover);

		// get screen device width and height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int wPix = dm.widthPixels;
		int hPix = wPix / 2 + 50;

		// change cover image width and height
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
		imgCover.setLayoutParams(lp);

		// checking internet connection
		if (!Utils.isNetworkAvailable(Home.this)) {
			Toast.makeText(Home.this, getString(R.string.no_internet),
					Toast.LENGTH_SHORT).show();
		}

		mma = new MainMenuAdapter(this);
		dbhelper = new DBHelper(this);
		listMainMenu.setAdapter(mma);
		
		user_id = 1;

		// create database
		try {
			dbhelper.createDataBase();
		} catch (IOException ioe) {
			throw new Error("Unable to create database");
		}

		// then, the database will be open to use
		try {
			dbhelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		// if user has already ordered food previously then show confirm dialog
		if (dbhelper.isPreviousDataExist()) {
			showAlertDialog();
		}

		// event listener to handle list when clicked
		listMainMenu.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					// go to category page
					 LogedUser.getInstance().setfav(0);
					 LogedUser.getInstance().setHistory(0);
					Intent iMenuList = new Intent(Home.this, CategoryList.class);
					startActivity(iMenuList);
					break;
				case 1:
					// go to my order page
					Intent iMyOrder = new Intent(Home.this, YourOrder.class);
					startActivity(iMyOrder);
					break;
				case 2:
					// go to order history
					Intent iOrderHistory = new Intent(Home.this, OrderHistory.class);
					startActivity(iOrderHistory);
					break;
				case 3:
					// go to favorite page
					LogedUser.getInstance().setfav(1);
					LogedUser.getInstance().setHistory(0);
					Intent iFavorite = new Intent(Home.this, CategoryList.class);
					startActivity(iFavorite);
					break;
				case 4:
					// go to reservation page
					Intent iReservation = new Intent(Home.this,
							Reservation.class);
					startActivity(iReservation);
					break;
				}
			}
		});

		// event listener to handle news button when clicked
		imgNews.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// go to news page
				Intent iNews = new Intent(Home.this, News.class);
				startActivity(iNews);
			}
		});

		// event listener to handle contact button when clicked
		imgContact.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// go to contact page
				Intent iContact = new Intent(Home.this, Contact.class);
				startActivity(iContact);
			}
		});

		// event listener to handle setting button when clicked
		imgSetting.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub
				// go to setting page
				Intent iSetting = new Intent(Home.this,
						SettingPreferences.class);
				startActivity(iSetting);
			}
		});

	}

	// show confirm dialog to ask user to delete previous order or not
	void showAlertDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		builder.setMessage(getString(R.string.db_exist_alert));
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.yes),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// delete order data when yes button clicked
						dbhelper.deleteAllData();
						dbhelper.close();

					}
				});

		builder.setNegativeButton(getString(R.string.no),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						// close dialog when no button clicked
						dbhelper.close();
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();

	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		dbhelper.close();
		finish();
	}

}
