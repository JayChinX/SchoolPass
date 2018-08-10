package com.yuanding.schoolpass.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年2月25日 下午4:03:26
 * 自动获取验证码
 */
public class ReadSmsContent extends ContentObserver {
    private Cursor cursor = null;
    private Activity activity;

    private String smsContent = "";
    private EditText editText = null;

    public ReadSmsContent(Activity activity, Handler handler, EditText edittext) {
        super(handler);
        this.activity = activity;
        this.editText = edittext;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);       
        /** 判断是否有读取短信的权限 */
        PackageManager pm = activity.getPackageManager();  
        boolean permission = (PackageManager.PERMISSION_GRANTED ==   pm.checkPermission("android.permission.READ_SMS", "packageName"));  
        if (!permission) {  
        	return;
        }         
        // 读取收件箱中指定号码的短信(managedQuery方法已经被弃用)
        cursor = activity.managedQuery(Uri.parse("content://sms/inbox"),
                new String[] { "_id", "address", "read", "body" }, "address=? and read=?",
                new String[] {AppStrStatic.SEND_YANZHENG_MESSAGE_NO, "0" }, "_id desc");
        // 按短信id排序，如果按date排序的话，修改手机时间后，读取的短信就不准了
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                String smsbody = cursor.getString(cursor.getColumnIndex("body"));
                String regEx = "(?<![0-9])([0-9]{" + AppStrStatic.SEND_YANZHENG_MESSAGE_COUNT + "})(?![0-9])";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsbody.toString());
                while (m.find()) {
                    smsContent = m.group();
                    editText.setText(smsContent);
                }
            }
        }
        
        //在用managedQuery的时候，不能主动调用close()方法， 否则在Android 4.0+的系统上，         会发生崩溃
        if (Build.VERSION.SDK_INT < 14) {
            cursor.close();
        }
    }
}
