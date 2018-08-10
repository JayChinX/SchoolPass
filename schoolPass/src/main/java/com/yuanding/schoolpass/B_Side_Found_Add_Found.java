package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.service.Api.InterSideFoundSent;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_UpLoad_Photo;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.FileUtils;
import com.yuanding.schoolpass.utils.NetUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.date.SlideDateTimeListener;
import com.yuanding.schoolpass.view.date.SlideDateTimePicker;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 添加失物招领
 */
public class B_Side_Found_Add_Found extends A_0_CpkBaseTitle_Navi {

    @SuppressLint("SimpleDateFormat")

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final String IMAGE_FILE_NAME = "my.png";
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private MyGridView noScrollgridview;
    private GridAdapter adapter;
    private Intent intent;
    private String lost_id = "";
    private String type;
    private String time;
    private String post_photo_urls = "";
    private EditText et_lost_name, et_lost_place, et_lost_phone, et_lost_desc, et_lost_time;
    public static String temppath = "";
    private int screenWidth;


    private int fileSize = 0;
    private int downloadSize = 0;
    private int FIRST_UPLOAD_IMAGE = 0;// 上传图片
    private String failure_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_side_add_found);
        A_0_App.Notice_more = 3;

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();
        time = sdf.format(new Date());
        et_lost_name = (EditText) findViewById(R.id.et_lost_name);
        et_lost_place = (EditText) findViewById(R.id.et_lost_place);
        et_lost_time = (EditText) findViewById(R.id.et_lost_time);
        et_lost_phone = (EditText) findViewById(R.id.et_lost_phone);
        et_lost_desc = (EditText) findViewById(R.id.et_lost_desc);
        Bimp.found_phone = A_0_App.USER_PHONE;
        intent = getIntent();
        type = intent.getStringExtra("type");
        et_lost_time.setInputType(InputType.TYPE_NULL);//始终不弹出软件盘
        if (type.equals("1")) {
            setTitleText("新增招领");
            et_lost_name.setHint("拾获物品");
            et_lost_place.setHint("拾获地点");
            et_lost_time.setHint("拾获时间");
        } else if (type.equals("2")) {
            setTitleText("寻物启事");
            et_lost_name.setHint("丢失物品");
            et_lost_place.setHint("丢失地点");
            et_lost_time.setHint("丢失时间");
        }
        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setZuiRightBtn(R.drawable.navigationbar_save);
        noScrollgridview = (MyGridView) findViewById(R.id.noScrollgridview);
        noScrollgridview.setSelector(new ColorDrawable(Color.TRANSPARENT));
        adapter = new GridAdapter(this);
        noScrollgridview.setAdapter(adapter);
        adapter.update();

        noScrollgridview.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (arg2 == Bimp.drr.size()) {
                    init();
                    if (A_0_App.biaozhi == Bimp.drr.size()) {
                        photo();
                    }
                } else {
                    if (Bimp.drr.size() >= arg2) {
                        if (A_0_App.biaozhi == Bimp.drr.size()) {
                            //判断图片上传成功或者失败
                            if (A_0_App.map_url.get(Bimp.drr.get(arg2)).contains(Environment.getExternalStorageDirectory() + "")) {
                                failure_url = Bimp.drr.get(arg2);
                                FIRST_UPLOAD_IMAGE = 1;
                                String newStr = A_0_App.map_url.get(Bimp.drr.get(arg2))
                                        .substring(
                                                A_0_App.map_url.get(Bimp.drr.get(arg2))
                                                        .lastIndexOf("/") + 1,
                                                A_0_App.map_url.get(Bimp.drr.get(arg2))
                                                        .lastIndexOf("."));
                                String upload_path = "";
                                upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                                upload_single_failure(upload_path);

                            }

                        }
                    }


                }
            }
        });

        et_lost_place.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
        et_lost_time.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    date_choose();
                }
            }
        });
        et_lost_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                date_choose();
            }
        });
        /**
         * 过滤表情
         */
        et_lost_name.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int index = et_lost_name.getSelectionStart() - 1;
                if (index > 0) {
                    if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
                        Editable edit = et_lost_name.getText();
                        edit.delete(index, index + 1);
                    }
                }
            }
        });
        et_lost_place.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int index = et_lost_place.getSelectionStart() - 1;
                if (index > 0) {
                    if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
                        Editable edit = et_lost_place.getText();
                        edit.delete(index, index + 1);
                    }
                }
            }
        });
        et_lost_desc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                int index = et_lost_desc.getSelectionStart() - 1;
                if (index > 0) {
                    if (PubMehods.isEmojiCharacter(editable.charAt(index))) {
                        Editable edit = et_lost_desc.getText();
                        edit.delete(index, index + 1);
                    }
                }
            }
        });
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

        et_lost_name.setText(Bimp.found_name);
        et_lost_time.setText(Bimp.found_date);
        et_lost_desc.setText(Bimp.found_desc);
        if (et_lost_phone.getText().toString().equals("") || et_lost_phone.getText().toString().equals(A_0_App.USER_PHONE)) {

        } else {
            et_lost_phone.setText(Bimp.found_phone);
        }
        et_lost_place.setText(Bimp.found_place);
    }

    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (arg0.getId()) {
                case R.id.et_lost_time:// 设置定时
                    // date_choose();
                    break;

                default:
                    break;
            }

        }

    };

    void init() {

        Bimp.found_name = et_lost_name.getText().toString();
        Bimp.found_place = et_lost_place.getText().toString();
        Bimp.found_date = et_lost_time.getText().toString();
        Bimp.found_desc = et_lost_desc.getText().toString();
    }

    /**
     * 选择时间监听
     */
    private SlideDateTimeListener listener = new SlideDateTimeListener() {
        @Override
        public void onDateTimeSet(Date date) {
            String s = sdf.format(date);
            if ((date.getTime() - System.currentTimeMillis()) < 0) {
                et_lost_time.setText(s);
            } else {
                et_lost_time.setText(time);
                if (type.equals("1")) {
                    PubMehods.showToastStr(B_Side_Found_Add_Found.this, "拾获时间不能大于当前时间");
                } else {
                    PubMehods.showToastStr(B_Side_Found_Add_Found.this, "丢失时间不能大于当前时间");
                }
            }

        }

        @Override
        public void onDateTimeCancel() {
            et_lost_time.setText(time);
        }
    };

    public void date_choose() {

        new SlideDateTimePicker.Builder(getSupportFragmentManager())
                .setListener(listener).setInitialDate(new Date())
                // .setMinDate(minDate)
                // .setMaxDate(maxDate)
                .setIs24HourTime(true)
                // .setTheme(SlideDateTimePicker.HOLO_DARK)
                .setIndicatorColor(Color.parseColor("#1EC348"))
                .build().show();
    }

    /**
     * 图片
     */
    public void photo() {

        final Dialog dialog = new Dialog(B_Side_Found_Add_Found.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.upload_choosepicture);
        dialog.show();
        Button btn1 = (Button) dialog.findViewById(R.id.btn1);
        Button btn2 = (Button) dialog.findViewById(R.id.btn2);
        Button btn3 = (Button) dialog.findViewById(R.id.btn3);
        btn3.setText("取消");
        btn1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                //拍照 如果是6.0 弹窗  否则  提示
                dialog.dismiss();
                PermissionGen.needPermission(B_Side_Found_Add_Found.this, REQUECT_CODE_CAMERA,
                        new String[]{
                                Manifest.permission.CAMERA
                        });

            }
