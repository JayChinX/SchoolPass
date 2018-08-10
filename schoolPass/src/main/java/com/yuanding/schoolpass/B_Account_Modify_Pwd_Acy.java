package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Change_Pwd;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月10日 下午3:08:12
 * 设置里面的修改密码
 */
public class B_Account_Modify_Pwd_Acy extends A_0_CpkBaseTitle_Navi{
	
	private Button btn_user_sure_modify;
	private EditText user_set_old_pwd,user_set_new_pwd,user_sure_new_pwd;
	private LinearLayout ll_old_pwd,ll_new_pwd,ll_sure_pwd;
	private TextView tv_setpwd_title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_acc_modify_pwd);
		setTitleText("修改密码");
		
		tv_setpwd_title = (TextView)findViewById(R.id.tv_setpwd_title);
		user_set_old_pwd = (EditText)findViewById(R.id.user_set_old_pwd);
		user_set_new_pwd = (EditText)findViewById(R.id.user_set_new_pwd);
		user_sure_new_pwd = (EditText)findViewById(R.id.user_sure_new_pwd);
		ll_old_pwd=(LinearLayout) findViewById(R.id.ll_old_pwd);
		ll_sure_pwd=(LinearLayout) findViewById(R.id.ll_sure_pwd);
		ll_new_pwd=(LinearLayout) findViewById(R.id.ll_new_pwd);
		tv_setpwd_title.setText(PubMehods.getPwdSetTitle());
		
		user_set_old_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{
					ll_old_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else
				{
					ll_old_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		user_set_new_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{
					ll_new_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else
				{
					ll_new_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		user_sure_new_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
	
	     @Override
	     public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					ll_sure_pwd
							.setBackgroundResource(R.drawable.login_input_hover_bg);
				} else {
					ll_sure_pwd
							.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
	       }
        });
		btn_user_sure_modify = (Button)findViewById(R.id.btn_user_sure_modify);
		btn_user_sure_modify.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String old_pwd = user_set_old_pwd.getText().toString();
				String aa = user_set_new_pwd.getText().toString();
				String bb = user_sure_new_pwd.getText().toString();
				
				if (old_pwd == null || old_pwd.length() <= 0) {
					PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this,"请输入您的旧密码");
					return;
				}
				
				String regEx = "^[A-Za-z0-9]+$";
				if (aa != null && aa.length() > 0 && bb != null && bb.length() > 0) {

				    if ((aa.length() < AppStrStatic.PWD_MIN_LENGTH) || (aa.length() > AppStrStatic.PWD_MAX_LENGTH)) {
						PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, PubMehods.ToastPwdSetTitle());
						return;
					}
				    if ((bb.length() < AppStrStatic.PWD_MIN_LENGTH) || (bb.length() > AppStrStatic.PWD_MAX_LENGTH)) {
						PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, PubMehods.ToastPwdSetTitle());
						return;
					}
					if (!aa.matches(regEx)) {
						PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, "设置密码中不能包含特殊符号");
						return;
					}
					if (!bb.matches(regEx)) {
						PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, "重置密码中不能包含特殊符号");
						return;
					}
					
					if(!aa.equals(bb)){
						PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, "两次密码输入不一致，请检查");
						return;
					}
					
				} else {
					PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, "密码不能为空");
					return;
				}
				changge_Pwd(aa, old_pwd);
			}
		});
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void changge_Pwd(String newPasswd,String oldPasswd){
	    A_0_App.getInstance().showProgreDialog(B_Account_Modify_Pwd_Acy.this, "修改中",true);
		A_0_App.getApi().changge_Pwd(newPasswd, oldPasswd, A_0_App.USER_TOKEN, new Inter_Change_Pwd() {
			
			@Override
			public void onSuccess() {
	            if (isFinishing())
	                  return;
				A_0_App.getInstance().CancelProgreDialog(B_Account_Modify_Pwd_Acy.this);
				Intent intent = new Intent(B_Account_Modify_Pwd_Acy.this, A_3_4_Forget_pwd_Succ_Acy.class);
				intent.putExtra("acy_type", "changge_Pwd");
				intent.putExtra("type", 2);
				startActivity(intent);
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                     return;
                A_0_App.getInstance().CancelProgreDialog(B_Account_Modify_Pwd_Acy.this);
                PubMehods.showToastStr(B_Account_Modify_Pwd_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
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
                    //A_0_App.getInstance().showExitDialog(B_Account_Modify_Pwd_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_Modify_Pwd_Acy.this,AppStrStatic.kicked_offline());
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
