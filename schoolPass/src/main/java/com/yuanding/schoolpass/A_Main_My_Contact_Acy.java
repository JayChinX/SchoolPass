package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月9日 下午1:19:14 类说明
 */
public class A_Main_My_Contact_Acy extends A_0_CpkBaseTitle_Tab {

    public static final int FRAGMENT_ONE = 0;
    public static final int FRAGMENT_TWO = 1;
    public static final int FRAGMENT_THREE = 2;
    public static final int FRAGMENT_FOUR = 3;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setTitleTextCenter("通讯录");
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
	            startListtenerRongYun();// 监听融云网络变化
	     }
		if(A_0_App.USER_STATUS.equals("2")){
			setZuiRightBtn(R.drawable.navigationbar_search_button);
			 showTitleBt(ZUI_RIGHT_BUTTON, true);
		}else{
			showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
    }

    @Override
    protected int supplyTabs(List<TabInfo> tabs) {
        tabs.add(new TabInfo(FRAGMENT_ONE, "同学", B_Contact_Main_Colleague.class));
        tabs.add(new TabInfo(FRAGMENT_TWO, "老师", B_Contact_Main_Teacher.class));
        tabs.add(new TabInfo(FRAGMENT_THREE, "校内", B_Contact_Main_In_School.class));
        tabs.add(new TabInfo(FRAGMENT_FOUR, "校外", B_Contact_Main_Out_School.class));
        return FRAGMENT_ONE;
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
                    //A_0_App.getInstance().showExitDialog(A_Main_My_Contact_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(A_Main_My_Contact_Acy.this,AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case ZUI_RIGHT_BUTTON:
            if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
			 Intent intent=new Intent(A_Main_My_Contact_Acy.this, B_Mess_Notice_All_Search.class);
		      startActivity(intent);}
			break;
		
		default:
			break;
		}
	}
	
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("A_Main_My_Contact_Acy",
                "A_Main_My_Contact_Acy_sudent==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("A_Main_My_Contact_Acy",
                "A_Main_My_Contact_Acy_sudent==>" + msg);
    }
}

