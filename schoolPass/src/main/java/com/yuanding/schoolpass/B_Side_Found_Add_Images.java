package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.io.Serializable;
import java.util.List;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AlbumHelper;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.ImageBucket;
import com.yuanding.schoolpass.utils.ImageBucketAdapter;

/**
 * @author MyPC 选择相册
 */
public class B_Side_Found_Add_Images extends A_0_CpkBaseTitle_Navi {

    List<ImageBucket> dataList;
    GridView gridView;
    ImageBucketAdapter adapter;
    AlbumHelper helper;
    public static final String EXTRA_IMAGE_LIST = "imagelist";
    public static Bitmap bimap;
    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity_rongyun(this);
        setView(R.layout.activity_image_bucket);
        setTitleText("相册");
        PermissionGen.needPermission(B_Side_Found_Add_Images.this, REQUECT_CODE_READ_EXTERNAL_STORAGE,
                new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
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
        helper = AlbumHelper.getHelper();
        helper.init(getApplicationContext());
        type = getIntent().getStringExtra("type");
        initData();
        initView();
    }

    @PermissionFail(requestCode = REQUECT_CODE_READ_EXTERNAL_STORAGE)
    public void requestSDFailed() {
        A_0_App.getInstance().PermissionToas("存储", B_Side_Found_Add_Images.this);
//		Toast.makeText(B_Side_Found_Add_Images.this, "需要开启存储权限，请手动开启！", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void initData() {
        dataList = helper.getImagesBucketList(false);
        for (int i = 0; i < dataList.size(); i++) {
            if (dataList.get(i).bucketName.equals("formats")) {
                dataList.remove(i);

            }
        }
        bimap = BitmapFactory.decodeResource(getResources(),
                R.drawable.side_lost_add);
    }

    private void initView() {
        gridView = (GridView) findViewById(R.id.gridview);
        adapter = new ImageBucketAdapter(B_Side_Found_Add_Images.this, dataList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(B_Side_Found_Add_Images.this,
                        B_Side_Found_Add_ImageGridActivity.class);
                intent.putExtra(B_Side_Found_Add_Images.EXTRA_IMAGE_LIST,
                        (Serializable) dataList.get(position).imageList);
                intent.putExtra("type", type);
                startActivity(intent);
            }

        });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {

        switch (resId) {
            case BACK_BUTTON:
                if (Bimp.act_bool) {
                    if (Bimp.add_edit.equals("1")) {
                        Intent intent = new Intent(B_Side_Found_Add_Images.this,
                                B_Side_Found_Edit_Found.class);
                        intent.putExtra("type", type);
                        intent.putExtra("add_edit", "2");
                        Bimp.edit_biaoshi = "2";
                        startActivity(intent);
                        Bimp.act_bool = false;
                    } else if (Bimp.add_edit.equals("2")) {

                        Intent intent = new Intent(B_Side_Found_Add_Images.this,
                                B_Side_Found_Add_Found.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        Bimp.act_bool = false;

                    } else {
                        Bimp.act_bool = false;
                    }

                }
                this.finish();

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

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Add_Images.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_Add_Images.this, AppStrStatic.kicked_offline());
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
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:

                    if (Bimp.act_bool) {
                        if (Bimp.add_edit.equals("1")) {
                            Intent intent = new Intent(B_Side_Found_Add_Images.this,
                                    B_Side_Found_Edit_Found.class);
                            intent.putExtra("type", type);
                            intent.putExtra("add_edit", "2");
                            Bimp.edit_biaoshi = "2";
                            startActivity(intent);
                            Bimp.act_bool = false;
                        } else if (Bimp.add_edit.equals("2")) {

                            Intent intent = new Intent(B_Side_Found_Add_Images.this,
                                    B_Side_Found_Add_Found.class);
                            intent.putExtra("type", type);
                            startActivity(intent);
                            Bimp.act_bool = false;

                        }

                    }
                    this.finish();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
}