package com.yuanding.schoolpass.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.yuanding.schoolpass.R;

public class GeneralDialog extends PublicDialog {

	public GeneralDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		this.SetContentView(R.layout.dialog_general);
	}
	
	public GeneralDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.SetContentView(R.layout.dialog_general);
	}
	
	public void setTitle(int resid){
		TextView v = (TextView) findViewById(R.id.dialog_title_version_name);
		v.setText(resid);
	}
	
	public void setTitle(CharSequence text){
		TextView v = (TextView) findViewById(R.id.dialog_title_version_name);
		v.setText(text);
	}
	
	public void setContent(int resid){
		TextView v = (TextView) findViewById(R.id.tv_dialog_content);
		v.setText(resid);
	}
	
	public void setContent(CharSequence text){
		TextView v = (TextView) findViewById(R.id.tv_dialog_content);
		v.setText(text);
	}
	
	public void showLeftButton(int text, View.OnClickListener listener){
		TextView button = (TextView) findViewById(R.id.tv_left_button);
		button.setVisibility(View.VISIBLE);
		button.setText(text);
		button.setOnClickListener(listener);
	}
	
	public void showMiddleButton(int text, View.OnClickListener listener){
		TextView button = (TextView) findViewById(R.id.tv_middle_button);
		button.setVisibility(View.VISIBLE);
		button.setText(text);
		button.setOnClickListener(listener);
	}
	
	public void showRightButton(int text, View.OnClickListener listener){
		TextView button = (TextView) findViewById(R.id.tv_right_button);
		button.setVisibility(View.VISIBLE);
		button.setText(text);
		button.setOnClickListener(listener);
	}
}
