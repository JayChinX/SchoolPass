package com.yuanding.schoolpass;

import io.rong.imkit.MainActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import android.Manifest;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterApayBind;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;


/**
 * 学生个人帮帮》》》》提现绑定
 */
public class B_Account_Befriend_Center_Main_1_PresentBind extends A_0_CpkBaseTitle_Navi {

    private RelativeLayout rela_no_bind, rela_bind;
    private TextView tv_bind;
    private LinearLayout liner_bind;
    private Button btn_bind;
    private String bindStatus;
    private EditText et_apay_acc, et_pass;
    private TextView tv_name, tv_acc, tv_no_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_befriend_bind);
        setTitleText("提现绑定");
        showTitleBt(ZUI_RIGHT_TEXT, true);
        setZuiYouText("联系客服");
        rela_no_bind = (RelativeLayout) findViewById(R.id.rela_no_bind);
        rela_bind = (RelativeLayout) findViewById(R.id.rela_bind);
        tv_bind = (TextView) findViewById(R.id.tv_bind);
        liner_bind = (LinearLayout) findViewById(R.id.liner_bind);
        et_apay_acc = (EditText) findViewById(R.id.et_apay_acc);
        et_pass = (EditText) findViewById(R.id.et_pass);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_no_name = (TextView) findViewById(R.id.tv_no_name);
        tv_acc = (TextView) findViewById(R.id.tv_acc);
        btn_bind = (Button) findViewById(R.id.btn_bind);
        btn_bind.setOnClickListener(onClick);
        rela_no_bind.setOnClickListener(onClick);
        bindStatus = getIntent().getStringExtra("bindStatus");
        et_pass.setHint("请输入"+A_0_App.APP_NAME+"登录密码");
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        tv_no_name.setText(A_0_App.USER_NAME);
        if (bindStatus != null) {
            if (bindStatus.equals("0")) {
                rela_no_bind.setVisibility(View.VISIBLE);
            } else if (bindStatus.equals("1")) {
                rela_bind.setVisibility(View.VISIBLE);
                tv_bind.setVisibility(View.VISIBLE);
                liner_bind.setVisibility(View.GONE);
                btn_bind.setVisibility(View.GONE);
                rela_no_bind.setVisibility(View.GONE);
                tv_name.setText(getIntent().getStringExtra("accountName"));
                tv_acc.setText(getIntent().getStringExtra("account"));
            }
        }

    }


    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_bind:


                    String str_phone = et_apay_acc.getText().toString().trim();
                    String str_pwd = et_pass.getText().toString().trim();

                    if (str_phone == null || str_phone.length() <= 0 || str_phone.equals("")) {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_PresentBind.this, "请输入手机号或邮箱");
                        return;
                    }


                    if (str_pwd == null || str_pwd.length() <= 0 || str_pwd.equals("")) {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_PresentBind.this, "请输入登录密码");
                        return;
                    }
                    String channelId = A_0_App.getInstance().getChannelId();
                    String clientid = A_0_App.getInstance().getClientid();
                    if (channelId == null || channelId.equals("")) {
                        channelId = "";
                    }
                    if (clientid == null || clientid.equals("")) {
                        clientid = "";
                    }
                    bind(str_phone, str_pwd);
                    break;
                case R.id.rela_no_bind:
                    liner_bind.setVisibility(View.VISIBLE);
                    btn_bind.setVisibility(View.VISIBLE);
                    rela_no_bind.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }
    };


    private void bind(String account, String password) {
        A_0_App.getApi().getApayBind(B_Account_Befriend_Center_Main_1_PresentBind.this, A_0_App.USER_TOKEN, account, password, new InterApayBind() {

            @Override
            public void onSuccess(String msg, String bindStatus, String account, String accountName) {
                if (bindStatus.equals("1")) {
                    rela_bind.setVisibility(View.VISIBLE);
                    tv_bind.setVisibility(View.VISIBLE);
                    liner_bind.setVisibility(View.GONE);
                    btn_bind.setVisibility(View.GONE);
                    rela_no_bind.setVisibility(View.GONE);
                    tv_name.setText(accountName);
                    tv_acc.setText(account);
                }
                //PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_PresentBind.this, msg);

            }
        }, new Inter_Call_Back() {

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(String msg) {
                PubMehods.showToastStr(B_Account_Befriend_Center_Main_1_PresentBind.this, msg);
            }

            @Override
            public void onCancelled() {

            }
        });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_TEXT:
                A_0_App.getInstance().callSb(B_Account_Befriend_Center_Main_1_PresentBind.this, "客服电话", "400-007-3298", new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Account_Befriend_Center_Main_1_PresentBind.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });

                    }
                });


                break;
            default:
                break;
        }
    }

    private static final int REQUECT_CODE_CAMERA = 2;
    private static final int REQUECT_CODE_ACCESS_FINE_LOCATION = 3;
    private static final int REQUECT_CODE_CALLPHONE = 4;

    //    PermissionGen.needPermission(MainActivity.this, 100,
//            new String[] {
//        Manifest.permission.READ_EXTERNAL_STORAGE,
//                Manifest.permission.CALL_PHONE
//    });
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneSuccess() {
        PubMehods.callPhone(B_Account_Befriend_Center_Main_1_PresentBind.this, "400-007-3298");
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Account_Befriend_Center_Main_1_PresentBind.this);
    }


    @Override
    protected void onResume() {
        super.onResume();

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
                    //A_0_App.getInstance().showExitDialog(B_Side_Lectures_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_Befriend_Center_Main_1_PresentBind.this, AppStrStatic.kicked_offline());
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
    protected void onDestroy() {
        super.onDestroy();
    }

}
