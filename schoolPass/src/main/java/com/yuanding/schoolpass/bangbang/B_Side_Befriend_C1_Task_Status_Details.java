package com.yuanding.schoolpass.bangbang;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.Pub_WebView_Load_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Befriend_Pick_Task_Details;
import com.yuanding.schoolpass.bean.Befriend_Publish_Task_Details;
import com.yuanding.schoolpass.bean.Befriend_Task_Form;
import com.yuanding.schoolpass.service.Api.InterDeleteTaskStatus;
import com.yuanding.schoolpass.service.Api.InterPickTaskStatus;
import com.yuanding.schoolpass.service.Api.InterTaskStatus;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.DensityUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.TimesTextView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;

import io.rong.imkit.MainActivity;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.umeng.socialize.utils.ContextUtil.getContext;
import static io.rong.imlib.statistics.UserData.phone;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月19日 任务详情页
 */
public class B_Side_Befriend_C1_Task_Status_Details extends A_0_CpkBaseTitle_Navi {
    private ImageView statusIcon, WorkrSex, WorkrPhone;
    private TextView taskStatus, statusIconTi, taskBefore, taskDoing, taskDone, taskPaying, payBtn, workrSch,
            workrTime, createrTime, taskWorkrName, taskNamer, taskName, taskMoney, taskStartAddress, taskstartadds, taskEndAddress,
            taskStartImer, taskStartIme, taskStartPhoneNums, taskStartPhoneNum, taskEndPhoneNum, taskOther,
            taskPayOrder, taskPayOrderFrom, taskPayTime,tv_money_true,tv_money_coupon;
    private TimesTextView taskTime;
    private LinearLayout taskStatusDo, payTI, taskWorkr, taskStartPhone, taskEndPhone, taskStartAdd, taskEndAdd, others,
            side_lecture_detail_loading, home_load_loading, for_help_status_loading_error, linePayOrder;
    private TextView error_line;
    private TextView tishi01, tishi02, tishi03;
    private String task_id;
    boolean payFrom;
    int type;
    String task_user_type;
    int deleteType;
    private AnimationDrawable drawable;

