package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Reset_Pwd;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月9日 下午5:56:29
 * 设置密码页面(忘记密码功能)--2(忘记密码))
 */
public class A_3_1_3_Forget_SetPassWord_Acy extends A_0_CpkBaseTitle_Navi{

	private Button btn_sure_pwd;
	private EditText et_reg2_pwd,et_reg2_sure_pwd;
	private TextView tv_register2_title,tv_register2_sure_title,tv_setpwd_title;
	private LinearLayout liner_register2_pwd,liner_register2_sure; 
	private String user_phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        if (A_0_App.getInstance().isRuning()) {
            finish();
            return;
        }
		A_0_App.getInstance().addActivity(this);
		A_0_App.getInstance().addReSetPwdAcy(this);
		A_0_App.getInstance().addRegisterPwdAcy(this);
		setView(R.layout.activity_forget_3);
		
		setTitleText("密码重置");
		user_phone = getIntent().getExtras().getString("user_phone");
		
		tv_setpwd_title = (TextView)findViewById(R.id.tv_setpwd_title);
		btn_sure_pwd = (Button)findViewById(R.id.btn_sure_pwd);
		et_reg2_pwd = (EditText)findViewById(R.id.et_reg2_pwd);
		et_reg2_sure_pwd = (EditText)findViewById(R.id.et_reg2_sure_pwd);
		
		liner_register2_pwd = (LinearLayout)findViewById(R.id.liner_register2_pwd);
		liner_register2_sure = (LinearLayout)findViewById(R.id.liner_register2_sure);
		tv_register2_title = (TextView) findViewById(R.id.tv_register2_title);
		tv_register2_sure_title = (TextView) findViewById(R.id.tv_register2_sure_title);
		
		liner_register2_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
		tv_register2_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
		et_reg2_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					liner_register2_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
					//tv_register2_title.setTextColor(getResources().getColor(R.color.title_focus_login));
				    tv_register2_title.setVisibility(View.GONE);
				}else{
					liner_register2_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
					//tv_register2_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
			}
		});
		
		liner_register2_sure.setBackgroundResource(R.drawable.login_input_normal_bg);
		tv_register2_sure_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
		et_reg2_sure_pwd.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				
				if(arg1){
					liner_register2_sure.setBackgroundResource(R.drawable.login_input_hover_bg);
					//tv_register2_sure_title.setTextColor(getResources().getColor(R.color.title_focus_login));
				    tv_register2_sure_title.setVisibility(View.GONE);
				}else{
					liner_register2_sure.setBackgroundResource(R.drawable.login_input_normal_bg);
					//tv_register2_sure_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				}
			}
		});
		
		btn_sure_pwd.setOnClickListener(onClick);
		tv_setpwd_title.setText(PubMehods.getPwdSetTitle());
	}
	
	
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
		    //确认
			case R.id.btn_sure_pwd:
				String aa = et_reg2_pwd.getText().toString();
				String bb = et_reg2_sure_pwd.getText().toString();
				
				String regEx = "^[A-Za-z0-9]+$";
				if (aa != null && aa.length() > 0 && bb != null && bb.length() > 0 && !bb.equals("") && !aa.equals("")) {

				    if ((aa.length() < AppStrStatic.PWD_MIN_LENGTH) || (aa.length() > AppStrStatic.PWD_MAX_LENGTH)) {
						PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, PubMehods.ToastPwdSetTitle());
						return;
					}
				    if ((bb.length() < AppStrStatic.PWD_MIN_LENGTH) || (bb.length() > AppStrStatic.PWD_MAX_LENGTH)) {
						PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, PubMehods.ToastPwdSetTitle());
						return;
					}
					if (!aa.matches(regEx)) {
						PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, "设置密码中不能包含特殊符号");
						return;
					}
					if (!bb.matches(regEx)) {
						PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, "重置密码中不能包含特殊符号");
						return;
					}
					
					if(!aa.equals(bb)){
						PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, "两次密码输入不一致，请检查");
						return;
					}
					
				} else {
					PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, "重复或设置密码为空");
					return;
				}
				
				A_0_App.getInstance().setUserPwd(aa);
				resetPwd(aa, user_phone);					
				break;
			}
		}
	};
	
	//重设密码
	private void resetPwd(String passwd,String mobile) {
A_0_App.getInstance().showProgreDialog(A_3_1_3_Forget_SetPassWord_Acy.this, "重置密码中，请稍候",true);
		A_0_App.getApi().reset_Pwd(passwd, mobile, new Inter_Reset_Pwd() {
			
			@Override
			public void onSuccess() {
				A_0_App.getInstance().CancelProgreDialog(A_3_1_3_Forget_SetPassWord_Acy.this);
				Intent intent = new Intent();
				intent.setClass(A_3_1_3_Forget_SetPassWord_Acy.this, A_3_4_Forget_pwd_Succ_Acy.class);
				intent.putExtra("acy_type", "resetPwd");
				intent.putExtra("type", 1);
				startActivity(intent);
			}
			
		},new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                A_0_App.getInstance().CancelProgreDialog(A_3_1_3_Forget_SetPassWord_Acy.this);
                PubMehods.showToastStr(A_3_1_3_Forget_SetPassWord_Acy.this, msg);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

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
