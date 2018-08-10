package com.yuanding.schoolpass;

import java.util.List;

import io.rong.imkit.RongIM;
import io.rong.imkit.RongIM.OnSendMessageListener;
import io.rong.imkit.RongIM.SentMessageErrorCode;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.ResultCallback;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Index_Notice_Message;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * 
 * @ClassName: ConversationActivity
 * @Description: TODO(单聊--单聊--单聊)
 * @author Jiaohaili
 * @date 2015年12月9日 下午4:03:32
 * 
 */
public class ConversationActivity extends A_0_CpkBaseTitle_Navi {

	private String uniqid ="";
	private boolean group =false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity_rongyun(this);
		setView(R.layout.conversation);
		
		Intent intent=getIntent();
		Uri uri= intent.getData();

		setTitleText(uri.getQueryParameter("title").toString());
		showTitleBt(ZUI_RIGHT_BUTTON, true);
		
		if (RongIM.getInstance().getCurrentConnectionStatus().equals(ConnectionStatus.CONNECTED)) {
            System.out.println("融云已连接");
        }else{
            reconnect(A_0_App.USER_QUTOKEN, ConversationActivity.this);
        }
	      
		if(uri.getPath().equals("/conversation/private")){
			group = false;
			setZuiRightBtn(R.drawable.navigationbar_persion_button);
		}else{
			group = true;
			setZuiRightBtn(R.drawable.navigationbar_group_button);
		}
		
		uniqid = uri.getQueryParameter("targetId");
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		RongIM.getInstance().setSendMessageListener(new OnSendMessageListener() {
			
			@Override
			public boolean onSent(Message arg0, SentMessageErrorCode arg1) {

				return false;
			}
			
			@Override
			public Message onSend(Message msg) {
				return msg;
			}
		});
	}
	
	private void reconnect(String token, final Context context) {
        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                if(isFinishing())
                    return;
                PubMehods.showToastStr(ConversationActivity.this, "抱歉，重连接服务器失败，请检查您的网络设置");
                finish();
            }

            @Override
            public void onSuccess(String s) {}

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                if(isFinishing())
                    return;
                PubMehods.showToastStr(ConversationActivity.this, "抱歉，重连接服务器失败，请检查您的网络设置");
                finish();
            }
        });
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
                    //A_0_App.getInstance().showExitDialog(ConversationActivity.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(ConversationActivity.this, AppStrStatic.kicked_offline());
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
		case BACK_BUTTON:
			this.finish();
			break;
		case ZUI_RIGHT_BUTTON:
			if (!uniqid.equals("")) {
                if (!group) {
					Intent intent = new Intent(ConversationActivity.this,B_Mess_Persion_Info.class);
					intent.putExtra("uniqid", uniqid);
                    startActivity(intent);
                } else {
                    if (A_0_App.USER_STATUS.equals("2")) {
                        if (A_0_App.USER_QUNIQID != null && A_0_App.USER_QUNIQID.length() > 0) {
                            Intent intent = new Intent(ConversationActivity.this,B_Mess_Group_Info.class);
                            intent.putExtra("uniqid", A_0_App.USER_QUNIQID);
                            startActivity(intent);

                        } else {
                            PubMehods.showToastStr(ConversationActivity.this,R.string.str_title_no_group);
                        }
                    } else {
                        PubMehods.showToastStr(ConversationActivity.this, "未认证，该功能暂不开放");
                    }
                }
			} else {
				PubMehods.showToastStr(ConversationActivity.this, "uniqi为空");
			}
			break;
		default:
			break;
		}

	}

}
