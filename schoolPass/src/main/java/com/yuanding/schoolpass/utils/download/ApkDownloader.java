package com.yuanding.schoolpass.utils.download;

import java.io.File;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.utils.download.AsyncDownLoadManager.OnDownLoadListener;
import com.yuanding.schoolpass.utils.download.AsyncDownLoadManager.OnDownLoadProgrossListener;

/**
 * 
 * @author dllik 2013-11-23
 */
public class ApkDownloader {
	private Context activity = null;

	/**
	 * apk name
	 */
	private String strApkName;

	/**
     */
	private String strApkUrl;

	/**
     */
	private int intIconResouId;

	private int intapkId = 123456;

	/**
     * 
     */
	private NotificationManager notificationManager = null;

	/**
     */
	private Notification notification = null;

	private OnDownLoadProgrossListener OnDownLoadProgrossListener;

	public ApkDownloader(Context activity, String url) {
		this.activity = activity;
		this.strApkUrl = url;
	}

	public ApkDownloader(Context activity, String url, int iconResouceId) {
		strApkUrl = url;
		this.intIconResouId = iconResouceId;
		this.activity = activity;
	}

	public void setApkId(int id) {
		intapkId = id;
	}

	/**
	 * @param activity
	 * @param url
	 * @param iconResouceId
	 * @param apkName
	 */
	public ApkDownloader(Context activity, String url, int iconResouceId,
			String apkName,
			OnDownLoadProgrossListener OnDownLoadProgrossListener) {
		strApkUrl = url;
		this.OnDownLoadProgrossListener = OnDownLoadProgrossListener;
		this.intIconResouId = iconResouceId;
		this.strApkName = apkName;
		this.activity = activity;
	}

//	private void showDownLoadNotification() {
//		notificationManager = (NotificationManager) activity
//				.getSystemService(Context.NOTIFICATION_SERVICE);
//		notification = new Notification();
//				
//		notification.contentView = new RemoteViews(activity.getPackageName(),R.layout.notice_view);		
//		
//		if (intIconResouId != 0) {
//			notification.icon = intIconResouId;
//		}
//
//		notification.tickerText = strApkName;
//
//		Intent intent = new Intent();
//
//		PendingIntent pIntent = PendingIntent.getActivity(
//				activity.getApplicationContext(), 0, intent, 0);
//		notification.contentIntent = pIntent;
//		notificationManager.notify(intapkId, notification);
//
//	}
	

