package com.yuanding.schoolpass;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 失物招领————失物招领————失物招领
 */
public class B_Side_Repair_Main extends A_0_CpkBaseTitle_Tab {

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		showTitleBt(BACK_BUTTON, true);
		
		if(A_0_App.USER_STATUS.equals("2")){
		    showTitleBt(ZUI_RIGHT_BUTTON, true);
			setZuiRightBtn(R.drawable.navigationbar_add_button);
		}else{
		    showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
		setTitleText("报修");
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		tabs.add(new TabInfo(FRAGMENT_ONE, "全部",
				B_Side_Repair_All.class));
		tabs.add(new TabInfo(FRAGMENT_TWO,
				"我的",
				B_Side_Repair_My.class));

		return FRAGMENT_ONE;
	}

	
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;
		case ZUI_RIGHT_BUTTON:
			
			if (A_0_App.USER_STATUS.equals("2")) {
				Intent intent=new Intent(B_Side_Repair_Main.this, B_Side_Repair_Add.class);
				startActivity(intent);
			}
			
				
			
			
			
			break;
		default:
			break;
		}

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
                            A_0_App.getInstance().showExitDialog(B_Side_Repair_Main.this, AppStrStatic.kicked_offline());
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
	protected void onDestroy() {
		super.onDestroy();
	}
}
