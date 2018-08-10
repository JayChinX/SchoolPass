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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterCall_Back;
import com.yuanding.schoolpass.service.Api.InterCall_Back_Judge;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.ReadSmsContent;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月9日 下午5:56:29
 * 忘记密码（功能）
 */
public class A_3_1_1_Forget_Main_Acy extends A_0_CpkBaseTitle_Navi{

    ReadSmsContent readSmsContent;
	private Button btn_get_yanzheng_code,btn_next_step;
	private EditText et_reg_mobile_no,tv_yanzheng_text;
	private TextView tv_register_title,tv_yan_zheng_title;
	private LinearLayout liner_register_no,liner_register_yanzheng; 
	private static A_3_1_1_Forget_Main_Acy instance;
	public static A_3_1_1_Forget_Main_Acy getInstance(){
		return instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        if (A_0_App.getInstance().isRuning()) {
            finish();
            return;
        }
		A_0_App.getInstance().addActivity(this);
		A_0_App.getInstance().addReSetPwdAcy(this);
		A_0_App.getInstance().addRegisterPwdAcy(this);
		setView(R.layout.activity_forget_1);
		
		instance = this;
		setTitleText("忘记密码");
		
		btn_get_yanzheng_code = (Button)findViewById(R.id.btn_get_yanzheng_code);
		btn_next_step = (Button)findViewById(R.id.btn_next_step);
		
		et_reg_mobile_no = (EditText)findViewById(R.id.et_reg_mobile_no);
		tv_yanzheng_text = (EditText)findViewById(R.id.tv_yanzheng_text);
		
		liner_register_no = (LinearLayout)findViewById(R.id.liner_register_no);
		liner_register_yanzheng = (LinearLayout)findViewById(R.id.liner_register_yanzheng);
		tv_register_title = (TextView) findViewById(R.id.tv_register_title);
		tv_yan_zheng_title = (TextView) findViewById(R.id.tv_yan_zheng_title);
		
		liner_register_no.setBackgroundResource(R.drawable.login_input_normal_bg);
		tv_register_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
		et_reg_mobile_no.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					liner_register_no.setBackgroundResource(R.drawable.login_input_hover_bg);
					//tv_register_title.setTextColor(getResources().getColor(R.color.title_focus_login));
				    tv_register_title.setVisibility(View.GONE);
				}else{
					liner_register_no.setBackgroundResource(R.drawable.login_input_normal_bg);
					//tv_register_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
			}
		});
		
		liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_normal_bg);
		tv_yan_zheng_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
		tv_yanzheng_text.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				
				if(arg1){
					liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_hover_bg);
					//tv_yan_zheng_title.setTextColor(getResources().getColor(R.color.title_focus_login));
				    tv_yan_zheng_title.setVisibility(View.GONE);
				}else{
					liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_normal_bg);
					//tv_yan_zheng_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
			}
		});
		
		btn_get_yanzheng_code.setOnClickListener(onClick);
		btn_next_step.setOnClickListener(onClick);
		
