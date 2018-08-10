package com.yuanding.schoolpass;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.baidu.android.pushservice.PushManager;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午6:25:00
 * 消息免打扰
 */
public class B_Account_Push_Trouble_Set_Acy extends A_0_CpkBaseTitle_Navi{

	
	private RelativeLayout rel_trouble_open,rel_trouble_free,rel_trouble_close;
	private ImageView iv_select_open,iv_select_free,iv_select_close;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_push_trouble);
		setTitleText("消息免打扰");
		
		rel_trouble_open = (RelativeLayout)findViewById(R.id.rel_trouble_open);
		rel_trouble_free = (RelativeLayout)findViewById(R.id.rel_trouble_free);
		rel_trouble_close = (RelativeLayout)findViewById(R.id.rel_trouble_close);
		
		iv_select_open = (ImageView)findViewById(R.id.iv_select_open);
		iv_select_free = (ImageView)findViewById(R.id.iv_select_free);
		iv_select_close = (ImageView)findViewById(R.id.iv_select_close);
		
		rel_trouble_open.setOnClickListener(onclick);
		rel_trouble_free.setOnClickListener(onclick);
		rel_trouble_close.setOnClickListener(onclick);
	    //加载声音开关状态//0表示关闭 1 表示夜间模式  2表示打开
        switch (A_0_App.getInstance().mPushState) {
            case 0:
                showSelect(false, false, true);
                break;
            case 1:
                showSelect(false, true, false);
                break;    
            case 2:
                 showSelect(true, false, false);
                break;
            default:
                break;
        }
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
//	"设置免打扰时段,在免打扰时段内,当用户收到通知时,会去除通知的提示音、"
//    + "振动以及提示灯闪烁.\n\n注意事项:\n1.如果开始时间小于结束时间，"
//    + "免打扰时段为当天的起始时间到结束时间.\n2.如果开始时间大于结束时间，"
//    + "免打扰时段为第一天起始时间到第二天结束时间.\n3.如果开始时间和结束时间"
//    + "的设置均为00:00时,取消免打扰时段功能.\n";
	
//  0表示取消免打扰时段功能1 表示指定免打扰时段  2表示全天开启免打扰
	OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rel_trouble_open://全天开启免打扰
			    showSelect(true, false, false);
                A_0_App.getInstance().setmPushState(2);
                PushManager.setNoDisturbMode(B_Account_Push_Trouble_Set_Acy.this, 00, 00, 23, 59);
				break;
			case R.id.rel_trouble_free://指定免打扰时段
				showSelect(false, true, false);
				A_0_App.getInstance().setmPushState(1);
				PushManager.setNoDisturbMode(B_Account_Push_Trouble_Set_Acy.this, 22, 00, 8, 00);
				break;
			case R.id.rel_trouble_close://取消免打扰时段功能,全天都有震动和声音
				showSelect(false, false, true);
                A_0_App.getInstance().setmPushState(0);
                PushManager.setNoDisturbMode(B_Account_Push_Trouble_Set_Acy.this, 00, 00, 00, 00);
				break;
			default:
				break;
			}

		}
	};
	
	
	private void showSelect(Boolean open, Boolean free, Boolean close) {

		if (open) {
			iv_select_open.setVisibility(View.VISIBLE);
			iv_select_free.setVisibility(View.GONE);
			iv_select_close.setVisibility(View.GONE);
		} else if (free) {
			iv_select_open.setVisibility(View.GONE);
			iv_select_free.setVisibility(View.VISIBLE);
			iv_select_close.setVisibility(View.GONE);
		} else if (close) {
			iv_select_open.setVisibility(View.GONE);
			iv_select_free.setVisibility(View.GONE);
			iv_select_close.setVisibility(View.VISIBLE);
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
                    //A_0_App.getInstance().showExitDialog(B_Account_Push_Trouble_Set_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_Push_Trouble_Set_Acy.this, AppStrStatic.kicked_offline());
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