	private void showDownLoadNotification() {
		notificationManager = (NotificationManager) activity .getSystemService(Context.NOTIFICATION_SERVICE);
		notification = new Notification();

	
		//手机型号： android.os.Build.MODEL,手机厂商：android.os.Build.MANUFACTURER
		int defaultNotificationTitleColor = getNotificationColor();
		if("vivo".equals(android.os.Build.MANUFACTURER)){
			//白字 带大边距
			notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_largermargin);
		}else if("Xiaomi".equals(android.os.Build.MANUFACTURER) || "OPPO".equals(android.os.Build.MANUFACTURER) ){
			//白字 带小边距
			notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_smallmargin);
		}else if("HUAWEI".equals(android.os.Build.MANUFACTURER)){			
			
			//解决部分华为手机（低于emui3.1）是白色通知栏
			if (-1 != defaultNotificationTitleColor) {
				if (isColorSimilar(defaultNotificationTitleColor)) {
					//title的颜色近似黑色，也就是说通知栏背景近似白色
					notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_dark_margin);					
				}else{
					//title的颜色近似白色，也就是说通知栏背景近似黑色
				   //白字 不带边距
					notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_nomargin);
				}
			}else{
				//没有获取到默认的通知的标题的颜色
				notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_nomargin);				
			}
		}else{
			//其他厂商的手机 或者 没有获取到手机厂商 获取通知栏默认的通知的标题的颜色			
			
			if (-1 != defaultNotificationTitleColor) {
				if (isColorSimilar(defaultNotificationTitleColor)) {
					//title的颜色近似黑色，也就是说通知栏背景近似白色
					notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_dark_margin);
					
				}else{
					//title的颜色近似白色，也就是说通知栏背景近似黑色
					notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_largermargin);
				}
			}else{
				//没有获取到默认的通知的标题的颜色
				notification.contentView = new RemoteViews( activity.getPackageName(), R.layout.notice_view_white_largermargin);				
			}
		}

		
		if (intIconResouId != 0) {
			notification.icon = intIconResouId;
		}
		notification.tickerText = strApkName;
		notification.flags = Notification.FLAG_NO_CLEAR;
		Intent intent = new Intent();
		PendingIntent pIntent = PendingIntent.getActivity( activity.getApplicationContext(), 0, intent, 0);
		notification.contentIntent = pIntent;
		notificationManager.notify(intapkId, notification);

	}
	
	/**
	 * 获取当前通知栏默认通知的标题的文本颜色
	 * @return
	 */
	private int getNotificationColor() {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this.activity);
		Notification notification = builder.build();
		ViewGroup viewgroup = (ViewGroup)notification.contentView.apply(this.activity, new LinearLayout(this.activity));
	    TextView title = (TextView)viewgroup.findViewById(android.R.id.title);
	    if(null==title){
	    	//因为有些ROM厂商会把id改掉，导致找到的title为空。
	    	return -1;
	    }else{
	    	return title.getCurrentTextColor();
	    }
	    
	}
		
	
	/**
	 * 计算color近似白色（false）还是黑色（true）
	 * @param color
	 * @return  白色（false） 黑色（true）
	 */
	public static boolean isColorSimilar(int color){
		int baseColor = Color.BLACK;
		int simpleBaseColor = baseColor | 0xff000000;
		int simpleColor = color | 0xff000000;
		int baseRed = Color.red(simpleBaseColor) - Color.red(simpleColor);
		int baseGreen = Color.green(simpleBaseColor) - Color.green(simpleColor);
		int baseBlue = Color.blue(simpleBaseColor) - Color.blue(simpleColor);
		double value = Math.sqrt(baseRed * baseRed + baseGreen * baseGreen+ baseBlue *baseBlue);
		if(value<COLOR_THRESHOLD){
			return true;
		}else{
			return false;
		}
	}
	private static final double COLOR_THRESHOLD = 180.0;
	
	
	/**
     */
	public void downLoadApp() {
		showDownLoadNotification();
		AsyncDownLoadManager downLoadManager = AsyncDownLoadManager
				.getAsyncManager(activity);
		WebResource mResource = new WebResource();
		String path = "Android/data/" + activity.getPackageName();
		File file = new FileUtils(activity.getApplicationContext())
				.creatSDDir(path);
		mResource.filePath = file.getAbsolutePath() + "/";
		mResource.url = strApkUrl;
		mResource.fileName = strApkUrl
				.substring(strApkUrl.lastIndexOf("/") + 1);
		downLoadManager.addDownTask(mResource, onDownLoadListener);

	}

	private String getMIMEType(File f) {
		String type = "";
		String fName = f.getName();
		String end = fName.substring(fName.lastIndexOf(".") + 1,
				fName.length()).toLowerCase();
		if (end.equals("m4a") || end.equals("mp3") || end.equals("mid")
				|| end.equals("xmf") || end.equals("ogg")
				|| end.equals("wav")) {
			type = "audio";
		} else if (end.equals("3gp") || end.equals("mp4")) {
			type = "video";
		} else if (end.equals("jpg") || end.equals("gif")
				|| end.equals("png") || end.equals("jpeg")
				|| end.equals("bmp")) {
			type = "image";
		} else if (end.equals("apk")) {
			type = "application/vnd.android.package-archive";
		} else {
			type = "*";
		}
		if (end.equals("apk")) {
		} else {
			type += "/*";
		}
		return type;
	}

	public void openFile(File f) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		String type = getMIMEType(f);
		intent.setDataAndType(Uri.fromFile(f), type);
		activity.startActivity(intent);
	}

	int currentProgress = 0;

	OnDownLoadListener onDownLoadListener = new OnDownLoadListener() {

		@Override
		public void onUpdataDownLoadProgross(WebResource Resource, int progross) {
			if (progross > currentProgress) {
				OnDownLoadProgrossListener.onUpdataDownLoadProgross(Resource,
						progross);
				notification.contentView.setTextViewText(R.id.notice_view_text,
						strApkName + progross + "%");
				notification.contentView.setProgressBar(
						R.id.notice_view_progress, 100, progross, false);
				notificationManager.notify(intapkId, notification);
			}
			currentProgress = progross;
		}

		@Override
		public void onFinshDownLoad(WebResource mResource) {

			// dialog.dismiss();
			File appFile = new File(mResource.filePath + mResource.fileName);
			openFile(appFile);
			notificationManager.cancel(intapkId);
			A_0_App.getInstance().mUpdating = false;
		}

		@Override
		public void onError(String error) {
			A_0_App.getInstance().mUpdating = false;
			notificationManager.cancel(intapkId);
			Toast.makeText(activity.getApplicationContext(), error,
					Toast.LENGTH_SHORT).show();
		}
	};
}
