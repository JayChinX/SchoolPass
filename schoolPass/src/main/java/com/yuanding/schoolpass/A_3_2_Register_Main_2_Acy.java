package com.yuanding.schoolpass;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.CpkUserModel;
import com.yuanding.schoolpass.service.Api.InterUser_Regedit;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年11月23日 下午2:10:47
 * 类说明
 */
public class A_3_2_Register_Main_2_Acy extends A_0_CpkBaseTitle_Navi {
    private TextView et_select_your_school;
    private Button btn_complete_registration;
    private String phone_no,schoolId,recommend_phone ="";

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
        setView(R.layout.activity_register_main_2);

        setTitleText(AppStrStatic.str_register_text());
        
        if (getIntent().getExtras() != null) {
            phone_no = getIntent().getExtras().getString("phone_no");
            recommend_phone = getIntent().getExtras().getString("recommend_phone");
        }
        
        et_select_your_school = (TextView)findViewById(R.id.et_select_your_school);
        btn_complete_registration = (Button)findViewById(R.id.btn_complete_registration);
        
        et_select_your_school.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(A_3_2_Register_Main_2_Acy.this, A_3_3_Complete_Selcet_School.class);
                intent.putExtra("title_name", "选择学校");
                intent.putExtra("user_phone", phone_no);
                startActivityForResult(intent, 2);
            }
        });
        
        btn_complete_registration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (null == schoolId || "".equals(schoolId)) {
                    PubMehods.showToastStr(A_3_2_Register_Main_2_Acy.this, "请选择所属学校");
                    return;
                }
                
                if(phone_no == null || phone_no.equals("")||phone_no.length()<=0){
                    PubMehods.showToastStr(A_3_2_Register_Main_2_Acy.this, "请返回注册重新输入手机号");
                    return;
                }
                
                String user_pwd = A_0_App.getInstance().getUserPwd();
                if(user_pwd == null || user_pwd.equals("")||user_pwd.length()<=0){
                    PubMehods.showToastStr(A_3_2_Register_Main_2_Acy.this, "请返回注册重新设置密码");
                    return;
                }
                
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    user_regedit(phone_no, user_pwd, schoolId, recommend_phone);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null && data.getExtras().getString("modify_content") != null) {
            String result = data.getExtras().getString("modify_content");
            if (!"".equals(result)) {
                if (requestCode == 2) {
                    // 学校
                    schoolId = data.getExtras().getString("school_id");
                    et_select_your_school.setText(result);
                    btn_complete_registration.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_bg));
                }
            }
        }
    }
    
    //用户注册
    private void user_regedit(final String mobile,String passwd,String school_id,String recommend_phone) {
          PushManager.startWork(A_3_2_Register_Main_2_Acy.this, PushConstants.LOGIN_TYPE_API_KEY, AppStrStatic.API_KEY_BAI_DU_PUSH);//重读百度云
          String channelId = A_0_App.getInstance().getChannelId(); 
          String clientID = A_0_App.getInstance().getClientid();
          A_0_App.getInstance().showProgreDialog(A_3_2_Register_Main_2_Acy.this, "正在注册，请稍候",false);
          A_0_App.getApi().get_user_regedit(channelId, clientID, mobile, passwd, school_id, recommend_phone,new InterUser_Regedit() {
                      @Override
                      public void onSuccess(CpkUserModel user) {
                          A_0_App.getInstance().saveUserPhone(mobile);
                          A_0_App.getInstance().saveUserLoginInfo(user);
                          A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_2_Acy.this);
                          PubMehods.showToastStr(A_3_2_Register_Main_2_Acy.this, "注册成功");
                          startActivity(new Intent(A_3_2_Register_Main_2_Acy.this,A_Main_Acy.class));
                          A_0_App.getInstance().regedit_enter = true;
                          A_0_App.getInstance().exitRegisterProcess();
                          finish();
                      }
                  },new Inter_Call_Back() {
                    
                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                        
                    }
                    
                    @Override
                    public void onFailure(String msg) {
                        A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_2_Acy.this);
                        PubMehods.showToastStr(A_3_2_Register_Main_2_Acy.this, msg);
  
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
