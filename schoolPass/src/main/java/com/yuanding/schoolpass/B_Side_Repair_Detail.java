package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Repair_Detail;
import com.yuanding.schoolpass.service.Api.InteRepairDetail;
import com.yuanding.schoolpass.service.Api.InterSideRepairStatus;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 报修详情
 */
@SuppressLint("NewApi")
public class B_Side_Repair_Detail extends A_0_CpkBaseTitle_Navi {

    private TextView tv_detail_type, tv_detail_title, tv_detail_sort,
            tv_detail_place, tv_detail_time, tv_detail_name, tv_detail_phone,
            tv_detail_desc, tv_reject_desc;
    private Cpk_Side_Repair_Detail cpk_Side_Repair_Detail;
    private View mLinerLoadError, mLinerWholeView, side_lecture_detail_loading;
    private String repair_id;
    private ACache maACache;
    private JSONObject jsonObject;
    private Button btn_persion_send_zhitiao, btn_persion_info_tel;
    private LinearLayout liner_reject, ll_reject;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_side_repair_detail);
        setTitleText("报修详情");
        repair_id = getIntent().getExtras().getString("repair_id");
        mLinerLoadError = findViewById(R.id.side_repair_detail_load_error);
        mLinerWholeView = findViewById(R.id.liner_repair_detail_whole);
        side_lecture_detail_loading = findViewById(R.id.side_repair_detail_loading);
        mLinerLoadError.setOnClickListener(onClick);

        home_load_loading = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        btn_persion_send_zhitiao = (Button) findViewById(R.id.btn_persion_send_zhitiao);
        btn_persion_info_tel = (Button) findViewById(R.id.btn_persion_info_tel);
        btn_persion_send_zhitiao.setOnClickListener(onClick);
        liner_reject = (LinearLayout) findViewById(R.id.liner_reject);
        ll_reject = (LinearLayout) findViewById(R.id.ll_reject);
        tv_detail_type = (TextView) findViewById(R.id.tv_detail_type);
        tv_detail_title = (TextView) findViewById(R.id.tv_detail_title);
        tv_detail_sort = (TextView) findViewById(R.id.tv_detail_sort);
        tv_detail_place = (TextView) findViewById(R.id.tv_detail_place);
        tv_detail_time = (TextView) findViewById(R.id.tv_detail_time);
        tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
        tv_detail_phone = (TextView) findViewById(R.id.tv_detail_phone);
        tv_detail_desc = (TextView) findViewById(R.id.tv_detail_desc);
        tv_reject_desc = (TextView) findViewById(R.id.tv_reject_desc);
        maACache = ACache.get(this);
        jsonObject = maACache
                .getAsJSONObject(AppStrStatic.cache_key_repair_detail + A_0_App.USER_UNIQID + repair_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        } else {
            updateInfo();
        }

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    private void updateInfo() {
        MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
        updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
        private Context context;

        MyAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
        }

        /**
         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
         */
        @Override
        protected Integer doInBackground(Void... params) {
            getLectureDetail(repair_id);
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
            // logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                // goData();
                finish();
                overridePendingTransition(R.anim.animal_push_right_in_normal,
                        R.anim.animal_push_right_out_normal);
                break;
            default:
                break;
        }

    }

    private void getLectureDetail(String article_id) {
        A_0_App.getApi().getRepairDetail(B_Side_Repair_Detail.this,
                A_0_App.USER_TOKEN, repair_id, new InteRepairDetail() {

                    @Override
                    public void onSuccess(Cpk_Side_Repair_Detail detail) {
                        Success(detail);
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true);
                        }
                        PubMehods.showToastStr(B_Side_Repair_Detail.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void showInfo(JSONObject jsonObject) {
        // TODO Auto-generated method stub
        Cpk_Side_Repair_Detail info = getInfo(jsonObject);
        Success(info);
    }

    @SuppressLint("NewApi")
    public void Success(Cpk_Side_Repair_Detail detail) {
        if (isFinishing())
            return;
        havaSuccessLoadData = true;
        cpk_Side_Repair_Detail = detail;
        tv_detail_title.setText(cpk_Side_Repair_Detail.getTitle());
        tv_detail_sort.setText(cpk_Side_Repair_Detail.getType_name());
        tv_detail_place.setText(cpk_Side_Repair_Detail.getPlace());
        tv_detail_time.setText(PubMehods.getFormatDate(
                cpk_Side_Repair_Detail.getCreate_time(), "yyyy/MM/dd  HH:mm"));
        //tv_detail_name.setText(cpk_Side_Repair_Detail.getTrue_name());
        tv_detail_phone.setText(cpk_Side_Repair_Detail.getPhone());
        tv_detail_desc.setText(cpk_Side_Repair_Detail.getDetails());
        tv_reject_desc.setText(cpk_Side_Repair_Detail.getReject_details());
        if (cpk_Side_Repair_Detail.getStatus().equals("0")) {
            tv_detail_type.setText("未受理");
            tv_detail_type.setTextColor(getResources().getColor(
                    R.color.repair_blue));

            btn_persion_send_zhitiao.setBackground(getResources().getDrawable(
                    R.drawable.side_repair_detail_btn_disable_bg));
            btn_persion_info_tel.setVisibility(View.GONE);
        } else if (cpk_Side_Repair_Detail.getStatus().equals("1")) {
            liner_reject.setVisibility(View.VISIBLE);
            ll_reject.setVisibility(View.VISIBLE);
            tv_detail_type.setText("已驳回");
            tv_detail_type.setTextColor(getResources().getColor(
                    R.color.repair_red));
        } else if (cpk_Side_Repair_Detail.getStatus().equals("2")) {
            tv_detail_type.setText("已受理");
            tv_detail_type.setTextColor(getResources().getColor(
                    R.color.GREENlIGHT));
            if (cpk_Side_Repair_Detail.getIs_self().equals("1")) {
                btn_persion_send_zhitiao.setVisibility(View.VISIBLE);
            }

        } else if (cpk_Side_Repair_Detail.getStatus().equals("3")) {
            tv_detail_type.setText("已完工");
            tv_detail_type.setTextColor(getResources().getColor(
                    R.color.repair_grey));
            if (cpk_Side_Repair_Detail.getIs_self().equals("1")) {
                btn_persion_send_zhitiao.setVisibility(View.VISIBLE);
            }
            btn_persion_send_zhitiao.setBackground(getResources().getDrawable(
                    R.drawable.side_repair_detail_btn_disable_bg));
        }
        showLoadResult(false, true, false);
    }

    private Cpk_Side_Repair_Detail getInfo(JSONObject jsonObject) {
        try {
            int state = jsonObject.getInt("status");
            Cpk_Side_Repair_Detail contace = new Cpk_Side_Repair_Detail();
            if (state == 1) {
                Cpk_Side_Repair_Detail mlistContact1 = JSON.parseObject(jsonObject + "",
                        Cpk_Side_Repair_Detail.class);
                contace = JSON.parseObject(mlistContact1.getInfo(),
                        Cpk_Side_Repair_Detail.class);
            }
            return contace;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void goData() {
        Intent it = new Intent();
        it.putExtra("read_count", "0");
        it.putExtra("repley_count", "0");
        setResult(1, it);
    }

    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_lecture_detail_load_error:
                    showLoadResult(true, false, false);
                    getLectureDetail(repair_id);
                    break;
                case R.id.btn_persion_send_zhitiao:
                    if (cpk_Side_Repair_Detail.getStatus().equals("2")) {
                        changeStatus();
                    }
                    break;
                case R.id.btn_persion_info_tel:
                    phoneName = cpk_Side_Repair_Detail.getClass_name();
                    telephone = cpk_Side_Repair_Detail.getPhone();
                    A_0_App.getInstance().callSb(B_Side_Repair_Detail.this, phoneName, telephone, new A_0_App.PhoneCallBack() {
                        @Override
                        public void sPermission() {
                            PermissionGen.needPermission(B_Side_Repair_Detail.this, REQUECT_CODE_CALLPHONE,
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
    };
    String phoneName;
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
        PubMehods.callPhone(B_Side_Repair_Detail.this, telephone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Side_Repair_Detail.this);
    }


    void changeStatus() {
        if (isFinishing())
            return;
        A_0_App.getInstance().showProgreDialog(B_Side_Repair_Detail.this,
                "提交中，请稍候", true);
        A_0_App.getApi().SideRepairStatus(A_0_App.USER_TOKEN, repair_id,
                new InterSideRepairStatus() {

                    @Override
                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        tv_detail_type.setText("已完工");
                        tv_detail_type.setTextColor(getResources().getColor(
                                R.color.repair_grey));
                        cpk_Side_Repair_Detail.setStatus("3");
                        btn_persion_send_zhitiao.setBackground(getResources().getDrawable(
                                R.drawable.side_repair_detail_btn_disable_bg));
                        A_0_App.getInstance().CancelProgreDialog(B_Side_Repair_Detail.this);
                        PubMehods.showToastStr(B_Side_Repair_Detail.this, "已完工！");
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(B_Side_Repair_Detail.this);
                        PubMehods.showToastStr(B_Side_Repair_Detail.this, msg);
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
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Repair_Detail.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Repair_Detail.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }

    private void showLoadResult(boolean loading, boolean show_content,
                                boolean loadFaile) {
        if (show_content)
            mLinerWholeView.setVisibility(View.VISIBLE);
        else
            mLinerWholeView.setVisibility(View.GONE);

        if (loadFaile)
            mLinerLoadError.setVisibility(View.VISIBLE);
        else
            mLinerLoadError.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            side_lecture_detail_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            side_lecture_detail_loading.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        drawable.stop();
        drawable = null;
        super.onDestroy();
    }

}
