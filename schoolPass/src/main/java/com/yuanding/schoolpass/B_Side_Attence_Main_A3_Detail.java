package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONObject;
import org.xutils.x;
import org.xutils.image.ImageOptions;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.baidu.BaiduLocation;
import com.yuanding.schoolpass.baidu.BaiduLocation.IGetLocation;
import com.yuanding.schoolpass.bean.Cpk_Attence_Common;
import com.yuanding.schoolpass.bean.Cpk_Attence_List;
import com.yuanding.schoolpass.bean.Cpk_Attence_List_Time;
import com.yuanding.schoolpass.bean.Cpk_Attence_List_Type;
import com.yuanding.schoolpass.bean.Cpk_Side_Attence_Detail;
import com.yuanding.schoolpass.service.Api.InteAttdenceDetail;
import com.yuanding.schoolpass.service.Api.InterSideAttdenceStatus;
import com.yuanding.schoolpass.service.Api.InterSideSelectAttdenceTime;
import com.yuanding.schoolpass.service.Api.InterUploadAttdenceImage;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_UpLoad_Photo;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.Bimp;
import com.yuanding.schoolpass.utils.FileUtils;
import com.yuanding.schoolpass.utils.NetUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.AlwaysMarqueeTextView;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.MyListView;

/**
 * @author 考勤详情
 */
@SuppressLint("SimpleDateFormat")
public class B_Side_Attence_Main_A3_Detail extends Activity {

    private View side_attence_detail_load_error, liner_attence_detail_whole,
            side_attence_detail_loading;

    private LinearLayout check_liner_image, check_liner_lack, check_liner_late,
            check_liner_leave_early, check_liner_leave;
    private TextView tv_leave, tv_early, tv_late, tv_lack;
    private RelativeLayout liner_position;
    private MyGridView side_check_gridview_lack, side_check_gridview_late,
            side_check_gridview_leave_early, side_check_gridview_leave;
    private MyListView side_check_list;

    private My_GridView_Lack_Aapter my_gridView_lack_adapter;
    private My_GridView_Late_Aapter my_gridView_late_adapter;
    private My_GridView_Leave_Early_Aapter my_gridView_leave_early_adapter;
    private My_GridView_leave_Aapter my_gridView_leave_adapter;
    private My_GridView_Class_Aapter my_gridView_class_adapter;
    private My_GridView_Mark_Aapter my_gridView_mark_adapter;

    private My_ListView_Aapter my_listView_adapter;
    private My_ListView_Time_Aapter my_listView_time_adapter;

    private List<Cpk_Attence_List> list_all = new ArrayList<Cpk_Attence_List>();
    private List<Cpk_Attence_List> list_class = new ArrayList<Cpk_Attence_List>();
    private List<Cpk_Attence_Common> list_lack = new ArrayList<Cpk_Attence_Common>();
    private List<Cpk_Attence_Common> list_late = new ArrayList<Cpk_Attence_Common>();
    private List<Cpk_Attence_Common> list_leave_early = new ArrayList<Cpk_Attence_Common>();
    private List<Cpk_Attence_Common> list_leave = new ArrayList<Cpk_Attence_Common>();
    private List<String> list_type = new ArrayList<String>();
    private List<String> list_type_temp = new ArrayList<String>();
    private List<Cpk_Attence_List_Time> List_Times = new ArrayList<Cpk_Attence_List_Time>();
    private Button btn_position_check;
    private ImageView iv_image_check;
    private String attence_atd_id;
    /**
     * 添加多张图片
     */
    private MyGridView side_check_gridview_image;
    private GridAdapter my_grid_image_adapter;
    public static String temppath = "";

    /**
     * 缺勤 ,迟到 ,请假 ,早退 ,出勤
     */
    private String people_type = "出勤";
    private String st_image_url = "";

    protected ImageLoader imageLoader;
    private DisplayImageOptions options;
    protected ImageOptions bitmapUtils;

    private boolean havaSuccessLoadData = false;
    private ACache maACache;
    private JSONObject jsonObject;
    private Cpk_Side_Attence_Detail attence_detail;

    private String user_id;
    private String post_type;
    private String class_id;
    private Map<String, List<Cpk_Attence_Common>> map = new HashMap<String, List<Cpk_Attence_Common>>();
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    /**
     * 位置考勤与点名标识
     */
    private int position_biaoshi = 0;
    private String time;// 考勤时间段
    private ScrollView check_sll;

    private BaiduLocation baiduLocation;// 百度定位
    private BDLocation location;
    private int screenWidth;

    private LinearLayout liner_titlebar_attence,
            liner_titlebar_zui_right_attence;
    private AlwaysMarqueeTextView tv_titlebar_title_attence;
    private boolean attence_activity_early = false;

    private int back = 0;// 线程返回崩溃 暂时添加
    private int delete_biaoshi = 0;
    private int fileSize = 0;
    private int downloadSize = 0;
    private int first_getimage = 0;// 防止重复图片上传
    private int load = 0;//此参数表示删除或上传的提示
    private String failure_url = "";

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    private String temph = "";
    private int load_finish = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setContentView(R.layout.activity_side_check_detail);

        A_0_App.Notice_more = 3;

        liner_titlebar_attence = (LinearLayout) findViewById(R.id.liner_titlebar_attence);
        liner_titlebar_zui_right_attence = (LinearLayout) findViewById(R.id.liner_titlebar_zui_right_attence);
        tv_titlebar_title_attence = (AlwaysMarqueeTextView) findViewById(R.id.tv_titlebar_title_attence);
        bitmapUtils = A_0_App.getBitmapUtils(this, R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg, false);

        attence_atd_id = getIntent().getExtras().getString("attence_atd_id");
        attence_activity_early = getIntent().getExtras().getBoolean(
                B_Side_Attence_Main_A0.ATTENCE_ACY_EARLY);

        side_attence_detail_load_error = findViewById(R.id.side_attence_detail_load_error);
        liner_attence_detail_whole = findViewById(R.id.liner_attence_detail_whole);
        side_attence_detail_loading = findViewById(R.id.side_attence_detail_loading);

        check_sll = (ScrollView) findViewById(R.id.check_sll);
        check_liner_image = (LinearLayout) findViewById(R.id.check_liner_image);
        check_liner_lack = (LinearLayout) findViewById(R.id.check_liner_lack);
        check_liner_late = (LinearLayout) findViewById(R.id.check_liner_late);
        check_liner_leave_early = (LinearLayout) findViewById(R.id.check_liner_leave_early);
        check_liner_leave = (LinearLayout) findViewById(R.id.check_liner_leave);
        liner_position = (RelativeLayout) findViewById(R.id.rela_position);

        home_load_loading = (LinearLayout) side_attence_detail_loading.findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        tv_leave = (TextView) findViewById(R.id.tv_leave);
        tv_early = (TextView) findViewById(R.id.tv_early);
        tv_late = (TextView) findViewById(R.id.tv_late);
        tv_lack = (TextView) findViewById(R.id.tv_lack);

        side_check_gridview_image = (MyGridView) findViewById(R.id.side_check_gridview_image);
        side_check_gridview_lack = (MyGridView) findViewById(R.id.side_check_gridview_lack);
        side_check_gridview_late = (MyGridView) findViewById(R.id.side_check_gridview_late);
        side_check_gridview_leave_early = (MyGridView) findViewById(R.id.side_check_gridview_leave_early);
        side_check_gridview_leave = (MyGridView) findViewById(R.id.side_check_gridview_leave);

        side_check_list = (MyListView) findViewById(R.id.side_check_list);

        btn_position_check = (Button) findViewById(R.id.btn_position_check);
        iv_image_check = (ImageView) findViewById(R.id.btn_image_check);
        btn_position_check.setOnClickListener(onClick);
        iv_image_check.setOnClickListener(onClick);
        side_attence_detail_load_error.setOnClickListener(onClick);

        side_check_gridview_image.setSelector(new ColorDrawable(
                Color.TRANSPARENT));
        my_grid_image_adapter = new GridAdapter(this);
        Bimp.upload_biao = true;
        my_grid_image_adapter.update();
        side_check_gridview_image.setAdapter(my_grid_image_adapter);

        imageLoader = A_0_App.getInstance().getimageLoader();
        options = A_0_App.getInstance().getOptions(
                R.drawable.i_default_por_120, R.drawable.i_default_por_120,
                R.drawable.i_default_por_120);

        my_gridView_lack_adapter = new My_GridView_Lack_Aapter();
        my_gridView_late_adapter = new My_GridView_Late_Aapter();
        my_gridView_leave_early_adapter = new My_GridView_Leave_Early_Aapter();
        my_gridView_leave_adapter = new My_GridView_leave_Aapter();

        my_listView_time_adapter = new My_ListView_Time_Aapter();

        my_listView_adapter = new My_ListView_Aapter();
        side_check_gridview_lack.setAdapter(my_gridView_lack_adapter);
        side_check_gridview_late.setAdapter(my_gridView_late_adapter);
        side_check_gridview_leave_early
                .setAdapter(my_gridView_leave_early_adapter);
        side_check_gridview_leave.setAdapter(my_gridView_leave_adapter);

        side_check_list.setAdapter(my_listView_adapter);
        attence_detail = new Cpk_Side_Attence_Detail();

        liner_titlebar_zui_right_attence.setVisibility(View.GONE);
        if (A_0_App.USER_STATUS.equals("2")) {
            readCache();
        } else {
            showLoadResult(false, false, true);
        }
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        selectTime();
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        screenWidth = wm.getDefaultDisplay().getWidth();

