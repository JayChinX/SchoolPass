package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.view.toggle.ToggleButton;
import com.yuanding.schoolpass.view.toggle.ToggleButton.OnToggleChanged;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午2:53:58
 * 类说明
 */
public class B_Account_Push_Set_Acy extends A_0_CpkBaseTitle_Navi{
	
	private TextView tv_mian_darao;
	private ToggleButton tb_set_voice,tb_set_vir;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_push_set);
		setTitleText("消息推送");
		
		tb_set_voice = (ToggleButton)findViewById(R.id.tb_set_voice);
		tb_set_vir = (ToggleButton)findViewById(R.id.tb_set_vir);
		
		tv_mian_darao = (TextView)findViewById(R.id.tv_mian_darao);
		tv_mian_darao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivity(new Intent(B_Account_Push_Set_Acy.this, B_Account_Push_Trouble_Set_Acy.class));
			}
		});
		
		tb_set_voice.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				
			}
		});
		
		tb_set_vir.setOnToggleChanged(new OnToggleChanged() {
			@Override
			public void onToggle(boolean on) {
				
			}
		});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
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
                    //A_0_App.getInstance().showExitDialog(B_Account_Push_Set_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_Push_Set_Acy.this, AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
		
	}

}
