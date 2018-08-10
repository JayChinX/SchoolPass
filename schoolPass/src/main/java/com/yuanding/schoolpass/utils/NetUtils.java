package com.yuanding.schoolpass.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月17日 下午4:43:41 网络判断工具
 */
public class NetUtils {
	
	private NetUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	//判断网络是否连接
	public static boolean isConnected(Context context) {

		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (null != connectivity) {

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (null != info && info.isConnected()) {
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否是wifi连接
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (cm == null)
			return false;
		return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity) {
		if (Integer.parseInt(Build.VERSION.SDK) <= 10) {
			Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			activity.startActivity(intent);
		} else {
			activity.startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
		}
	}

}
