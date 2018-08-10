
package com.yuanding.schoolpass.utils;

import java.util.Calendar;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年1月25日 下午3:35:45
 * 防止重复点击
 */
public abstract class NoDoubleClickListener implements OnClickListener {

    public static final int MIN_CLICK_DELAY_TIME = AppStrStatic.interval_double_click;
    protected abstract void onNoDoubleClick(View v);
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }else{
            A_Main_My_Message_Acy.logE("重复点击了按钮");
        }
    }
}
