package com.yuanding.schoolpass.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.yuanding.schoolpass.R;

public class PublicShowToast {
    
    private static Toast toast = null;
    private static PublicShowToast mToast = null;

    public static PublicShowToast getInstance() {
        return mToast;
    }

    public PublicShowToast(Toast toast) {
        super();
        mToast = this;
    }
    
    public static void showToastStr(Context con, CharSequence text) {
        if (toast == null) {
            toast = new PublicToast(con, text,Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM,0);
        } else {
            View layout = LayoutInflater.from(con).inflate(R.layout.pub_toast_default, null);
            TextView textView = (TextView) layout.findViewById(R.id.toast_text);
            textView.setText(text);
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        }
        toast.show();
    }
    
    /**
     * 
    * @Title: showCustHighStr
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @param @param con
    * @param @param text
    * @param @param gravity
    * @param @param high    设定自定义高度
    * @return void    返回类型
    * @throws
     */
    public static void showCustHighStr(Context con, CharSequence text, int high) {
        if (toast == null) {
            toast = new PublicToast(con, text, Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, high);
        } else {
            View layout = LayoutInflater.from(con).inflate(R.layout.pub_toast_default, null);
            TextView textView = (TextView) layout.findViewById(R.id.toast_text);
            textView.setText(text);
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, high);
        }
        toast.show();
    }
}
