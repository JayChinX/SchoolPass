package com.yuanding.schoolpass.bangbang;

import com.yuanding.schoolpass.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
/**
 * @author qinxiaojie 
 * @version 创建时间：2017年2月8日 帮帮功能引导类
 */
public class GuidePage {
    private int layoutId;
    private int knowViewId;
    private String pageTag;
    private boolean mCancel = false;

    private Activity activity;
    private FrameLayout rootLayout;
    private View layoutView;
    private ImageView imageView;
    int[] guideResourceId;
    int i = 1;
   
    //设置为 protected或private, 不被外部直接调用
    protected GuidePage(){
    }

    public static class Builder{
        private GuidePage guidePage = new GuidePage();

        public Builder(Activity activity){
            guidePage.activity = activity;
        }

        public Builder setLayoutId(@LayoutRes int layoutId){
            guidePage.layoutId = layoutId;
            return this;
        }

        public Builder setKnowViewId(@IdRes int knowViewId){
            guidePage.knowViewId = knowViewId;
            return this;
        }
        public Builder setImageIdList(@IdRes int[] guideResourceId){
            guidePage.guideResourceId = guideResourceId;
            return this;
        }

        /**
         * 引导唯一的标记，用作存储到SharedPreferences的key值，不同引导页必须不一样
         * @param pageTag
         * @return
         */
        public Builder setPageTag(String pageTag){
            guidePage.pageTag = pageTag;
            return this;
        }

        public Builder setCloseOnTouchOutside(boolean cancel){
            guidePage.mCancel = cancel;
            return this;
        }

        public GuidePage builder(){
            if(TextUtils.isEmpty(guidePage.pageTag)){
                throw new RuntimeException("the guide page must set page tag");
            }
            guidePage.setLayoutView();
            guidePage.setKnowEvent();
            guidePage.setImageIdList();
            guidePage.setCloseOnTouchOutside();
            
            
            return guidePage;
        }
    }

    public void setLayoutView(){
        rootLayout = (FrameLayout) activity.findViewById(android.R.id.content);
        layoutView = View.inflate(activity, layoutId, null);
        imageView = (ImageView) layoutView.findViewById(knowViewId);
    }

    public void setImageIdList() {
		// TODO Auto-generated method stub
		
	}
    public Bitmap resizeBitmap(Bitmap bitmap,int w,int h)
    {
        if(bitmap!=null)
        {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int newWidth = w + 20;
            int newHeight = h + 20;
            float scaleWight = ((float)newWidth)/width;
            float scaleHeight = ((float)newHeight)/height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWight, scaleHeight);
            Bitmap res = Bitmap.createBitmap(bitmap, 0,0,width, height, matrix, true);
            return res;
             
        }
        else{
            return null;
        }
    }
    public static Bitmap FitTheScreenSizeImage(Bitmap m,int ScreenWidth, int ScreenHeight)
    {
            float width  = (float)ScreenWidth/m.getWidth();
            float height = (float)ScreenHeight/m.getHeight();
            Matrix matrix = new Matrix();
            matrix.postScale(width,height);
            return Bitmap.createBitmap(m, 0, 0, m.getWidth(), m.getHeight(), matrix, true);
     }
	public void setKnowEvent(){
		WindowManager wm = (WindowManager) activity.getSystemService(
                Context.WINDOW_SERVICE);
		final Resources res=activity.getResources();
        final int width = wm.getDefaultDisplay().getWidth();
        final int height = wm.getDefaultDisplay().getHeight();
        imageView.setBackgroundResource(guideResourceId[0]);
        if(layoutView!=null) {
        	layoutView.findViewById(knowViewId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                	i++;
					switch (i) {
					case 1:

						break;
					case 2:
						 imageView.setBackgroundResource(guideResourceId[1]);
						break;
					case 3:
						 imageView.setBackgroundResource(guideResourceId[2]);
						break;
					case 4:
						cancel();
						break;
					}
                }
            });
        }
    }

    public void setCloseOnTouchOutside(){
    	
        if(layoutView == null)
            return;
        WindowManager wm = (WindowManager) activity.getSystemService(
                Context.WINDOW_SERVICE);
		final Resources res=activity.getResources();
        final int width = wm.getDefaultDisplay().getWidth();
        final int height = wm.getDefaultDisplay().getHeight();
        imageView.setBackgroundResource(guideResourceId[0]);
        layoutView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mCancel){
                	i++;
					switch (i) {
					case 1:

						break;
					case 2:
						 imageView.setBackgroundResource(guideResourceId[1]);
						break;
					case 3:
						 imageView.setBackgroundResource(guideResourceId[2]);
						break;
					case 4:
						cancel();
						break;

					}
                }
                return true;  //消费事件，不下发
            }
        });
    }

    public void apply(){
        rootLayout.addView(layoutView);
    }

    public void cancel(){
        if(rootLayout!=null && layoutView!=null) {
            rootLayout.removeView(layoutView);
            GuidePageManager.setHasShowedGuidePage(activity, pageTag, true);
        }
    }
    
}