        liner_titlebar_attence.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //System.exit(0);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        if (load_finish == 0) {
                            clear();
                            finish();
                        }
                    }
                }, 500);

            }
        });

        liner_titlebar_zui_right_attence.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                if (load_finish == 0) {
                    Bimp.act_bool = true;
                    Bimp.drr.clear();
                    Bimp.bmp.clear();
                    Bimp.max = 0;
                    showLoadResult(true, false, false);

                    if (mControlTimer != null)
                        mControlTimer.cancel();
                    startReadData();
                } else {
                    PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, "图片上传中...");
                }
            }

        });

    }

    private void readCache() {
        maACache = ACache.get(B_Side_Attence_Main_A3_Detail.this);
        jsonObject = maACache
                .getAsJSONObject(AppStrStatic.cache_key_attdence_detail
                        + A_0_App.USER_UNIQID + attence_atd_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
            load_finish = 0;
        } else {
            updateInfo();
        }
    }

    @SuppressLint("NewApi")
    private void showInfo(JSONObject jsonObject) {
        if (isFinishing())
            return;

        int state = jsonObject.optInt("status");
        String atdList = jsonObject.optString("info");
        if (state == 1) {
            havaSuccessLoadData = true;
            Cpk_Side_Attence_Detail side_Attence_Detail = JSON.parseObject(
                    atdList, Cpk_Side_Attence_Detail.class);
            attence_detail = side_Attence_Detail;
            list_all.clear();
            list_lack.clear();
            list_late.clear();
            list_leave_early.clear();
            list_leave.clear();
            list_class.clear();
            list_type.clear();
            list_type_temp.clear();
            map.clear();

            if (attence_detail.getPlace().equals("")) {
                tv_titlebar_title_attence
                        .setText(attence_detail.getTitle());
            } else {
                tv_titlebar_title_attence.setText(attence_detail.getTitle()
                        + "(" + attence_detail.getPlace() + ")");
            }

            list_all = JSON.parseArray(attence_detail.getList(),
                    Cpk_Attence_List.class);
            for (int i = 0; i < list_all.size(); i++) {
                if (list_all.get(i).getStatus().equals("2")) {
                    list_lack = JSON.parseArray(list_all.get(i).getData(),
                            Cpk_Attence_Common.class);
                } else if (list_all.get(i).getStatus().equals("3")) {
                    list_late = JSON.parseArray(list_all.get(i).getData(),
                            Cpk_Attence_Common.class);
                } else if (list_all.get(i).getStatus().equals("4")) {
                    list_leave_early = JSON.parseArray(list_all.get(i)
                            .getData(), Cpk_Attence_Common.class);
                } else if (list_all.get(i).getStatus().equals("5")) {
                    list_leave = JSON.parseArray(list_all.get(i).getData(),
                            Cpk_Attence_Common.class);
                } else {
                    list_class.add(list_all.get(i));

                }
            }
            List<Cpk_Attence_List_Type> liCpk_Attence_List_Types = new ArrayList<Cpk_Attence_List_Type>();
            liCpk_Attence_List_Types = JSON.parseArray(
                    attence_detail.getType(), Cpk_Attence_List_Type.class);
            for (int j = 0; j < liCpk_Attence_List_Types.size(); j++) {
                list_type.add(liCpk_Attence_List_Types.get(j).getName());
            }
            for (int j = 0; j < list_class.size(); j++) {
                if (JSON.parseArray(list_class.get(j).getData(),
                        Cpk_Attence_Common.class) != null) {
                    map.put(list_class.get(j).getClass_id(), JSON
                            .parseArray(list_class.get(j).getData(),
                                    Cpk_Attence_Common.class));
                } else {
                    List<Cpk_Attence_Common> list = new ArrayList<Cpk_Attence_Common>();
                    map.put(list_class.get(j).getClass_id(), list);
                }

            }


            if (list_lack.size() > 0) {
                check_liner_lack.setVisibility(View.VISIBLE);
            } else {
                check_liner_lack.setVisibility(View.GONE);
            }
            if (list_late.size() > 0) {
                check_liner_late.setVisibility(View.VISIBLE);
            } else {
                check_liner_late.setVisibility(View.GONE);
            }
            if (list_leave.size() > 0) {
                check_liner_leave.setVisibility(View.VISIBLE);
            } else {
                check_liner_leave.setVisibility(View.GONE);
            }
            if (list_leave_early.size() > 0) {
                check_liner_leave_early.setVisibility(View.VISIBLE);
            } else {
                check_liner_leave_early.setVisibility(View.GONE);
            }
            my_gridView_lack_adapter.notifyDataSetChanged();
            my_gridView_late_adapter.notifyDataSetChanged();
            my_gridView_leave_adapter.notifyDataSetChanged();
            my_gridView_leave_early_adapter.notifyDataSetChanged();
            side_check_list.requestLayout();
            my_listView_adapter.notifyDataSetChanged();

            tv_leave.setText("请假人员（" + list_leave.size() + "人）");
            tv_lack.setText("缺勤人员（" + list_lack.size() + "人）");
            tv_late.setText("迟到人员（" + list_late.size() + "人）");
            tv_early.setText("早退人员（" + list_leave_early.size() + "人）");

            try {
                interval = Long.parseLong(attence_detail.getAtd_time())
                        - Long.parseLong(time);
            } catch (Exception e) {
            }
//				if (attence_detail.getStatus().equals("2")) {
//
//					liner_position.setVisibility(View.VISIBLE);
//					btn_position_check.setText("已完成位置考勤");
//					btn_position_check.setClickable(false);
//					btn_position_check.setBackground(getResources()
//							.getDrawable(R.drawable.login_btn_disabled));
//				} else if (attence_detail.getStatus().equals("1")) {
//
//					liner_position.setVisibility(View.VISIBLE);
//					btn_position_check.setClickable(false);
//					btn_position_check.setBackground(getResources()
//							.getDrawable(R.drawable.login_btn_disabled));
//					if (mControlTimer != null){
//						mControlTimer.cancel();}
//					startContrlTimer();
//				} else if (attence_detail.getStatus().equals("3")) {
//					liner_titlebar_zui_right_attence.setVisibility(View.GONE);
//					liner_position.setVisibility(View.GONE);
//					check_sll.setPadding(0, 0, 0, 0);
//
//				} else if (attence_detail.getStatus().equals("0")) {
//					liner_position.setVisibility(View.VISIBLE);
//				}


            check_sll.setPadding(0, 0, 0, 0);
            liner_titlebar_zui_right_attence.setVisibility(View.GONE);
            liner_position.setVisibility(View.GONE);

//				if (!attence_detail.getImage_url().equals("")&& attence_detail.getImage_url().length() > 0) {
//					String photo_urls[] = attence_detail.getImage_url()
//							.replaceAll("\\\\", "").split(",");
//					back = photo_urls.length;
//					for (int i = 0; i < photo_urls.length; i++) {
//						A_0_App.map_url.put(photo_urls[i],
//								photo_urls[i]);
//						Bimp.drr.add(photo_urls[i]);
//						A_0_App.biaozhi++;
//						Bimp.max++;
//						
//					}
//					my_grid_image_adapter.update();
//				} 
            showLoadResult(false, true, false);
        }


    }

    private void updateInfo() {
        MyAsyncTask updateLectureInfo = new MyAsyncTask(
                B_Side_Attence_Main_A3_Detail.this);
        updateLectureInfo.execute();
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
        @SuppressWarnings("unused")
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

            startReadData();
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {

        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {

        }
    }

    private void showLoadResult(boolean loading, boolean whole,
                                boolean loadFaile) {
        if (whole)
            liner_attence_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_attence_detail_whole.setVisibility(View.GONE);

        if (loadFaile)
            side_attence_detail_load_error.setVisibility(View.VISIBLE);
        else
            side_attence_detail_load_error.setVisibility(View.GONE);

        if (loading) {
            drawable.start();
            side_attence_detail_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            side_attence_detail_loading.setVisibility(View.GONE);
        }
    }

    private void startReadData() {
        A_0_App.getApi().getAttdenceDetail(B_Side_Attence_Main_A3_Detail.this,
                A_0_App.USER_TOKEN, attence_atd_id, new InteAttdenceDetail() {

                    @SuppressLint("NewApi")
                    public void onSuccess(
                            Cpk_Side_Attence_Detail side_Attence_Detail,
                            String time) {
                        if (isFinishing())
                            return;
                        havaSuccessLoadData = true;
                        A_0_App.map_url.clear();
                        Bimp.drr.clear();
                        A_0_App.biaozhi = 0;
                        ;
                        Bimp.max = 0;

                        attence_detail = side_Attence_Detail;
                        list_all.clear();
                        list_lack.clear();
                        list_late.clear();
                        list_leave_early.clear();
                        list_leave.clear();
                        list_class.clear();
                        list_type.clear();
                        list_type_temp.clear();
                        map.clear();

                        if (attence_detail.getPlace().equals("")) {
                            tv_titlebar_title_attence.setText(attence_detail
                                    .getTitle());
                        } else {
                            tv_titlebar_title_attence.setText(attence_detail
                                    .getTitle()
                                    + "("
                                    + attence_detail.getPlace() + ")");
                        }

                        list_all = JSON.parseArray(attence_detail.getList(),
                                Cpk_Attence_List.class);
                        for (int i = 0; i < list_all.size(); i++) {
                            if (list_all.get(i).getStatus().equals("2")) {
                                list_lack = JSON.parseArray(list_all.get(i)
                                        .getData(), Cpk_Attence_Common.class);
                            } else if (list_all.get(i).getStatus().equals("3")) {
                                list_late = JSON.parseArray(list_all.get(i)
                                        .getData(), Cpk_Attence_Common.class);
                            } else if (list_all.get(i).getStatus().equals("4")) {
                                list_leave_early = JSON.parseArray(list_all
                                                .get(i).getData(),
                                        Cpk_Attence_Common.class);
                            } else if (list_all.get(i).getStatus().equals("5")) {
                                list_leave = JSON.parseArray(list_all.get(i)
                                        .getData(), Cpk_Attence_Common.class);
                            } else {
                                list_class.add(list_all.get(i));

                            }
                        }
                        List<Cpk_Attence_List_Type> liCpk_Attence_List_Types = new ArrayList<Cpk_Attence_List_Type>();
                        liCpk_Attence_List_Types = JSON.parseArray(
                                attence_detail.getType(),
                                Cpk_Attence_List_Type.class);
                        for (int j = 0; j < liCpk_Attence_List_Types.size(); j++) {
                            list_type.add(liCpk_Attence_List_Types.get(j)
                                    .getName());
                        }
                        for (int j = 0; j < list_class.size(); j++) {
                            if (JSON.parseArray(list_class.get(j).getData(),
                                    Cpk_Attence_Common.class) != null) {
                                map.put(list_class.get(j).getClass_id(), JSON
                                        .parseArray(
                                                list_class.get(j).getData(),
                                                Cpk_Attence_Common.class));
                            } else {
                                List<Cpk_Attence_Common> list = new ArrayList<Cpk_Attence_Common>();
                                map.put(list_class.get(j).getClass_id(), list);
                            }

                        }


                        if (list_lack.size() > 0) {
                            check_liner_lack.setVisibility(View.VISIBLE);
                        } else {
                            check_liner_lack.setVisibility(View.GONE);
                        }
                        if (list_late.size() > 0) {
                            check_liner_late.setVisibility(View.VISIBLE);
                        } else {
                            check_liner_late.setVisibility(View.GONE);
                        }
                        if (list_leave.size() > 0) {
                            check_liner_leave.setVisibility(View.VISIBLE);
                        } else {
                            check_liner_leave.setVisibility(View.GONE);
                        }
                        if (list_leave_early.size() > 0) {
                            check_liner_leave_early.setVisibility(View.VISIBLE);
                        } else {
                            check_liner_leave_early.setVisibility(View.GONE);
                        }
                        my_gridView_lack_adapter.notifyDataSetChanged();
                        my_gridView_late_adapter.notifyDataSetChanged();
                        my_gridView_leave_adapter.notifyDataSetChanged();
                        my_gridView_leave_early_adapter.notifyDataSetChanged();
                        my_listView_adapter.notifyDataSetChanged();

                        tv_leave.setText("请假人员（" + list_leave.size() + "人）");
                        tv_lack.setText("缺勤人员（" + list_lack.size() + "人）");
                        tv_late.setText("迟到人员（" + list_late.size() + "人）");
                        tv_early.setText("早退人员（" + list_leave_early.size()
                                + "人）");

                        try {
                            interval = Long.parseLong(attence_detail
                                    .getAtd_time()) - Long.parseLong(time);
                        } catch (Exception e) {
                        }
                        if (attence_detail.getStatus().equals("2")) {

                            liner_position.setVisibility(View.VISIBLE);
                            btn_position_check.setText("已完成位置考勤");
                            btn_position_check.setClickable(false);
                            btn_position_check
                                    .setBackground(getResources().getDrawable(
                                            R.drawable.login_btn_disabled));
                        } else if (attence_detail.getStatus().equals("1")) {


                            btn_position_check.setClickable(false);
                            btn_position_check
                                    .setBackground(getResources().getDrawable(
                                            R.drawable.login_btn_disabled));
                            startContrlTimer();

                        } else if (attence_detail.getStatus().equals("3")) {
                            liner_titlebar_zui_right_attence
                                    .setVisibility(View.GONE);
                            liner_position.setVisibility(View.GONE);
                            check_sll.setPadding(0, 0, 0, 0);

                        } else if (attence_detail.getStatus().equals("0")) {
                            liner_position.setVisibility(View.VISIBLE);
                        }

                        if (attence_activity_early == true) {
                            check_sll.setPadding(0, 0, 0, 0);
                            liner_titlebar_zui_right_attence
                                    .setVisibility(View.GONE);
                            liner_position.setVisibility(View.GONE);
                        }


                        if (!attence_detail.getImage_url().equals("") && attence_detail.getImage_url().length() > 0) {
                            String photo_urls[] = attence_detail.getImage_url()
                                    .replaceAll("\\\\", "").split(",");
                            back = photo_urls.length;
                            for (int i = 0; i < photo_urls.length; i++) {
                                A_0_App.map_url.put(photo_urls[i],
                                        photo_urls[i]);
                                Bimp.drr.add(photo_urls[i]);
                                A_0_App.biaozhi++;
                                Bimp.max++;

                            }
                            my_grid_image_adapter.update();
                        } else {
                            load_finish = 0;
                        }
                        showLoadResult(false, true, false);


                    }

                    public void onStart() {
                        load_finish = 1;
                    }

                    public void onLoading(long total, long current,
                                          boolean isUploading) {

                    }

                    public void onKickedOffline(boolean audit, String logout_text) {

                        if (isFinishing())
                            return;
                        if (audit) {
                            A_0_App.getInstance().showExitDialog(B_Side_Attence_Main_A3_Detail.this, logout_text);
                        } else {
                            A_0_App.getInstance().showExitToast(B_Side_Attence_Main_A3_Detail.this, logout_text);
                        }
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    public void onFailure(String msg) {

                        if (isFinishing())
                            return;
                        load_finish = 0;
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true);
                        }
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    OnClickListener onClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_attence_detail_load_error:
                    showLoadResult(true, false, false);
                    selectTime();
                    startReadData();
                    break;

                case R.id.btn_position_check:

                    if (location == null) {
                        position();
                    }
                    break;
                case R.id.btn_image_check:

                    if (Bimp.drr.size() < 3) {
                        if (A_0_App.biaozhi == Bimp.drr.size()) {
                            photo();
                        }
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this,
                                "最多添加3张图片");
                    }

                    break;
                default:
                    break;
            }
        }

    };

    private static final String IMAGE_FILE_NAME = "pic.jpg";
    private String path = "";
    private Bitmap photo;
    private static final int IMAGE_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private static final int RESULT_REQUEST_CODE = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            File tempFile = new File(Environment.getExternalStorageDirectory()
                    + "/" + B_Side_Attence_Main_A3_Detail.temppath
                    + IMAGE_FILE_NAME);

            // saveMyBitmap(temppath, photo);
            path = android.os.Environment.getExternalStorageDirectory() + "/"
                    + B_Side_Attence_Main_A3_Detail.temppath + IMAGE_FILE_NAME;
            if (Bimp.drr.size() < 3) {
                Bimp.drr.add(path);
            }
            first_getimage = 0;
            delete_biaoshi = 1;
            load = 0;
            my_grid_image_adapter.update();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 图片
     */
    private void photo() {
        final Dialog dialog = new Dialog(B_Side_Attence_Main_A3_Detail.this,
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
                PermissionGen.needPermission(B_Side_Attence_Main_A3_Detail.this, REQUECT_CODE_CAMERA,
                        new String[]{
                                Manifest.permission.CAMERA
                        });

            }
        });
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
                Intent intent = new Intent(B_Side_Attence_Main_A3_Detail.this,
                        B_Side_Found_Add_Images.class);
                intent.putExtra("type", "1");
                Bimp.add_edit = "4";
                first_getimage = 0;
                startActivity(intent);
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
        A_0_App.getInstance().PermissionToas("摄像头", B_Side_Attence_Main_A3_Detail.this);
    }

    @PermissionSuccess(requestCode = REQUECT_CODE_ACCESS_FINE_LOCATION)
    public void requestDingWeiSuccess() {
        dingWei();
    }

    @PermissionFail(requestCode = REQUECT_CODE_ACCESS_FINE_LOCATION)
    public void requestDingWeiFailed() {
        A_0_App.getInstance().CancelProgreDialog(
                B_Side_Attence_Main_A3_Detail.this);
        A_0_App.getInstance().PermissionToas("位置", B_Side_Attence_Main_A3_Detail.this);
    }

    private void openPhoto() {//拍照 方法
        Bimp.add_edit = "-1";

        B_Side_Attence_Main_A3_Detail.temppath = System
                .currentTimeMillis() + "";
        Intent intentFromCapture = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
                .fromFile(new File(Environment
                        .getExternalStorageDirectory(),
                        B_Side_Attence_Main_A3_Detail.temppath
                                + IMAGE_FILE_NAME)));
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

    // 保存图片
    public static void saveMyBitmap(String bitName, Bitmap mBitmap) {
        File dir = new File(
                android.os.Environment.getExternalStorageDirectory()
                        + AppStrStatic.SD_PIC);
        if (!dir.exists() && !dir.isDirectory())
            dir.mkdir();
        if (mBitmap == null)
            return;

        FileOutputStream fos;
        try {
            File bitmapFile = new File(
                    android.os.Environment.getExternalStorageDirectory()
                            + AppStrStatic.SD_PIC, bitName + ".jpg");

            fos = new FileOutputStream(bitmapFile);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * @author 图片适配器
     */
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
            if (Bimp.drr.size() > 3)
                return Bimp.drr.size() / 2;
            return Bimp.drr.size();

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
            ViewHolder_image holder = null;
            if (convertView == null) {

                convertView = inflater.inflate(R.layout.item_add_notice_images,
                        parent, false);
                holder = new ViewHolder_image();
                holder.image = (ImageView) convertView
                        .findViewById(R.id.item_grida_image);
                holder.delete = (ImageView) convertView
                        .findViewById(R.id.item_grida_delete);
                holder.item_half = (ImageView) convertView
                        .findViewById(R.id.item_half);
                holder.itme_process_text = (TextView) convertView
                        .findViewById(R.id.itme_process_text);
                holder.pb = (ProgressBar) convertView.findViewById(R.id.pb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder_image) convertView.getTag();
            }

            RelativeLayout.LayoutParams lp_menpiao1 = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp_menpiao1.width = (int) (screenWidth / 4);
            lp_menpiao1.height = (int) (screenWidth / 4);
            lp_menpiao1.setMargins(0, 30, 0, 0);
            holder.image.setLayoutParams(lp_menpiao1);
            holder.item_half.setLayoutParams(lp_menpiao1);
            holder.pb.setLayoutParams(lp_menpiao1);
            holder.delete.setPadding(screenWidth / 4 - 30, 0, 0, 0);
            holder.delete.setVisibility(View.GONE);
            holder.image.setScaleType(ScaleType.CENTER_CROP);
            holder.itme_process_text.setLayoutParams(lp_menpiao1);
            holder.item_half.getBackground().mutate().setAlpha(100);
            if (Bimp.drr.size() >= back) {
                holder.item_half.setVisibility(View.GONE);
            } else {
                holder.item_half.setVisibility(View.VISIBLE);
            }

            if (Bimp.drr.get(position).contains("http")) {
                if (!Bimp.drr.get(position).equals(holder.image.getTag())) {
                    if (Bimp.drr.get(position) != null) {
                        x.image().bind(holder.image, Bimp.drr.get(position));
                    }

                    holder.image.setTag(Bimp.drr.get(position));
                }

            } else {
                try {
                    if (!Bimp.drr.get(position).equals(holder.image.getTag())) {
                        if (Bimp.drr.get(position) != null) {
                            x.image().bind(holder.image, "file://" + Bimp.drr.get(position));
                        }

                        holder.image.setTag(Bimp.drr.get(position));
                    }

                } catch (Exception e) {

                }

            }
            try {
                if (A_0_App.map_url.size() > 0
                        && !Bimp.drr.get(position).contains("http")) {

                    if (A_0_App.map_url.get(Bimp.drr.get(position)).equals("")) {

                        holder.item_half.setVisibility(View.GONE);
                        holder.pb.setVisibility(View.VISIBLE);
                        holder.itme_process_text.setVisibility(View.VISIBLE);
                        holder.pb.setMax(fileSize);
                        holder.pb.setProgress(downloadSize);
                        float price = downloadSize / fileSize;
                        // DecimalFormat decimalFormat=new
                        // DecimalFormat(".00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
                        // String p=decimalFormat.format(price);//format 返回的是字符串

                        float num = (float) (Math.round(price * 100) / 100);// 如果要求精确4位就*10000然后/10000

                        holder.itme_process_text.setText(num * 100 + "%");

                    } else if (A_0_App.map_url.get(Bimp.drr.get(position))
                            .contains("http")) {
                        holder.image.setVisibility(View.VISIBLE);
                        holder.pb.setVisibility(View.GONE);
                        holder.itme_process_text.setVisibility(View.GONE);
                        holder.item_half.setVisibility(View.GONE);
                    } else {
                        holder.item_half.setVisibility(View.VISIBLE);
                        holder.itme_process_text.setVisibility(View.GONE);
                        holder.image.setVisibility(View.VISIBLE);
                        holder.pb.setVisibility(View.GONE);
                        if (!Bimp.drr.get(position).equals(holder.image.getTag())) {
                            if (Bimp.drr.get(position) != null) {
                                x.image().bind(holder.image, "file://" + Bimp.drr.get(position));
                            }
                            holder.image.setTag(Bimp.drr.get(position));
                        }
                        holder.item_half.setImageBitmap(BitmapFactory
                                .decodeResource(getResources(),
                                        R.drawable.ico_side_notice));
                    }
                }

            } catch (Exception e) {

            }

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (Bimp.drr.size() == A_0_App.map_url.size()) {

                        if (A_0_App.map_url.get(Bimp.drr.get(position)).contains(Environment.getExternalStorageDirectory() + "")) {
                            String newStr = Bimp.drr.get(position)
                                    .substring(
                                            Bimp.drr.get(position)
                                                    .lastIndexOf("/") + 1,
                                            Bimp.drr.get(position)
                                                    .lastIndexOf("."));
                            String upload_path = "";
                            upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                            first_getimage = 1;
                            failure_url = Bimp.drr.get(position);
                            upload_single_failure(upload_path);
                        } else {

                            ArrayList<String> path = new ArrayList<String>();
                            for (int i = 0; i < Bimp.drr.size(); i++) {
                                path.add(A_0_App.map_url.get(Bimp.drr.get(i)));
                            }

                            Intent intent = new Intent(
                                    B_Side_Attence_Main_A3_Detail.this,
                                    B_Side_Found_BigImage.class);
                            intent.putStringArrayListExtra("path", path);
                            intent.putExtra("num", position);
                            startActivity(intent);
                        }
                    }

                }
            });
            convertView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View arg0) {
                    if (attence_activity_early == false) {
                        if (load_finish == 0) {
                            delete_image(position);
                        } else {
                            PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, "图片上传中...");
                        }

                    }

                    return false;
                }
            });
            return convertView;
        }
    }

    public class ViewHolder_image {
        public ImageView image, item_half;
        public ImageView delete;
        public TextView itme_process_text;
        ProgressBar pb;
    }

    public void delete_image(final int position) {
        final GeneralDialog upDateDialog = new GeneralDialog(
                B_Side_Attence_Main_A3_Detail.this, R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent("删除该图片?");
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

                FileUtils.delFile(Bimp.drr.get(position));
                A_0_App.map_url.remove(Bimp.drr.get(position));
                Bimp.drr.remove(position);
                Bimp.max = Bimp.max - 1;
                A_0_App.biaozhi--;
                back--;
                delete_biaoshi = 1;
                String path = "";
                if (Bimp.drr.size() > 0) {
                    for (int i = 0; i < Bimp.drr.size(); i++) {
                        if (A_0_App.map_url.get(Bimp.drr.get(i)).contains("http")) {
                            path = path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                        }

                    }
                }

                st_image_url = path;
                if (st_image_url.length() > 0) {
                    st_image_url = st_image_url.substring(0,
                            st_image_url.length() - 1);
                }
                UploadImage();


            }


        });

        upDateDialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {

            }
        });
        upDateDialog.show();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:

                    if (Bimp.drr.size() > 0) {
                        check_liner_image.setVisibility(View.VISIBLE);
                    } else {
                        check_liner_image.setVisibility(View.GONE);
                    }

                    if (Bimp.drr.size() > back && Bimp.drr.size() == A_0_App.map_url.size() && first_getimage == 0) {
                        first_getimage = 1;
                        String path = "";
                        for (int i = 0; i < Bimp.drr.size(); i++) {
                            if (A_0_App.map_url.get(Bimp.drr.get(i)).contains("http")) {
                                path = path + A_0_App.map_url.get(Bimp.drr.get(i))
                                        + ",";
                            }

                        }

                        if (A_0_App.biaozhi == Bimp.drr.size() && Bimp.drr.size() > 0 && load == 0) {
                            st_image_url = path.substring(0, path.length() - 1);
                            delete_biaoshi = 0;
                            UploadImage();
                        }
                    }

                    if (my_grid_image_adapter != null) {
                        if (!isFinishing()) {
                            my_grid_image_adapter.notifyDataSetChanged();
                        }

                    } else {
                        clear();
                    }
                    if (Bimp.add_edit.equals("")) {
                        load_finish = 0;
                    }
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

                        if (A_0_App.map_url.size() >= 0 && first_getimage == 0 && Bimp.drr.size() > back && Bimp.drr.size() != A_0_App.biaozhi) {
                            load = 0;
                            first_getimage = 1;
                            if (Bimp.drr.size() > A_0_App.biaozhi) {
                                String newStr = Bimp.drr.get(A_0_App.biaozhi).substring(
                                        Bimp.drr.get(A_0_App.biaozhi)
                                                .lastIndexOf("/") + 1,
                                        Bimp.drr.get(A_0_App.biaozhi)
                                                .lastIndexOf("."));
                                String upload_path = "";
                                upload_path = FileUtils.SDPATH + newStr + ".JPEG";
                                upload_single(upload_path);

                            }
                        }

                        break;
                    } else {

                        try {

                            if (Bimp.drr.size() - Bimp.max >= 0) {
                                String path = Bimp.drr.get(Bimp.max);
                                Bitmap bm = Bimp.revitionImageSize(path);
                                Bimp.bmp.add(bm);
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

                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    /**
     * 缺勤适配器
     */
    public class My_GridView_Lack_Aapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list_lack != null)
                return list_lack.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_grid_list, null);
                holder.ivHead = (CircleImageView) converView
                        .findViewById(R.id.iv_contact_por);
                holder.tv_item_check_name = (TextView) converView
                        .findViewById(R.id.tv_item_check_name);
                holder.tv_item_check_type = (TextView) converView
                        .findViewById(R.id.tv_item_check_type);
                holder.tv_item_check_meter = (TextView) converView
                        .findViewById(R.id.tv_item_check_meter);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_item_check_name.setText(list_lack.get(posi)
                    .getTrue_name());
            holder.tv_item_check_type.setText("缺勤");
            holder.tv_item_check_type.setVisibility(View.VISIBLE);
            holder.tv_item_check_type.setBackground(getResources().getDrawable(
                    R.drawable.attence_sign_absence));
            if (posi % 8 == 0) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
            }

            if (attence_detail.getAtd_time().length() <= 0) {

                holder.tv_item_check_meter.setText("缺省");
                holder.tv_item_check_meter.setTextColor(getResources()
                        .getColor(R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    holder.tv_item_check_meter.setText("缺省");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else if (list_lack.get(posi).getMeter().equals("")
                        || list_lack.get(posi).getMeter().equals(null)) {
                    holder.tv_item_check_meter.setText("无回应");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(list_lack.get(posi).getMeter()) < 500) {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_blue));
                    } else {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_red));
                    }
                    holder.tv_item_check_meter.setText(list_lack.get(posi)
                            .getMeter() + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, list_lack.get(posi).getPhoto_url(),
                    holder.ivHead, options);
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        people_type = "缺勤";
                        class_id = list_lack.get(posi).getClass_id();
                        mark(posi, list_lack.get(posi).getTrue_name(), list_lack
                                .get(posi).getMeter(), list_lack.get(posi)
                                .getPhoto_url(), list_lack.get(posi).getUser_id());
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);
                    }


                }
            });
            return converView;
        }

    }

    /**
     * 迟到适配器
     */
    public class My_GridView_Late_Aapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list_late != null)
                return list_late.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_grid_list, null);
                holder.ivHead = (CircleImageView) converView
                        .findViewById(R.id.iv_contact_por);
                holder.tv_item_check_name = (TextView) converView
                        .findViewById(R.id.tv_item_check_name);
                holder.tv_item_check_type = (TextView) converView
                        .findViewById(R.id.tv_item_check_type);
                holder.tv_item_check_meter = (TextView) converView
                        .findViewById(R.id.tv_item_check_meter);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_item_check_name.setText(list_late.get(posi)
                    .getTrue_name());
            holder.tv_item_check_type.setText("迟到");
            holder.tv_item_check_type.setVisibility(View.VISIBLE);
            holder.tv_item_check_type.setBackground(getResources().getDrawable(
                    R.drawable.attence_sign_late));
            if (posi % 8 == 0) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
            }

            if (attence_detail.getAtd_time().length() <= 0) {

                holder.tv_item_check_meter.setText("缺省");
                holder.tv_item_check_meter.setTextColor(getResources()
                        .getColor(R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    holder.tv_item_check_meter.setText("缺省");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else if (list_late.get(posi).getMeter().equals("")
                        || list_late.get(posi).getMeter().equals(null)) {
                    holder.tv_item_check_meter.setText("无回应");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(list_late.get(posi).getMeter()) < 500) {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_blue));
                    } else {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_red));
                    }
                    holder.tv_item_check_meter.setText(list_late.get(posi)
                            .getMeter() + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, list_late.get(posi).getPhoto_url(),
                    holder.ivHead, options);
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        people_type = "迟到";
                        class_id = list_late.get(posi).getClass_id();
                        mark(posi, list_late.get(posi).getTrue_name(), list_late
                                .get(posi).getMeter(), list_late.get(posi)
                                .getPhoto_url(), list_late.get(posi).getUser_id());
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);

                    }
                    ;
                }
            });
            return converView;
        }

    }

    /**
     * 早退适配器
     */
    public class My_GridView_Leave_Early_Aapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list_leave_early != null)
                return list_leave_early.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_grid_list, null);
                holder.ivHead = (CircleImageView) converView
                        .findViewById(R.id.iv_contact_por);
                holder.tv_item_check_name = (TextView) converView
                        .findViewById(R.id.tv_item_check_name);
                holder.tv_item_check_type = (TextView) converView
                        .findViewById(R.id.tv_item_check_type);
                holder.tv_item_check_meter = (TextView) converView
                        .findViewById(R.id.tv_item_check_meter);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_item_check_name.setText(list_leave_early.get(posi)
                    .getTrue_name());
            holder.tv_item_check_type.setText("早退");
            holder.tv_item_check_type.setVisibility(View.VISIBLE);
            holder.tv_item_check_type.setBackground(getResources().getDrawable(
                    R.drawable.attence_sign_installed));
            if (posi % 8 == 0) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
            }

            if (attence_detail.getAtd_time().length() <= 0) {

                holder.tv_item_check_meter.setText("缺省");
                holder.tv_item_check_meter.setTextColor(getResources()
                        .getColor(R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    holder.tv_item_check_meter.setText("缺省");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else if (list_leave_early.get(posi).getMeter().equals("")
                        || list_leave_early.get(posi).getMeter().equals(null)) {
                    holder.tv_item_check_meter.setText("无回应");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(list_leave_early.get(posi).getMeter()) < 500) {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_blue));
                    } else {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_red));
                    }
                    holder.tv_item_check_meter.setText(list_leave_early.get(
                            posi).getMeter()
                            + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, list_leave_early.get(posi).getPhoto_url(),
                    holder.ivHead, options);
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        people_type = "早退";
                        class_id = list_leave_early.get(posi).getClass_id();
                        mark(posi, list_leave_early.get(posi).getTrue_name(),
                                list_leave_early.get(posi).getMeter(),
                                list_leave_early.get(posi).getPhoto_url(),
                                list_leave_early.get(posi).getUser_id());
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);
                    }

                }
            });
            return converView;
        }

    }

    /**
     * 请假适配器
     */
    public class My_GridView_leave_Aapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (list_leave != null)
                return list_leave.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_grid_list, null);
                holder.ivHead = (CircleImageView) converView
                        .findViewById(R.id.iv_contact_por);
                holder.tv_item_check_name = (TextView) converView
                        .findViewById(R.id.tv_item_check_name);
                holder.tv_item_check_type = (TextView) converView
                        .findViewById(R.id.tv_item_check_type);
                holder.tv_item_check_meter = (TextView) converView
                        .findViewById(R.id.tv_item_check_meter);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_item_check_name.setText(list_leave.get(posi)
                    .getTrue_name());
            holder.tv_item_check_type.setText("请假");
            holder.tv_item_check_type.setVisibility(View.VISIBLE);
            holder.tv_item_check_type.setBackground(getResources().getDrawable(
                    R.drawable.attence_sign_leave));
            if (posi % 8 == 0) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
            }

            if (attence_detail.getAtd_time().length() <= 0) {

                holder.tv_item_check_meter.setText("缺省");
                holder.tv_item_check_meter.setTextColor(getResources()
                        .getColor(R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    holder.tv_item_check_meter.setText("缺省");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else if (list_leave.get(posi).getMeter().equals("")
                        || list_leave.get(posi).getMeter().equals(null)) {
                    holder.tv_item_check_meter.setText("无回应");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(list_leave.get(posi).getMeter()) < 500) {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_blue));
                    } else {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_red));
                    }
                    holder.tv_item_check_meter.setText(list_leave.get(posi)
                            .getMeter() + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, list_leave.get(posi).getPhoto_url(),
                    holder.ivHead, options);
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        people_type = "请假";
                        class_id = list_leave.get(posi).getClass_id();
                        mark(posi, list_leave.get(posi).getTrue_name(), list_leave
                                .get(posi).getMeter(), list_leave.get(posi)
                                .getPhoto_url(), list_leave.get(posi).getUser_id());
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);

                    }
                }
            });
            return converView;
        }

    }

    /**
     * 出勤适配器
     */
    public class My_GridView_Class_Aapter extends BaseAdapter {
        private List<Cpk_Attence_Common> list_temp = new ArrayList<Cpk_Attence_Common>();

        public My_GridView_Class_Aapter(List<Cpk_Attence_Common> list_temp) {

            this.list_temp = list_temp;
        }

        @Override
        public int getCount() {
            if (list_temp != null)
                return list_temp.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_grid_list, null);
                holder.ivHead = (CircleImageView) converView
                        .findViewById(R.id.iv_contact_por);
                holder.tv_item_check_name = (TextView) converView
                        .findViewById(R.id.tv_item_check_name);
                holder.tv_item_check_type = (TextView) converView
                        .findViewById(R.id.tv_item_check_type);
                holder.tv_item_check_meter = (TextView) converView
                        .findViewById(R.id.tv_item_check_meter);
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.tv_item_check_name.setText(list_temp.get(posi)
                    .getTrue_name());
            if (posi % 8 == 0) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_one);

            } else if (posi % 8 == 1) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_two);
            } else if (posi % 8 == 2) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_three);
            } else if (posi % 8 == 3) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_four);
            } else if (posi % 8 == 4) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_five);
            } else if (posi % 8 == 5) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_six);
            } else if (posi % 8 == 6) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_seven);
            } else if (posi % 8 == 7) {
                holder.ivHead.setBackgroundResource(R.drawable.photo_eight);
            }

            if (attence_detail.getAtd_time().length() <= 0) {

                holder.tv_item_check_meter.setText("缺省");
                holder.tv_item_check_meter.setTextColor(getResources()
                        .getColor(R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    holder.tv_item_check_meter.setText("缺省");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else if (list_temp.get(posi).getMeter().equals("")
                        || list_temp.get(posi).getMeter().equals(null)) {
                    holder.tv_item_check_meter.setText("无回应");
                    holder.tv_item_check_meter.setTextColor(getResources()
                            .getColor(R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(list_temp.get(posi).getMeter()) < 500) {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_blue));
                    } else {
                        holder.tv_item_check_meter.setTextColor(getResources()
                                .getColor(R.color.check_red));
                    }
                    holder.tv_item_check_meter.setText(list_temp.get(posi)
                            .getMeter() + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, list_temp.get(posi).getPhoto_url(),
                    holder.ivHead, options);

            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        people_type = "出勤";
                        class_id = list_temp.get(posi).getClass_id();
                        mark(posi, list_temp.get(posi).getTrue_name(), list_temp
                                .get(posi).getMeter(), list_temp.get(posi)
                                .getPhoto_url(), list_temp.get(posi).getUser_id());

                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);

                    }
                }
            });
            return converView;
        }

    }

    class ViewHolder {
        CircleImageView ivHead;
        TextView tv_item_check_name;
        TextView tv_item_check_type;
        TextView tv_item_check_meter;
        Button check_btn_leave;
    }

    /**
     * 标记适配器
     */
    public class My_GridView_Mark_Aapter extends BaseAdapter {
        int position;

        public My_GridView_Mark_Aapter(int position) {

            this.position = position;
        }

        @Override
        public int getCount() {
            if (list_type_temp != null)
                return list_type_temp.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            final ViewHolder holder;
            if (converView == null) {
                holder = new ViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_mark_list, null);

                holder.check_btn_leave = (Button) converView
                        .findViewById(R.id.check_btn_leave);

                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            holder.check_btn_leave.setText(list_type_temp.get(posi));

            holder.check_btn_leave.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {

                    if (NetUtils.isConnected(B_Side_Attence_Main_A3_Detail.this)) {
                        dialog.dismiss();
                        if (people_type == "迟到") {
                            user_id = list_late.get(position).getUser_id();
                            if (holder.check_btn_leave.getText().toString()
                                    .equals("出勤")) {
                                map.get(class_id).add(list_late.get(position));
                                list_late.remove(position);
                                my_listView_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("早退")) {
                                list_leave_early.add(list_late.get(position));
                                list_late.remove(position);
                                check_liner_leave_early.setVisibility(View.VISIBLE);
                                my_gridView_leave_early_adapter
                                        .notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("请假")) {
                                list_leave.add(list_late.get(position));
                                list_late.remove(position);
                                check_liner_leave.setVisibility(View.VISIBLE);
                                my_gridView_leave_adapter.notifyDataSetChanged();
                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("缺勤")) {
                                list_lack.add(list_late.get(position));
                                list_late.remove(position);
                                check_liner_lack.setVisibility(View.VISIBLE);
                                my_gridView_lack_adapter.notifyDataSetChanged();
                            }
                            my_gridView_late_adapter.notifyDataSetChanged();
                            if (list_late.size() > 0) {
                                check_liner_late.setVisibility(View.VISIBLE);
                            } else {
                                check_liner_late.setVisibility(View.GONE);
                            }

                        } else if (people_type == "出勤") {

                            if (position_biaoshi == 0) {
                                user_id = map.get(class_id).get(position)
                                        .getUser_id();
                                if (holder.check_btn_leave.getText().toString()
                                        .equals("迟到")) {

                                    list_late.add(map.get(class_id).get(position));
                                    map.get(class_id).remove(position);
                                    check_liner_late.setVisibility(View.VISIBLE);
                                    my_gridView_late_adapter.notifyDataSetChanged();

                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("早退")) {
                                    list_leave_early.add(map.get(class_id).get(
                                            position));
                                    map.get(class_id).remove(position);
                                    check_liner_leave_early
                                            .setVisibility(View.VISIBLE);
                                    my_gridView_leave_early_adapter
                                            .notifyDataSetChanged();

                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("请假")) {
                                    list_leave.add(map.get(class_id).get(position));
                                    map.get(class_id).remove(position);
                                    check_liner_leave.setVisibility(View.VISIBLE);
                                    my_gridView_leave_adapter
                                            .notifyDataSetChanged();
                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("缺勤")) {
                                    list_lack.add(map.get(class_id).get(position));
                                    if (map.size() > 0 && map.size() >= position) {
                                        map.get(class_id).remove(position);
                                    }

                                    check_liner_lack.setVisibility(View.VISIBLE);
                                    my_gridView_lack_adapter.notifyDataSetChanged();
                                }
                                my_listView_adapter.notifyDataSetChanged();
                            } else {

                            }

                        } else if (people_type == "缺勤") {

                            if (position_biaoshi == 0) {
                                user_id = list_lack.get(position).getUser_id();
                                if (holder.check_btn_leave.getText().toString()
                                        .equals("出勤")) {
                                    map.get(list_lack.get(position).getClass_id())
                                            .add(list_lack.get(position));
                                    list_lack.remove(position);
                                    my_listView_adapter.notifyDataSetChanged();

                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("迟到")) {
                                    list_late.add(list_lack.get(position));
                                    list_lack.remove(position);
                                    check_liner_late.setVisibility(View.VISIBLE);
                                    my_gridView_late_adapter.notifyDataSetChanged();

                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("早退")) {
                                    list_leave_early.add(list_lack.get(position));
                                    list_lack.remove(position);
                                    check_liner_leave_early
                                            .setVisibility(View.VISIBLE);
                                    my_gridView_leave_early_adapter
                                            .notifyDataSetChanged();

                                } else if (holder.check_btn_leave.getText()
                                        .toString().equals("请假")) {
                                    list_leave.add(list_lack.get(position));
                                    list_lack.remove(position);
                                    check_liner_leave.setVisibility(View.VISIBLE);
                                    my_gridView_leave_adapter
                                            .notifyDataSetChanged();

                                }
                                if (list_lack.size() > 0) {
                                    check_liner_lack.setVisibility(View.VISIBLE);
                                } else {
                                    check_liner_lack.setVisibility(View.GONE);
                                }
                                my_gridView_lack_adapter.notifyDataSetChanged();
                            } else {
                                user_id = map.get(class_id).get(position)
                                        .getUser_id();
                            }

                        } else if (people_type == "早退") {
                            user_id = list_leave_early.get(position).getUser_id();
                            if (holder.check_btn_leave.getText().toString()
                                    .equals("出勤")) {
                                map.get(class_id).add(
                                        list_leave_early.get(position));
                                list_leave_early.remove(position);
                                my_listView_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("迟到")) {

                                list_late.add(list_leave_early.get(position));
                                list_leave_early.remove(position);
                                check_liner_late.setVisibility(View.VISIBLE);
                                my_gridView_late_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("请假")) {
                                list_leave.add(list_leave_early.get(position));
                                list_leave_early.remove(position);
                                check_liner_leave.setVisibility(View.VISIBLE);
                                my_gridView_leave_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("缺勤")) {
                                list_lack.add(list_leave_early.get(position));
                                list_leave_early.remove(position);
                                check_liner_lack.setVisibility(View.VISIBLE);
                                my_gridView_lack_adapter.notifyDataSetChanged();

                            }

                            if (list_leave_early.size() > 0) {
                                check_liner_leave_early.setVisibility(View.VISIBLE);
                            } else {
                                check_liner_leave_early.setVisibility(View.GONE);
                            }
                            my_gridView_leave_early_adapter.notifyDataSetChanged();

                        } else if (people_type == "请假") {
                            user_id = list_leave.get(position).getUser_id();
                            if (holder.check_btn_leave.getText().toString()
                                    .equals("出勤")) {
                                map.get(class_id).add(list_leave.get(position));
                                list_leave.remove(position);
                                my_listView_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("迟到")) {

                                list_late.add(list_leave.get(position));
                                list_leave.remove(position);
                                check_liner_late.setVisibility(View.VISIBLE);
                                my_gridView_late_adapter.notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("早退")) {
                                list_leave_early.add(list_leave.get(position));
                                list_leave.remove(position);
                                check_liner_leave_early.setVisibility(View.VISIBLE);
                                my_gridView_leave_early_adapter
                                        .notifyDataSetChanged();

                            } else if (holder.check_btn_leave.getText().toString()
                                    .equals("缺勤")) {
                                list_lack.add(list_leave.get(position));
                                list_leave.remove(position);
                                check_liner_lack.setVisibility(View.VISIBLE);
                                my_gridView_lack_adapter.notifyDataSetChanged();

                            }
                            if (list_leave.size() > 0) {
                                check_liner_leave.setVisibility(View.VISIBLE);
                            } else {
                                check_liner_leave.setVisibility(View.GONE);
                            }
                            my_gridView_leave_adapter.notifyDataSetChanged();

                        }

                        if (holder.check_btn_leave.getText().toString()
                                .equals("缺勤")) {
                            post_type = "2";
                        } else if (holder.check_btn_leave.getText().toString()
                                .equals("出勤")) {
                            post_type = "1";
                        } else if (holder.check_btn_leave.getText().toString()
                                .equals("早退")) {
                            post_type = "4";
                        } else if (holder.check_btn_leave.getText().toString()
                                .equals("迟到")) {
                            post_type = "3";
                        } else if (holder.check_btn_leave.getText().toString()
                                .equals("请假")) {
                            post_type = "5";
                        }
                        tv_leave.setText("请假人员（" + list_leave.size() + "人）");
                        tv_lack.setText("缺勤人员（" + list_lack.size() + "人）");
                        tv_late.setText("迟到人员（" + list_late.size() + "人）");
                        tv_early.setText("早退人员（" + list_leave_early.size() + "人）");
                        postAttdenceStatus(post_type, user_id);
                    } else {
                        PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, R.string.error_title_net_error);

                    }
                }
            });
            return converView;
        }

    }

    private Dialog dialog;

    /**
     * 标记个人出勤
     */
    private void mark(final int posi, String name, String meter,
                      String photo_url, String user_id) {
        if (!attence_detail.getStatus().equals("3")
                && attence_activity_early == false) {
            dialog = new Dialog(B_Side_Attence_Main_A3_Detail.this,
                    android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.item_check_mark);
            dialog.show();
            CircleImageView iv_mark_photo = (CircleImageView) dialog
                    .findViewById(R.id.iv_mark_photo);
            TextView tv_check_name = (TextView) dialog
                    .findViewById(R.id.tv_check_name);
            TextView tv_check_class = (TextView) dialog
                    .findViewById(R.id.tv_check_class);
            TextView tv_check_position = (TextView) dialog
                    .findViewById(R.id.tv_check_position);
            iv_mark_photo.setBackgroundResource(R.drawable.photo_one);

            for (int i = 0; i < list_class.size(); i++) {
                if (list_class.get(i).getClass_id().equals(class_id)) {
                    tv_check_class.setText(list_class.get(i).getName());
                }
            }

            tv_check_name.setText(name);

            if (attence_detail.getAtd_time().length() <= 0) {

                tv_check_position.setText("缺省");
                tv_check_position.setTextColor(getResources().getColor(
                        R.color.title_no_focus_login));

            } else {
                if (attence_detail.getStatus().equals("0")) {
                    tv_check_position.setText("缺省");
                    tv_check_position.setTextColor(getResources().getColor(
                            R.color.title_no_focus_login));
                } else if (meter.equals("") || meter.equals(null)) {
                    tv_check_position.setText("无回应");
                    tv_check_position.setTextColor(getResources().getColor(
                            R.color.title_no_focus_login));
                } else {
                    if (Integer.parseInt(meter) < 500) {
                        tv_check_position.setTextColor(getResources().getColor(
                                R.color.check_blue));
                    } else {
                        tv_check_position.setTextColor(getResources().getColor(
                                R.color.check_red));
                    }
                    tv_check_position.setText(meter + "m");
                }
            }
            PubMehods.loadServicePic(imageLoader, photo_url, iv_mark_photo, options);
            LinearLayout liner_check_mark = (LinearLayout) dialog
                    .findViewById(R.id.liner_check_mark);
            liner_check_mark.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                }
            });
            list_type_temp.clear();
            for (int i = 0; i < list_type.size(); i++) {
                if (list_type.get(i).equals(people_type)) {

                } else {
                    list_type_temp.add(list_type.get(i));
                }
            }
            my_gridView_mark_adapter = new My_GridView_Mark_Aapter(posi);
            MyGridView side_check_gridview_mark = (MyGridView) dialog
                    .findViewById(R.id.side_check_gridview_mark);
            side_check_gridview_mark.setAdapter(my_gridView_mark_adapter);
        } else if (attence_activity_early == true) {
//			String class_name = "";
//			for (int i = 0; i < list_class.size(); i++) {
//				if (list_class.get(i).getClass_id().equals(class_id)) {
//					class_name = list_class.get(i).getName();
//				}
//			}
//
//			Intent intent = new Intent(B_Side_Attence_Main_A3_Detail.this,
//					B_Side_Attence_Main_A5_0_Personal_Statistics.class);
//			intent.putExtra("name", name);
//			intent.putExtra("photo", photo_url);
//			intent.putExtra("class", class_name);
//			intent.putExtra("user_id", user_id);
//			startActivity(intent);
        }

    }

    /**
     * 位置考勤
     */
    private void position() {

        final Dialog dialog = new Dialog(B_Side_Attence_Main_A3_Detail.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.item_check_position);
        MyListView side_check_list_time = (MyListView) dialog
                .findViewById(R.id.side_check_list_time);
        Button btn_start = (Button) dialog.findViewById(R.id.btn_start);
        RadioGroup rg_check = (RadioGroup) dialog.findViewById(R.id.rg_check);
        final RadioButton rb_check_five = (RadioButton) dialog
                .findViewById(R.id.rb_check_five);
        final RadioButton rb_check_fifth = (RadioButton) dialog
                .findViewById(R.id.rb_check_fifth);
        final RadioButton rb_check_thirty = (RadioButton) dialog
                .findViewById(R.id.rb_check_thirty);
        side_check_list_time.setAdapter(my_listView_time_adapter);
        btn_start.setOnClickListener(new OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View arg0) {
                if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
                    A_0_App.getInstance().showProgreDialog(
                            B_Side_Attence_Main_A3_Detail.this, "", true);
                    for (int i = 0; i < list_class.size(); i++) {
                        if (map.get(list_class.get(i).getClass_id()) != null) {
                            list_lack.addAll(map.get(list_class.get(i)
                                    .getClass_id()));
                            map.get(list_class.get(i).getClass_id()).clear();
                        }

                    }
                    if (list_lack.size() > 0) {
                        check_liner_lack.setVisibility(View.VISIBLE);
                    } else {
                        check_liner_lack.setVisibility(View.GONE);
                    }
                    my_gridView_lack_adapter.notifyDataSetChanged();
                    my_listView_adapter.notifyDataSetChanged();



                    dialog.dismiss();
                    PermissionGen.needPermission(B_Side_Attence_Main_A3_Detail.this, REQUECT_CODE_ACCESS_FINE_LOCATION,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION
                            });

                    tv_leave.setText("请假人员（" + list_leave.size() + "人）");
                    tv_lack.setText("缺勤人员（" + list_lack.size() + "人）");
                    tv_late.setText("迟到人员（" + list_late.size() + "人）");
                    tv_early.setText("早退人员（" + list_leave_early.size() + "人）");
                }
            }
        });
        rb_check_five.setChecked(true);
        rg_check.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int checkedId) {
                if (checkedId == rb_check_five.getId()) {
                    interval = 300;
                } else if (checkedId == rb_check_fifth.getId()) {
                    interval = 900;
                } else if (checkedId == rb_check_thirty.getId()) {
                    interval = 1800;
                }
            }
        });
        dialog.show();

    }

    private void dingWei() {
        btn_position_check.setClickable(false);
        btn_position_check.setBackground(getResources().getDrawable(
                R.drawable.login_btn_disabled));
        baiduLocation = new BaiduLocation(B_Side_Attence_Main_A3_Detail.this);
        baiduLocation.startLocation(true); // 10s的超时定位
        baiduLocation.setCallBack(new IGetLocation() {
            @Override
            public void successful(BDLocation loca) {
                if (isFinishing())
                    return;
                location = loca;
                baiduLocation.stopLocation(true);
                if (temph.equals("")) {
                    temph = "a";
                    beginAttendance();
                }
                baiduLocation = null;
            }

            @Override
            public void overTimeDo() {
                if (isFinishing())
                    return;
                temph = "";
                btn_position_check.setClickable(true);
                btn_position_check.setText("开始考勤");
                btn_position_check.setBackgroundResource(R.drawable.btn_login_hover);
                baiduLocation.stopLocation(true);
                A_0_App.getInstance().CancelProgreDialog(
                        B_Side_Attence_Main_A3_Detail.this);
                PubMehods.showToastStr(
                        B_Side_Attence_Main_A3_Detail.this,
                        R.string.str_attence_overtime);
                baiduLocation = null;
            }

            @Override
            public void failure(String msg) {
                if (isFinishing())
                    return;
                temph = "";
                btn_position_check.setClickable(true);
                btn_position_check.setText("开始考勤");
                btn_position_check.setBackgroundResource(R.drawable.btn_login_hover);
                baiduLocation.stopLocation(true);
                A_0_App.getInstance().CancelProgreDialog(
                        B_Side_Attence_Main_A3_Detail.this);
                PubMehods.showToastStr(
                        B_Side_Attence_Main_A3_Detail.this, msg);
                baiduLocation = null;
            }
        });


    }

    /*
     * 定时器，发送验证码用
     */
    private Timer mControlTimer;
    private long interval = 0;

    private void startContrlTimer() {
        mControlTimer = new Timer();
        mControlTimer.schedule(new timerControl(), 0, 1000);// 过1秒执行一个动作,单次的
    }

    private class timerControl extends TimerTask {
        @Override
        public void run() {
            Message mes = new Message();
            mes.what = 1;
            handcontrol.sendMessage(mes);
        }
    }

    private Handler handcontrol = new Handler() {
        @SuppressLint("NewApi")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    liner_titlebar_zui_right_attence.setVisibility(View.VISIBLE);
                    interval--;
                    if (interval < 1) {
                        btn_position_check.setText("已完成位置考勤");
                        btn_position_check.setClickable(false);
                        btn_position_check.setBackground(getResources()
                                .getDrawable(R.drawable.login_btn_disabled));
                        if (mControlTimer != null)
                            mControlTimer.cancel();
                    } else {
                        liner_position.setVisibility(View.VISIBLE);
                        btn_position_check.setText("位置考勤  " + "（"
                                + String.valueOf(interval) + " s）");
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    public class My_ListView_Aapter extends BaseAdapter {

        @Override
        public int getCount() {

            if (list_class != null)
                return list_class.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            ListViewHolder holder;
            if (converView == null) {
                holder = new ListViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_listview_list, null);

                holder.gridView_class = (MyGridView) converView
                        .findViewById(R.id.side_check_gridview_class);
                holder.tv_title = (TextView) converView
                        .findViewById(R.id.tv_side_check_name);
                holder.liner_check = (LinearLayout) converView
                        .findViewById(R.id.liner_check);
                converView.setTag(holder);
            } else {
                holder = (ListViewHolder) converView.getTag();
            }

            List<Cpk_Attence_Common> list = new ArrayList<Cpk_Attence_Common>();
            list = map.get(list_class.get(posi).getClass_id());
            ForegroundColorSpan greenSpan = new ForegroundColorSpan(getResources().getColor(R.color.GREENlIGHT));
            SpannableStringBuilder builder = new SpannableStringBuilder(list_class.get(posi).getName() + "  出勤（" + list.size() + "人）");
            builder.setSpan(greenSpan, list_class.get(posi).getName().length(), (list_class.get(posi).getName() + "  出勤（" + list.size() + "人）").length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tv_title.setText(builder);
            my_gridView_class_adapter = new My_GridView_Class_Aapter(list);
            holder.gridView_class.setAdapter(my_gridView_class_adapter);

            holder.liner_check.setVisibility(View.VISIBLE);

            return converView;
        }

    }

    private int times = 0;// 选择时间的区分

    public class My_ListView_Time_Aapter extends BaseAdapter {

        @Override
        public int getCount() {

            if (List_Times != null)
                return List_Times.size();
            return 0;
        }

        @Override
        public Object getItem(int v) {
            return v;
        }

        @Override
        public long getItemId(int v) {
            return v;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            final ListViewHolder holder;
            if (converView == null) {
                holder = new ListViewHolder();
                converView = LayoutInflater.from(
                        B_Side_Attence_Main_A3_Detail.this).inflate(
                        R.layout.item_check_listview_list_time, null);

                holder.tv_title = (TextView) converView
                        .findViewById(R.id.select_content);
                holder.imageView = (ImageView) converView
                        .findViewById(R.id.select_image);
                converView.setTag(holder);
            } else {
                holder = (ListViewHolder) converView.getTag();
            }
            if (times == posi) {
                time = List_Times.get(posi).getTime();
                interval = Integer.parseInt(List_Times.get(posi).getTime()) * 60;
                holder.imageView.setBackground(getResources().getDrawable(
                        R.drawable.button_radio_selected));
            } else {
                holder.imageView.setBackground(getResources().getDrawable(
                        R.drawable.button_radio));
            }
            holder.tv_title.setText(List_Times.get(posi).getName());
            converView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    times = posi;
                    time = List_Times.get(posi).getTime();
                    interval = Integer.parseInt(List_Times.get(posi).getTime()) * 60;
                    holder.imageView.setBackground(getResources().getDrawable(
                            R.drawable.button_radio_selected));
                    my_listView_time_adapter.notifyDataSetChanged();
                }
            });

            return converView;
        }

    }

    class ListViewHolder {
        MyGridView gridView_class;
        TextView tv_title;
        LinearLayout liner_check;
        ImageView imageView;
    }

    /**
     * 上传图片此处返回就上传图片并添加进度条 一张一张上传
     */
    private void upload_single(final String url) {

        A_0_App.getApi().upload_Photo(new File(url), new Inter_UpLoad_Photo() {

            @Override
            public void onSuccess(String imageUrl) {
                if (isFinishing())
                    return;
                if (Bimp.drr.size() < A_0_App.biaozhi || Bimp.drr.size() <= 0)
                    return;
                fileSize = 0;
                downloadSize = 0;
                A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi),
                        imageUrl);


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
                    my_grid_image_adapter.update();
                } else {
                    load_finish = 0;
                    first_getimage = 0;
                    my_grid_image_adapter.update();
                }


            }

            @Override
            public void onStart() {
                load_finish = 1;
                if (isFinishing())
                    return;
                fileSize = 0;
                downloadSize = 0;

                A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), "");
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
                my_grid_image_adapter.update();
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


                PubMehods.showToastStr(B_Side_Attence_Main_A3_Detail.this, msg);

                fileSize = 0;
                downloadSize = 0;
                A_0_App.map_url.put(Bimp.drr.get(A_0_App.biaozhi), url);
                A_0_App.biaozhi++;
                System.out.println(A_0_App.biaozhi + "失败"
                        + Bimp.drr.size());
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
                } else {
                    load_finish = 0;
                }
                my_grid_image_adapter.notifyDataSetChanged();
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
                        A_0_App.map_url.put(failure_url, imageUrl);
                        String path = "";
                        if (Bimp.drr.size() > 0) {
                            for (int i = 0; i < Bimp.drr.size(); i++) {
                                if (A_0_App.map_url.get(Bimp.drr.get(i)).contains("http")) {
                                    path = path + A_0_App.map_url.get(Bimp.drr.get(i)) + ",";
                                }
                            }
                        }
                        st_image_url = path;
                        if (st_image_url.length() > 0) {
                            st_image_url = st_image_url.substring(0,
                                    st_image_url.length() - 1);
                        }
                        my_grid_image_adapter.update();
                        UploadImage();
                    }

                    @Override
                    public void onStart() {
                        load_finish = 1;
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
                        my_grid_image_adapter.update();
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
                        load_finish = 0;
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_A3_Detail.this, msg);
                        fileSize = 0;
                        downloadSize = 0;
                        A_0_App.map_url.put(failure_url, url);
                        my_grid_image_adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }


    private void postAttdenceStatus(String type, String user_id) {
        A_0_App.getApi().SideAttdenceStatus(attence_detail.getAtd_id(),
                user_id, type, new InterSideAttdenceStatus() {

                    @Override
                    public void onSuccess() {
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_A3_Detail.this, "标记成功");
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_A3_Detail.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    /**
     * 开始考勤
     */
    private void beginAttendance() {

        A_0_App.getApi().SideBeginAttdence(attence_atd_id, time,
                location.getLatitude() + "", location.getLongitude() + "",
                new InterSideAttdenceStatus() {

                    @Override
                    public void onSuccess() {
                        startContrlTimer();
                        A_0_App.getInstance().CancelProgreDialog(
                                B_Side_Attence_Main_A3_Detail.this);
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_A3_Detail.this, "考勤开始");
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        temph = "";
                        PubMehods.showToastStr(
                                B_Side_Attence_Main_A3_Detail.this, msg);
                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });

    }

    /**
     * 时间列表
     */
    private void selectTime() {
        A_0_App.getApi().SideSelectAttdenceTime(
                new InterSideSelectAttdenceTime() {

                    @Override
                    public void onSuccess(
                            List<Cpk_Attence_List_Time> attence_List_Times) {
                        List_Times = attence_List_Times;
                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub

                    }
                });
    }

    /**
     * 图片上传
     */
    private void UploadImage() {
        A_0_App.getApi().UploadAttdenceImage(attence_atd_id, st_image_url,
                new InterUploadAttdenceImage() {

                    @Override
                    public void onSuccess() {
                        load_finish = 0;
                        if (delete_biaoshi == 0) {

//							PubMehods.showToastStr(
//									B_Side_Attence_Main_A3_Detail.this,
//									"上传图片成功！");
                        } else {
                            load = 1;
                            my_grid_image_adapter.update();
                            PubMehods.showToastStr(
                                    B_Side_Attence_Main_A3_Detail.this,
                                    "删除图片成功！");

                        }

                    }
                }, new Inter_Call_Back() {

                    @Override
                    public void onFinished() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void onFailure(String msg) {
                        load_finish = 0;
                        if (delete_biaoshi == 0) {

                            PubMehods.showToastStr(
                                    B_Side_Attence_Main_A3_Detail.this,
                                    msg);
                        } else {
                            load = 1;
                            my_grid_image_adapter.update();
                            PubMehods.showToastStr(
                                    B_Side_Attence_Main_A3_Detail.this,
                                    msg);

                        }
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
                    // A_0_App.getInstance().showExitDialog(
                    // B_Side_Attence_Main_A3_Detail.this,
                    // getResources().getString(R.string.token_timeout));
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
                                    B_Side_Attence_Main_A3_Detail.this,
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
        super.onResume();
        if (Bimp.drr.size() > A_0_App.map_url.size() && Bimp.add_edit.equals("4")) {
            first_getimage = 0;
            load = 0;
            load_finish = 1;
            my_grid_image_adapter.update();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        drawable.stop();
        drawable = null;
        if (baiduLocation != null) {
            baiduLocation.appOnDestroy();
        }
        clear();
    }

    void clear() {
        Bimp.act_bool = true;
        Bimp.drr.clear();
        Bimp.bmp.clear();
        Bimp.max = 0;
        my_gridView_lack_adapter = null;
        my_gridView_late_adapter = null;
        my_gridView_leave_early_adapter = null;
        my_gridView_leave_adapter = null;
        my_gridView_class_adapter = null;
        my_gridView_mark_adapter = null;
        my_listView_adapter = null;
        my_listView_time_adapter = null;
        list_all.clear();
        list_lack.clear();
        list_late.clear();
        list_leave_early.clear();
        list_leave.clear();
        list_class.clear();
        list_type.clear();
        list_type_temp.clear();
        map.clear();
        my_grid_image_adapter = null;
        Bimp.add_edit = "";
        A_0_App.map_url.clear();
        A_0_App.biaozhi = 0;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    //System.exit(0);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (load_finish == 0) {
                                clear();
                                finish();
                            }
                        }
                    }, 500);

                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

}
