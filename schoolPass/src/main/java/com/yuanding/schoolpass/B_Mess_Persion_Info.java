package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ErrorCode;
import io.rong.imlib.RongIMClient.GetBlacklistCallback;
import io.rong.imlib.RongIMClient.OperationCallback;
import io.rong.imlib.model.Conversation;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import org.json.JSONObject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import com.yuanding.schoolpass.bangbang.pay.PayStrStatic;
import com.yuanding.schoolpass.bean.Cpk_Student_Info;
import com.yuanding.schoolpass.service.Api.InterStudentInfo;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.NetUtils;
import com.yuanding.schoolpass.utils.PubMehods;

import com.yuanding.schoolpass.utils.WeiXinUtil;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.ShareDialog;
import com.yuanding.schoolpass.view.ShareDialog.Builder;
import com.yuanding.schoolpass.view.ShareDialog.Builder.DialogBtnClickCallBack;
import com.yuanding.schoolpass.view.toggle.ToggleButton;
import com.yuanding.schoolpass.view.toggle.ToggleButton.OnToggleChanged;

import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneSession;
import static com.tencent.mm.opensdk.modelmsg.SendMessageToWX.Req.WXSceneTimeline;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月16日 下午2:09:03
 *          个人资料
 */
public class B_Mess_Persion_Info extends Activity {

    private View lienr_ifno_view_temp002, view_ifno_view_temp002, view_fudaoyuan, view_ifno_view_loading;
    private Button btn_persion_info_tel, btn_persion_send_zhitiao;
    private ToggleButton tb_get_send_message;
    private String phone, uniqid, trueName;

    private CircleImageView iv_persion_por_info;
    private TextView tv_persion_info_name, tv_persion_info_ren, tv_persion_info_phone, tv_persion_info_banji;

    private RelativeLayout rel_fudaoyuan;
    private TextView tv_organ_title_one, tv_persion_info_fudao;//android:text="所属机构"

    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
    //private BitmapUtils bitmapUtils;
    private int on_off = 0;//1表示在黑名单中