//		et_reg_mobile_no.setText(PubMehods.getPhoneNo(this));
		
		readSmsContent = new ReadSmsContent(this, new Handler(), tv_yanzheng_text);
        // 注册短信内容监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true,
                readSmsContent);
	}
	
	
	private String getInputMobile() {
		return et_reg_mobile_no.getText().toString();
	}
	
	
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			//获取验证码
			case R.id.btn_get_yanzheng_code:
				if (getInputMobile() != null && getInputMobile().length() > 0) {
					
					if(!PubMehods.isMobileNO(getInputMobile())){
						PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, "手机号码格式错误");
						 return;
					}
					
					 if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
		                    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
		                    	get_yan_zheng("reset", getInputMobile());
		                    } else {
		                       
		                    }
		                } else {
		                    PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this,R.string.error_title_net_error);
		                }
					
				} else {
					PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this,"请输入手机号");
				}
				break;
		    //下一步
			case R.id.btn_next_step:
				String code = tv_yanzheng_text.getText().toString();
				if (getInputMobile() != null && getInputMobile().length() > 0) {
                    if(!PubMehods.isMobileNO(getInputMobile())){
                        PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, "手机号码格式错误");
                        return;
                    }
				}else{
				    PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this,"请输入手机号");
				    return;
				}
                    
				if (code != null && !code.equals("") && code.length() > 0) {
					if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
	                    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
	                    	judge_code("reset", getInputMobile(), code);
	                    } else {
	                       
	                    }
	                } else {
	                    PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this,R.string.error_title_net_error);
	                }
					
				} else {
					PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this,"请输入您手机接收到的验证码");
				}

				break;
			}
		}
	};
	
	
	private boolean fangDianji = false;
	// 获取验证码
	private void get_yan_zheng(String type,String mobile) {
		if (fangDianji){
			PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, "短信发送正在发送，请稍后");
			return;
		}
		fangDianji = true;
		btn_get_yanzheng_code.setEnabled(false);
		A_0_App.getInstance().showProgreDialog(A_3_1_1_Forget_Main_Acy.this,"短信发送中",true);
		A_0_App.getApi().get_yan_zheng_code(type, mobile, new InterCall_Back() {
			@Override
			public void onSuccess(String code) {
				 fangDianji = false;
				 if (isFinishing())
				 return;
				 A_0_App.getInstance().CancelProgreDialog(A_3_1_1_Forget_Main_Acy.this);
//				 if (code != null) {
					 btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code_error));
					 startContrlTimer();
					 PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, "短信发送成功，请注意查收");
					 tv_yanzheng_text.setText(code);
//				 }else{
//					 btn_get_yanzheng_code.setEnabled(true);
//					 btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code_error));
//					 PubMehods.showToastStr(A_3_1_Register_Acy.this, "短信系统故障，请稍后再试");
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
                PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, msg);
                cancelTimer();
                A_0_App.getInstance().CancelProgreDialog(A_3_1_1_Forget_Main_Acy.this);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
	}
	
	
	// 验证验证码的正确性
	private void judge_code(String send_type, final String mobile, String code) {
		A_0_App.getInstance().showProgreDialog(A_3_1_1_Forget_Main_Acy.this, "正在验证",true);
		A_0_App.getApi().judge_yan_zheng_code(send_type, mobile, code,new InterCall_Back_Judge() {
					@Override
					public void onSuccess(String user_status,String friendlist_is_display,String recommend_phone) {
						if (isFinishing())
							return;
						A_0_App.getInstance().CancelProgreDialog(A_3_1_1_Forget_Main_Acy.this);
						//重设密码
						if(user_status.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)){
						    if(friendlist_is_display.equals("0")){
        						Intent intent = new Intent();
        						intent.setClass(A_3_1_1_Forget_Main_Acy.this,A_3_1_2_Forget_Friend_Acy.class);
        						intent.putExtra("acy_type", 2);
        						intent.putExtra("user_phone", mobile);
        						startActivity(intent);
						    }else{
						        Intent intent = new Intent();
	                            intent.setClass(A_3_1_1_Forget_Main_Acy.this,A_3_1_3_Forget_SetPassWord_Acy.class);
	                            intent.putExtra("acy_type", 2);
	                            intent.putExtra("user_phone", mobile);
	                            startActivity(intent);
						    }
						}else{
						    Intent intent = new Intent();
			                intent.setClass(A_3_1_1_Forget_Main_Acy.this,A_3_1_3_Forget_SetPassWord_Acy.class);
			                intent.putExtra("acy_type", 2);
			                intent.putExtra("user_phone", mobile);
			                startActivity(intent);
						}
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
                        PubMehods.showToastStr(A_3_1_1_Forget_Main_Acy.this, msg);
                        A_0_App.getInstance().CancelProgreDialog(A_3_1_1_Forget_Main_Acy.this);
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
