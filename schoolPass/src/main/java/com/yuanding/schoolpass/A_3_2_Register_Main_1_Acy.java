
package com.yuanding.schoolpass;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.CpkUserModel;
import com.yuanding.schoolpass.service.Api.InterCall_Back;
import com.yuanding.schoolpass.service.Api.InterCall_Back_Judge;
import com.yuanding.schoolpass.service.Api.InterUser_Regedit;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.ReadSmsContent;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年11月23日 下午2:10:47 类说明
 */
public class A_3_2_Register_Main_1_Acy extends A_0_CpkBaseTitle_Navi{

    ReadSmsContent readSmsContent;
    private TextView tv_receive_code_no;
    private Button btn_get_yanzheng_code,btn_next_step;
    private EditText tv_yanzheng_text;

    private EditText et_set_pwd_num,et_set_pwd_sure_num;
    private LinearLayout liner_register_yanzheng,liner_set_pwd,liner_set_pwd_sure;
    private static A_3_2_Register_Main_1_Acy instance;
    public static A_3_2_Register_Main_1_Acy getInstance(){
        return instance;
    }
    private String phone_no,user_status,recommend_phone = "";
    
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
        setView(R.layout.activity_register_main_1);
        
        instance = this;
        setTitleText(AppStrStatic.str_register_text());
        
        if (getIntent().getExtras() != null) {
            phone_no = getIntent().getExtras().getString("phone_no");
            user_status = getIntent().getExtras().getString("user_status");
            recommend_phone = getIntent().getExtras().getString("recommend_phone");
        }
        
        tv_receive_code_no = (TextView)findViewById(R.id.tv_receive_code_no);
        btn_next_step = (Button)findViewById(R.id.btn_next_step_1);
        btn_get_yanzheng_code = (Button)findViewById(R.id.btn_get_yanzheng_code_1);
        
