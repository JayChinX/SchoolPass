package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterPhone_Judge;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月9日 下午5:56:29
 * 注册（功能）页面==>2016.11.23 v2.4.0重新修改
 */
public class A_3_2_Register_Main_0_Acy extends A_0_CpkBaseTitle_Navi{

	private Boolean user_agree = true;
	private Button btn_next_step;
	private ImageView iv_agree_ment;
	private EditText et_reg_mobile_no;
	private TextView user_agreement;
	private LinearLayout liner_register_no;
	
	private static A_3_2_Register_Main_0_Acy instance;
	public static A_3_2_Register_Main_0_Acy getInstance(){
		return instance;
	}
	
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
		setView(R.layout.activity_register_main_0);
		
		instance = this;
		setTitleText(AppStrStatic.str_register_text());
		
		btn_next_step = (Button)findViewById(R.id.btn_next_step_0);
		iv_agree_ment = (ImageView)findViewById(R.id.iv_agree_ment);
		liner_register_no = (LinearLayout)findViewById(R.id.liner_register_no_0);
		
		user_agreement = (TextView) findViewById(R.id.user_agreement);
		et_reg_mobile_no = (EditText)findViewById(R.id.et_reg_mobile_no_0);
		et_reg_mobile_no.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				if(arg1){
					liner_register_no.setBackgroundResource(R.drawable.login_input_hover_bg);
				}else{
					liner_register_no.setBackgroundResource(R.drawable.login_input_normal_bg);
				}
			}
		});
		
        user_agreement.setText("《" + AppStrStatic.str_regedit_agree_title() + "》");
		btn_next_step.setOnClickListener(onClick);
		iv_agree_ment.setOnClickListener(onClick);
		user_agreement.setOnClickListener(onClick);
	}
	
	
	private String getInputMobile() {
		return et_reg_mobile_no.getText().toString().trim();
	}
	
	
	OnClickListener onClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
		    //下一步
			case R.id.btn_next_step_0:
			    if (getInputMobile() != null && getInputMobile().length() > 0) {
                    if(!PubMehods.isMobileNO(getInputMobile())){
                        PubMehods.showToastStr(A_3_2_Register_Main_0_Acy.this, "手机号码格式错误");
                        return;
                    }
                } else {
                    PubMehods.showToastStr(A_3_2_Register_Main_0_Acy.this,"请输入手机号");
                    return;
                }
                if (!user_agree) {
                    PubMehods.showToastStr(A_3_2_Register_Main_0_Acy.this, "请勾选同意用户协议");
                    return;
                }
                
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    judge_yan_zheng_phone(getInputMobile());
                }
				break;
			//同意协议
			case R.id.iv_agree_ment:
				if (user_agree) {
					iv_agree_ment.setBackgroundResource(R.drawable.register_box_unselected);
					user_agree = false;
				} else {
					iv_agree_ment.setBackgroundResource(R.drawable.register_box_selected);
					user_agree = true;
				}
				break;
			//查看用户协议
			case R.id.user_agreement:
				Intent intent = new Intent();
				intent.setClass(A_3_2_Register_Main_0_Acy.this,Pub_WebView_Load_Acy.class);
                intent.putExtra("title_text", AppStrStatic.str_regedit_agree_title());
                intent.putExtra("url_text", AppStrStatic.LINK_USER_REGEDIT);
                intent.putExtra("tag_skip", "1");
                intent.putExtra("tag_show_refresh_btn", "2");
				
				startActivity(intent);
				break;
				//查看用户协议
			}
		}
	};
	
	private boolean fangDianji = false;
    //校验用户状态
    private void judge_yan_zheng_phone(final String mobile) {
        if (fangDianji){
            return;
        }
        fangDianji = true;
        A_0_App.getInstance().showProgreDialog(A_3_2_Register_Main_0_Acy.this,"",true);
        A_0_App.getApi().judge_yan_zheng_phone(mobile, new InterPhone_Judge() {
            @Override
            public void onSuccess(String auth_status,String recommend_phone,String msg) {
                 fangDianji = false;
                 if (isFinishing())
                     return;
                A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_0_Acy.this);
                if (auth_status.equals(AppStrStatic.USER_ROLE_INACTIVATED) ||auth_status.equals(AppStrStatic.USER_ROLE_NEW_USER)) {
                    Intent intent = new Intent(A_3_2_Register_Main_0_Acy.this, A_3_2_Register_Main_1_Acy.class);
                    intent.putExtra("user_status", auth_status);
                    intent.putExtra("phone_no", mobile);
                    intent.putExtra("recommend_phone", recommend_phone);
                    startActivity(intent);
                }else{
                    PubMehods.showToastStr(A_3_2_Register_Main_0_Acy.this, msg);
                }
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
                PubMehods.showToastStr(A_3_2_Register_Main_0_Acy.this, msg);
                A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_0_Acy.this);
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
