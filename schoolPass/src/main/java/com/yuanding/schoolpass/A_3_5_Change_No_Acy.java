package com.yuanding.schoolpass;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterCall_Back;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Change_Mobile;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.ReadSmsContent;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月10日 下午5:11:16
 * 修改手机号
 */
public class A_3_5_Change_No_Acy  extends A_0_CpkBaseTitle_Navi{
	
    ReadSmsContent readSmsContent;
	private Button btn_sure_change,btn_get_yanzheng_code;
	private TextView tv_user_old_phone_no;
	private EditText tv_modify_login_pwd,tv_new_mobile_text,tv_yanzheng_text_code;
	private LinearLayout ll_login_pwd,ll_new_phone,ll_yanzheng_code;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_acc_change_no);
		
		setTitleText("变更手机号");
		
		tv_user_old_phone_no = (TextView)findViewById(R.id.tv_user_old_phone_no);
		tv_modify_login_pwd = (EditText)findViewById(R.id.tv_modify_login_pwd);
		tv_new_mobile_text = (EditText)findViewById(R.id.tv_new_mobile_text);
		tv_yanzheng_text_code = (EditText)findViewById(R.id.tv_yanzheng_text_code);
		
		btn_get_yanzheng_code = (Button) findViewById(R.id.btn_get_yanzheng_code);
		btn_sure_change = (Button) findViewById(R.id.btn_sure_change);
		
		tv_user_old_phone_no.setText(A_0_App.USER_PHONE);
		ll_login_pwd=(LinearLayout) findViewById(R.id.liner_login_pwd);
		ll_new_phone=(LinearLayout) findViewById(R.id.liner_new_phone);
		ll_yanzheng_code=(LinearLayout) findViewById(R.id.liner_yanzheng_code);
		tv_modify_login_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{
					ll_login_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else
				{
					ll_login_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		tv_new_mobile_text.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(hasFocus)
				{
					ll_new_phone.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else
				{
					ll_new_phone.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		tv_yanzheng_text_code.setOnFocusChangeListener(new OnFocusChangeListener() {
	
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						// TODO Auto-generated method stub
						if (hasFocus) {
							ll_yanzheng_code
									.setBackgroundResource(R.drawable.login_input_hover_bg);
						} else {
							ll_yanzheng_code
									.setBackgroundResource(R.drawable.login_input_normal_bg);
						}
					}
		});

		btn_sure_change.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				
				String aa= tv_modify_login_pwd.getText().toString();
				String bb= tv_new_mobile_text.getText().toString();
				String cc= tv_yanzheng_text_code.getText().toString();
				
				if (aa == null || aa.length() <= 0) {
					PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "请输入您的登录密码");
					return;
				} else if (bb == null || bb.length() <= 0) {
					PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "请输入您的新手机号");
					return;
				} else if (cc == null || cc.length() <= 0) {
					PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "请输入新手机接收到的验证码");
					return;
				}
				
				if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
					changge_moblile(bb, cc, aa);
                } else {
                	
                }
			}
		});
		
		
		btn_get_yanzheng_code.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (getInputMobile() != null && getInputMobile().length() > 0) {
					if(!PubMehods.isMobileNO(getInputMobile())){
						PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "手机号码格式错误");
						 return;
					}
					get_yan_zheng("modify", getInputMobile());
				} else {
					PubMehods.showToastStr(A_3_5_Change_No_Acy.this,"请输入有效的手机号码");
				}
			}
		});
		
		//tv_new_mobile_text.setText(PubMehods.getPhoneNo(this));
        
        readSmsContent = new ReadSmsContent(this, new Handler(), tv_yanzheng_text_code);
        // 注册短信内容监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true,
                readSmsContent);
		
	}
	
	private String getInputMobile() {
		return tv_new_mobile_text.getText().toString();
	}
	
	private void changge_moblile(String mobile,String code,String passwd){
	    A_0_App.getInstance().showProgreDialog(A_3_5_Change_No_Acy.this, "正在提交，请稍候",true);
		A_0_App.getApi().changge_moblile(mobile,code,passwd, A_0_App.USER_TOKEN, new Inter_Change_Mobile() {
			@Override
			public void onSuccess() {
				A_0_App.getInstance().CancelProgreDialog(A_3_5_Change_No_Acy.this);
				startActivity(new Intent(A_3_5_Change_No_Acy.this, A_3_6_Change_No_Success_Acy.class));
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                A_0_App.getInstance().CancelProgreDialog(A_3_5_Change_No_Acy.this);
                PubMehods.showToastStr(A_3_5_Change_No_Acy.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	private boolean fangDianji = false;
	// 获取验证码
	private void get_yan_zheng(String type,String mobile) {
		if (fangDianji){
			PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "短信发送正在发送，请稍后");
			return;
		}
		fangDianji = true;
		btn_get_yanzheng_code.setEnabled(false);
		A_0_App.getInstance().showProgreDialog(A_3_5_Change_No_Acy.this,"短信发送中",true);
		A_0_App.getApi().get_yan_zheng_code(type, mobile, new InterCall_Back() {
			@Override
			public void onSuccess(String code) {
				 fangDianji = false;
				 if (isFinishing())
				 return;
				 A_0_App.getInstance().CancelProgreDialog(A_3_5_Change_No_Acy.this);
//				 if (code != null) {
					 btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code_error));
					 startContrlTimer();
					 PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "短信发送成功，请注意查收");
//					 tv_yanzheng_text_code.setText(code);
//				 }else{
//					 btn_get_yanzheng_code.setEnabled(true);
//					 btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.navigationbar_text_button));
//					 PubMehods.showToastStr(A_3_5_Change_No_Acy.this, "短信系统故障，请稍后再试");
//				 }
				
			}
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                fangDianji = false;
                if (isFinishing())
                    return;
                PubMehods.showToastStr(A_3_5_Change_No_Acy.this, msg);
                cancelTimer();
                A_0_App.getInstance().CancelProgreDialog(A_3_5_Change_No_Acy.this);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	/*
     * 定时器，发送验证码用
     */
    private Timer mControlTimer;
    private int interval = AppStrStatic.message_interval;

    private void startContrlTimer() {
        mControlTimer = new Timer();
        mControlTimer.schedule(new timerControl(), 0, 1000);// 过1秒执行一个动作,单次的
    }

    private class timerControl extends TimerTask {
        @Override
        public void run() {
            Message mes = new Message();
            mes.what = 1;
            handcontrol.sendMessage(mes);
        }
    }

    private void cancelTimer() {
        btn_get_yanzheng_code.setText("重新获取");
        btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code));
        btn_get_yanzheng_code.setEnabled(true);
        if (mControlTimer != null && btn_get_yanzheng_code != null) {
            mControlTimer.cancel();
        }
    }
    
    private Handler handcontrol = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    interval--;
                    if (interval < 1) {
                    	btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code));
                    	btn_get_yanzheng_code.setText("发送验证码");
                    	btn_get_yanzheng_code.setEnabled(true);
                    	interval = AppStrStatic.message_interval;
                    	 if (mControlTimer != null)
                    	    mControlTimer.cancel();
                    } else {
                    	btn_get_yanzheng_code.setText(String.valueOf(interval) + "s后重发");
                    }
                    break;
                default:
                    break;
            }
        };
    };
	
	
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
	
   @Override
    protected void onDestroy() {
        this.getContentResolver().unregisterContentObserver(readSmsContent);
        super.onDestroy();
    }
}