        tv_yanzheng_text = (EditText)findViewById(R.id.tv_yanzheng_text);
        liner_register_yanzheng = (LinearLayout)findViewById(R.id.liner_register_yanzheng);
        et_set_pwd_num=(EditText) findViewById(R.id.et_set_pwd_num);
        et_set_pwd_sure_num=(EditText) findViewById(R.id.et_set_pwd_sure_num);
        liner_set_pwd=(LinearLayout) findViewById(R.id.liner_set_pwd);
        liner_set_pwd_sure=(LinearLayout) findViewById(R.id.liner_set_sure_pwd);
        liner_set_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
        liner_set_pwd_sure.setBackgroundResource(R.drawable.login_input_normal_bg);
        et_set_pwd_num.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if(arg1){
                    liner_set_pwd.setBackgroundResource(R.drawable.login_input_hover_bg);
                }else{
                    liner_set_pwd.setBackgroundResource(R.drawable.login_input_normal_bg);
                }
            }
        });
        et_set_pwd_sure_num.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if(arg1){
                    liner_set_pwd_sure.setBackgroundResource(R.drawable.login_input_hover_bg);
                }else{
                    liner_set_pwd_sure.setBackgroundResource(R.drawable.login_input_normal_bg);
                }
            }
        });
        liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_normal_bg);
        tv_yanzheng_text.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                
                if(arg1){
                    liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_hover_bg);
                    //tv_yan_zheng_title.setTextColor(getResources().getColor(R.color.title_focus_login));
                }else{
                    liner_register_yanzheng.setBackgroundResource(R.drawable.login_input_normal_bg);
                    //tv_yan_zheng_title.setTextColor(getResources().getColor(R.color.title_no_focus_login));
                }
            }
        });
        
        btn_get_yanzheng_code.setOnClickListener(onClick);
        btn_next_step.setOnClickListener(onClick);
        
        readSmsContent = new ReadSmsContent(this, new Handler(), tv_yanzheng_text);
        // 注册短信内容监听
        this.getContentResolver().registerContentObserver(Uri.parse("content://sms/"), true,readSmsContent);
        
        String firstTitle = getResources().getString(R.string.str_receiver_no_title) +"  " + phone_no;
        ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.GREENlIGHT));
        SpannableStringBuilder builder = new SpannableStringBuilder(firstTitle);
        builder.setSpan(greenSpan, 9, firstTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
        tv_receive_code_no.setText(builder); 
        
        if(user_status.equals(AppStrStatic.USER_ROLE_INACTIVATED)){
            btn_next_step.setText(R.string.str_complete_regedit);
        }else if(user_status.equals(AppStrStatic.USER_ROLE_NEW_USER)){
            btn_next_step.setText(R.string.pub_next_step);
        }
    }
    
    private String getInputMobile() {
        return phone_no;
    }
    
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
            //获取验证码
            case R.id.btn_get_yanzheng_code_1:
                if (getInputMobile() != null && getInputMobile().length() > 0) {
                    if(!PubMehods.isMobileNO(getInputMobile())){
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "手机号码格式错误");
                         return;
                    }
                    get_yan_zheng("verify", getInputMobile());
                } else {
                    PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this,"请输入手机号");
                }
                break;
            //注册
            case R.id.btn_next_step_1:
                String code = tv_yanzheng_text.getText().toString().trim();
                String phone = phone_no;
                if (phone == null || phone.equals("") || phone.length() <= 0) {
                    PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "请按返回键，并输入正确的手机号");
                    return;
                }
                if (code == null || code.equals("") || code.length() <= 0) {
                    PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "请输入您手机接收到的验证码");
                    tv_yanzheng_text.requestFocus();
                    return;
                }
                
                String aa = et_set_pwd_num.getText().toString().trim();
                String bb = et_set_pwd_sure_num.getText().toString().trim();
                
                String regEx = "^[A-Za-z0-9]+$";
                if (aa != null && aa.length() > 0 && bb != null && bb.length() > 0 && !bb.equals("") && !aa.equals("")) {

                    if ((aa.length() < AppStrStatic.PWD_MIN_LENGTH) || (aa.length() > AppStrStatic.PWD_MAX_LENGTH)) {
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this,PubMehods.ToastPwdSetTitle());
                        et_set_pwd_num.requestFocus();
                        return;
                    }
                    if ((bb.length() < AppStrStatic.PWD_MIN_LENGTH) || (bb.length() > AppStrStatic.PWD_MAX_LENGTH)) {
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, PubMehods.ToastPwdSetTitle());
                        et_set_pwd_sure_num.requestFocus();
                        return;
                    }
                    if (!aa.matches(regEx)) {
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "设置密码中不能包含特殊符号");
                        et_set_pwd_num.requestFocus();
                        return;
                    }
                    if (!bb.matches(regEx)) {
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "重置密码中不能包含特殊符号");
                        et_set_pwd_sure_num.requestFocus();
                        return;
                    }
                    
                    if(!aa.equals(bb)){
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "两次密码输入不一致，请检查");
                        et_set_pwd_sure_num.requestFocus();
                        return;
                    }
                    
                } else {
                    PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "密码不能为空");
                    return;
                }
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    judge_code("verify", getInputMobile(), code, aa);
                }
                break;
            }
        }
    };
    
    private boolean fangDianji = false;
    // 获取验证码
    private void get_yan_zheng(String type,String mobile) {
        if (fangDianji){
            PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "短信发送正在发送，请稍后");
            return;
        }
        fangDianji = true;
        btn_get_yanzheng_code.setEnabled(false);
        A_0_App.getInstance().showProgreDialog(A_3_2_Register_Main_1_Acy.this,"短信发送中",true);
        A_0_App.getApi().get_yan_zheng_code(type, mobile, new InterCall_Back() {
            @Override
            public void onSuccess(String code) {
                 fangDianji = false;
                 if (isFinishing())
                 return;
                 A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                if (code != null && code.length() <= 0) {
                     btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code_error));
                     startContrlTimer();
                     PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "短信发送成功，请注意查收");
               }else{
                   btn_get_yanzheng_code.setEnabled(true);
                   btn_get_yanzheng_code.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_get_yanzheng_code_error));
                   PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, R.string.str_sms_system_error);
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
                PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, msg);
                cancelTimer();
                A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    //用户注册
    private void user_regedit(final String mobile,String passwd,String school_id,String recommend_phone) {
          PushManager.startWork(A_3_2_Register_Main_1_Acy.this, PushConstants.LOGIN_TYPE_API_KEY, AppStrStatic.API_KEY_BAI_DU_PUSH);//重读百度云
          String channelId = A_0_App.getInstance().getChannelId(); 
          String clientID = A_0_App.getInstance().getClientid();
          A_0_App.getInstance().showProgreDialog(A_3_2_Register_Main_1_Acy.this, "正在注册，请稍候",false);
          A_0_App.getApi().get_user_regedit(channelId, clientID, mobile, passwd, school_id, recommend_phone,new InterUser_Regedit() {
                      @Override
                      public void onSuccess(CpkUserModel user) {
                          A_0_App.getInstance().saveUserPhone(mobile);
                          A_0_App.getInstance().saveUserLoginInfo(user);
                          A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                          PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, "注册成功");
                          startActivity(new Intent(A_3_2_Register_Main_1_Acy.this,A_Main_Acy.class));
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
                        A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, msg);
  
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
    }
    
    // 验证验证码的正确性
    private void judge_code(String send_type, final String mobile, String code, final String pwd) {
        A_0_App.getInstance().showProgreDialog(A_3_2_Register_Main_1_Acy.this, "正在验证",true);
        A_0_App.getApi().judge_yan_zheng_code(send_type, mobile, code,new InterCall_Back_Judge() {
                    @Override
                    public void onSuccess(String auth_status,String friendlist_is_display,String recommend_phone) {
                          if (isFinishing())
                              return;
                          A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                          A_0_App.getInstance().setUserPwd(pwd);
                          Intent intent = new Intent();
                          if (user_status.equals(AppStrStatic.USER_ROLE_NEW_USER)) {// 下一步
                              intent.setClass(A_3_2_Register_Main_1_Acy.this,A_3_2_Register_Main_2_Acy.class);
                              intent.putExtra("phone_no", phone_no);
                              intent.putExtra("recommend_phone", recommend_phone);
                              startActivity(intent);
                          } else if(user_status.equals(AppStrStatic.USER_ROLE_INACTIVATED)){
                               user_regedit(phone_no, pwd, "", recommend_phone);
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
                        PubMehods.showToastStr(A_3_2_Register_Main_1_Acy.this, msg);
                        A_0_App.getInstance().CancelProgreDialog(A_3_2_Register_Main_1_Acy.this);
                        tv_yanzheng_text.requestFocus();
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
    //关闭定时器
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
                        btn_get_yanzheng_code.setText(getResources().getString(R.string.str_get_verification_code));
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