//					B_Side_Found_Add_Found.temppath=System.currentTimeMillis()+"";
//					Intent intentFromCapture = new Intent(
//							MediaStore.ACTION_IMAGE_CAPTURE);
//					intentFromCapture.putExtra(
//							MediaStore.EXTRA_OUTPUT,
//							Uri.fromFile(new File(Environment.getExternalStorageDirectory(), B_Side_Found_Add_Found.temppath+IMAGE_FILE_NAME)));
//					startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
//					dialog.dismiss();
//
//			}
        });
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(B_Side_Found_Add_Found.this,
                        B_Side_Found_Add_Images.class);
                intent.putExtra("type", type);
                Bimp.add_edit = "2";
                startActivity(intent);
                finish();
            }
        });
        btn3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
    }

    ;

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

    @PermissionSuccess(requestCode = REQUECT_CODE_CAMERA)
    public void requestPhotoSuccess() {
        openPhoto();
    }

    @PermissionFail(requestCode = REQUECT_CODE_CAMERA)
    public void requestPhotoFailed() {
        A_0_App.getInstance().PermissionToas("摄像头", B_Side_Found_Add_Found.this);
    }

    private void openPhoto() {//拍照 方法

        B_Side_Found_Add_Found.temppath = System.currentTimeMillis() + "";
        Intent intentFromCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(
                MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), B_Side_Found_Add_Found.temppath + IMAGE_FILE_NAME)));
        startActivityForResult(intentFromCapture, CAMERA_REQUEST_CODE);
    }

    public static boolean getCameraPermission(Context context) {

        boolean isCanUse = false;
        Camera mCamera = null;
        try {
            mCamera = Camera.open();
            Camera.Parameters mParameters = mCamera.getParameters();
            mCamera.setParameters(mParameters);
            isCanUse = true;
        } catch (Exception e) {
            isCanUse = false;
        } finally {
            // 释放相机，这个必须要，必须要，必须要！！！！
            if (mCamera != null) {
                try {
                    mCamera.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return isCanUse;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {

            File tempFile = new File(
                    Environment.getExternalStorageDirectory() + "/"
                            + B_Side_Found_Add_Found.temppath + IMAGE_FILE_NAME);
            String path = android.os.Environment.getExternalStorageDirectory()
                    + "/" + B_Side_Found_Add_Found.temppath + IMAGE_FILE_NAME;
            if (Bimp.drr.size() < 3) {
                Bimp.drr.add(path);
            }
            init();
            FIRST_UPLOAD_IMAGE = 0;
            adapter.update();


        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public class GridAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private int selectedPosition = -1;
        private boolean shape;

        public boolean isShape() {
            return shape;
        }

        public void setShape(boolean shape) {
            this.shape = shape;
        }

        public GridAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        public void update() {
            loading();
        }

        public int getCount() {
            return 4;
        }

        public Object getItem(int arg0) {

            return null;
        }

        public long getItemId(int arg0) {

            return 0;
        }

        public void setSelectedPosition(int position) {
            selectedPosition = position;
        }

        public int getSelectedPosition() {
            return selectedPosition;
        }

        /**
         * ListView Item
         */
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_add_found_images,
                        parent, false);
                holder = new ViewHolder();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.item_grida_delete);
                holder.relativeLayout = (RelativeLayout) convertView
                        .findViewById(R.id.rela_found);
                holder.pb = (ProgressBar) convertView.findViewById(R.id.pb);
                holder.item_half = (ImageView) convertView
                        .findViewById(R.id.item_half);
                holder.itme_process_text = (TextView) convertView
                        .findViewById(R.id.itme_process_text);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            RelativeLayout.LayoutParams lp_menpiao1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_menpiao1.width = (int) (screenWidth / 3);
            lp_menpiao1.height = (int) (screenWidth / 3);
            lp_menpiao1.setMargins(0, 20, 0, 0);
            holder.pb.setLayoutParams(lp_menpiao1);
            holder.item_half.setLayoutParams(lp_menpiao1);
            holder.image.setLayoutParams(lp_menpiao1);
            holder.itme_process_text.setLayoutParams(lp_menpiao1);
            holder.delete.setPadding(screenWidth / 3 - 30, 0, 0, 0);
            holder.delete.setVisibility(View.GONE);
            if (position == Bimp.bmp.size()) {
                holder.item_half.setVisibility(View.GONE);
                holder.delete.setVisibility(View.GONE);
                holder.image.setImageBitmap(BitmapFactory.decodeResource(
                        getResources(), R.drawable.add));
                if (position == 3) {
                    holder.delete.setVisibility(View.GONE);
                    holder.image.setVisibility(View.GONE);
                }
            } else {

                try {
                    holder.image.setImageBitmap(Bimp.bmp.get(position));
                    holder.item_half.setVisibility(View.VISIBLE);
                    holder.delete.setVisibility(View.VISIBLE);
                    holder.item_half.getBackground().mutate().setAlpha(100);
                } catch (Exception e) {
                    holder.image.setImageBitmap(BitmapFactory.decodeResource(
                            getResources(), R.drawable.side_lost_posi));
                }
            }

            try {
                if (A_0_App.map_url.size() > 0) {
                    if (A_0_App.map_url.get(Bimp.drr.get(position)).equals(
                            "")) {
                        holder.item_half.setVisibility(View.GONE);
                        holder.pb.setVisibility(View.VISIBLE);
                        holder.itme_process_text
                                .setVisibility(View.VISIBLE);
                        holder.pb.setMax(fileSize);
                        holder.pb.setProgress(downloadSize);
                        float price = downloadSize / fileSize;
                        //DecimalFormat decimalFormat=new DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        //String p=decimalFormat.format(price);//format 返回的是字符串

                        float num = (float) (Math.round(price * 100) / 100);//如果要求精确4位就*10000然后/10000
                        holder.itme_process_text.setText(num * 100 + "%");

                    } else if (A_0_App.map_url.get(Bimp.drr.get(position)).contains("http")) {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.pb.setVisibility(View.GONE);
                        holder.item_half.setVisibility(View.GONE);
                        holder.itme_process_text.setVisibility(View.GONE);

                    } else {

                        holder.itme_process_text.setVisibility(View.GONE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.pb.setVisibility(View.GONE);
                        holder.item_half.setVisibility(View.VISIBLE);
                        holder.item_half.setImageBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ico_side_notice));
                    }
                }

            } catch (Exception e) {

            }

            if (A_0_App.biaozhi == Bimp.drr.size()) {
                holder.delete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        try {
                            init();
                            FileUtils.delFile(Bimp.drr.get(position));
                            A_0_App.map_url.remove(Bimp.drr.get(position));
                            Bimp.drr.remove(position);
                            if (Bimp.bmp.size() > position) {
                                Bimp.bmp.remove(position);
                            }
                            Bimp.max = Bimp.max - 1;
                            A_0_App.biaozhi--;
                            loading();
                            String temp_path = "";
                            for (int i = 0; i < Bimp.drr.size(); i++) {
                                temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                            }
//							if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
//								btn_sent.getBackground().setAlpha(255);
//							}
                        } catch (Exception e) {
                            loading();
                        }

                    }
                });
            }

            return convertView;
        }
    }

    public class ViewHolder {
        public ImageView image, item_half;
        public ImageView delete;
        RelativeLayout relativeLayout;
        ProgressBar pb;
        public TextView itme_process_text;
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    Bimp.act_bool = true;

                    adapter.notifyDataSetChanged();

                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void loading() {
        new Thread(new Runnable() {
            public void run() {
                while (true) {
                    if (isFinishing())
                        return;

                    if (Bimp.max == Bimp.drr.size()) {
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);

                        if (A_0_App.map_url.size() >= 0) {
                            if (Bimp.drr.size() > 0 && FIRST_UPLOAD_IMAGE == 0) {
                                if (Bimp.drr.size() > A_0_App.biaozhi) {
                                    FIRST_UPLOAD_IMAGE = 1;
                                    String newStr = Bimp.drr.get(A_0_App.biaozhi).substring(Bimp.drr.get(A_0_App.biaozhi)
                                                    .lastIndexOf("/") + 1,
                                            Bimp.drr.get(A_0_App.biaozhi)
                                                    .lastIndexOf("."));
                                    String upload_path = "";
                                    upload_path = FileUtils.SDPATH + newStr
                                            + ".JPEG";

                                    upload_single(upload_path);

                                }
                            }
                        }
                        break;
                    } else {
                        try {
                            if (Bimp.drr.size() - Bimp.max >= 0) {
                                String path = Bimp.drr.get(Bimp.max);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bitmap bmBitmap = PubMehods.rotateBitmapByDegree(bm, PubMehods.getBitmapDegree(path));
                                Bimp.bmp.add(bmBitmap);
                                String newStr = path.substring(
                                        path.lastIndexOf("/") + 1,
                                        path.lastIndexOf("."));

                                FileUtils.saveBitmap(bm, "" + newStr);
                                Bimp.max += 1;
                                Message message = new Message();
                                message.what = 1;
                                handler.sendMessage(message);
                            }

                        } catch (Exception e) {
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                if (A_0_App.biaozhi == Bimp.drr.size()) {
                    clear();
                }
                break;
            case ZUI_RIGHT_BUTTON:

                if (NetUtils.isConnected(B_Side_Found_Add_Found.this)) {
                    if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                        sent();
                    } else {
//					PubMehods.showToastStr(B_Side_Found_Add_Found.this,"请勿重复操作！");
                    }
                } else {
                    PubMehods.showToastStr(B_Side_Found_Add_Found.this,
                            "请检查您的网络连接");
                }

                break;
            default:
                break;
        }

    }

    private void sent() {

        if (et_lost_name.getText().toString().equals("")) {
            if (type.equals("1")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入拾获物品名称");
            } else if (type.equals("2")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入丢失物品名称");
            }
            return;
        }
        if (et_lost_place.getText().toString().equals("")) {
            if (type.equals("1")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入拾获地址");
            } else if (type.equals("2")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入丢失地址");
            }
            return;
        }
        if (et_lost_time.getText().toString().equals("")) {
            if (type.equals("1")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入拾获时间");
            } else if (type.equals("2")) {
                PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入丢失时间");
            }
            return;
        }
        if (et_lost_phone.getText().toString().length() > 0) {
            Bimp.found_phone = et_lost_phone.getText().toString();
        } else {
            Bimp.found_phone = A_0_App.USER_PHONE;

        }
        if (!PubMehods.isMobileNO(Bimp.found_phone)) {
            PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入正确的手机号");
            return;
        }
        if (et_lost_desc.getText().toString().equals("")) {
            PubMehods.showToastStr(B_Side_Found_Add_Found.this, "请输入描述内容");
            return;
        }
        init();
        if (Bimp.drr.size() > 0 && Bimp.drr.size() == A_0_App.map_url.size()) {
            String temp_path = "";
            for (int i = 0; i < Bimp.drr.size(); i++) {
                temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
            }
            if (A_0_App.biaozhi == Bimp.drr.size()
                    && !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
                post_photo_urls = temp_path.substring(0, temp_path.length() - 1);
                post_data();
            } else {
                dialog_post();
            }
        } else {
            post_data();
        }

    }


    public void dialog_post() {
        final GeneralDialog upDateDialog = new GeneralDialog(
                B_Side_Found_Add_Found.this, R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent("您有图片上传失败，是否继续提交?");
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
                String temp_path = "";
                for (int i = 0; i < Bimp.drr.size(); i++) {
                    if (A_0_App.map_url.get(Bimp.drr.get(i)).contains("http")) {
                        temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                    }

                }
                if (temp_path.length() > 0) {
                    post_photo_urls = temp_path.substring(0, temp_path.length() - 1);
                    post_data();
                }

            }


        });

        upDateDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {

            }
        });
        upDateDialog.show();
    }

    /**
     * 上传图片此处返回就上传图片并添加进度条 一张一张上传
     */

    private void upload_single(final String url) {

        A_0_App.getApi().upload_Photo(new File(url),
                new Inter_UpLoad_Photo() {

                    @Override
                    public void onSuccess(String imageUrl) {
                        if (isFinishing())
                            return;
                        fileSize = 0;
                        downloadSize = 0;
                        if (Bimp.drr.size() > A_0_App.biaozhi) {
                            A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi),
                                    imageUrl);
                            A_0_App.biaozhi++;
                            if (Bimp.drr.size() != A_0_App.biaozhi) {
                                String newStr = Bimp.drr.get(A_0_App.biaozhi).substring(
                                        Bimp.drr.get(A_0_App.biaozhi)
                                                .lastIndexOf("/") + 1,
                                        Bimp.drr.get(A_0_App.biaozhi)
                                                .lastIndexOf("."));
                                String upload_path = "";
                                upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                                upload_single(upload_path);
                            }
                            String temp_path = "";
                            for (int i = 0; i < Bimp.drr.size(); i++) {
                                temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                            }
//							if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
//								btn_sent.getBackground().setAlpha(255);
//							}
                            adapter.update();
                        }

                    }

                    @Override
                    public void onStart() {

                        if (isFinishing())
                            return;
                        fileSize = 0;
                        downloadSize = 0;
                        if (Bimp.drr.size() > A_0_App.biaozhi) {
                            A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), "");
                        }
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                        if (isFinishing())
                            return;
                        fileSize = (int) total;
                        downloadSize = (int) current;
                        if (total == -1)
                            return;

                        adapter.update();
                    }

                    @Override
                    public void onWaiting() {
                        // TODO Auto-generated method stub

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

                        PubMehods.showToastStr(B_Side_Found_Add_Found.this, msg);
                        fileSize = 0;
                        downloadSize = 0;
                        if (Bimp.drr.size() > A_0_App.biaozhi) {
                            A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), url);
                            A_0_App.biaozhi++;
                            if (Bimp.drr.size() != A_0_App.biaozhi) {
                                String newStr = Bimp.drr.get(A_0_App.biaozhi)
                                        .substring(
                                                Bimp.drr.get(A_0_App.biaozhi)
                                                        .lastIndexOf("/") + 1,
                                                Bimp.drr.get(A_0_App.biaozhi)
                                                        .lastIndexOf("."));
                                String upload_path = "";
                                upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                                upload_single(upload_path);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }


                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void upload_single_failure(final String url) {

        A_0_App.getApi().upload_Photo(new File(url),
                new Inter_UpLoad_Photo() {

                    @Override
                    public void onSuccess(String imageUrl) {
                        if (isFinishing())
                            return;
                        fileSize = 0;
                        downloadSize = 0;
                        System.out.println(">>>dd成功>>" + imageUrl);
                        A_0_App.map_url.put(failure_url, imageUrl);
                        String temp_path = "";
                        for (int i = 0; i < Bimp.drr.size(); i++) {
                            temp_path = temp_path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                        }
//						if (A_0_App.biaozhi == Bimp.drr.size()&& !temp_path.contains(Environment.getExternalStorageDirectory() + "")) {
//							btn_sent.getBackground().setAlpha(255);
//						}
                        adapter.update();
                    }

                    @Override
                    public void onStart() {

                        if (isFinishing())
                            return;
                        fileSize = 0;
                        downloadSize = 0;
                        A_0_App.map_url.put(failure_url, "");
                    }

                    @Override
                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                        if (isFinishing())
                            return;
                        fileSize = (int) total;
                        downloadSize = (int) current;
                        if (total == -1)
                            return;

                        adapter.update();
                    }

                    @Override
                    public void onWaiting() {
                        // TODO Auto-generated method stub

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
                        fileSize = 0;
                        downloadSize = 0;
                        System.out.println(">>>dd失败>>");
                        A_0_App.map_url.put(failure_url, url);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    public void post_data() {
        A_0_App.getInstance().showProgreDialog(B_Side_Found_Add_Found.this, "", true);
        A_0_App.getApi().SideFoundSent(A_0_App.USER_TOKEN, lost_id,
                Bimp.found_name, Bimp.found_place, Bimp.found_date, type, Bimp.found_phone, Bimp.found_desc, post_photo_urls,
                new InterSideFoundSent() {

                    public void onSuccess() {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(B_Side_Found_Add_Found.this);
                        Intent intent1 = new Intent("notice");
                        sendBroadcast(intent1);
                        clear();
                        PubMehods.showToastStr(B_Side_Found_Add_Found.this, "提交成功！");
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
                        A_0_App.getInstance().CancelProgreDialog(B_Side_Found_Add_Found.this);
                        PubMehods.showToastStr(B_Side_Found_Add_Found.this, msg);

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    if (A_0_App.biaozhi == Bimp.drr.size()) {
                        clear();
                    }
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    void clear() {
        A_0_App.biaozhi = 0;
        A_0_App.map_url.clear();
        Bimp.add_edit = "";
        Bimp.found_date = "";
        Bimp.found_desc = "";
        Bimp.found_name = "";
        Bimp.found_phone = "";
        Bimp.found_place = "";
        Bimp.bmp.clear();
        Bimp.upload_path.clear();
        Bimp.drr.clear();
        Bimp.max = 0;
        FileUtils.deleteDir();
        overridePendingTransition(R.anim.animal_push_right_in_normal,
                R.anim.animal_push_right_out_normal);
        finish();
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Found_Add_Found.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Found_Add_Found.this, AppStrStatic.kicked_offline());
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