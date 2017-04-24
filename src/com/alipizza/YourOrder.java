package com.alipizza;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.alipizza.R;
import com.alipizza.Reservation.sendData;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.SQLException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class YourOrder extends Activity {

	// declare view objects
	ImageButton imgNavBack;
	ListView listOrder;
	ProgressBar prgLoading;
	TextView txtTotalLabel, txtTotal, txtAlert;
	Button btnClear, btnCheckout;
	RelativeLayout lytOrder;
	RadioButton rdbPaymentCash, rdbPaymentCreditCard;
	RadioButton rdbTakeAwayTrue, rdbTakeAwayFalse;

	// declate dbhelper and adapter objects
	DBHelper dbhelper;
	YourOrderListAdapter mola;

	// declare static variables to store tax and currency data
	static double Tax;
	static String Currency;
	int isTakeAway = 1;
	String PaymentMethod = "Cash";

	String Result;
	String json_server_request = "";
	String user_id = LogedUser.getInstance().getID();

	// declare arraylist variable to store data
	static ArrayList<ArrayList<Object>> data;
	static ArrayList<Integer> ORDER_DETAIL_ID = new ArrayList<Integer>();
	static ArrayList<Integer> Menu_ID = new ArrayList<Integer>();
	static ArrayList<String> Menu_name = new ArrayList<String>();
	static ArrayList<Integer> Quantity = new ArrayList<Integer>();
	static ArrayList<Double> Sub_total_price = new ArrayList<Double>();
	static ArrayList<Integer> size = new ArrayList<Integer>();
	static ArrayList<Integer> extra_onion = new ArrayList<Integer>();
	static ArrayList<Integer> extra_olives = new ArrayList<Integer>();
	static ArrayList<Integer> extra_bacon = new ArrayList<Integer>();
	static ArrayList<Integer> extra_cheese = new ArrayList<Integer>();
	static ArrayList<Integer> extra_pinaples = new ArrayList<Integer>();

	double Total_price;
	final int CLEAR_ALL_ORDER = 0;
	final int CLEAR_ONE_ORDER = 1;
	int FLAG;
	int ID;
	String TaxCurrencyAPI;
	int IOConnect = 0;

	// create price format
	public static DecimalFormat formatter = new DecimalFormat("#,##");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.your_order);

		// connect view objects with xml id
		imgNavBack = (ImageButton) findViewById(R.id.imgNavBack);
		prgLoading = (ProgressBar) findViewById(R.id.prgLoading);
		listOrder = (ListView) findViewById(R.id.listOrder);
		txtTotalLabel = (TextView) findViewById(R.id.txtTotalLabel);
		txtTotal = (TextView) findViewById(R.id.txtTotal);
		txtAlert = (TextView) findViewById(R.id.txtAlert);
		btnClear = (Button) findViewById(R.id.btnClear);
		btnCheckout = (Button) findViewById(R.id.btnCheckout);
		lytOrder = (RelativeLayout) findViewById(R.id.lytOrder);

		rdbPaymentCash = (RadioButton) findViewById(R.id.invoicing_payment_method_cash);
		rdbPaymentCreditCard = (RadioButton) findViewById(R.id.invoicing_payment_method_creditcard);

		rdbTakeAwayFalse = (RadioButton) findViewById(R.id.rdbIsTakeAwayFalse);
		rdbTakeAwayTrue = (RadioButton) findViewById(R.id.rdbIsTakeAwayTrue);

		// tax and currency API url
		TaxCurrencyAPI = Utils.TaxCurrencyAPI + "?accesskey=" + Utils.AccessKey;

		user_id = LogedUser.getInstance().getID();
		mola = new YourOrderListAdapter(this);
		dbhelper = new DBHelper(this);

		// open database
		try {
			dbhelper.openDataBase();
		} catch (SQLException sqle) {
			throw sqle;
		}

		// call asynctask class to request tax and currency data from server
		new getTaxCurrency().execute();

		rdbPaymentCash.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PaymentMethod = "Cash";
			}
		});
		rdbPaymentCreditCard.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PaymentMethod = "Credit Card";
			}
		});

		rdbTakeAwayFalse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isTakeAway = 0;
			}
		});

		rdbTakeAwayTrue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				isTakeAway = 1;
			}
		});

		// event listener to handle clear button when clicked
		btnClear.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// show confirmation dialog
				showClearDialog(CLEAR_ALL_ORDER, 1111);
			}
		});

		btnCheckout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {

				Gson gson = new Gson();
				json_server_request = gson.toJson(data);

				new sendOrderToServer().execute();

			}
		});

		// event listener to handle list when clicked
		listOrder.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// show confirmation dialog
				showClearDialog(CLEAR_ONE_ORDER, ORDER_DETAIL_ID.get(position));
			}
		});

		// event listener to handle back button when clicked
		imgNavBack.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// close database and back to previous page
				dbhelper.close();
				finish();
			}
		});

	}

	// method to create dialog
	void showClearDialog(int flag, int id) {
		FLAG = flag;
		ID = id;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.confirm);
		switch (FLAG) {
		case 0:
			builder.setMessage(getString(R.string.clear_all_order));
			break;
		case 1:
			builder.setMessage(getString(R.string.clear_one_order));
			break;
		}
		builder.setCancelable(false);
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				switch (FLAG) {
				case 0:
					// clear all menu in order table
					dbhelper.deleteAllData();
					listOrder.invalidateViews();
					clearData();
					new getDataTask().execute();
					break;
				case 1:
					// clear selected menu in order table
					dbhelper.deleteData(ID);
					listOrder.invalidateViews();
					clearData();
					new getDataTask().execute();
					break;
				}

			}
		});

		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				// close dialog
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	// method to post data to server
	public String getRequest(String json_query, String user_id) {
		String result = "";

		HttpClient client = new DefaultHttpClient();
		HttpPost request = new HttpPost(Utils.SendOrderAPI);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);

			nameValuePairs
					.add(new BasicNameValuePair("json_query", json_query));
			nameValuePairs.add(new BasicNameValuePair("user_id", user_id));
			nameValuePairs.add(new BasicNameValuePair("order_amount", ""
					+ Total_price));
			nameValuePairs.add(new BasicNameValuePair("PaymentMethod",
					PaymentMethod));
			nameValuePairs.add(new BasicNameValuePair("isTakeAway", ""
					+ isTakeAway));

			request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
					HTTP.UTF_8));

			HttpResponse response = client.execute(request);

			result = request(response);

		} catch (Exception ex) {
			result = "Unable to connect.";
		}

		return result;
	}

	public static String request(HttpResponse response) {
		String result = "";
		try {
			InputStream in = response.getEntity().getContent();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(in));
			StringBuilder str = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				str.append(line + "\n");
			}
			in.close();
			result = str.toString();
		} catch (Exception ex) {
			result = "Error";
		}
		return result;
	}

	// asynctask class to send data to server in background
	public class sendOrderToServer extends AsyncTask<Void, Void, Void> {
		ProgressDialog dialog;

		// show progress dialog
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			dialog = ProgressDialog.show(YourOrder.this, "",
					getString(R.string.sending_alert), true);

		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			// send data to server and store result to variable
			Result = getRequest(json_server_request, user_id);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// if finish, dismis progress dialog and show toast message

			// delete current invoice
			// if (Result.equalsIgnoreCase("OK")) {

			dbhelper.deleteAllData();
			listOrder.invalidateViews();
			clearData();

			new getDataTask().execute();

			// } else {
			// Toast.makeText(YourOrder.this,
			// "Could not contact the restaurant", Toast.LENGTH_LONG)
			// .show();
			//
			// }

			resultAlert(Result);

			finish();

		}
	}

	// method to show toast message
	public void resultAlert(String HasilProses) {
		if (HasilProses.trim().equalsIgnoreCase("OK")) {
			Toast.makeText(
					YourOrder.this,
					"Order transfered to the restaurant. You can check its status on Order History.",
					Toast.LENGTH_LONG).show();
			finish();
		} else if (HasilProses.trim().equalsIgnoreCase("Failed")) {
			Toast.makeText(YourOrder.this, R.string.failed_alert,
					Toast.LENGTH_LONG).show();
		} else {
			Log.d("HasilProses", HasilProses);
		}
	}

	// asynctask class to handle parsing json in background
	public class getTaxCurrency extends AsyncTask<Void, Void, Void> {

		// show progressbar first
		getTaxCurrency() {
			if (!prgLoading.isShown()) {
				prgLoading.setVisibility(0);
				txtAlert.setVisibility(8);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// parse json data from server in background
			parseJSONDataTax();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// when finish parsing, hide progressbar
			prgLoading.setVisibility(8);
			// if internet connection available request data form server
			// otherwise, show alert text
			if (IOConnect == 0) {
				new getDataTask().execute();
			} else {
				txtAlert.setVisibility(0);
				txtAlert.setText(R.string.alert);
			}

		}
	}

	// method to parse json data from server
	public void parseJSONDataTax() {

		try {
			// request data from tax and currency API
			HttpClient client = new DefaultHttpClient();
			HttpConnectionParams
					.setConnectionTimeout(client.getParams(), 15000);
			HttpConnectionParams.setSoTimeout(client.getParams(), 15000);
			HttpUriRequest request = new HttpGet(TaxCurrencyAPI);
			HttpResponse response = client.execute(request);
			InputStream atomInputStream = response.getEntity().getContent();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					atomInputStream));

			String line;
			String str = "";
			while ((line = in.readLine()) != null) {
				str += line;
			}

			// parse json data and store into tax and currency variables
			JSONObject json = new JSONObject(str);
			JSONArray data = json.getJSONArray("data"); // this is the "items: [
														// ] part

			JSONObject object_tax = data.getJSONObject(0);
			JSONObject tax = object_tax.getJSONObject("tax_n_currency");

			Tax = Double.parseDouble(tax.getString("Value"));

			JSONObject object_currency = data.getJSONObject(1);
			JSONObject currency = object_currency
					.getJSONObject("tax_n_currency");

			Currency = currency.getString("Value");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			IOConnect = 1;
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// clear arraylist variables before used
	void clearData() {
		ORDER_DETAIL_ID.clear();
		Menu_ID.clear();
		Menu_name.clear();
		Quantity.clear();
		Sub_total_price.clear();
		size.clear();
		extra_onion.clear();
		extra_olives.clear();
		extra_bacon.clear();
		extra_cheese.clear();
		extra_pinaples.clear();
	}

	// asynctask class to handle parsing json in background
	public class getDataTask extends AsyncTask<Void, Void, Void> {

		// show progressbar first
		getDataTask() {
			if (!prgLoading.isShown()) {
				prgLoading.setVisibility(0);
				lytOrder.setVisibility(8);
				txtAlert.setVisibility(8);
			}
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			// TODO Auto-generated method stub
			// get data from database
			getDataFromDatabase();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			// show data
			txtTotal.setText(String.format("%.2f", Total_price) + " "
					+ Currency);
			txtTotalLabel.setText(getString(R.string.total_order) + " (Tax "
					+ (Tax) + "%)");
			prgLoading.setVisibility(8);
			// if data available show data on list
			// otherwise, show alert text
			if (ORDER_DETAIL_ID.size() > 0) {
				lytOrder.setVisibility(0);
				listOrder.setAdapter(mola);
			} else {
				txtAlert.setVisibility(0);
			}

		}
	}

	// method to get data from server
	public void getDataFromDatabase() {

		Total_price = 0;
		clearData();
		data = dbhelper.getAllData();

		// store data to arraylist variables
		for (int i = 0; i < data.size(); i++) {
			ArrayList<Object> row = data.get(i);

			ORDER_DETAIL_ID.add(Integer.parseInt(row.get(0).toString()));
			Menu_ID.add(Integer.parseInt(row.get(1).toString()));
			Menu_name.add(row.get(2).toString());
			Quantity.add(Integer.parseInt(row.get(3).toString()));
			Sub_total_price.add(Double.parseDouble(row.get(4).toString()));
			Menu_ID.add(Integer.parseInt(row.get(5).toString()));
			Menu_ID.add(Integer.parseInt(row.get(6).toString()));
			Menu_ID.add(Integer.parseInt(row.get(7).toString()));
			Menu_ID.add(Integer.parseInt(row.get(8).toString()));
			Menu_ID.add(Integer.parseInt(row.get(9).toString()));
			Menu_ID.add(Integer.parseInt(row.get(10).toString()));

			Total_price += Sub_total_price.get(i);
		}

		// count total order
		Total_price = (Total_price * (1 + (Tax / 100)));

	}

	// when back button pressed close database and back to previous page
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		dbhelper.close();
		finish();
	}

	@Override
	public void onConfigurationChanged(final Configuration newConfig) {
		// Ignore orientation change to keep activity from restarting
		super.onConfigurationChanged(newConfig);
	}

}
