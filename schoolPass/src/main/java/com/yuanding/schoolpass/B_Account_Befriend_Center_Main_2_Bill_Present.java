package com.yuanding.schoolpass;

import io.rong.imkit.MainActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.text.DecimalFormat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.yuanding.schoolpass.bangbang.B_Side_Befriend_B0_Take_Result_Dialog;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterApayCrash;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.CashierInputFilter;
import com.yuanding.schoolpass.utils.PubMehods;


/**
 * 学生个人帮帮》》》》余额提现
 */
public class B_Account_Befriend_Center_Main_2_Bill_Present extends A_0_CpkBaseTitle_Navi {

    private String bindStatus;
    private String money, temp, withdrawAmountLow;
    private TextView tv_present_name;
    private Button btn_present_commit;
    private EditText et_present_money;
    private String times;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_befriend_bill_present);
        setTitleText("余额提现");
        showTitleBt(ZUI_RIGHT_TEXT, true);
        setZuiYouText("联系客服");

        bindStatus = getIntent().getStringExtra("bindStatus");
        money = getIntent().getStringExtra("money");
        tv_present_name = (TextView) findViewById(R.id.tv_present_name);
        btn_present_commit = (Button) findViewById(R.id.btn_present_commit);
        et_present_money = (EditText) findViewById(R.id.et_present_money);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        if (money != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            double temp = Double.parseDouble(money) / 100.00;
            et_present_money.setHint("最多能提现" + df.format(temp));
            InputFilter[] filters = {new CashierInputFilter()};
            et_present_money.setFilters(filters);
            withdrawAmountLow = getIntent().getStringExtra("low");
            if (bindStatus.equals("1")) {
                tv_present_name.setText("(" + getIntent().getStringExtra("accountName") + ")" + getIntent().getStringExtra("account"));
                btn_present_commit.setBackgroundResource(R.color.for_help_pay_start);
            } else {
                btn_present_commit.setBackgroundColor(R.color.title_line);
            }
        }

        btn_present_commit.setOnClickListener(onClick);
    }


    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_lecture_load_error:

                    break;
                case R.id.btn_present_commit:
                    temp = et_present_money.getText().toString();
                    DecimalFormat df = new DecimalFormat("0.00");
                    double temp1 = Double.parseDouble(withdrawAmountLow) / 100.00;
                    if (temp == null || temp.length() <= 0 || temp.equals("")) {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, "输入金额不能为空");
                        return;
                    }
                    if (Double.valueOf(temp) < temp1) {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, "输入金额不能低于" + df.format(temp1));
                        return;
                    }
                    if (Double.valueOf(temp) * 100 > Double.valueOf(money)) {
                        PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, "输入金额大于自身金额");
                        return;
                    }
                    if (bindStatus.equals("1")) {

                        showForwardDialog();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void showForwardDialog() {
        final Dialog upDateDialog = new Dialog(B_Account_Befriend_Center_Main_2_Bill_Present.this, R.style.Theme_GeneralDialog);
        upDateDialog.setContentView(R.layout.dialog_cash_pass);
        TextView cancel = (TextView) upDateDialog.findViewById(R.id.tv_left_button);
        TextView summit = (TextView) upDateDialog.findViewById(R.id.tv_right_button);
        final EditText et_pass = (EditText) upDateDialog.findViewById(R.id.et_pass);
        cancel.setText("取消");
        summit.setText("确定");
        upDateDialog.show();
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                upDateDialog.dismiss();

            }
        });
        summit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String temp = et_pass.getText().toString();
                if (temp == null || temp.length() <= 0 || temp.equals("")) {
                    PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, "密码不能为空！");
                    return;
                }
                upDateDialog.dismiss();
                Apply_Withdrawals(temp);
            }
        });
    }

    private void Apply_Withdrawals(String pass) {
        A_0_App.getApi().getApayCrash(B_Account_Befriend_Center_Main_2_Bill_Present.this, A_0_App.USER_TOKEN, subZeroAndDot(((Double.valueOf(temp))) * 100 + ""), pass, new InterApayCrash() {

            @Override
            public void onSuccess(String msg, String totalAmount, String withdrawTimes) {
                money = totalAmount;
                times = withdrawTimes;
                DecimalFormat df = new DecimalFormat("0.00");
                double temp = Double.parseDouble(money) / 100.00;
                et_present_money.setHint("最多能提现" + df.format(temp));

                et_present_money.setText("");
                final B_Side_Befriend_B0_Take_Result_Dialog result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                        B_Account_Befriend_Center_Main_2_Bill_Present.this, R.style.Theme_dim_Dialog, 12, null);
                result_Dialog.show();
                result_Dialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                    @Override
                    public void doConfirm() {
                        Intent it = new Intent();
                        it.putExtra("money", money);
                        it.putExtra("times", times);
                        setResult(1, it);
                        finish();
                        result_Dialog.dismiss();
                    }

                    @Override
                    public void doCancel() {
                        Intent it = new Intent();
                        it.putExtra("money", money);
                        it.putExtra("times", times);
                        setResult(1, it);
                        finish();
                        result_Dialog.dismiss();
                    }
                });
            }
        }, new Inter_Call_Back() {

            @Override
            public void onFinished() {

            }

            @Override
            public void onFailure(String msg) {
                if (!msg.contains("密码")) {
                    PubMehods.showToastStr(B_Account_Befriend_Center_Main_2_Bill_Present.this, msg);
                } else {
                    final B_Side_Befriend_B0_Take_Result_Dialog result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                            B_Account_Befriend_Center_Main_2_Bill_Present.this, R.style.Theme_dim_Dialog, 11, null);
                    result_Dialog.show();
                    result_Dialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                        @Override
                        public void doConfirm() {
                            result_Dialog.dismiss();
                            showForwardDialog();
                        }

                        @Override
                        public void doCancel() {
                            result_Dialog.dismiss();
                        }
                    });
                }
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
                Intent it = new Intent();
                it.putExtra("money", money);
                it.putExtra("times", times);
                setResult(1, it);
                finish();
                break;
            case ZUI_RIGHT_TEXT:
                A_0_App.getInstance().callSb(B_Account_Befriend_Center_Main_2_Bill_Present.this, "客服电话", "400-007-3298", new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Account_Befriend_Center_Main_2_Bill_Present.this, REQUECT_CODE_CALLPHONE,
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
        PubMehods.callPhone(B_Account_Befriend_Center_Main_2_Bill_Present.this, "400-007-3298");
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Account_Befriend_Center_Main_2_Bill_Present.this);
    }


    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0    
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉    
        }
        return s;
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
                            A_0_App.getInstance().showExitDialog(B_Account_Befriend_Center_Main_2_Bill_Present.this, AppStrStatic.kicked_offline());
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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    Intent it = new Intent();
                    it.putExtra("money", money);
                    it.putExtra("times", times);
                    setResult(1, it);
                    finish();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}
