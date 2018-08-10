package com.yuanding.schoolpass.utils;

import com.yuanding.schoolpass.A_0_App;

import android.util.Log;

public class LogUtils {

	public static boolean isOn() {
		return A_0_App.debug_Log;
	}

	public static void logE(String tag, String msg) {
		if (!isOn())
			return;
		Log.e(tag, msg);
	}

	public static void logD(String tag, String msg) {
		if (!isOn())
			return;
		Log.d(tag, msg);
	}
}