    boolean popWin = false;
    private RelativeLayout rela_coupon,rela_true;
    private View temp_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_befriend_task_for_help_status);

        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setZuiRightBtn(R.drawable.navigationbar_more_button);
        if (getIntent().getExtras() != null) {
            type = getIntent().getIntExtra("type", 99);
            switch (type) {
                case 0:// 1-从我发布的任务跳转
                    task_id = getIntent().getStringExtra("id");
                    final B_Side_Befriend_B0_Take_Result_Dialog result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                            B_Side_Befriend_C1_Task_Status_Details.this, R.style.Theme_dim_Dialog, 5, null);
                    result_Dialog.show();
                    result_Dialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                        @Override
                        public void doConfirm() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void doCancel() {
                            // TODO Auto-generated method stub
                            result_Dialog.dismiss();
                        }
                    });
                    break;
                case 1:// 1-从我发布的任务跳转
                    task_id = getIntent().getStringExtra("id");
                    break;
                case 2:// 2-从我领取的任务跳转
                    task_id = getIntent().getStringExtra("id");
                    break;
                case 3:// 推送进入3我发布的
                    task_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
                    break;
                case 4:// 推送进入4我领取的
                    task_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
                    break;
                case 5://领取成功进入
                    task_id = getIntent().getStringExtra("id");
                    final B_Side_Befriend_B0_Take_Result_Dialog taskCGDialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                            B_Side_Befriend_C1_Task_Status_Details.this, R.style.Theme_dim_Dialog, 1, null);
                    taskCGDialog.show();
                    taskCGDialog.setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {
                        @Override
                        public void doConfirm() {
                            // TODO Auto-generated method stub

                        }

                        @Override
                        public void doCancel() {
                            // TODO Auto-generated method stub
                            taskCGDialog.dismiss();
                        }
                    });
                    break;
            }
        }
        Log.i("任务详情", "type" + type + "任务id" + task_id);
        initView();
        initData();

    }

    private void initView() {
        // TODO Auto-generated method stub
        tv_money_true = (TextView) findViewById(R.id.tv_money_true);
        tv_money_coupon = (TextView) findViewById(R.id.tv_money_coupon);
        //优惠券下划线
        temp_view =(View)findViewById(R.id.temp_view);
        //优惠券抵用
       rela_coupon=(RelativeLayout) findViewById(R.id.rela_coupon);//
        //实付款
        rela_true=(RelativeLayout) findViewById(R.id.rela_true);
        // 任务状态
        // 图标
        statusIcon = (ImageView) findViewById(R.id.for_help_status_time_icon);
        // 状态
        taskStatus = (TextView) findViewById(R.id.for_help_status_status);
        // 图标右边小字提示
        statusIconTi = (TextView) findViewById(R.id.for_help_status_status_tishi);
        // 剩余时间
        taskTime = (TimesTextView) findViewById(R.id.for_help_status_time);

        // 任务状态
        taskStatusDo = (LinearLayout) findViewById(R.id.for_help_status_task_status);
        // 待领取
        taskBefore = (TextView) findViewById(R.id.for_help_status_task_before);
        // 服务中
        taskDoing = (TextView) findViewById(R.id.for_help_status_task_doing);
        // 已完成
        taskDone = (TextView) findViewById(R.id.for_help_status_task_done);
        // 待支付
        taskPaying = (TextView) findViewById(R.id.for_help_status_task_paying);

        // 确认并付款按钮
        payBtn = (TextView) findViewById(R.id.for_help_status_task_payBtn);

        // tishi
        payTI = (LinearLayout) findViewById(R.id.for_help_status_task_tishi);
        // 01
        tishi01 = (TextView) findViewById(R.id.for_help_status_task_tishi_01);
        // 02变色
        tishi02 = (TextView) findViewById(R.id.for_help_status_task_tishi_color_02);
        // 03
        tishi03 = (TextView) findViewById(R.id.for_help_status_task_tishi_03);

        // 帮忙人信息
        taskWorkr = (LinearLayout) findViewById(R.id.for_help_status_task_workr);
        // 名字
        taskWorkrName = (TextView) findViewById(R.id.for_help_status_task_work);
        // 性别
        WorkrSex = (ImageView) findViewById(R.id.for_help_status_task_work_sex);
        // 电话
        WorkrPhone = (ImageView) findViewById(R.id.for_help_status_task_work_phone);
        // 所在校园
        workrSch = (TextView) findViewById(R.id.for_help_status_task_workr_school);
        // 接任务时间
        workrTime = (TextView) findViewById(R.id.for_help_status_task_workr_taskTime);

        createrTime = (TextView) findViewById(R.id.for_help_status_task_workr_taskTimes);

        // 任务详情
        // 任务名字
        taskNamer = (TextView) findViewById(R.id.for_help_status_task_namer);
        taskName = (TextView) findViewById(R.id.for_help_status_task_name);
        // 任务酬金
        taskMoney = (TextView) findViewById(R.id.for_help_status_task_money);

        // 开始地址
        taskStartAdd = (LinearLayout) findViewById(R.id.for_help_status_task_take_add);
        // 开始地址
        taskStartAddress = (TextView) findViewById(R.id.for_help_status_task_take_address);
        taskstartadds = (TextView) findViewById(R.id.for_help_status_task_take_adds);
        // 结束地址
        taskEndAdd = (LinearLayout) findViewById(R.id.for_help_status_task_send_add);
        // 结束地址
        taskEndAddress = (TextView) findViewById(R.id.for_help_status_task_send_address);
        // 任务时间
        taskStartImer = (TextView) findViewById(R.id.for_help_status_task_take_timer);
        taskStartIme = (TextView) findViewById(R.id.for_help_status_task_take_time);
        // 开始电话
        taskStartPhone = (LinearLayout) findViewById(R.id.for_help_status_task_take_phone);
        taskStartPhoneNums = (TextView) findViewById(R.id.for_help_status_task_take_phoneNums);
        taskStartPhoneNum = (TextView) findViewById(R.id.for_help_status_task_take_phoneNum);
        // 结束电话
        taskEndPhone = (LinearLayout) findViewById(R.id.for_help_status_task_send_phone);
        taskEndPhoneNum = (TextView) findViewById(R.id.for_help_status_task_send_phoneNum);
        // 备注
        others = (LinearLayout) findViewById(R.id.for_help_status_task_others);
        taskOther = (TextView) findViewById(R.id.for_help_status_task_other);

        // 订单详情
        // 订单号
        linePayOrder = (LinearLayout) findViewById(R.id.for_help_status_task_pay_line);
        taskPayOrder = (TextView) findViewById(R.id.for_help_status_task_pay_order);
        // 支付方式
        taskPayOrderFrom = (TextView) findViewById(R.id.for_help_status_task_pay_order_from);
        // 支付时间
        taskPayTime = (TextView) findViewById(R.id.for_help_status_task_pay_time);
        // 加载失败
        for_help_status_loading_error = (LinearLayout) findViewById(R.id.for_help_status_loading_error);
        error_line = (TextView) findViewById(R.id.tv_reload);
        // 加载动画
        side_lecture_detail_loading = (LinearLayout) findViewById(R.id.for_help_status_loading);

        home_load_loading = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        side_lecture_detail_loading.setOnClickListener(onClick);
        error_line.setOnClickListener(onClick);


        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }


    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_attdence_load_error:
                    switch (type) {
                        case 0:
                            getTaskDetails(task_id);
                            deleteType = 6;
                            break;
                        case 1:
                            getTaskDetails(task_id);
                            deleteType = 6;
                            break;
                        case 2:
                            SetPickTaskDetails(task_id);
                            deleteType = 7;
                            break;
                        case 3:
                            getTaskDetails(task_id);
                            deleteType = 6;
                            break;
                        case 4:
                            SetPickTaskDetails(task_id);
                            deleteType = 7;
                            break;
                        case 5://领取成功进入
                            SetPickTaskDetails(task_id);
                            deleteType = 7;
                            break;
                    }
                    break;
                case R.id.tv_reload:
                    getTaskDetails(task_id);
                    break;
            }
        }
    };

    /**
     * 我发布的任务
     *
     * @param task_id 任务id
     */
    private void getTaskDetails(String task_id) {
        // TODO Auto-generated method stub
        // 获取任务相关信息
        side_lecture_detail_loading.setVisibility(View.VISIBLE);
        drawable.start();
        A_0_App.getApi().getPublishTaskDetails(B_Side_Befriend_C1_Task_Status_Details.this, A_0_App.USER_TOKEN, task_id,
                new InterTaskStatus() {

                    @Override
                    public void onSuccess(Befriend_Publish_Task_Details publishTaskDetails) {
                        // TODO Auto-generated method stub
                        /*******
                         * 页面上部判断显示
                         */
                        if (publishTaskDetails.getCouponStatus().equals("1")){
                            rela_coupon.setVisibility(View.VISIBLE);
                            rela_true.setVisibility(View.VISIBLE);
                            temp_view.setVisibility(View.VISIBLE);
                            BigDecimal b1=new BigDecimal(Double.parseDouble(publishTaskDetails.getTotal_amount()));
                            BigDecimal b2=new BigDecimal(Double.parseDouble(publishTaskDetails.getCouponAmount()));
                            BigDecimal b3=new BigDecimal(100.00);

                            tv_money_coupon.setText("-"+(b2.divide(b3,2,BigDecimal.ROUND_HALF_UP)).floatValue());
                            tv_money_true.setText("¥"+(b1.subtract(b2)).divide(b3)+"");
                        }

                        switch (type) {
                            case 0:
                                type = 1;
                                setTitleText("待领取");
                                Log.i("开始设置", "在这里开始");
                                statusIcon.setImageResource(R.drawable.bangbang_ico_dlq);// 图标
                                taskStatus.setText("等待领取中");// 图标右边的任务状态
                                statusIconTi.setVisibility(View.GONE);// 图标右边的提示
                                //已经在倒计时的时候不再开启计时
                                if (!taskTime.isRun()) {
                                    taskTime.setTimes(getStandardDate(publishTaskDetails.getExpiredTime(), publishTaskDetails.getNowTime()));
                                    taskTime.start();
                                    taskTime.setVisibility(View.VISIBLE);
                                }

                                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                                taskBefore.setTextColor(0xff47c433);// 待领取变色
                                // 服务中taskDoing
                                // 已完成taskDone
                                // 待支付taskPaying
                                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                                payTI.setVisibility(View.GONE);// 按钮下的提示信息区
                                taskWorkr.setVisibility(View.GONE);// 帮忙人信息区

                                // 任务详情
                                SetTaskDetails(publishTaskDetails.getItems(), publishTaskDetails.getTotal_amount(),
                                        publishTaskDetails.getType());
                                // 付款详情
                                SetTaskPayDetails(publishTaskDetails.getTradeNo(), publishTaskDetails.getOrderFrom(),
                                        publishTaskDetails.getPayTime());

                                popWin = true;
                                break;
                            default:

                                SetDetails(publishTaskDetails);
                                break;
                        }

                        if (drawable != null) {
                            drawable.stop();
                            side_lecture_detail_loading.setVisibility(View.GONE);
                        }
                        for_help_status_loading_error.setVisibility(View.GONE);
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated method stub
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(B_Side_Befriend_C1_Task_Status_Details.this, msg);
                        if (drawable != null) {
                            drawable.stop();
                            side_lecture_detail_loading.setVisibility(View.GONE);
                            Log.i("显示错误页面", "位置1");
                            for_help_status_loading_error.setVisibility(View.VISIBLE);
                            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                        }
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                    }
                });

    }

    static long timess;

    public static long getStandardDate(String timeStr, String timeNow) {
        Log.i("剩余时间", "服务器时间" + timeNow + "过期时间" + timeStr);
        long t = Long.parseLong(timeStr);
        long n = Long.parseLong(timeNow);
        long time = t - n;
        return time;
    }

    /**
     * 设置视图
     *
     * @param publishTaskDetails 我发布的任务详情实体类
     */
    public void SetDetails(Befriend_Publish_Task_Details publishTaskDetails) {
        Log.i("详情", publishTaskDetails.toString());
        // 付款详情
        linePayOrder.setVisibility(View.VISIBLE);
        SetTaskPayDetails(publishTaskDetails.getTradeNo(), publishTaskDetails.getOrderFrom(),
                publishTaskDetails.getPayTime());
        // 任务详情
        SetTaskDetails(publishTaskDetails.getItems(), publishTaskDetails.getTotal_amount(),
                publishTaskDetails.getType());
        // 任务状态：1_等待认领，2_已认领，3_待打款，4_已打款，5_任务超期， 6_被举报，7_服务方取消超期，8_已完结,，9_发起人取消后
        switch (publishTaskDetails.getTaskStatus()) {
            case "1":// 等待认领
                setTitleText("待领取");
                statusIcon.setImageResource(R.drawable.bangbang_ico_dlq);// 图标
                taskStatus.setText("等待领取中");// 图标右边的任务状态
                statusIconTi.setVisibility(View.GONE);// 图标右边的提示
                //已经在倒计时的时候不再开启计时
                if (!taskTime.isRun()) {
                    taskTime.setTimes(getStandardDate(publishTaskDetails.getExpiredTime(), publishTaskDetails.getNowTime()));
                    taskTime.start();
                    ;
                    taskTime.setVisibility(View.VISIBLE);
                }

                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                taskBefore.setTextColor(0xff47c433);// 待领取变色 服务中taskDoing
                // 已完成taskDone 待支付taskPaying
                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.GONE);// 按钮下的提示信息区
                taskWorkr.setVisibility(View.GONE);// 帮忙人信息区
                popWin = true;
                break;
            case "2":// 服务中
                setTitleText("服务中");
                statusIcon.setImageResource(R.drawable.bangbang_ico_fwz);// 图标
                taskStatus.setText("正在服务中");// 图标右边的任务状态
                statusIconTi.setVisibility(View.GONE);// 图标右边的提示
                taskTime.setVisibility(View.GONE);// 剩余时间
                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                taskBefore.setTextColor(0xff47c433);// 待领取变色 服务中taskDoing
                // 已完成taskDone 待支付taskPaying
                taskDoing.setTextColor(0xff47c433);
                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.GONE);// 按钮下的提示信息区
                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区
                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());
                popWin = false;
                break;
            case "3":// 对方完成
                setTitleText("待确认付款");
                statusIcon.setImageResource(R.drawable.bangbang_ico_dfk);// 图标
                taskStatus.setText("待确认付款");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText("若48h仍未确认，平台自动付款。");


                //已经在倒计时的时候不再开启计时
                if (!taskTime.isRun()) {
                    taskTime.setTimes(getStandardDate(publishTaskDetails.getAutomaticPayTime(), publishTaskDetails.getNowTime()));
                    taskTime.start();
                    taskTime.setVisibility(View.VISIBLE);
                }
                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                taskBefore.setTextColor(0xff47c433);// 待领取变色 服务中taskDoing
                // 已完成taskDone 待支付taskPaying
                taskDoing.setTextColor(0xff47c433);
                taskDone.setTextColor(0xff47c433);
                payBtn.setText("确认并付款");
                payBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub

                        final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Befriend_C1_Task_Status_Details.this,
                                R.style.Theme_GeneralDialog);
                        upDateDialog.setTitle("确认付款");
                        upDateDialog.setContent("确认完成后，任务酬金才会到达服务方账户。");
                        upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                DoneWorkTask(5);
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.show();
                    }
                });
                payBtn.setBackgroundResource(R.drawable.bangbang_btn_fukuan);
                payBtn.setVisibility(View.VISIBLE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("对方仍未完成？");
                tishi02.setText("冻结自动付款");
                tishi02.setVisibility(View.VISIBLE);
                tishi02.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub
                        final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Befriend_C1_Task_Status_Details.this,
                                R.style.Theme_GeneralDialog);
                        upDateDialog.setTitle("冻结自动付款");
                        upDateDialog.setContent("冻结后，只有您主动完成确认，酬金才会到达服务方账户。");
                        upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DoneWorkTask(4);
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.show();


                    }
                });
                tishi02.setTextColor(0xff47c433);
                tishi03.setVisibility(View.GONE);

                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区
                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());

                popWin = false;
                break;
            case "4":// 已打款
                setTitleText("已付款");
                statusIcon.setImageResource(R.drawable.bangbang_ico_ysk);// 图标
                taskStatus.setText("已付款");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText(times(publishTaskDetails.getAgreePayTime()));
                taskTime.setVisibility(View.GONE);
                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                taskBefore.setTextColor(0xff47c433);// 待领取变色 服务中taskDoing
                // 已完成taskDone 待支付taskPaying
                taskDoing.setTextColor(0xff47c433);
                taskDone.setTextColor(0xff47c433);
                taskPaying.setTextColor(0xff47c433);

                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("平台已将酬金转入对方账户");
                tishi02.setVisibility(View.GONE);
                tishi03.setVisibility(View.GONE);

                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区

                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());
                popWin = false;
                break;
            case "5":// 任务超期
                setTitleText("已失效");
                statusIcon.setImageResource(R.drawable.bangbang_ico_sx);// 图标
                taskStatus.setText("任务自动失效");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText(times(publishTaskDetails.getExpiredTime()));
                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示
                taskTime.setVisibility(View.GONE);
                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("任务无人领取自动失效，酬金已原路退回。");
                tishi02.setVisibility(View.GONE);
                tishi03.setVisibility(View.GONE);

                taskWorkr.setVisibility(View.GONE);// 帮忙人信息区
                popWin = false;
                break;
            case "6":// 被举报
                setTitleText("任务违规");
                statusIcon.setImageResource(R.drawable.bangbang_ico_qx);// 图标
                taskStatus.setText("任务违规");// 图标右边的任务状态
                statusIconTi.setVisibility(View.GONE);// 图标右边的提示
                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示
                taskTime.setVisibility(View.GONE);
                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("违规原因：");
                tishi02.setText(publishTaskDetails.getMisdeedType() + "。");//////////////////////////////////////////////////////////////////////////////
                tishi02.setTextColor(0xff47c433);
                tishi03.setText("酬金已原路退回。");

                taskWorkr.setVisibility(View.GONE);// 帮忙人信息区
                popWin = true;
                break;
            case "7":// 服务方取消
                setTitleText("已失效");
                statusIcon.setImageResource(R.drawable.bangbang_ico_qx);// 图标
                taskStatus.setText("任务被取消已失效");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText(times(publishTaskDetails.getCancelTime()));
                taskTime.setVisibility(View.GONE);// 剩余时间
                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示

                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.GONE);// 按钮下的提示信息区
                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区

                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());
                popWin = false;
                break;
            case "8":// 已完成、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、、

                statusIcon.setImageResource(R.drawable.bangbang_ico_ysk);// 图标
                taskStatus.setText("任务完成");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText(times(publishTaskDetails.getSubmitTime()));
                taskTime.setVisibility(View.GONE);
                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示

                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区

                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区

                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());
                popWin = false;
                break;
            case "9":// 发起人取消
                setTitleText("已取消");
                statusIcon.setImageResource(R.drawable.bangbang_ico_qx);// 图标
                taskStatus.setText("您已取消任务");// 图标右边的任务状态
                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                statusIconTi.setText(times(publishTaskDetails.getCancelTime()));
                taskTime.setVisibility(View.GONE);// 剩余时间
                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示

                payBtn.setVisibility(View.GONE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("任务酬金已原路退回。");
                tishi02.setVisibility(View.GONE);
                tishi02.setVisibility(View.GONE);
                tishi03.setVisibility(View.GONE);

                taskWorkr.setVisibility(View.GONE);// 帮忙人信息区

                popWin = false;
                break;
            case "10":// 发起人冻结任务
                setTitleText("待确认付款");
                statusIcon.setImageResource(R.drawable.bangbang_ico_dfk);// 图标
                taskStatus.setText("待确认付款");// 图标右边的任务状态
                statusIconTi.setVisibility(View.GONE);// 图标右边的提示
                taskTime.setVisibility(View.GONE);// 剩余时间
                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                taskBefore.setTextColor(0xff47c433);// 待领取变色 服务中taskDoing
                // 已完成taskDone 待支付taskPaying
                taskDoing.setTextColor(0xff47c433);
                taskDone.setTextColor(0xff47c433);
                payBtn.setText("确认并付款");
                payBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        // TODO Auto-generated method stub

                        final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Befriend_C1_Task_Status_Details.this,
                                R.style.Theme_GeneralDialog);
                        upDateDialog.setTitle("确认付款");
                        upDateDialog.setContent("确认完成后，任务酬金才会到达服务方账户。");
                        upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DoneWorkTask(5);
                                upDateDialog.cancel();
                            }
                        });
                        upDateDialog.show();
                    }
                });
                payBtn.setBackgroundResource(R.drawable.bangbang_btn_fukuan);
                payBtn.setVisibility(View.VISIBLE);// 待确认付款按钮
                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                tishi01.setText("已冻结平台自动付款");
                tishi02.setVisibility(View.GONE);
                tishi02.setVisibility(View.GONE);
                tishi03.setVisibility(View.GONE);

                taskWorkr.setVisibility(View.VISIBLE);// 帮忙人信息区
                // 帮忙人信息
                SetTaskWorkDetails(publishTaskDetails.getWorkCreateUsername(), publishTaskDetails.getWorkSex(),
                        publishTaskDetails.getWorkPhone(), publishTaskDetails.getWorkCreateSchoolName(),
                        publishTaskDetails.getWorkCreateTime());

                popWin = false;
                break;
        }
    }

    /**
     * 我领取的任务
     *
     * @param task_id 任务id
     */
    private void SetPickTaskDetails(String task_id) {
        side_lecture_detail_loading.setVisibility(View.VISIBLE);
        drawable.start();
        A_0_App.getApi().getPickTaskDetails(B_Side_Befriend_C1_Task_Status_Details.this, A_0_App.USER_TOKEN, task_id,
                new InterPickTaskStatus() {

                    @Override
                    public void onSuccess(Befriend_Pick_Task_Details pickTaskDetails) {
                        // TODO Auto-generated method stub
                        /*******
                         * 页面上部判断显示
                         */
                        taskWorkr.setVisibility(View.VISIBLE);// 发布人信息区
                        // 发布人信息
                        SetTaskCreatorDetails(pickTaskDetails.getCreateUsername(), pickTaskDetails.getSex(),
                                pickTaskDetails.getCreatorPhone(), pickTaskDetails.getSchoolName(),
                                pickTaskDetails.getCreateTime());

                        // 任务详情
                        SetPickTaskDetails(pickTaskDetails.getItems(), pickTaskDetails.getTotalAmount(),
                                pickTaskDetails.getType());
                        // 付款详情
                        linePayOrder.setVisibility(View.GONE);

                        switch (pickTaskDetails.getTaskWorkStatus()) {
                            case "0":
                                setTitleText("待完成");
                                Log.i("开始设置", "在这里开始");
                                statusIcon.setImageResource(R.drawable.bangbang_ico_dwc);// 图标
                                taskStatus.setText("任务待完成");// 图标右边的任务状态
                                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                                statusIconTi.setText(times(pickTaskDetails.getWorkCreateTime()));
                                taskTime.setVisibility(View.GONE);
                                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                                taskBefore.setTextColor(0xff47c433);// 待领取变色
                                // 服务中taskDoing
                                // 已完成taskDone
                                // 待支付taskPaying
                                taskBefore.setText("待完成");
                                taskDoing.setText("  一  已完成");
                                taskDone.setText("  一  已收款");
                                taskPaying.setVisibility(View.GONE);
                                payBtn.setVisibility(View.VISIBLE);// 待确认付款按钮
                                payBtn.setText("确认已完成");
                                payBtn.setBackgroundResource(R.drawable.bangbang_btn_jubao_hover);
                                payBtn.setOnClickListener(new View.OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        // TODO Auto-generated method stub


                                        final B_Side_Befriend_B0_Take_Result_Dialog b0_Take_Result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                                                B_Side_Befriend_C1_Task_Status_Details.this, R.style.Theme_dim_Dialog, 13, null);
                                        b0_Take_Result_Dialog.show();
                                        b0_Take_Result_Dialog
                                                .setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                                                    @Override
                                                    public void doConfirm() {
                                                        // TODO Auto-generated method stub
                                                        b0_Take_Result_Dialog.dismiss();
                                                        DoneWorkTask(3);
                                                    }

                                                    @Override
                                                    public void doCancel() {
                                                        // TODO Auto-generated method stub
                                                        b0_Take_Result_Dialog.dismiss();

                                                    }
                                                });

                                    }
                                });
                                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                                tishi01.setText("请及时完成任务，完成后请点击");
                                tishi02.setVisibility(View.VISIBLE);
                                tishi02.setText("【确认已完成】");
                                tishi03.setVisibility(View.GONE);

                                popWin = true;
                                break;
                            case "1":
                                setTitleText("待收款");
                                Log.i("开始设置", "在这里开始");
                                statusIcon.setImageResource(R.drawable.bangbang_ico_dsk);// 图标
                                taskStatus.setText("待收款");// 图标右边的任务状态
                                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                                statusIconTi.setText(times(pickTaskDetails.getSubmitTime()));
                                taskTime.setVisibility(View.GONE);
                                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                                taskBefore.setTextColor(0xff47c433);// 待领取变色
                                // 服务中taskDoing
                                // 已完成taskDone
                                // 待支付taskPaying
                                taskBefore.setText("待完成");
                                taskDoing.setText("  一  已完成");
                                taskDoing.setTextColor(0xff47c433);
                                taskDone.setText("  一  已收款");
                                taskPaying.setVisibility(View.GONE);
                                payBtn.setVisibility(View.GONE);// 待确认付款按钮

                                payTI.setVisibility(View.GONE);// 按钮下的提示信息区

                                popWin = false;
                                break;
                            case "2":
                                setTitleText("已收款");
                                Log.i("开始设置", "在这里开始");
                                statusIcon.setImageResource(R.drawable.bangbang_ico_dsk);// 图标
                                taskStatus.setText("任务完成，已收款");// 图标右边的任务状态
                                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                                statusIconTi.setText(times(pickTaskDetails.getAgreePayTime()));
                                taskTime.setVisibility(View.GONE);
                                taskStatusDo.setVisibility(View.VISIBLE);// 任务状态进度提示
                                taskBefore.setTextColor(0xff47c433);// 待领取变色
                                // 服务中taskDoing
                                // 已完成taskDone
                                // 待支付taskPaying
                                taskBefore.setText("待完成");
                                taskDoing.setText("  一  已完成");
                                taskDoing.setTextColor(0xff47c433);
                                taskDone.setText("  一  已收款");
                                taskDone.setTextColor(0xff47c433);
                                taskPaying.setVisibility(View.GONE);
                                payBtn.setVisibility(View.GONE);// 待确认付款按钮

                                payTI.setVisibility(View.VISIBLE);// 按钮下的提示信息区
                                tishi01.setText("酬金已转入您的");
                                tishi02.setVisibility(View.VISIBLE);
                                tishi02.setText("账户余额。");
                                tishi03.setVisibility(View.GONE);
                                popWin = false;
                                break;
                            case "9":
                                setTitleText("您已取消");
                                Log.i("开始设置", "在这里开始");
                                statusIcon.setImageResource(R.drawable.bangbang_ico_qx);// 图标
                                taskStatus.setText("您已取消任务");// 图标右边的任务状态
                                statusIconTi.setVisibility(View.VISIBLE);// 图标右边的提示
                                statusIconTi.setText(times(pickTaskDetails.getCancelTime()));
                                taskTime.setVisibility(View.GONE);
                                taskStatusDo.setVisibility(View.GONE);// 任务状态进度提示

                                taskPaying.setVisibility(View.GONE);
                                payBtn.setVisibility(View.GONE);// 待确认付款按钮

                                payTI.setVisibility(View.GONE);// 按钮下的提示信息区
                                tishi01.setVisibility(View.GONE);
                                tishi02.setVisibility(View.GONE);
                                tishi03.setVisibility(View.GONE);
                                popWin = false;
                                break;
                        }

                        if (drawable != null) {
                            drawable.stop();
                            side_lecture_detail_loading.setVisibility(View.GONE);
                            for_help_status_loading_error.setVisibility(View.GONE);
                        }
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated method stub
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(B_Side_Befriend_C1_Task_Status_Details.this, msg);
                        if (drawable != null) {
                            drawable.stop();
                            side_lecture_detail_loading.setVisibility(View.GONE);
                            Log.i("显示错误页面", "位置2");
                            for_help_status_loading_error.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                    }
                });

    }

    /*****
     * 设置帮忙人详情
     *
     * @param workrName  帮忙人姓名
     * @param workrSex   帮忙人性别
     * @param workrPhone 帮忙人电话
     * @param school     帮忙人学校
     * @param workTime   领取任务时间
     */

    private void SetTaskWorkDetails(final String workrName, String workrSex, final String workrPhone, String school,
                                    String workTime) {
        // TODO Auto-generated method stub
        taskWorkrName.setText("帮忙人：" + workrName);
        switch (workrSex) {
            case "0":
                WorkrSex.setImageResource(R.drawable.bangbang_ico_xingbienv);
                break;
            case "1":
                WorkrSex.setImageResource(R.drawable.bangbang_ico_xingbienan);
                break;
        }

        workrSch.setText(school);
        workrTime.setText(times(workTime));
        createrTime.setText("领取时间");
        WorkrPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                telephone = workrPhone;
                A_0_App.getInstance().callSb(B_Side_Befriend_C1_Task_Status_Details.this, workrName, workrPhone, new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Side_Befriend_C1_Task_Status_Details.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });

                    }
                });

            }
        });
    }

    String telephone;
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
        PubMehods.callPhone(B_Side_Befriend_C1_Task_Status_Details.this, telephone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Side_Befriend_C1_Task_Status_Details.this);
    }

    /**
     * 设置发布人详情
     *
     * @param workrName  发布人姓名
     * @param workrSex   发布人性别
     * @param workrPhone 发布人电话
     * @param school     发布人学校
     * @param workTime   任务发布时间
     */
    private void SetTaskCreatorDetails(final String workrName, String workrSex, final String workrPhone, String school,
                                       String workTime) {
        // TODO Auto-generated method stub
        taskWorkrName.setText("发布人：" + workrName);
        switch (workrSex) {
            case "0":
                WorkrSex.setImageResource(R.drawable.bangbang_ico_xingbienv);
                break;
            case "1":
                WorkrSex.setImageResource(R.drawable.bangbang_ico_xingbienan);
                break;
        }

        workrSch.setText(school);
        workrTime.setText(times(workTime));
        createrTime.setText("发起时间");
        WorkrPhone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                telephone = workrPhone;
                A_0_App.getInstance().callSb(B_Side_Befriend_C1_Task_Status_Details.this, workrName, workrPhone, new A_0_App.PhoneCallBack() {
                    @Override
                    public void sPermission() {
                        PermissionGen.needPermission(B_Side_Befriend_C1_Task_Status_Details.this, REQUECT_CODE_CALLPHONE,
                                new String[]{
                                        Manifest.permission.CALL_PHONE
                                });

                    }
                });
            }
        });
    }

    /**
     * 设置订单详情
     *
     * @param order      订单号
     * @param order_from 付款方式
     * @param order_time 付款时间
     */
    private void SetTaskPayDetails(String order, String order_from, String order_time) {
        // TODO Auto-generated method stub
        taskPayOrder.setText(order);
        taskPayOrderFrom.setText(order_from);
        taskPayTime.setText(times(order_time));
    }

    /**
     * 设置任务详情
     *
     * @param iBeans   任务详情
     * @param moneyNum 酬金
     * @param type     任务类型
     */
    private void SetTaskDetails(List<Befriend_Publish_Task_Details.itemsBean> iBeans, String moneyNum, String type) {
        // TODO Auto-generated method stub
        DecimalFormat df = new DecimalFormat("0.00");
        double money = (double) Integer.valueOf(moneyNum) / 100.00;
        taskMoney.setText("￥" + df.format(money));
        Log.i("任务名称类型", type + "");
        switch (type) {
            case "0":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮取：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 5) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }

                break;
            case "1":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮取：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 5) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "2":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮送：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 5) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "3":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮教：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "4":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮修：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "99":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮其他：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void SetPickTaskDetails(List<Befriend_Pick_Task_Details.itemsBean> iBeans, String moneyNum, String type) {
        // TODO Auto-generated method stub

        DecimalFormat df = new DecimalFormat("0.00");
        double money = (double) Integer.valueOf(moneyNum) / 100.00;
        taskMoney.setText("酬金  " + "￥" + df.format(money));
        Log.i("任务名称类型", type + "");
        switch (type) {
            case "0":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮取：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 6) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }

                break;
            case "1":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮取：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 6) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "2":
                taskStartAdd.setVisibility(View.VISIBLE);
                taskEndAdd.setVisibility(View.VISIBLE);
                taskEndPhone.setVisibility(View.VISIBLE);
                taskNamer.setText("帮送：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartAddress.setText(iBeans.get(1).getContent());
                taskEndAddress.setText(iBeans.get(2).getContent());

                taskStartImer.setText(iBeans.get(3).getTitle());
                taskStartIme.setText(times(iBeans.get(3).getContent()));

                taskStartPhoneNums.setText(iBeans.get(4).getTitle());
                taskStartPhoneNum.setText(iBeans.get(4).getContent());
                taskEndPhoneNum.setText(iBeans.get(5).getContent());

                if (iBeans.size() > 6) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(6).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "3":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮教：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "4":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮修：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
            case "99":
                taskStartAdd.setVisibility(View.GONE);
                taskEndAdd.setVisibility(View.GONE);
                taskEndPhone.setVisibility(View.GONE);
                taskNamer.setText("帮其他：");
                taskName.setText(iBeans.get(0).getContent());

                taskStartImer.setText(iBeans.get(1).getTitle());
                taskStartIme.setText(times(iBeans.get(1).getContent()));

                taskStartPhoneNums.setText(iBeans.get(2).getTitle());
                taskStartPhoneNum.setText(iBeans.get(2).getContent());

                if (iBeans.size() > 3) {
                    others.setVisibility(View.VISIBLE);
                    taskOther.setText(iBeans.get(3).getContent());
                } else {
                    others.setVisibility(View.GONE);
                }
                break;
        }
    }

    private SpannableString time_Surplus;

    private SpannableString timeSurplus(String time) {
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        Log.i("时间戳", i + "");
        Date date = new Date(lcc * 1000);
        SimpleDateFormat sd = new SimpleDateFormat("MM/dd HH:mm");
        one = sd.format(date);

        time_Surplus = new SpannableString(one);
        //设置TextView,可以被当做字符串设置给TextView

        return time_Surplus;
    }

    private String one;

    /**
     * 时间戳换算
     */
    private String times(String time) {
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        Log.i("时间戳", i + "");
        Date date = new Date(lcc * 1000);
        SimpleDateFormat sd = new SimpleDateFormat("MM/dd HH:mm");
        one = sd.format(date);
        return one;

    }

    LinearLayout quxiao, kefu, wenti, quxiaoLine;
    PopupWindow statusPopup;

    private void showWindow(View parent) {
        if (statusPopup == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_befriend_task_for_help_task_status_pop, null);
            quxiaoLine = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_quxiao_line);
            quxiao = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_quxiao);
            kefu = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_kefu);
            wenti = (LinearLayout) view.findViewById(R.id.liner_lecture_detail_wenti);
            statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        if (popWin) {
            quxiaoLine.setVisibility(View.VISIBLE);
        } else {
            quxiaoLine.setVisibility(View.GONE);
        }
        statusPopup.setFocusable(true);
        statusPopup.setOutsideTouchable(true);
        statusPopup.setBackgroundDrawable(new BitmapDrawable());
        int x = DensityUtils.dip2px(B_Side_Befriend_C1_Task_Status_Details.this, 125);
        statusPopup.showAsDropDown(parent, -x, A_0_App.getInstance().getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

        quxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {
                    statusPopup.dismiss();
                }
                final B_Side_Befriend_B0_Take_Result_Dialog b0_Take_Result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                        B_Side_Befriend_C1_Task_Status_Details.this, R.style.Theme_dim_Dialog, deleteType, null);
                b0_Take_Result_Dialog.show();
                b0_Take_Result_Dialog
                        .setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                            @Override
                            public void doConfirm() {
                                // TODO Auto-generated method stub
                                // 继续等待
                                // 确认取消
                                b0_Take_Result_Dialog.dismiss();
                                switch (type) {
                                    case 4:
                                        DeleteTask(2);
                                        break;
                                    case 5://
                                        DeleteTask(2);
                                        break;
                                    case 2:// 服务方取消任务
                                        Log.i("服务方取消任务id", task_id);
                                        DeleteTask(2);
                                        break;
                                }
                            }

                            @Override
                            public void doCancel() {
                                // TODO Auto-generated method stub
                                b0_Take_Result_Dialog.dismiss();
                                // 取消任务
                                // 不取消
                                switch (type) {
                                    case 0:
                                        DeleteTask(1);
                                        break;
                                    case 1:// 发布方取消任务
                                        DeleteTask(1);

                                        break;
                                    case 3:
                                        DeleteTask(1);
                                        break;
                                }

                            }
                        });
            }
        });

        kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {
                    statusPopup.dismiss();
                    telephone = "400-007-3298";
                    A_0_App.getInstance().callSb(B_Side_Befriend_C1_Task_Status_Details.this, "客服电话", telephone, new A_0_App.PhoneCallBack() {
                        @Override
                        public void sPermission() {
                            PermissionGen.needPermission(B_Side_Befriend_C1_Task_Status_Details.this, REQUECT_CODE_CALLPHONE,
                                    new String[]{
                                            Manifest.permission.CALL_PHONE
                                    });

                        }
                    });
                }

            }
        });

        wenti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {

                    statusPopup.dismiss();
                    Intent intent = new Intent();
                    intent.setClass(B_Side_Befriend_C1_Task_Status_Details.this, Pub_WebView_Load_Acy.class);
                    intent.putExtra("title_text", "常见问题");
                    intent.putExtra("url_text", AppStrStatic.LINK_USER_BEFRIEND_HELP);
                    intent.putExtra("tag_skip", "1");
                    intent.putExtra("tag_show_refresh_btn", "2");
                    startActivity(intent);
                }

            }
        });
    }

    private void DeleteTask(final int delete) {
        A_0_App.getApi().getDeleteTaskDetails(B_Side_Befriend_C1_Task_Status_Details.this,
                A_0_App.USER_TOKEN, task_id, delete, new InterDeleteTaskStatus() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated
                        // method stub
                        switch (delete) {
                            case 1:
                                final B_Side_Befriend_B0_Take_Result_Dialog b0_Take_Result_Dialog = new B_Side_Befriend_B0_Take_Result_Dialog(
                                        B_Side_Befriend_C1_Task_Status_Details.this, R.style.Theme_dim_Dialog, 10, null);
                                b0_Take_Result_Dialog.show();
                                b0_Take_Result_Dialog
                                        .setClicklistener(new B_Side_Befriend_B0_Take_Result_Dialog.ClickListenerInterface() {

                                            @Override
                                            public void doConfirm() {
                                                // TODO Auto-generated method stub
                                                b0_Take_Result_Dialog.dismiss();
                                            }

                                            @Override
                                            public void doCancel() {
                                                // TODO Auto-generated method stub
                                                getTaskDetails(task_id);
                                                b0_Take_Result_Dialog.dismiss();
                                            }
                                        });
                                break;

                            case 2:
                                SetPickTaskDetails(task_id);
                                break;
                        }


                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated
                        // method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated
                        // method stub
                        if (isFinishing())
                            return;
                        PubMehods.showToastStr(B_Side_Befriend_C1_Task_Status_Details.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated
                        // method stub

                    }
                });
    }

    public void DoneWorkTask(final int a) {

        if (drawable != null) {
            drawable.start();
        }


        A_0_App.getApi().getDeleteTaskDetails(B_Side_Befriend_C1_Task_Status_Details.this, A_0_App.USER_TOKEN, task_id,
                a, new InterDeleteTaskStatus() {

                    @Override
                    public void onSuccess() {
                        // TODO Auto-generated
                        // method stub
                        switch (type) {
                            case 0:
                                getTaskDetails(task_id);
                                break;
                            case 1:
                                getTaskDetails(task_id);
                                break;
                            case 2:
                                SetPickTaskDetails(task_id);
                                break;
                            case 3:
                                getTaskDetails(task_id);
                                break;
                            case 4:
                                SetPickTaskDetails(task_id);
                                break;
                            case 5://领取成功进入
                                SetPickTaskDetails(task_id);
                                break;
                        }

                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated
                        // method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated
                        // method stub
                        if (isFinishing())
                            return;
                        switch (a) {
                            case 5:
                                PubMehods.showToastStr(B_Side_Befriend_C1_Task_Status_Details.this, "支付失败");
                                break;

                            default:
                                break;
                        }

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated
                        // method stub
                    }
                });


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().getExtras() != null) {
            type = getIntent().getIntExtra("type", 99);
            switch (type) {
                case 3:// 推送我发布的任务
                    task_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
                    getTaskDetails(task_id);
                    deleteType = 6;
                    break;
                case 4:// 推送我领取的任务
                    task_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
                    SetPickTaskDetails(task_id);
                    deleteType = 7;
                    break;
            }
        }
    }


    private void initData() {
        // TODO Auto-generated method stub
        // 0 未从发布任务进入
        switch (type) {
            case 0:
                setTitleText("待领取");
                getTaskDetails(task_id);
                deleteType = 6;
                break;
            case 1:// 我发布的任务
                getTaskDetails(task_id);
                deleteType = 6;
                break;
            case 2:// 我领取的任务
                SetPickTaskDetails(task_id);
                deleteType = 7;
                break;
            case 3:// 推送我发布的任务
                getTaskDetails(task_id);
                deleteType = 6;
                break;
            case 4:// 推送我领取的任务
                SetPickTaskDetails(task_id);
                deleteType = 7;
                break;
            case 5:
                SetPickTaskDetails(task_id);
                deleteType = 7;
                break;
        }
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        // TODO Auto-generated method stub
        switch (resId) {
            case BACK_BUTTON:
                finish();
                break;
            case ZUI_RIGHT_BUTTON:
                showWindow(v);
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
                    //A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_0_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Befriend_C1_Task_Status_Details.this,
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
