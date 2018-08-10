package com.yuanding.schoolpass.view;



import com.yuanding.schoolpass.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ShareDialog extends Dialog{

	public ShareDialog(Context context) {  
        super(context);  
    }  
  
    public ShareDialog(Context context, int theme) {  
        super(context, theme);  
    }  
  
    public static class Builder {  
        private Context context;      
//        private View contentView;         
        DialogBtnClickCallBack mDialogBtnClickCallBack;
        boolean isShowWXBtn;
        
        String topText = "";
    	String twoText = "";
    	String threeText = "";
        
        public Builder(Context context,String topText, String twoText,String threeText,boolean isShowWXBtn,DialogBtnClickCallBack mDialogBtnClickCallBack) {  
            this.context = context;  
            this.mDialogBtnClickCallBack = mDialogBtnClickCallBack;
            this.isShowWXBtn = isShowWXBtn;
            
            this.topText = topText;
            this.twoText = twoText;
            this.threeText = threeText;
            
        }      
       
  
        public ShareDialog create() {  
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
            // instantiate the dialog with the custom Theme  
            final ShareDialog dialog = new ShareDialog(context,R.style.shareDialog);  
            View layout = inflater.inflate(R.layout.dialog_share, null); 
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));  

            /**初始化dialog布局按钮*/
            
            //top
            TextView tv_top = getViewById(layout, R.id.tv_top);
            tv_top.setText(topText);
            RelativeLayout layout_wxfriend = getViewById(layout, R.id.layout_wxfriend);			
            layout_wxfriend.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mDialogBtnClickCallBack.OnClickWXFriend();	
					dialog.cancel();
				}
			});
            
          //two
            TextView tv_two = getViewById(layout, R.id.tv_two);
            tv_two.setText(twoText);
            RelativeLayout layout_wxcicle = getViewById(layout, R.id.layout_wxcicle);           
            layout_wxcicle.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					mDialogBtnClickCallBack.OnClickWXCicle();
					dialog.cancel();
				}
			});
            
            if (isShowWXBtn) {
				layout_wxfriend.setVisibility(View.VISIBLE);
				layout_wxcicle.setVisibility(View.VISIBLE);
			}else{
				layout_wxfriend.setVisibility(View.GONE);
				layout_wxcicle.setVisibility(View.GONE);
			}
            
            //three
            TextView tv_three = getViewById(layout, R.id.tv_three);
            tv_three.setText(threeText);
            RelativeLayout layout_copyurl = getViewById(layout, R.id.layout_copyurl);
            layout_copyurl.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					mDialogBtnClickCallBack.OnClickCopyUrl();
					dialog.cancel();
					
				}
			});
            
            RelativeLayout layout_cance = getViewById(layout, R.id.layout_cance);
            layout_cance.setOnClickListener(new android.view.View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					dialog.cancel();					
				}
			});
            
            dialog.setContentView(layout);  
            return dialog;  
        }  
        
        private <T extends View> T getViewById( View layout,int id) {
    		return (T) layout.findViewById(id);
    	}
        
        /**
         * 对话框点击回调
         * @author Administrator
         *
         */
        public static abstract class DialogBtnClickCallBack{
        	public  abstract void OnClickWXFriend();
        	public  abstract void OnClickWXCicle();
        	public  abstract void OnClickCopyUrl();
//        	public  abstract void OnClickCancel();
        }
    }   
    
    
    
    
    
}