    private LinearLayout liner_la_hei;
    private ACache maACache;
    private JSONObject jsonObject;
    private ImageView iv_rendzheng, btn_back;
    private TextView tv_account_no;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    boolean isInvativeAble = true;  //是否显示邀请 除了2（已认证）
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setContentView(R.layout.activity_mess_person__info);
        //注册到微信
        api = WXAPIFactory.createWXAPI(this, PayStrStatic.WX_APP_ID, true);
        api.registerApp(PayStrStatic.WX_APP_ID);
        //判断是否支持分享
        isInstallWeiXin = api.isWXAppSupportAPI();
        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(R.drawable.i_default_por_120, R.drawable.i_default_por_120, R.drawable.i_default_por_120);
        //bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.i_person_img, R.drawable.i_person_img);
        try {
            uniqid = getIntent().getExtras().getString("uniqid");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //通讯录进入      首页单聊 进入        群信息进入
        if (uniqid == null || uniqid.equals("") || uniqid.length() <= 0) {
            PubMehods.showToastStr(B_Mess_Persion_Info.this, "单聊 uniqid 为空 ,不是异常错误");
            return;
        }

        view_fudaoyuan = findViewById(R.id.view_fudaoyuan);
        view_ifno_view_temp002 = findViewById(R.id.view_ifno_view_temp002);
        lienr_ifno_view_temp002 = findViewById(R.id.lienr_ifno_view_temp002);
        view_ifno_view_loading = findViewById(R.id.view_ifno_view_loading);

        home_load_loading = (LinearLayout) view_ifno_view_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        tb_get_send_message = (ToggleButton) findViewById(R.id.tb_get_send_message);
        btn_persion_info_tel = (Button) findViewById(R.id.btn_persion_info_tel);
        btn_persion_send_zhitiao = (Button) findViewById(R.id.btn_persion_send_zhitiao);

        liner_la_hei = (LinearLayout) findViewById(R.id.liner_la_hei);
        iv_persion_por_info = (CircleImageView) findViewById(R.id.iv_persion_por_info);
        tv_persion_info_name = (TextView) findViewById(R.id.tv_persion_info_name);
        tv_persion_info_ren = (TextView) findViewById(R.id.tv_persion_info_ren);
        tv_persion_info_phone = (TextView) findViewById(R.id.tv_persion_info_phone);
        tv_persion_info_banji = (TextView) findViewById(R.id.tv_persion_info_banji);

        rel_fudaoyuan = (RelativeLayout) findViewById(R.id.rel_fudaoyuan);
        tv_persion_info_fudao = (TextView) findViewById(R.id.tv_persion_info_fudao);
        tv_organ_title_one = (TextView) findViewById(R.id.tv_organ_title_one);
        iv_rendzheng = (ImageView) findViewById(R.id.iv_renzheng);
        tv_account_no = (TextView) findViewById(R.id.tv_account_no);
        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_persion_info_tel.setOnClickListener(onclick);
        btn_persion_send_zhitiao.setOnClickListener(onclick);
        btn_back.setOnClickListener(onclick);

        RongIM.getInstance().getBlacklist(new GetBlacklistCallback() {
            @Override
            public void onSuccess(String[] arg0) {
                if (arg0 != null) {
                    for (int i = 0; i < arg0.length; i++) {
                        if (arg0[i].equals(uniqid)) {
                            tb_get_send_message.setToggleOn();
                            on_off = 1;
                            // btn_persion_send_zhitiao.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onError(ErrorCode arg0) {

            }
        });

        //不接收消息
        tb_get_send_message.setOnToggleChanged(new OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                if (RongIM.getInstance() == null) {
                    PubMehods.showToastStr(B_Mess_Persion_Info.this, "网络错误");
                    return;
                }
                if (on_off == 0) {

                    RongIM.getInstance().addToBlacklist(uniqid, new OperationCallback() {

                        @Override
                        public void onSuccess() {
                            on_off = 1;
                            //btn_persion_send_zhitiao.setVisibility(View.GONE);
                            PubMehods.showToastStr(B_Mess_Persion_Info.this, "加入黑名单成功，您可在设置中修改");
                        }

                        @Override
                        public void onError(ErrorCode arg0) {
                            PubMehods.showToastStr(B_Mess_Persion_Info.this, "网络错误,加入黑名单失败");
                        }
                    });
                } else {
                    RongIM.getInstance().removeFromBlacklist(uniqid, new RongIMClient.OperationCallback() {

                        @Override
                        public void onError(ErrorCode arg0) {
                            PubMehods.showToastStr(B_Mess_Persion_Info.this, "网络错误,剔除黑名单失败");
                        }

                        @Override
                        public void onSuccess() {
                            btn_persion_send_zhitiao.setVisibility(View.VISIBLE);
                            on_off = 0;

                        }
                    });
                }
            }
        });

        //重读个人信息
        view_ifno_view_temp002.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false);
                updateInfo();
            }
        });

        readCache(uniqid);

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

    }


    private void readCache(String uniqid) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_person_info + uniqid);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            int state = jsonObject.optInt("status");
            Cpk_Student_Info student = new Cpk_Student_Info();
            if (state == 1) {
                student = JSON.parseObject(jsonObject + "", Cpk_Student_Info.class);
                showInfo(student);
            } else if (state == 101) {
                showLoadResult(false, false, true);
                PubMehods.showToastStr(B_Mess_Persion_Info.this, "该用户已删除");
                return;
            }
        } else {
            updateInfo();
        }
    }


    private void showInfo(Cpk_Student_Info student) {
        if (isFinishing())
            return;
        if (A_0_App.USER_UNIQID.equals(student.getUniqid())) {
            A_0_App.getInstance().updateUserLoginInfo(student.getTeacher_status(), student.getUniqid(), student.getPhone(),
                    student.getName(), student.getPhoto_url());
        }
        String uri = student.getPhoto_url();
        String user_Status;
        if (student.getType().equals("1")) {//教师
            user_Status = student.getTeacher_status();
        } else {
            user_Status = student.getStudent_status();
        }
        A_0_App.USER_STATUS = user_Status;
        havaSuccessLoadData = true;
        if (iv_persion_por_info.getTag() == null) {
            PubMehods.loadServicePic(imageLoader, uri, iv_persion_por_info, options);
            iv_persion_por_info.setTag(uri);
        } else {
            if (!iv_persion_por_info.getTag().equals(uri)) {
                PubMehods.loadServicePic(imageLoader, uri, iv_persion_por_info, options);
                iv_persion_por_info.setTag(uri);
            }
        }
        //bitmapUtils.display(iv_persion_por_info, student.getPhoto_url());
        phone = student.getPhone();
        trueName = student.getName();
        tv_persion_info_name.setText(trueName);
        if (user_Status.equals("2")) {
            tv_persion_info_ren.setText("已认证");
            iv_rendzheng.setBackgroundResource(R.drawable.icon_account_renzheng);
            //tv_persion_info_ren.setTextColor(getResources().getColor(R.color.col_mess_persion_renzheng));
        } else {
            tv_persion_info_ren.setText("未激活");
            iv_rendzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
            //tv_persion_info_ren.setTextColor(getResources().getColor(R.color.title_no_focus_login));
        }

        //设置是否显示邀请
        if (!user_Status.equals("2")) {
            isInvativeAble = true;
            btn_persion_send_zhitiao.setText("邀请");
            if (null != student.getRec_url() && student.getRec_url().length() > 0) {
                default_shareInviteUrl = student.getRec_url();
            }
            if (null != student.getInvite_sms_content() && student.getInvite_sms_content().length() > 0) {
                sms_share_content = student.getInvite_sms_content();
            }

        } else {
            isInvativeAble = false;
            btn_persion_send_zhitiao.setText("发消息");
        }

        tv_persion_info_phone.setText(phone);

        if (phone.equals(A_0_App.USER_PHONE)) {
            liner_la_hei.setVisibility(View.GONE);
        } else {
            liner_la_hei.setVisibility(View.VISIBLE);
        }
//      PubMehods.showToastStr(B_Mess_Persion_Info.this, student.getType());
        if (student.getType().equals("1")) {//教师
            tv_persion_info_banji.setText(student.getOrgan_name());
            tv_organ_title_one.setText("所属机构");
            view_fudaoyuan.setVisibility(View.GONE);
            rel_fudaoyuan.setVisibility(View.GONE);
            tv_account_no.setText("工号:" + student.getSn_number());

        } else {
            tv_persion_info_banji.setText(student.getClass_name());
            tv_organ_title_one.setText("所属班级");
            view_fudaoyuan.setVisibility(View.VISIBLE);
            rel_fudaoyuan.setVisibility(View.VISIBLE);
            tv_persion_info_fudao.setText(student.getCounsellor());
            tv_account_no.setText("学号:" + student.getStudent_number());
        }
        save();
        showLoadResult(false, true, false);
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
        PubMehods.callPhone(B_Mess_Persion_Info.this, phone);
    }

    @PermissionFail(requestCode = REQUECT_CODE_CALLPHONE)
    public void requestCallPhoneFailed() {
        A_0_App.getInstance().PermissionToas("拨打电话", B_Mess_Persion_Info.this);
    }

    OnClickListener onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                //打电话
                case R.id.btn_persion_info_tel:
