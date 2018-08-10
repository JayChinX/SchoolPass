package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午2:53:58
 *          类说明
 */
public class B_Account_MySet_About_Call_Acy extends A_0_CpkBaseTitle_Navi {


    private ImageView iv_dimensional_bar_code;
    private LinearLayout liner_contact_us;
    private TextView tv_temp_weinxin_number;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_acc_about_call_us);
        setTitleText(getResources().getString(R.string.str_contact_us));

        tv_temp_weinxin_number = (TextView) findViewById(R.id.tv_temp_weinxin_number);
        liner_contact_us = (LinearLayout) findViewById(R.id.liner_contact_us);
        iv_dimensional_bar_code = (ImageView) findViewById(R.id.iv_dimensional_bar_code);
        tv_temp_weinxin_number.setText(AppStrStatic.str_about_weinxin_number());
        iv_dimensional_bar_code.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // 将文本内容放到系统剪贴板里。
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(AppStrStatic.str_about_weinxin_number());
                PubMehods.showToastStr(B_Account_MySet_About_Call_Acy.this, AppStrStatic.str_app_weixin_copyurlsuccess());
            }
        });

        liner_contact_us.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                A_0_App.getInstance().callSb(B_Account_MySet_About_Call_Acy.this, "客服电话", "400-007-3298", new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Account_MySet_About_Call_Acy.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });

                    }
                });

            }
        });

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    //    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//    Uri uri = Uri.fromParts("package", getPackageName(), null);
//    intent.setData(uri);
//    startActivity(intent);
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
        PubMehods.callPhone(B_Account_MySet_About_Call_Acy.this, "400-007-3298");
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Account_MySet_About_Call_Acy.this);
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
                    //A_0_App.getInstance().showExitDialog(B_Account_Push_Set_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_MySet_About_Call_Acy.this, AppStrStatic.kicked_offline());
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
