package com.yuanding.schoolpass;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Exit_Login;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import java.io.File;
import java.text.DecimalFormat;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 上午11:35:32
 *          设置页面
 */
public class B_Account_My_Set_Acy extends A_0_CpkBaseTitle_Navi {

    private TextView tv_set_push, tv_set_blacklist, tv_change_pwd, tv_set_feedback, tv_exit_account,tv_temp_set;
    private RelativeLayout rel_clear_cache, rel_check_update;
    private TextView tv_current_version, tv_cache_size, tv_set_jurisdiction;

    private File rootFolder;
    private File folder;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_setting);

        setTitleText("设置");

        PermissionGen.needPermission(B_Account_My_Set_Acy.this, REQUECT_CODE_READ_EXTERNAL_STORAGE,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });

        //file和string两种文件路径


    }

    private static final int REQUECT_CODE_READ_EXTERNAL_STORAGE = 5;

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

    @PermissionSuccess(requestCode = REQUECT_CODE_READ_EXTERNAL_STORAGE)
    public void requestSDSuccess() {
        rootFolder = new File(Environment.getExternalStorageDirectory().getPath());
        folder = new File(rootFolder + "/" + AppStrStatic.SD_PIC);
        url = rootFolder + "/" + AppStrStatic.SD_PIC;
        initView();
    }

    @PermissionFail(requestCode = REQUECT_CODE_READ_EXTERNAL_STORAGE)
    public void requestSDFailed() {
        A_0_App.getInstance().PermissionToas("存储", B_Account_My_Set_Acy.this);
//		Toast.makeText(B_Account_My_Set_Acy.this, "需要开启存储权限，请手动开启！", Toast.LENGTH_SHORT).show();
        finish();
    }


    private void initView() {
        tv_temp_set=(TextView) findViewById(R.id.tv_temp_set);
        tv_set_push = (TextView) findViewById(R.id.tv_set_push);
        tv_set_blacklist = (TextView) findViewById(R.id.tv_set_blacklist);
        tv_change_pwd = (TextView) findViewById(R.id.tv_change_pwd);
        tv_set_feedback = (TextView) findViewById(R.id.tv_set_feedback);
        tv_exit_account = (TextView) findViewById(R.id.tv_exit_account);
        tv_current_version = (TextView) findViewById(R.id.tv_current_version);
        tv_cache_size = (TextView) findViewById(R.id.tv_cache_size);
        tv_set_jurisdiction = (TextView) findViewById(R.id.tv_set_jurisdiction);

        rel_clear_cache = (RelativeLayout) findViewById(R.id.rel_clear_cache);
        rel_check_update = (RelativeLayout) findViewById(R.id.rel_check_update);

        tv_set_push.setOnClickListener(onclick);
        tv_set_blacklist.setOnClickListener(onclick);
        tv_change_pwd.setOnClickListener(onclick);
        tv_set_feedback.setOnClickListener(onclick);
        tv_exit_account.setOnClickListener(onclick);

        rel_clear_cache.setOnClickListener(onclick);
        rel_check_update.setOnClickListener(onclick);
        tv_set_jurisdiction.setOnClickListener(onclick);
        tv_current_version.setText("当前版本：" + PubMehods.getVerName(getApplicationContext()));
        tv_temp_set.setText("关于"+A_0_App.APP_NAME);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    OnClickListener onclick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            switch (v.getId()) {
                case R.id.tv_set_push:
                    //推送消息
                    startActivity(new Intent(B_Account_My_Set_Acy.this, B_Account_Push_Set_Acy.class));
                    break;
                case R.id.tv_set_blacklist:
                    //黑名单
                    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW) || A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)
                            || A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {
                        PubMehods.showToastStr(B_Account_My_Set_Acy.this, R.string.str_no_certified_not_use);
                    } else {
                        startActivity(new Intent(B_Account_My_Set_Acy.this, B_Account_BlackList_Acy.class));
                    }
                    break;
                case R.id.tv_change_pwd:
                    //修改密码
                    startActivity(new Intent(B_Account_My_Set_Acy.this, B_Account_Modify_Pwd_Acy.class));
                    break;
                case R.id.tv_set_feedback:
                    //意见反馈
                    startActivity(new Intent(B_Account_My_Set_Acy.this, B_Account_FeedBack_Acy.class));
                    break;
                case R.id.rel_clear_cache:
                    //清除缓存,不清除内存信息
                    PubMehods.showToastStr(B_Account_My_Set_Acy.this, "正在处理,请稍候…");
                    deleteCacheFile(folder);
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    tv_cache_size.setText(getPathSize(url));
                    PubMehods.showToastStr(B_Account_My_Set_Acy.this, "清空缓存成功");
                    break;
                case R.id.tv_set_jurisdiction:
                    //运行权限设置
                    String pinpai = "";
                    if (A_0_App.getInstance().getDevice_info().getPinpai() != null) {
                        pinpai = A_0_App.getInstance().getDevice_info().getPinpai();
                    }
                    String url_text = AppStrStatic.LINK_USER_JURISDICTION + pinpai;
                    if (url_text == null || "".equals(url_text)) {
                        PubMehods.showToastStr(B_Account_My_Set_Acy.this, "数据请求错误，请重试");
                        return;
                    }
                    intent.setClass(B_Account_My_Set_Acy.this, Pub_WebView_Load_Other_Acy.class);
                    intent.putExtra("title_text", "运行权限设置");
                    intent.putExtra("url_text", url_text);
                    intent.putExtra("tag_skip", "1");
                    startActivity(intent);
                    break;
                case R.id.rel_check_update:
                    //关于微校邦
                    startActivity(new Intent(B_Account_My_Set_Acy.this, B_Account_MySet_About_Acy.class));
                    break;
                case R.id.tv_exit_account:
                    //退出账号
                    final GeneralDialog upDateDialog = new GeneralDialog(B_Account_My_Set_Acy.this,
                            R.style.Theme_GeneralDialog);
                    upDateDialog.setTitle(R.string.pub_title);
                    upDateDialog.setContent(R.string.put_title_remind_exit);
                    upDateDialog.showLeftButton(R.string.pub_cancel, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upDateDialog.cancel();
                        }
                    });
                    upDateDialog.showRightButton(R.string.pub_sure, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            upDateDialog.cancel();
                            if (A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                                    exit_login();
                                }
                            } else {
                                exit_login_result();
                            }
                        }
                    });
                    upDateDialog.show();
                    break;
                default:
                    break;
            }

        }
    };

    private void exit_login_result() {
        PubMehods.showToastStr(B_Account_My_Set_Acy.this, "退出账号成功");
        startActivity(new Intent(B_Account_My_Set_Acy.this, A_3_0_Login_Acy.class));
        A_0_App.getInstance().clearUserSpInfo(false);
        finish();
        overridePendingTransition(R.anim.animal_push_right_in_normal, R.anim.animal_push_right_out_normal);
    }

    private void exit_login() {
        A_0_App.getInstance().showProgreDialog(B_Account_My_Set_Acy.this, "正在退出", true);
        A_0_App.getApi().exit_login(A_0_App.USER_TOKEN, new Inter_Exit_Login() {
            @Override
            public void onSuccess() {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Account_My_Set_Acy.this);
                exit_login_result();
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
                A_0_App.getInstance().CancelProgreDialog(B_Account_My_Set_Acy.this);
                exit_login_result();
            }

            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub

            }
        });

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

    /*删除自定义下文件夹下中的文件*/
    public static void deleteCacheFile(File file) {
        if (Environment.getExternalStorageState().equals
                (Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                if (file.isFile()) {//文件直接删除
                    file.delete();
                }
                if (file.isDirectory()) {//文件夹
                    File[] childfFiles = file.listFiles();
                    if (childfFiles == null || childfFiles.length == 0) {//文件夹不存在子文件（文件夹）
                        file.delete();
                    } else {//文件夹中存在文件（子文件）
                        for (File chilFile : childfFiles) {
                            deleteCacheFile(chilFile);
                        }
                        file.delete();
                    }
                }
            }
        }
    }

    /* 计算自定义下文件的大小 */
    public static String getPathSize(String path) {
        String flieSizesString = "";
        File file = new File(path.trim());
        long fileSizes = 0;
        if (null != file && file.exists()) {
            if (file.isDirectory()) { // 如果路径是文件夹的时候
                fileSizes = getFileFolderTotalSize(file);
            } else if (file.isFile()) {

                fileSizes = file.length();
            }
        }
        if (fileSizes < 500) {
            flieSizesString = "0B";
        } else {
            flieSizesString = formatFileSizeToString(fileSizes);
        }
        return flieSizesString;
    }

    private static long getFileFolderTotalSize(File fileDir) {
        long totalSize = 0;
        File fileList[] = fileDir.listFiles();
        if (fileList != null && fileList.length > 0) {
            for (int fileIndex = 0; fileIndex < fileList.length; fileIndex++) {
                if (fileList[fileIndex].isDirectory()) {
                    totalSize =
                            totalSize + getFileFolderTotalSize(fileList[fileIndex]);
                } else {
                    if (fileList[fileIndex].getName().equals("journal")) {
                        totalSize = totalSize + 0;

                    } else {
                        totalSize = totalSize + fileList[fileIndex].length();
                    }
                }
            }
        }
        return totalSize;
    }

    private static String formatFileSizeToString(long fileSize) {// 转换文件大小
        String fileSizeString = "";
        DecimalFormat decimalFormat = new DecimalFormat("#0");
        if (fileSize < 1024) {
            fileSizeString = decimalFormat.format((double) fileSize) + "B";
        } else if (fileSize < (1 * 1024 * 1024)) {
            fileSizeString =
                    decimalFormat.format((double) fileSize / 1024) + "K";
        } else if (fileSize < (1 * 1024 * 1024 * 1024)) {
            fileSizeString =
                    decimalFormat.format((double) fileSize / (1 * 1024 * 1024))
                            + "M";
        } else {
            fileSizeString =
                    decimalFormat.format((double) fileSize
                            / (1 * 1024 * 1024 * 1024))
                            + "G";
        }
        return fileSizeString;
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
                            A_0_App.getInstance().showExitDialog(
                                    B_Account_My_Set_Acy.this,
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
    protected void onResume() {
        if (tv_cache_size != null) {
            tv_cache_size.setText(getPathSize(url));
        }
        super.onResume();
    }
}
