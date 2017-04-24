package com.alipizza;

import java.io.InputStream;
import java.io.OutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Utils {

	// Google Play configuration
	static String GPlayURL = "https://play.google.com/store/apps/details?id=your.package.com";

	// API URL configuration
	// pc_1
	// pc_2
	public static String AdminPageURL = "http://192.168.43.69:81/Admin_Panel/";
	static String CategoryAPI = AdminPageURL + "/api/get-all-category-data.php";
	static String MenuAPI = AdminPageURL
			+ "/api/get-menu-data-by-category-id.php";
	static String TaxCurrencyAPI = AdminPageURL
			+ "/api/get-tax-and-currency.php";
	static String MenuDetailAPI = AdminPageURL + "/api/get-menu-detail.php";
	static String SendDataAPI = AdminPageURL + "/api/add-reservation.php";
	static String SendOrderAPI = AdminPageURL + "/api/send-order.php";
	static String MenuSharingAPI = AdminPageURL + "/menu-sharing.php";
	static String OrderHistoryAPI = AdminPageURL + "/api/order-history.php";
	static String OrderHistoryDelaiAPI = AdminPageURL + "/api/order-history-detail.php";
	static String AddOrderAPI = AdminPageURL + "/api/add-order.php";
	static String FavoriteAPI = AdminPageURL + "/api/favorite.php";

	// change this access similar with accesskey in admin panel for security
	// reasonh
	static String AccessKey = "12345";

	// database path configuration
	@SuppressLint("SdCardPath")
	static String DBPath = "/data/data/com.alipizza/databases/";

	// method to check internet connection
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// method to handle images from server
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

}
