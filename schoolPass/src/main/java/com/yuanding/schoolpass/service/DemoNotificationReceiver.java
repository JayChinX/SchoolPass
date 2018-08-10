package com.yuanding.schoolpass.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import io.rong.push.notification.PushNotificationMessage;
import io.rong.push.notification.PushMessageReceiver;


public class DemoNotificationReceiver extends PushMessageReceiver {
    @Override
    public boolean onNotificationMessageArrived(Context context, PushNotificationMessage message) {
    	
        return false;
    }

    @Override
    public boolean onNotificationMessageClicked(Context context, PushNotificationMessage message) {
    	Intent intent = new Intent();
    	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	Uri.Builder builder = Uri.parse("rong://" + context.getPackageName()).buildUpon();

    	builder.appendPath("conversation").appendPath(message.getConversationType().getName())
    	        .appendQueryParameter("targetId", message.getTargetId())
    	        .appendQueryParameter("title", message.getTargetUserName());
    	Uri uri = builder.build();
    	intent.setData(uri);
    	context.startActivity(intent);
        return true;
    }

}
