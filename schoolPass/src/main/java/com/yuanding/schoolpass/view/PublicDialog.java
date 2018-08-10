package com.yuanding.schoolpass.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @author kongli
 *
 */
public class PublicDialog extends Dialog {
	public interface IDialogCallback {
		int transmitData(Object data);
	}

	protected IDialogCallback mCallback = null;
	private int mLayout;

	public PublicDialog(Context context, int theme, IDialogCallback callback) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		mCallback = callback;
	}

	public PublicDialog(Context context, int theme) {
		super(context, theme);
		// TODO Auto-generated constructor stub
	}

	public PublicDialog(Context context, IDialogCallback callback) {
		super(context);
		// TODO Auto-generated constructor stub
		mCallback = callback;
	}

	public PublicDialog(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

	}

	public void SetContentView(int layout) {
		mLayout = layout;
		setContentView(mLayout);
		
	}
	
	public void SetContentView(View view) {
		setContentView(view);
	}

	public boolean setOnClickListener(int id, View.OnClickListener listener) {
		View v = this.findViewById(id);
		if (v == null)
			return false;
		v.setOnClickListener(listener);
		return true;
	}
}