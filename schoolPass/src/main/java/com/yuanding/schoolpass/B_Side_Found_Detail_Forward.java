package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.io.Serializable;
import java.util.ArrayList;

import org.xutils.image.ImageOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Lost_List;
import com.yuanding.schoolpass.service.Api.InteFoundDetail;
import com.yuanding.schoolpass.service.Api.InterSideFoundStatus;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.DensityUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 转发失物详情
 */
public class B_Side_Found_Detail_Forward extends A_0_CpkBaseTitle_Navi {
    protected LinearLayout content_img_ll, liner_found;
    protected ImageLoader imageLoader;
    private DisplayImageOptions options, options_photo;
    private Cpk_Side_Lost_List cpk_Side_Lost_List;
    private String image_url = "";
    private TextView tv_detail_name, tv_detail_create_time, tv_detail_school,
            tv_detail_lost_name, tv_detail_lost_place, tv_detail_lost_time,
            tv_detail_phone, tv_detail_lost_desc;
    private CircleImageView iv_detail_photo;
    private Button btn_persion_send_zhitiao, btn_persion_sent,
            btn_persion_info_tel;
    private View mLinerLoadError, mLinerWholeView, side_lecture_detail_loading;
    private String lost_id = "";
    private String title;
    private ArrayList<String> path = new ArrayList<String>();
    private TextView tv_lost_type;
    private ImageOptions bitmapUtils;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_side_found_search_detail);
        setTitleText("失物详情");

        lost_id = getIntent().getStringExtra("lost_id");
        mLinerLoadError = findViewById(R.id.side_repair_detail_load_error);
        mLinerWholeView = findViewById(R.id.liner_repair_detail_whole);
        side_lecture_detail_loading = findViewById(R.id.side_repair_detail_loading);

        home_load_loading = (LinearLayout) side_lecture_detail_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        mLinerLoadError.setOnClickListener(onClick);
        bitmapUtils = A_0_App.getBitmapUtils(B_Side_Found_Detail_Forward.this, R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg, false);
        liner_found = (LinearLayout) findViewById(R.id.liner_found);
        btn_persion_send_zhitiao = (Button) findViewById(R.id.btn_persion_send_zhitiao);
        btn_persion_sent = (Button) findViewById(R.id.btn_persion_sent);
        btn_persion_info_tel = (Button) findViewById(R.id.btn_persion_info_tel);
        tv_detail_name = (TextView) findViewById(R.id.tv_detail_name);
        tv_detail_create_time = (TextView) findViewById(R.id.tv_detail_create_time);
        tv_detail_school = (TextView) findViewById(R.id.tv_detail_school);
        tv_detail_lost_name = (TextView) findViewById(R.id.tv_detail_lost_name);
        tv_detail_lost_place = (TextView) findViewById(R.id.tv_detail_lost_place);
        tv_detail_lost_time = (TextView) findViewById(R.id.tv_detail_lost_time);
        tv_detail_phone = (TextView) findViewById(R.id.tv_detail_phone);
        tv_detail_lost_desc = (TextView) findViewById(R.id.tv_detail_lost_desc);
        iv_detail_photo = (CircleImageView) findViewById(R.id.iv_detail_photo);
        tv_lost_type = (TextView) findViewById(R.id.tv_lost_type);
        btn_persion_send_zhitiao.setOnClickListener(onClick);
        btn_persion_sent.setOnClickListener(onClick);
        btn_persion_info_tel.setOnClickListener(onClick);

        content_img_ll = (LinearLayout) findViewById(R.id.image_container);
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(
                R.drawable.i_default_por_120,
                R.drawable.i_default_por_120,
                R.drawable.i_default_por_120);
        options_photo = A_0_App.getInstance().getOptions(
                R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg);
        getLectureDetail(lost_id);

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

        if (A_0_App.USER_STATUS.equals("2")) {
            showTitleBt(ZUI_RIGHT_BUTTON, true);
        } else {
            showTitleBt(ZUI_RIGHT_BUTTON, false);
        }
    }

    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_persion_sent:
                    A_0_App.getInstance().exit_rongyun(true);
                    RongIM.getInstance().startConversation(
                            B_Side_Found_Detail_Forward.this,
                            Conversation.ConversationType.PRIVATE,
                            cpk_Side_Lost_List.getUniqid(),
                            cpk_Side_Lost_List.getTrue_name());

                    break;
                case R.id.btn_persion_send_zhitiao:
                    if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
                        if (cpk_Side_Lost_List.getStatus().equals("0")) {
                            changeStatus();
                        }
                    }
                    break;
                case R.id.btn_persion_info_tel:
                    phoneName = cpk_Side_Lost_List.getTrue_name();
                    telephone = cpk_Side_Lost_List.getPhone();
                    A_0_App.getInstance().callSb(B_Side_Found_Detail_Forward.this, phoneName, telephone, new A_0_App.PhoneCallBack() {
                        @Override
                        public void sPermission() {
                            PermissionGen.needPermission(B_Side_Found_Detail_Forward.this, REQUECT_CODE_CALLPHONE,
                                    new String[]{
                                            Manifest.permission.CALL_PHONE
                                    });
                        }
                    });
                    break;
                case R.id.side_repair_detail_load_error:
                    showLoadResult(true, false, false);
                    getLectureDetail(lost_id);
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
        PubMehods.callPhone(B_Side_Found_Detail_Forward.this, telephone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Side_Found_Detail_Forward.this);
    }

    private void getLectureDetail(String lost_id) {
        A_0_App.getApi().getFoundDetail(B_Side_Found_Detail_Forward.this,
                A_0_App.USER_TOKEN, lost_id, new InteFoundDetail() {

                    @SuppressLint("NewApi")
                    @Override
                    public void onSuccess(Cpk_Side_Lost_List detail) {
                        if (isFinishing())
                            return;
                        if (detail.getDesc().equals("") || detail.getDesc() == null) {
                            showLoadResult(false, false, true);
                            PubMehods.showToastStr(B_Side_Found_Detail_Forward.this, "信息不存在！");
                        } else {
                            cpk_Side_Lost_List = detail;
                            String uri = cpk_Side_Lost_List.getUser_photo_url();
                            if (iv_detail_photo.getTag() == null) {
                                PubMehods.loadServicePic(imageLoader, uri, iv_detail_photo, options);
                                iv_detail_photo.setTag(uri);
                            } else {
                                if (!iv_detail_photo.getTag().equals(uri)) {
                                    PubMehods.loadServicePic(imageLoader, uri, iv_detail_photo, options);
                                    iv_detail_photo.setTag(uri);
                                }
                            }
                            tv_detail_name.setText(cpk_Side_Lost_List.getTrue_name());
                            tv_detail_create_time.setText(PubMehods.getFormatDate(
                                    cpk_Side_Lost_List.getCreate_time(), "MM/dd  HH:mm"));
                            tv_detail_school.setText(cpk_Side_Lost_List.getSchool_name());
                            tv_detail_lost_name.setText(cpk_Side_Lost_List.getName());
                            tv_detail_lost_place.setText(cpk_Side_Lost_List.getPlace());
                            tv_detail_lost_time.setText(PubMehods.getFormatDate(cpk_Side_Lost_List.getLost_time(), "MM/dd  HH:mm"));
                            tv_detail_phone.setText(cpk_Side_Lost_List.getPhone());
                            tv_detail_lost_desc.setText(cpk_Side_Lost_List.getDesc());
                            if (cpk_Side_Lost_List.getIs_self().equals("1")) {

                                setZuiRightBtn(R.drawable.navigationbar_more_button);
                                if (cpk_Side_Lost_List.getStatus().equals("1")) {
                                    btn_persion_send_zhitiao.setBackground(getResources()
                                            .getDrawable(
                                                    R.drawable.navigationbar_text_button_normal));
                                    btn_persion_send_zhitiao.setVisibility(View.GONE);
                                } else {
                                    btn_persion_send_zhitiao.setVisibility(View.VISIBLE);
                                }

                            } else {

                                setZuiRightBtn(R.drawable.navigationbar_more_share);
                                if (cpk_Side_Lost_List.getStatus().equals("1")) {
                                    liner_found.setVisibility(View.GONE);
                                } else {
                                    liner_found.setVisibility(View.VISIBLE);
                                }

                            }
                            if (cpk_Side_Lost_List.getStatus().equals("1")) {
                                tv_lost_type.setText("完璧归赵");
                                showTitleBt(ZUI_RIGHT_BUTTON, false);
                                setZuiRightBtn(R.drawable.navigationbar_more_button);

                            } else {
                                tv_lost_type.setText("");
                                showTitleBt(ZUI_RIGHT_BUTTON, true);
                                setZuiRightBtn(R.drawable.navigationbar_more_button);
                            }

                            if (cpk_Side_Lost_List.getPhoto_url().length() > 0 && cpk_Side_Lost_List.getPhoto_url() != "") {
                                String photo_urls[] = cpk_Side_Lost_List.getPhoto_url().split(",");
                                for (int i = 0; i < photo_urls.length; i++) {
                                    path.add(photo_urls[i]);
                                }
                                if (photo_urls.length == 1) {
                                    image_url = photo_urls[0];
                                    content_img_ll.removeAllViews();
                                    LinearLayout horizonLayout = new LinearLayout(
                                            B_Side_Found_Detail_Forward.this);
                                    RelativeLayout.LayoutParams params;
                                    ImageView image1 = new ImageView(B_Side_Found_Detail_Forward.this);
                                    float density = getDensity(B_Side_Found_Detail_Forward.this);
                                    WindowManager wm = (WindowManager) B_Side_Found_Detail_Forward.this
                                            .getSystemService(Context.WINDOW_SERVICE);
                                    int width = wm.getDefaultDisplay().getWidth();
                                    float imageLayWidth = width - (10 + 12 + 10 + 50) * density;
                                    params = new RelativeLayout.LayoutParams((int) (imageLayWidth),
                                            (int) (imageLayWidth));
                                    horizonLayout.addView(image1, params);
                                    PubMehods.loadBitmapUtilsPic(bitmapUtils, image1, photo_urls[0]);
                                    image1.setScaleType(ScaleType.CENTER_CROP);
                                    image1.setOnClickListener(new OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                            Intent intent = new Intent(B_Side_Found_Detail_Forward.this, B_Side_Found_BigImage.class);
                                            intent.putStringArrayListExtra("path", path);
                                            intent.putExtra("num", 0);
                                            startActivity(intent);
                                        }
                                    });
                                    content_img_ll.addView(horizonLayout);
                                } else if (photo_urls.length > 1) {
                                    image_url = photo_urls[0];
                                    content_img_ll.removeAllViews();
                                    LinearLayout horizonLayout = new LinearLayout(
                                            B_Side_Found_Detail_Forward.this);
                                    LinearLayout.LayoutParams params;
                                    float density = getDensity(B_Side_Found_Detail_Forward.this);
                                    WindowManager wm = (WindowManager) B_Side_Found_Detail_Forward.this
                                            .getSystemService(Context.WINDOW_SERVICE);
                                    int width = wm.getDefaultDisplay().getWidth();
                                    float imageLayWidth = width - (38) * density;
                                    for (int i = 0; i < photo_urls.length; i++) {
                                        params = new LinearLayout.LayoutParams(
                                                (int) (imageLayWidth / 3 - 8 * density), (int) (imageLayWidth / 3 - 8 * density));
                                        params.setMargins((int) (4 * density), (int) (4 * density),
                                                (int) (4 * density), (int) (4 * density));
                                        ImageView image2 = new ImageView(B_Side_Found_Detail_Forward.this);
                                        image2.setScaleType(ScaleType.CENTER_CROP);
                                        image2.setLayoutParams(params);
                                        final int a = i;
                                        horizonLayout.addView(image2);
                                        PubMehods.loadBitmapUtilsPic(bitmapUtils, image2, photo_urls[i]);
                                        image2.setOnClickListener(new OnClickListener() {

                                            @Override
                                            public void onClick(View arg0) {
                                                Intent intent = new Intent(B_Side_Found_Detail_Forward.this, B_Side_Found_BigImage.class);
                                                intent.putStringArrayListExtra("path", path);
                                                intent.putExtra("num", a);
                                                startActivity(intent);

                                            }
                                        });
                                    }
                                    content_img_ll.addView(horizonLayout);
                                }
                            }
                            showLoadResult(false, true, false);
                        }
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
                        showLoadResult(false, false, true);
                        PubMehods.showToastStr(B_Side_Found_Detail_Forward.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
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

    void changeStatus() {
        if (isFinishing())
            return;
        A_0_App.getInstance().showProgreDialog(
                B_Side_Found_Detail_Forward.this, "提交中，请稍候", true);
        A_0_App.getApi().SideFoundStatus(A_0_App.USER_TOKEN, cpk_Side_Lost_List.getLost_id(),
                new InterSideFoundStatus() {

                    @Override
                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Found_Detail_Forward.this);
                        PubMehods.showToastStr(B_Side_Found_Detail_Forward.this,
                                "提交成功！");
                        finish();
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
                        A_0_App.getInstance().CancelProgreDialog(B_Side_Found_Detail_Forward.this);
                        PubMehods.showToastStr(B_Side_Found_Detail_Forward.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private PopupWindow statusPopup;
    private LinearLayout mLinerCollct, mLinerForward;

    private void showWindow(View parent) {
        if (statusPopup == null) {
            LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater
                    .inflate(R.layout.item_found_detail, null);
            mLinerCollct = (LinearLayout) view
                    .findViewById(R.id.liner_lecture_detail_collect);
            mLinerForward = (LinearLayout) view
                    .findViewById(R.id.liner_lecture_detail_forward);
            statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
        }
        statusPopup.setFocusable(true);
        statusPopup.setOutsideTouchable(true);
        statusPopup.setBackgroundDrawable(new BitmapDrawable());
        int x = DensityUtils.dip2px(B_Side_Found_Detail_Forward.this, 125);
        statusPopup.showAsDropDown(parent, -x, A_0_App.getInstance()
                .getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

        mLinerForward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (statusPopup != null) {
                    statusPopup.dismiss();
                }
                if (cpk_Side_Lost_List.getType().equals("1")) {
                    title = "失物招领";
                } else if (cpk_Side_Lost_List.getType().equals("2")) {
                    title = "寻物启示";
                }
                Intent intent = new Intent(B_Side_Found_Detail_Forward.this,
                        B_Mess_Forward_Select.class);
                intent.putExtra("title", title);
                intent.putExtra("content", cpk_Side_Lost_List.getDesc());
                intent.putExtra("type", "5");
                intent.putExtra("image", image_url);
                intent.putExtra("acy_type", "4");
                intent.putExtra("noticeId", lost_id);
                startActivity(intent);
            }
        });

        mLinerCollct.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // PubMehods.showToastStr(B_Side_Found_Detail_Forward.this,"该功能暂未开放！");
                if (cpk_Side_Lost_List.getIs_self().equals("1")) {
                    if (statusPopup != null) {
                        statusPopup.dismiss();
                    }
                    Intent intent = new Intent(B_Side_Found_Detail_Forward.this,
                            B_Side_Found_Edit_Found.class);

                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable("content",
                            (Serializable) cpk_Side_Lost_List);
                    intent.putExtras(mBundle);
                    Bimp.edit_biaoshi = "1";
                    startActivity(intent);
                } else {
                    PubMehods.showToastStr(B_Side_Found_Detail_Forward.this, "只能编辑本人信息~");
                }


            }
        });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
                overridePendingTransition(R.anim.animal_push_right_in_normal,
                        R.anim.animal_push_right_out_normal);
                break;
            case ZUI_RIGHT_BUTTON:
                if (A_0_App.USER_STATUS.equals("2")) {
                    if (cpk_Side_Lost_List.getIs_self().equals("1")) {
                        showWindow(v);
                    } else {
                        if (cpk_Side_Lost_List.getType().equals("1")) {
                            title = "失物招领";
                        } else if (cpk_Side_Lost_List.getType().equals("2")) {
                            title = "寻物启示";
                        }
                        Intent intent = new Intent(B_Side_Found_Detail_Forward.this,
                                B_Mess_Forward_Select.class);
                        intent.putExtra("title", title);
                        intent.putExtra("content", cpk_Side_Lost_List.getDesc());
                        intent.putExtra("type", "5");
                        intent.putExtra("image", image_url);
                        intent.putExtra("acy_type", "4");
                        intent.putExtra("noticeId", lost_id);
                        startActivity(intent);
                    }

                }
                break;
            default:
                break;
        }

    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    finish();
                    overridePendingTransition(R.anim.animal_push_right_in_normal,
                            R.anim.animal_push_right_out_normal);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Detail_Forward.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_Detail_Forward.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }

    public static float getDensity(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        return dm.density;
    }

    @Override
    protected void onDestroy() {
        drawable.stop();
        drawable = null;
        super.onDestroy();
    }
}
