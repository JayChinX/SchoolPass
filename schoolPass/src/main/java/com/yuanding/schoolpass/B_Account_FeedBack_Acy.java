
package com.yuanding.schoolpass;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_FeedBack;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.R;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

/**
 * @ClassName: B_Account_FeedBack_Acy
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author Jiaohaili
 * @date 2015年11月11日 下午1:49:44 意见反馈
 */

public class B_Account_FeedBack_Acy extends A_0_CpkBaseTitle_Navi {

    private Button mBtnSubmit;

    private EditText et_feed_content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_feedback);

        setTitleText("意见反馈");
        et_feed_content = (EditText) findViewById(R.id.et_feed_content);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit_opinion);

        mBtnSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String content = et_feed_content.getText().toString();

                if (content.length() <= 0) {
                    PubMehods.showToastStr(B_Account_FeedBack_Acy.this, "请输入反馈内容");
                    return;
                }
                // if (content.length() < 10) {
                // PubMehods.showToastStr(B_Account_FeedBack_Acy.this,"反馈内容至少为10个汉字");
                // return;
                // }
                if (content.length() > 100) {
                    PubMehods.showToastStr(B_Account_FeedBack_Acy.this, "请把字数控制在100个以内");
                    return;
                }
                if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
                        submintFeedBack(content);
                    } else {
                       
                    }
                } else {
                    PubMehods.showToastStr(B_Account_FeedBack_Acy.this,R.string.error_title_net_error);
                }
              
            }
        });
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    /**
     * @Title: submintFeedBack
     * @Description: TODO(提交意见)
     * @return void 返回类型
     */
    private void submintFeedBack(String content) {
        A_0_App.getInstance().showProgreDialog(B_Account_FeedBack_Acy.this, "正在提交，请稍候",true);
        A_0_App.getApi().feedBack(content, A_0_App.USER_TOKEN, new Inter_FeedBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Account_FeedBack_Acy.this);
                PubMehods.showToastStr(B_Account_FeedBack_Acy.this, message);
                finish();
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
                A_0_App.getInstance().CancelProgreDialog(B_Account_FeedBack_Acy.this);
                PubMehods.showToastStr(B_Account_FeedBack_Acy.this, msg);

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
                    A_Main_My_Message_Acy
                            .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    // A_0_App.getInstance().showExitDialog(B_Account_FeedBack_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_FeedBack_Acy.this,
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

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            default:
                break;
        }
    }
}
