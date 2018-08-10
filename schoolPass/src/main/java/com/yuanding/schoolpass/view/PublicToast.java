package com.yuanding.schoolpass.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanding.schoolpass.R;

public class PublicToast extends Toast {

	public PublicToast(Context context, CharSequence text) {
		super(context);
		// TODO Auto-generated constructor stub
		View layout = LayoutInflater.from(context).inflate(R.layout.pub_toast_default, null);
		TextView textView = (TextView)layout.findViewById(R.id.toast_text);
		textView.setText(text);
		setView(layout);
		setDuration(LENGTH_SHORT);
		setGravity(Gravity.CENTER, 0, 0);
	}
	
	public PublicToast(Context context, CharSequence text,int gravity,int high) {
		super(context);
		// TODO Auto-generated constructor stub
		View layout = LayoutInflater.from(context).inflate(R.layout.pub_toast_default, null);
		TextView textView = (TextView)layout.findViewById(R.id.toast_text);
		textView.setText(text);
		setView(layout);
		setDuration(LENGTH_SHORT);
		setGravity(gravity, 0,high);
	}

}
