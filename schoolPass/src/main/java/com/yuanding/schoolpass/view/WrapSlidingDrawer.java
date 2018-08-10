package com.yuanding.schoolpass.view;



import com.yuanding.schoolpass.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SlidingDrawer;
/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日  帮帮   首页 求帮类型的底部菜单
 */
public class WrapSlidingDrawer extends SlidingDrawer implements SlidingDrawer.OnDrawerOpenListener,SlidingDrawer.OnDrawerCloseListener {
	 private boolean mVertical;  
	    private int mTopOffset;  
	    private final static boolean DEBUG = false;  
	    private final static String TAG = "WrapSlidingDrawer";
	      
	    public WrapSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle);  
	        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MySlider, defStyle, 0);

	        int orientation = a.getInt(R.styleable.MySlider_SlidingDrawer_orientation, ORIENTATION_VERTICAL);
//	        int orientation = attrs.getAttributeIntValue("android", "orientation", ORIENTATION_VERTICAL);  
	        mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);  
	        mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);  
	        
	        
	    }  
	  
	    public WrapSlidingDrawer(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	        
	        int orientation = attrs.getAttributeIntValue("android", "orientation", ORIENTATION_VERTICAL);  
	        mTopOffset = attrs.getAttributeIntValue("android", "topOffset", 0);  
	        mVertical = (orientation == SlidingDrawer.ORIENTATION_VERTICAL);  
	    }  
	  
	    @Override  
	    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {  
	        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);  
	        int widthSpecSize =  MeasureSpec.getSize(widthMeasureSpec);  
	        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);  
	        int heightSpecSize =  MeasureSpec.getSize(heightMeasureSpec);  
	        if (widthSpecMode == MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
	            throw new RuntimeException("SlidingDrawer cannot have UNSPECIFIED dimensions");
	        }
	        final View handle = getHandle();  
	        final View content = getContent();  
	        measureChild(handle, widthMeasureSpec, heightMeasureSpec);  
	  
	       

	        if (mVertical) {  
	            int height = heightSpecSize - handle.getMeasuredHeight() - mTopOffset;  
	            content.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, heightSpecMode));  
	            if(isOpened()){  
	                heightSpecSize = handle.getMeasuredHeight() + mTopOffset + content.getMeasuredHeight();  
	            } else {  
	                heightSpecSize = handle.getMeasuredHeight() + mTopOffset;  
	            }  
	            widthSpecSize = content.getMeasuredWidth();  
	            if (handle.getMeasuredWidth() > widthSpecSize) widthSpecSize = handle.getMeasuredWidth();  
	        }  
	        else {  
	            int width = widthSpecSize - handle.getMeasuredWidth() - mTopOffset;  
	            content.measure(MeasureSpec.makeMeasureSpec(width, widthSpecMode), heightMeasureSpec);  
	            if(isOpened()){  
	                widthSpecSize = handle.getMeasuredWidth() + mTopOffset + content.getMeasuredWidth();  
	            } else {  
	                widthSpecSize = handle.getMeasuredWidth() + mTopOffset ;  
	            }  
	            //widthSpecSize = handle.getMeasuredWidth() + mTopOffset + content.getMeasuredWidth();  
	            heightSpecSize = content.getMeasuredHeight();  
	            if (handle.getMeasuredHeight() > heightSpecSize) heightSpecSize = handle.getMeasuredHeight();  
	        }  
	  
	        setMeasuredDimension(widthSpecSize, heightSpecSize);  
	    } 
	    @Override  
	    public void onDrawerClosed() {  
	        if(DEBUG) Log.e(TAG, "onDrawerClosed"+ "-isOpen?=" + isOpened() +"-getParent()=" + getParent());  
	        requestLayout();  
	          
	    }  
	  
	    @Override  
	    public void onDrawerOpened() {  
	        if(DEBUG) Log.e(TAG, "onDrawerOpened"+ "-isOpen?=" + isOpened());  
	        requestLayout();  
	          
	    }  
}
