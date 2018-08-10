package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab.TabInfo;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.DensityUtils;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 帮帮任务处理
 */
public class B_Account_Befriend_Center_Main_1_Task extends A_0_CpkBaseTitle_Tab {

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	private static B_Account_Befriend_Center_Main_1_Task instance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		setTitleText("任务处理");
		showTitleBt(BACK_BUTTON, true);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        navigate(A_0_App.SIDE_NOTICE);
	}

	public static B_Account_Befriend_Center_Main_1_Task getInstance(){
	    return instance;
	}
	
	//显示选项卡未读标记
	@Override
	public void updateChildTips(int postion, boolean showTips) {
	    super.updateChildTips(postion, showTips);
	}
	
	/**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Repair_Main.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(B_Account_Befriend_Center_Main_1_Task.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		tabs.add(new TabInfo(FRAGMENT_ONE, getString(R.string.str_send_task_title),
				B_Account_Befriend_Center_Main_2_Task_Send.class));
		tabs.add(new TabInfo(FRAGMENT_TWO,
				getString(R.string.str_take_task_title),
				B_Account_Befriend_Center_Main_2_Task_Acquire.class));

		return FRAGMENT_ONE;
	}

	
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		default:
			break;
		}
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
