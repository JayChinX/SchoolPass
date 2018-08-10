
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterSideAddAttence;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2016年4月5日 下午2:21:48 类说明 新建上课考勤页面
 */
public class B_Side_Attence_Main_A1_New_Attence extends A_0_CpkBaseTitle_Navi {
    private EditText et_attence_title, et_attence_place;
    private TextView et_attence_participantor;
    private Button btn_add_participantor;
    private String organ_ids ="";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_side_attence_new_class_attence);

        setTitleText("新建协助考勤");
        showTitleBt(ZUI_RIGHT_BUTTON, true);

        if (A_0_App.USER_STATUS.equals("2")) {
            setZuiRightBtn(R.drawable.navigationbar_save);
        }

        et_attence_title = (EditText) findViewById(R.id.et_attence_title);
        et_attence_participantor = (TextView) findViewById(R.id.et_choose_participantor);
        et_attence_place = (EditText) findViewById(R.id.et_attence_place);
        btn_add_participantor = (Button) findViewById(R.id.btn_add_new_participantor);

        //添加考勤人
        btn_add_participantor.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
               intent.setClass(B_Side_Attence_Main_A1_New_Attence.this, B_Side_Attence_Main_A2_Add_Contact.class);
               intent.putExtra("organ_ids", organ_ids);
               startActivityForResult(intent, IMAGE_REQUEST_CODE);

            }
        });

        et_attence_title.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    et_attence_title.setBackgroundResource(R.drawable.login_input_hover_bg);
                } else {
                    et_attence_title.setBackgroundResource(R.drawable.login_input_normal_bg);
                }
            }
        });

        et_attence_participantor.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    et_attence_participantor.setBackgroundResource(R.drawable.login_input_hover_bg);
                } else {
                    et_attence_participantor
                            .setBackgroundResource(R.drawable.login_input_normal_bg);
                }
            }
        });

        et_attence_place.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg1) {
                    et_attence_place.setBackgroundResource(R.drawable.login_input_hover_bg);
                } else {
                    et_attence_place.setBackgroundResource(R.drawable.login_input_normal_bg);
                }
            }
        });
        
        /**
         * 过滤表情
         */
        et_attence_title.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                
            }
            
            @Override
            public void afterTextChanged(Editable editable) {
                  int index = et_attence_title.getSelectionStart() - 1;
                                  if (index > 0) {
                                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
                                        Editable edit = et_attence_title.getText();
                                          edit.delete(index, index + 1);
                                     }
                                 }
            }
        });
        
        /**
         * 过滤表情
         */
        et_attence_place.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                    int arg3) {
                
            }
            
            @Override
            public void afterTextChanged(Editable editable) {
                  int index = et_attence_place.getSelectionStart() - 1;
                                  if (index > 0) {
                                     if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
                                        Editable edit = et_attence_place.getText();
                                          edit.delete(index, index + 1);
                                     }
                                 }
            }
        });
        startListtenerRongYun();
    }
    
    // 添加考勤
    private void AddAttence(String token, String title, String place, String organ_ids) {
        A_0_App.getInstance().showProgreDialog(B_Side_Attence_Main_A1_New_Attence.this, "", true);
        A_0_App.getApi().sideAddAttence(token, title, place, organ_ids, new InterSideAddAttence() {

            @Override
            public void onSuccess(String message,String atd_id) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A1_New_Attence.this);
                PubMehods.showToastStr(B_Side_Attence_Main_A1_New_Attence.this, message);
                goDtail(atd_id);
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Attence_Main_A1_New_Attence.this);
                PubMehods.showToastStr(B_Side_Attence_Main_A1_New_Attence.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }

    private void goDtail(String attence_atd_id) {
        Intent intent = new Intent(B_Side_Attence_Main_A1_New_Attence.this,
                B_Side_Attence_Main_A3_Detail.class);
        intent.putExtra(B_Side_Attence_Main_A0.ATTENCE_ATD_ID, attence_atd_id);
        intent.putExtra(B_Side_Attence_Main_A0.ATTENCE_ACY_TYPE, 2);
        intent.putExtra(B_Side_Attence_Main_A0.ATTENCE_ACY_EARLY, false);
        startActivity(intent);
        finish();
    }

    public static final int IMAGE_REQUEST_CODE = 0;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String organ_id = data.getExtras().getString("organ_id");
            String organ_name = data.getExtras().getString("organ_name");
            if (!"".equals(organ_id)) {
                if (requestCode == IMAGE_REQUEST_CODE) {
                	if (organ_name.length()>1&&organ_id.length()>1) {
                		 et_attence_participantor.setText(organ_name.substring(0, organ_name.length()-1));
                         organ_ids = organ_id.substring(0, organ_id.length()-1);
					}
                   
                }
            }else{
            	organ_ids="";
            	et_attence_participantor.setText("");
            }
        }

    }
    
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_BUTTON:
                String title = et_attence_title.getText().toString();
                String participantor = et_attence_participantor.getText().toString();
                String place = et_attence_place.getText().toString();
                if (title == null || title.length() <= 0 || title.equals("")) {
                    PubMehods.showToastStr(this, "请输入课堂考勤的名称");
                    return;
                }
                if (participantor == null || participantor.length() <= 0 || participantor.equals("")) {
                    PubMehods.showToastStr(this, "请选择关联教师");
                    return;
                }
                 
                if (organ_ids.equals("")) {
                    PubMehods.showToastStr(this, "请选择关联教师");
                    return;
                }
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
            		AddAttence(A_0_App.USER_TOKEN, title, place, organ_ids);
				} else {
//					PubMehods.showToastStr(B_Side_Attence_Main_A1_New_Attence.this,"请勿重复操作！");
				}
               
                break;
            default:
                break;
        }

    }
    
    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements
            RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {
            switch (connectionStatus) {
            case CONNECTED:// 连接成功。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                break;
            case DISCONNECTED:// 断开连接。
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                //A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A0.this,getResources().getString(R.string.token_timeout));
                break;
            case CONNECTING:// 连接中。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
                break;
            case NETWORK_UNAVAILABLE:// 网络不可用。
                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
                break;
            case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                A_Main_My_Message_Acy
                        .logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                class LooperThread extends Thread {
                    public void run() {
                        Looper.prepare();
                        A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A1_New_Attence.this,
                                AppStrStatic.kicked_offline());
                        Looper.loop();
                    }
                }
                LooperThread looper = new LooperThread();
                looper.start();
                break;
            }
        }
    }

}