//			    PubMehods.callPhone(B_Mess_Persion_Info.this, phone);
                    final GeneralDialog upDateDialog = new GeneralDialog(B_Mess_Persion_Info.this,
                            R.style.Theme_GeneralDialog);
                    upDateDialog.setTitle(trueName);
                    upDateDialog.setContent(phone);
                    upDateDialog.showLeftButton(R.string.pub_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upDateDialog.cancel();
                        }
                    });
                    upDateDialog.showRightButton(R.string.pub_call, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upDateDialog.cancel();
                            PermissionGen.needPermission(B_Mess_Persion_Info.this, REQUECT_CODE_CALLPHONE,
                                    new String[]{
                                            Manifest.permission.CALL_PHONE
                                    });

                        }
                    });
                    upDateDialog.show();


                    break;
                //发消息
                case R.id.btn_persion_send_zhitiao:
                    if (isInvativeAble) {
                        /**
                         * 邀请对话框
                         */

                        ShareDialog.Builder builder = new Builder(B_Mess_Persion_Info.this, "微信好友", "微信朋友圈", "短信邀请", true, new DialogBtnClickCallBack() {

                            @Override
                            public void OnClickWXFriend() {
                                if (isInstallWeiXin) {
                                    shareReq(1);
                                }else {
                                    Toast.makeText(mContext, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void OnClickWXCicle() {
                                if (isInstallWeiXin) {
                                    shareReq(2);
                                }else {
                                    Toast.makeText(mContext, "请先安装或升级你的微信客户端来完成微信分享", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void OnClickCopyUrl() {
//							// 调起短信
                                String number = phone;
                                String body = sms_share_content;
                                Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
                                sendIntent.setData(Uri.parse("smsto:" + number));
                                sendIntent.putExtra("sms_body", body);
                                B_Mess_Persion_Info.this.startActivity(sendIntent);

                            }
                        });
                        builder.create().show();


                    } else if (on_off == 0) {
                        A_0_App.getInstance().exit_rongyun(true);
                        RongIM.getInstance().startConversation(B_Mess_Persion_Info.this,
                                Conversation.ConversationType.PRIVATE, uniqid, trueName);
                    } else {
                        PubMehods.showToastStr(B_Mess_Persion_Info.this, "该联系人已被加入黑名单");
                    }

                    break;


//                if (on_off == 0) {
//                	 A_0_App.getInstance().exit_rongyun(true);
//                    RongIM.getInstance().startConversation(B_Mess_Persion_Info.this,
//                            Conversation.ConversationType.PRIVATE, uniqid, trueName);
//                } else {
//                    PubMehods.showToastStr(B_Mess_Persion_Info.this, "该联系人已被加入黑名单");
//                }
//                break;
                //返回
                case R.id.btn_back:
                    finish();
                    break;
                default:
                    break;
            }

        }
    };

    //微信分享内容和链接
    String default_ShareTitle = "下载"+A_0_App.APP_NAME+"APP 500邦豆等你来领！";
    String default_ShareContent = A_0_App.APP_NAME+" 我的校园微生活";
    String default_shareInviteUrl = "http://www.weixiaobang.com/index.php?m=mobile&c=mobile&a=download";    //微信分享默认的链接

    //短信分享内容
    String sms_share_content = "小伙伴邀请你来体验"+A_0_App.APP_NAME+"APP，官方通知、课程表、老师同学通讯录一个"+A_0_App.APP_NAME+"全搞定，还有新用户大礼，快快下载吧！http://t.cn/RVSxTPk";//短信分享默认的内容


    boolean isInstallWeiXin = false; //是否安装了微信 默认未安装 防止调用失败
    Context mContext;
    private int mTargetScene;
    private static final int THUMB_SIZE = 150;

    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    /**
     * @param sharetype 分享类型
     *                  1 微信朋友 2 微信朋友圈
     */
    private void shareReq(int sharetype) {
        Context mContext = this;
        //检查网络
        if (!NetUtils.isConnected(mContext)) {
            PubMehods.showToastStr(mContext, mContext.getResources().getString(R.string.error_title_net_error));
            return;
        }
        if (1 == sharetype) {
            mTargetScene = WXSceneSession;
        }
        if (2 == sharetype) {
            mTargetScene = WXSceneTimeline;
        }
        if (default_shareInviteUrl.isEmpty()) {
            PubMehods.showToastStr(mContext, mContext.getResources().getString(R.string.str_my_account_invite_sharedisable_prompt));
            return;
        }
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = default_shareInviteUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = default_ShareTitle;
        msg.description = default_ShareContent;
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, 150, 150, true);
        bmp.recycle();
        msg.thumbData = WeiXinUtil.bmpToByteArray(thumbBmp, true, 32);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = mTargetScene;
        api.sendReq(req);

    }


    private void showLoadResult(boolean loading, boolean whole, boolean loadFaile) {
        if (whole)
            lienr_ifno_view_temp002.setVisibility(View.VISIBLE);
        else
            lienr_ifno_view_temp002.setVisibility(View.GONE);

        if (loadFaile)
            view_ifno_view_temp002.setVisibility(View.VISIBLE);
        else
            view_ifno_view_temp002.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            view_ifno_view_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            view_ifno_view_loading.setVisibility(View.GONE);
        }
    }

    private void getContaceInfo() {
        A_0_App.getApi().getStudentInfo(B_Mess_Persion_Info.this, uniqid, A_0_App.USER_TOKEN, new InterStudentInfo() {
            @Override
            public void onSuccess(Cpk_Student_Info student) {
                if (isFinishing())
                    return;

                showInfo(student);
            }
        }, new Inter_Call_Back() {

            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                //
            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                if (msg.equals(AppStrStatic.TAG_USER_IS_DELETE)) {
                    showLoadResult(false, false, true);
                    PubMehods.showToastStr(B_Mess_Persion_Info.this, "该用户已删除");
                } else {
                    if (!havaSuccessLoadData)
                        showLoadResult(false, false, true);
                    PubMehods.showToastStr(B_Mess_Persion_Info.this, msg);
                }
            }

            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub

            }
        });

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

            getContaceInfo();
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {
//	            logD("上传融云数据完毕");
        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
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
                    //A_0_App.getInstance().showExitDialog(B_Mess_Persion_Info.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Persion_Info.this, AppStrStatic.kicked_offline());
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
        // TODO Auto-generated method stub
        //bitmapUtils=null;
        drawable.stop();
        drawable = null;
        super.onDestroy();
    }

    private void save() {

        SharedPreferences.Editor editor = getSharedPreferences(AppStrStatic.SHARE_PREFER_USER_INFO, MODE_PRIVATE).edit();
        editor.putString(AppStrStatic.USER_LOGIN_STATUS, A_0_App.USER_STATUS);
        editor.commit();
    }
}
