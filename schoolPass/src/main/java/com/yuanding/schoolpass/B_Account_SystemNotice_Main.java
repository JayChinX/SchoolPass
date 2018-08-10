package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

import java.util.List;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolpass.utils.AppStrStatic;

import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;

/**
 * 系统通知
 * @author Administrator
 *
 */
public class B_Account_SystemNotice_Main extends A_0_CpkBaseTitle_Tab{

	 public static final int FRAGMENT_ONE = 0;
	 public static final int FRAGMENT_TWO = 1;
	
	 
	 @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("系统通知");
		showTitleBt(BACK_BUTTON, true);
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	 
	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		switch (resId) {
		case BACK_BUTTON:
			this.finish();
			overridePendingTransition(R.anim.animal_push_right_in_normal,
					R.anim.animal_push_right_out_normal);
			break;
		
		default:
			break;
		}

		
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				finish();
				overridePendingTransition(R.anim.animal_push_right_in_normal,
						R.anim.animal_push_right_out_normal);
				return true;
			default:
				break;
			}
		}
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		  tabs.add(new TabInfo(FRAGMENT_ONE, "活动",B_Account_SystemNotice_Acy.class));
	      tabs.add(new TabInfo(FRAGMENT_TWO, "校园头条",B_Account_SystemNotice_Info.class));
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Notice_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_SystemNotice_Main.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
}
