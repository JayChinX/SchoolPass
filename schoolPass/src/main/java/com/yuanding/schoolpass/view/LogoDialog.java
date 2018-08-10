package com.yuanding.schoolpass.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuanding.schoolpass.R;

/**
 * @version 创建时间：2013-11-29 上午10:23:40
 * 类说明
 */
public class LogoDialog extends PublicDialog{
	
	private AnimationDrawable animationDrawable;
	private ImageView mImageView;

	 private TextView mText;

	public LogoDialog(Context context, int theme, String msg,Drawable animalDrawable) {
		super(context, theme);
		// TODO Auto-generated constructor stub
		this.SetContentView(R.layout.dialog_logo);
		mImageView = (ImageView) findViewById(R.id.dialog_image_pic);
		mText = (TextView) findViewById(R.id.dialog_text_title);
		
		mImageView.setBackgroundDrawable(animalDrawable);
		mText.setText(msg);
		
		animationDrawable = (AnimationDrawable) mImageView.getBackground();
		
		//解决不能自动播放的问题
		mImageView.getViewTreeObserver().addOnPreDrawListener(listener);
	}
	
    OnPreDrawListener listener = new OnPreDrawListener() {
        @Override
        public boolean onPreDraw() {
            if (animationDrawable != null)
                animationDrawable.start();
            return true;
        }
    };
	
	public void closeDialog() {
		if (animationDrawable != null) {
			animationDrawable.stop();
			animationDrawable = null;
		}
	}
	
	public void showDialog(View.OnClickListener listener) {
		ImageView mImageView = (ImageView) findViewById(R.id.dialog_image_pic);
		mImageView.setOnClickListener(listener);
	}
}

