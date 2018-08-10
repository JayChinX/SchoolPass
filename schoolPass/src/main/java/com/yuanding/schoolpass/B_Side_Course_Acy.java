package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_CourseDetail;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_Week;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_WeekDetail;
import com.yuanding.schoolpass.bean.Cpk_User_Values;
import com.yuanding.schoolpass.service.Api.InteSideCourse;
import com.yuanding.schoolpass.service.Api.InterReportTaskSent;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_UpLoad_ChannelId;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.LogUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.WheelView;
import com.yuanding.schoolpass.view.frament.TitleIndicator;
/**
 * 
* @ClassName: B_Side_Course_Acy
* @Description: TODO(身边中的课程表)
* @author Jiaohaili 
* @date 2016年3月7日 下午3:32:31
*
 */
public class B_Side_Course_Acy extends Activity {
	
//	private SharedPreferences sp;
    public static int[] bg_course = {R.drawable.bg_course_01, R.drawable.bg_course_02, R.drawable.bg_course_03,
            R.drawable.bg_course_04};
    
    // 五种颜色的背景
    public int[] background = {
            R.drawable.bg_course_1, R.drawable.bg_course_2,R.drawable.bg_course_3,
            R.drawable.bg_course_4, R.drawable.bg_course_5};

    public static final String INTERT_COURSE_DTAIL = "side_course_detail";
	private String[] array_Week;
	private String STR_CURRENT_WEEK;//当前周名称___定值
	private int INT_CURRENT_WEEK_POSTION;//当前周在allWeek中的位置___定值
	
	private int temp_current_week_index = -1;//切换其他周
	private int temp_dialog_selected = -1;//临时数据传递
	
	/** 第一个无内容的格子 */
	protected TextView monthColum;
	/** 星期一、二、三的格子 */
	protected TextView monColum,tueColum,wedColum;
	/** 星期四、五、六、日的格子 */
	protected TextView thrusColum,friColum,satColum,sunColum;
	
	/** 课程表body部分布局 */
	protected RelativeLayout course_table_layout,rela_couse_bg;
	/** 屏幕宽度 **/
	protected int screenWidth;
	/** 课程格子平均宽度 **/
	protected int aveWidth;
	private LinearLayout liner_titlebar_back,liner_change_add,liner_change_share,liner_select_week;
	private TextView tv_titlebar_title;
	private boolean firstLoad = false;// 第一次进入
	private View side_course_load_error,liner_side_course_whole,side_course_loading,side_course_no_content;
	private int height,gridHeight;
	private Button btn_title_zuiyou_text_course;
	
	private Cpk_Side_Course_Week mCurrentWeek;
	private List<Cpk_Side_Course_WeekDetail> mSingleWeek;
	private List<Cpk_Side_Course_CourseDetail> mListCourse;
	
    private static final int DAY_TOTLE_NODE_NUM = 12;
	private String current_day_Str = "今天";//当前日期周的透明度设置
    private int current_day_Alpha = 34;//当前日期周的透明度设置
    private int no_current_day_Alpha = 170;//非当前日期周的透明度设置
    
    private int course_zui_left_Alpha = 170;//当前课堂节数(1~11)的透明度
    
    private int course_Alpha = 200;//当前课程的透明度
    
    private String totle_weeks,semester_id ="";
    private String course_id,course_user_uniqid,share_time = "";//分享进入新增加字段
    private String select_week = "";// 用户选择需要请求数据的周数,为空表示当前周
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private boolean havaSuccessLoadData = false;
    private int acy_type;//页面类型   正常进入11，分享课表进入10,推送进入1，校务助手12
    private String img_course,message_id = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_side_course_main);
		
        if (getIntent().getExtras() != null) {
            acy_type = getIntent().getExtras().getInt("acy_type",11);
            if (acy_type == 10) {// 分享课表进入
                course_id = getIntent().getExtras().getString("course_id","");
                course_user_uniqid = getIntent().getExtras().getString("course_user_uniqid","");
                share_time = getIntent().getExtras().getString("share_time","");
            } else if (acy_type == 11) {// 正常进入
                img_course = getIntent().getExtras().getString("img_course","");
            } else if (acy_type == 12) {// 校务助手进入
                img_course = getIntent().getExtras().getString("img_course","");
                message_id = getIntent().getExtras().getString("message_id","");
            }else if (acy_type == 1){//推送进入
                img_course = getIntent().getExtras().getString("img_course","");
            }
        }
        
		mCurrentWeek = new Cpk_Side_Course_Week();
		mSingleWeek = new ArrayList<Cpk_Side_Course_WeekDetail>();
		mListCourse = new ArrayList<Cpk_Side_Course_CourseDetail>();
		
		rela_couse_bg = (RelativeLayout) this.findViewById(R.id.rela_couse_bg);
		liner_titlebar_back = (LinearLayout) this.findViewById(R.id.liner_titlebar_back);
		tv_titlebar_title = (TextView) this.findViewById(R.id.tv_titlebar_title);
		liner_change_add = (LinearLayout) this.findViewById(R.id.liner_change_add);
		liner_change_share = (LinearLayout) this.findViewById(R.id.liner_change_share);
		liner_select_week = (LinearLayout) this.findViewById(R.id.liner_select_week);
		btn_title_zuiyou_text_course = (Button) this.findViewById(R.id.btn_title_zuiyou_text_course);
		
		side_course_load_error = findViewById(R.id.side_course_load_error);
		liner_side_course_whole = findViewById(R.id.liner_side_course_whole);
		side_course_loading = findViewById(R.id.side_course_loading);
		side_course_no_content = findViewById(R.id.side_course_no_content);
		
		home_load_loading = (LinearLayout) side_course_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
		
	    ImageView iv_blank_por = (ImageView)side_course_no_content.findViewById(R.id.iv_blank_por);
	    TextView tv_blank_name = (TextView)side_course_no_content.findViewById(R.id.tv_blank_name);
	    iv_blank_por.setBackgroundResource(R.drawable.no_kebiao);
	    tv_blank_name.setText("暂时没有数据~");

	    side_course_load_error.setOnClickListener(onClick);
	    liner_select_week.setOnClickListener(onClick);
	    
//	    readBgPic();//获取默认背景图片
		firstLoad = true;
		inItCourseTable();//加载表格信息
	
		liner_change_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (totle_weeks != null && !totle_weeks.equals("")) {
                    Intent intent = new Intent(B_Side_Course_Acy.this, B_Side_Course_Add.class);
                    intent.putExtra("totle_weeks", totle_weeks);
                    intent.putExtra("semester_id", semester_id);
                    intent.putExtra("acy_enter", 1);
                    startActivity(intent);
                } else {
                    PubMehods.showToastStr(B_Side_Course_Acy.this, "总周数为空");
                }
            }
        });
		
        liner_change_share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (img_course == null) {
                    img_course = "";
                }
                Intent intent = new Intent(B_Side_Course_Acy.this, B_Mess_Forward_Select.class);
                intent.putExtra("title", A_0_App.USER_NAME + "的课程表");
                intent.putExtra("content", "点一下即可复制到我的课表哦！");
                intent.putExtra("type", "6");//首页打开页面的类型
                intent.putExtra("image", img_course);
                intent.putExtra("acy_type", "11");
                intent.putExtra("noticeId", course_id);
                intent.putExtra("course_user_uniqid", A_0_App.USER_UNIQID);
                startActivity(intent);
            }
        });
        
        btn_title_zuiyou_text_course.setOnClickListener(new OnClickListener() {//复制课表
            @Override
            public void onClick(View arg0) {
                if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
                  showCopyCourseDialog();
                }
            }
        });
		
		liner_titlebar_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});
		
        if (A_0_App.USER_STATUS != null && A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
            readCache();
        } else {
            liner_change_add.setVisibility(View.GONE);
            liner_change_share.setVisibility(View.GONE);
            liner_select_week.setVisibility(View.GONE);
            showLoadResult(false, false, false, true);
        }
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void inItCourseTable() {
	 // 获得列头的控件
        monthColum = (TextView) this.findViewById(R.id.test_empty);
        monColum = (TextView) this.findViewById(R.id.test_monday_course);
        tueColum = (TextView) this.findViewById(R.id.test_tuesday_course);
        wedColum = (TextView) this.findViewById(R.id.test_wednesday_course);
        thrusColum = (TextView) this.findViewById(R.id.test_thursday_course);
        friColum = (TextView) this.findViewById(R.id.test_friday_course);
        satColum = (TextView) this.findViewById(R.id.test_saturday_course);
        sunColum = (TextView) this.findViewById(R.id.test_sunday_course);
        course_table_layout = (RelativeLayout) this.findViewById(R.id.test_course_rl);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 屏幕宽度
        int width = dm.widthPixels;
        // 平均宽度
        final int aveWidth = width / 15 * 2;
        // 第一个空白格子设置为25宽
        monthColum.setWidth(aveWidth / 2);
        monthColum.setHeight(aveWidth);

        monColum.setWidth(aveWidth);
        monColum.setHeight(aveWidth);

        tueColum.setWidth(aveWidth);
        tueColum.setHeight(aveWidth);

        wedColum.setWidth(aveWidth);
        wedColum.setHeight(aveWidth);

        thrusColum.setWidth(aveWidth);
        thrusColum.setHeight(aveWidth);

        friColum.setWidth(aveWidth);
        friColum.setHeight(aveWidth);

        satColum.setWidth(aveWidth);
        satColum.setHeight(aveWidth);

        sunColum.setWidth(aveWidth);
        sunColum.setHeight(aveWidth);

        monthColum.setBackgroundColor(getResources().getColor(R.color.white));
        monColum.setBackgroundColor(getResources().getColor(R.color.white));
        tueColum.setBackgroundColor(getResources().getColor(R.color.white));
        wedColum.setBackgroundColor(getResources().getColor(R.color.white));
        thrusColum.setBackgroundColor(getResources().getColor(R.color.white));
        friColum.setBackgroundColor(getResources().getColor(R.color.white));
        satColum.setBackgroundColor(getResources().getColor(R.color.white));
        sunColum.setBackgroundColor(getResources().getColor(R.color.white));

        monthColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        monColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        tueColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        wedColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        thrusColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        friColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        satColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
        sunColum.getBackground().mutate().setAlpha(no_current_day_Alpha);

        this.screenWidth = width;
        this.aveWidth = aveWidth;
        height = dm.heightPixels;
        gridHeight = aveWidth;

    }
	
    // 读取默认背景图片
    /*private void readBgPic() {
        sp = getSharedPreferences("wxb", Context.MODE_PRIVATE);
        if (!sp.getString("select", "").equals(null) && !sp.getString("select", "").equals("")) {
            File file = new File(sp.getString("select", ""));
            if (file.exists()) {
                Bitmap bm = BitmapFactory.decodeFile(sp.getString("select", ""));
                // 将图片显示到ImageView中
                Drawable drawable = new BitmapDrawable(bm);
                rela_couse_bg.setBackgroundDrawable(drawable);
            }
        }
    }*/
	
    private ACache maACache;
    private JSONObject jsonObject;
    private void readCache() {
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_b_side_course + A_0_App.USER_UNIQID + select_week);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }
        updateInfo();
    }
    
	private void showInfo(JSONObject jsonObject) {
	    havaSuccessLoadData = true;
        try {
//            JSONObject jsonObject = new JSONObject(result);
            int state = jsonObject.optInt("status");
            String message = jsonObject.optString("msg");
            String now_week = jsonObject.optString("curr_week");
            String totle_weeks = jsonObject.optString("count_week");
            String semester_id = jsonObject.optString("semester_id");
            String semester_status = jsonObject.optString("semester_status");
            String semester_msg = jsonObject.optString("semester_msg");
            if (state == 1) {
                JSONObject jsonArrayItem = jsonObject.optJSONObject("clist");
                Cpk_Side_Course_Week item_AA = null ;
                if (jsonArrayItem != null && jsonArrayItem.length() > 0) {
                    item_AA = new Cpk_Side_Course_Week();
                    String request_Week = jsonArrayItem.optString("week");
                    item_AA.setWeek(request_Week);
                    String month = jsonArrayItem.optString("month");
                    item_AA.setMonth(month);
                    item_AA.setDetail_id(jsonArrayItem.optString("detail_id"));
                    item_AA.setCourse_id(jsonArrayItem.optString("course_id"));
                    
                    List<Cpk_Side_Course_WeekDetail> listBB = new ArrayList<Cpk_Side_Course_WeekDetail>();
                    JSONArray itemOne = jsonArrayItem.optJSONArray("weekly_list");
                    if (itemOne != null && itemOne.length() > 0) {
                        for (int j = 0; j < itemOne.length(); j++) {
                            Cpk_Side_Course_WeekDetail item_BB = new Cpk_Side_Course_WeekDetail();
                            String date = itemOne.getJSONObject(j).optString("date");
                            item_BB.setDate(date);
                            String weekday = itemOne.getJSONObject(j).optString("weekday");
                            item_BB.setWeekday(weekday);
                            item_BB.setMonth(month);
                            List<Cpk_Side_Course_CourseDetail> listCC= new ArrayList<Cpk_Side_Course_CourseDetail>();
                            JSONArray dd = itemOne.getJSONObject(j).optJSONArray("course");
                            if (dd != null && dd.length() > 0) {
//                                    listCC = JSON.parseArray(dd+"",Cpk_Side_Course_CourseDetail.class);
                                for (int k = 0; k < dd.length(); k++) {
                                    Cpk_Side_Course_CourseDetail itemCC = new Cpk_Side_Course_CourseDetail();
                                    itemCC.setCoursecode(dd.getJSONObject(k).optString("coursecode"));
                                    itemCC.setCoursename(dd.getJSONObject(k).optString("coursename"));
                                    itemCC.setEndsection(dd.getJSONObject(k).optInt("endsection"));
                                    itemCC.setStartsection(dd.getJSONObject(k).optInt("startsection"));
                                    itemCC.setTeachername(dd.getJSONObject(k).optString("teachername"));
                                    
                                    itemCC.setPlace(dd.getJSONObject(k).optString("place"));
                                    itemCC.setCourseweek(dd.getJSONObject(k).optString("courseweek"));
                                    itemCC.setCm_id(dd.getJSONObject(k).optString("cm_id"));
                                    itemCC.setToday(Integer.parseInt(weekday));
                                    itemCC.setWeekday_id(dd.getJSONObject(k).optString("weekday_id"));
                                    itemCC.setDate(date);
                                    
                                    String organ_names = dd.getJSONObject(k).optString("organ_names");
                                    List<String> mList = new ArrayList<String>();
                                    if (organ_names != null&& !organ_names.equals("") && organ_names.length() > 0)
                                    {
                                        String[] temp = organ_names.split(",");
                                        for (int i = 0; i < temp.length; i++) {
                                            mList.add(temp[i]);
                                        }
                                    }
                                    
                                    itemCC.setOrgan_names(mList);
                                    
                                    List<Cpk_User_Values> list_values = new ArrayList<Cpk_User_Values>();
                                    list_values = JSON.parseArray(dd.getJSONObject(k).optJSONArray("course_detail") + "", Cpk_User_Values.class);
                                    itemCC.setUser_values(list_values);
                                    
                                    itemCC.setClass_ids(dd.getJSONObject(k).optString("class_ids"));
                                    
                                    listCC.add(itemCC);
                                 }
                            }
                            item_BB.setCourse(listCC);
                            listBB.add(item_BB);
                        }
                    }
                    item_AA.setWeekly_list(listBB);
//                    ACache.get(context).put(AppStrStatic.cache_key_b_side_course+A_0_App.USER_UNIQID+week,jsonObject);
                }
                onSuccessData(item_AA, totle_weeks,now_week,item_AA.getWeek(),semester_id,true,semester_msg,semester_status);
            }
        } catch (JSONException e) {
           PubMehods.showToastStr(B_Side_Course_Acy.this, "解析失败");
        }
	}
	
	private void updateInfo(){
        MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
        updateLectureInfo.execute();
    }
	
    private void readData(String week,String courseId,String course_user_uniqid,String share_time,String message_id) {
        A_0_App.getApi().getSideCourse(B_Side_Course_Acy.this, A_0_App.USER_TOKEN,week,courseId,course_user_uniqid,share_time,message_id,
                new InteSideCourse() {
                    @Override
                    public void onSuccess(Cpk_Side_Course_Week course_week,String totle_weeks,String now_week,String semester_id,
                            String semester_msg,String semester_status) {
                        if (isFinishing())
                            return;
                        if(course_week != null){
                            course_id = course_week.getCourse_id();
                            onSuccessData(course_week,totle_weeks,now_week,course_week.getWeek(),semester_id, false,semester_msg,semester_status);
                        }else{
                            tv_titlebar_title.setText(R.string.str_course_name);
                            liner_change_add.setVisibility(View.GONE);
                            liner_change_share.setVisibility(View.GONE);
                            liner_select_week.setVisibility(View.GONE);
                            showLoadResult(false, false, false, true);
                         }
                    }
                }, new Inter_Call_Back() {
                    @Override
                    public void onFinished() {}
                    
                    @Override
                    public void onFailure(String msg) {
                        if (isFinishing())
                            return;
                        if (!havaSuccessLoadData) {
                            showLoadResult(false, false, true, false);
                        }
                        PubMehods.showToastStr(B_Side_Course_Acy.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {}
                });
    }
    
    // 复制课表
    private void copyCourse(String token, String share_time, String course_user_uniqid,String semester_id,String course_id) {
        A_0_App.getInstance().showProgreDialog(B_Side_Course_Acy.this, "", true);
        A_0_App.getApi().copyOtherCourse(token, share_time, course_user_uniqid,semester_id,course_id, new Inter_UpLoad_ChannelId() {
            @Override
            public void onSuccess(String msg) {
                if (isFinishing())
                    return;
                acy_type = 11;
                btn_title_zuiyou_text_course.setVisibility(View.GONE);
                liner_change_add.setVisibility(View.VISIBLE);
                liner_change_share.setVisibility(View.VISIBLE);
                liner_select_week.setVisibility(View.VISIBLE);
                
                updateInfo();
                PubMehods.showToastStr(B_Side_Course_Acy.this, msg);
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Acy.this);
            }
        }, new Inter_Call_Back() {
            @Override
            public void onFinished() {
            }

            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Side_Course_Acy.this, msg);
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Acy.this);
            }

            @Override
            public void onCancelled() {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Acy.this);
            }
        });
    }
	 
    private void onSuccessData(Cpk_Side_Course_Week course_week,String totle_week,String now_week,String noWeek,
            String semesterId,boolean readCache,String semester_msg,String semester_status) {
        semester_id = semesterId;
        if(STR_CURRENT_WEEK == null || STR_CURRENT_WEEK.equals("")){
            STR_CURRENT_WEEK = now_week;
        }
        totle_weeks = totle_week;
        if(isFinishing())
            return;
        havaSuccessLoadData = true;
        if(STR_CURRENT_WEEK.equals("0")){
            PubMehods.showToastStr(B_Side_Course_Acy.this, R.string.str_course_no_plan);
        }
        if (course_week != null) {
            if(acy_type == 10){//分享课表进入
                btn_title_zuiyou_text_course.setVisibility(View.VISIBLE);
                liner_change_add.setVisibility(View.GONE);
                liner_change_share.setVisibility(View.GONE);
                liner_select_week.setVisibility(View.VISIBLE);
            }else if(acy_type == 11 ||acy_type == 1||acy_type == 12){//正常进入、推送进入
                btn_title_zuiyou_text_course.setVisibility(View.GONE);
                liner_change_add.setVisibility(View.VISIBLE);
                liner_change_share.setVisibility(View.VISIBLE);
                liner_select_week.setVisibility(View.VISIBLE);
            }
            
            clearData(false);
            clearWeekData(false);

            mCurrentWeek = course_week;
            if (temp_current_week_index >= 0 && temp_current_week_index != INT_CURRENT_WEEK_POSTION) {
                tv_titlebar_title.setText("第" + (temp_current_week_index + 1) + "周");
            } else {
                tv_titlebar_title.setText(showCurrentWeek(String.valueOf(now_week)));
            }
            mSingleWeek = mCurrentWeek.getWeekly_list();// 获取当前周数据
            init(mSingleWeek, readCache,noWeek);// 加载当前周数据

            getAllWeek(totle_weeks,now_week);//获取标题栏所有周
            showLoadResult(false,true, false,false);
        }else{
            liner_select_week.setVisibility(View.GONE);
            liner_change_add.setVisibility(View.GONE);
            liner_change_share.setVisibility(View.GONE);
            showLoadResult(false,false, false,true);
        }
        rela_couse_bg.setVisibility(View.VISIBLE);
        
        //未开始、进行中、已结束    1,2,3
        if(!readCache){
            if (semester_status.equals("1")||semester_status.equals("3")) {
                if(!getUserTips(semesterId, semester_status)){
                    showUserTitleDialog(semester_msg, semesterId, semester_status);
                }
            }
        }
        
    }
	
    private void init(final List<Cpk_Side_Course_WeekDetail> mSingleWeek,boolean readCache,final String no_Week) {
        course_table_layout.removeAllViews();
        for (int i = 1; i <= DAY_TOTLE_NODE_NUM; i++) {// 12表示一天的课节数
            for (int j = 1; j <= 8; j++) {// 8表示8列
                TextView tx = new TextView(B_Side_Course_Acy.this);
                tx.setTextSize(13);
                tx.setId((i - 1) * 8 + j);
                // 除了最后一列，都使用course_text_view_bg背景（最后一列没有右边框）
                if (i == 1) {
                    if (j > 2 && j < 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_bottom));
                    } else if (j == 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_14));
                    } else if (j == 2) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_13));
                    }

                } else if (i > 1 && i < DAY_TOTLE_NODE_NUM) {
                    if (j > 2 && j < 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt2));
                    } else if (j == 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_left));
                    } else if (j == 2) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_right));
                    }
                } else {
                    if (j > 2 && j < 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_top));
                    } else if (j == 8) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_11));
                    } else if (j == 2) {
                        tx.setBackgroundDrawable(B_Side_Course_Acy.this.getResources().getDrawable(R.drawable.jia_dt_12));
                    }
                }

                // 相对布局参数
                RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(aveWidth + 2, aveWidth);
                // 文字对齐方式
                tx.setGravity(Gravity.CENTER);
                // 字体样式
                tx.setTextAppearance(B_Side_Course_Acy.this, R.style.courseTableText);
                // 如果是第一列，需要设置课的序号（1 到 DAY_TOTLE_NODE_NUM）
                if (j == 1) {
                    tx.setText(String.valueOf(i));
                    tx.setBackgroundColor(getResources().getColor(R.color.white));
                    tx.getBackground().mutate().setAlpha(course_zui_left_Alpha);
                    
                    Drawable drawable = getResources().getDrawable(R.drawable.side_course_line);
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight()); //设置边界
                    tx.setCompoundDrawables(null, drawable, null, null);//下面的线
                    
                    rp.width = aveWidth / 2;
                    // 设置他们的相对位置
                    if (i == 1)
                        rp.addRule(RelativeLayout.BELOW, monthColum.getId());
                    else
                        rp.addRule(RelativeLayout.BELOW, (i - 1) * 8);
                } else {
                    rp.addRule(RelativeLayout.RIGHT_OF, (i - 1) * 8 + j - 1);
                    rp.addRule(RelativeLayout.ALIGN_TOP, (i - 1) * 8 + j - 1);
                    tx.setText("");
                }
                
                tx.setLayoutParams(rp);
                course_table_layout.addView(tx);
                
                if (acy_type == 11 || acy_type == 1||acy_type == 12) {//正常和推送进入做监听
                    if(!removeLeftNodeListener(tx.getText().toString(), DAY_TOTLE_NODE_NUM)){
                        if (i% 2 == 1) {//奇数
                            tx.setTag(R.id.add_start_note, i);//开始节数
                            tx.setTag(R.id.add_end_note, i + 1);//结束节数
                        } else if (i % 2 == 0) {//偶数
                            tx.setTag(R.id.add_start_note, i - 1);//开始节数
                            tx.setTag(R.id.add_end_note, i );//结束节数
                        }
                        tx.setTag(R.id.add_week_day, (j - 1));//周数
                        tx.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (totle_weeks != null && !totle_weeks.equals("")) {
                                    Intent intent = new Intent(B_Side_Course_Acy.this, B_Side_Course_Add.class);
                                    intent.putExtra("totle_weeks", totle_weeks);
                                    intent.putExtra("semester_id", semester_id);
                                    intent.putExtra("acy_enter", 3);
                                    //周几  第几周   开始、结束节数
                                    intent.putExtra("add_week", no_Week);
                                    intent.putExtra("add_start_note", String.valueOf(v.getTag(R.id.add_start_note)));
                                    intent.putExtra("add_end_note", String.valueOf(v.getTag(R.id.add_end_note)));
                                    intent.putExtra("add_week_day", String.valueOf(v.getTag(R.id.add_week_day)));
                                    startActivity(intent);
                                } else {
                                    PubMehods.showToastStr(B_Side_Course_Acy.this, "总周数为空");
                                }
                            }
                        });
                    }
                }
            }
        }
        
        /*************************************绘画当前周的课程*日期*月份*****************************************/
        if (mSingleWeek == null || mSingleWeek.size() <= 0) {
            if (!readCache)
                PubMehods.showToastStr(B_Side_Course_Acy.this, "本周没有制定课程");
            return;
        }
        
        String monthStr = mSingleWeek.get(0).getMonth();
        if (monthStr != null && monthStr.length() > 0)// 显示月份
            monthColum.setText(PubMehods.subMonthAdd(monthStr));
        
        List<Cpk_Side_Course_CourseDetail> allCourse = collectAllCourse(mSingleWeek,no_Week);
        if (allCourse.size() <= 0) {
            if (!readCache)
                PubMehods.showToastStr(B_Side_Course_Acy.this, "本周没有制定课程");
            return;
        }
        logE(allCourse.size() + "-课数-所有--本周-------");
        // 开始所有的课程信息
        for (int h = 0; h < allCourse.size(); h++) {
            Cpk_Side_Course_CourseDetail courese_Detail = new Cpk_Side_Course_CourseDetail();
            courese_Detail = allCourse.get(h);
            showCourseDetail(h,courese_Detail);
        }
    }

    private void showCourseDetail(int posi,final Cpk_Side_Course_CourseDetail courese_Detail) {
        
        TextView courseInfo = new TextView(this);
//        courseInfo.setText(courese_Detail.getCoursename() + "\n" + courese_Detail.getPlace());//课程名字 +上课地址    修改2：不要地址和老师
        courseInfo.setText(courese_Detail.getCoursename());//课程名字 +上课地址
        courseInfo.setPadding(4, 2, 4, 2);
        // 该textview的高度根据其节数的跨度来设置
        if (courese_Detail.getEndsection() > 13) {
            courese_Detail.setEndsection(12);
        }
        int during_lesstion = courese_Detail.getEndsection() - courese_Detail.getStartsection() + 1;
        if (courese_Detail.getStartsection() == courese_Detail.getEndsection()) {
            courseInfo.setMaxLines(3);
        }else{
            courseInfo.setMaxLines(5);
        }
        courseInfo.setEllipsize(TruncateAt.END);
        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(aveWidth + 2,
                (gridHeight) * (during_lesstion));// 本堂课所占的节数
        // textview的位置由课程开始节数和上课的时间（day of week）确定
        rlp.topMargin = (courese_Detail.getStartsection() - 1) * gridHeight + 2;// 本堂课开始的节数
        rlp.rightMargin = 2;
        rlp.leftMargin = 2;
        rlp.bottomMargin = 2;
        // 偏移由这节课是星期几决定
        rlp.addRule(RelativeLayout.RIGHT_OF, courese_Detail.getToday());// 周几
        // 字体剧中
//        courseInfo.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL);  修改2：不要地址和老师
        courseInfo.setGravity(Gravity.CENTER);
        // 设置一种背景
//        courseInfo.setBackgroundResource(background[PubMehods.getRandomIntData(background.length)]);//设置课程背景颜色
        sortGetBg(courseInfo, background.length, posi);
        courseInfo.setTextSize(12);
        courseInfo.setLayoutParams(rlp);
        courseInfo.setTextColor(Color.WHITE);
        // 设置不透明度
        courseInfo.getBackground().mutate().setAlpha(course_Alpha);
        course_table_layout.addView(courseInfo);

        
//        TextView courseInfo2 = new TextView(B_Side_Course_Acy.this);
//        courseInfo2.setText(courese_Detail.getTeachername());// 上课老师
//        // 该textview的高度根据其节数的跨度来设置
//        RelativeLayout.LayoutParams rlp1 = new RelativeLayout.LayoutParams(aveWidth,
//                (gridHeight)* (during_lesstion));// 本堂课所占的节数
//       // textview的位置由课程开始节数和上课的时间（day of week）确定
//        rlp1.topMargin = (courese_Detail.getStartsection() - 1) * gridHeight + 2;
//        rlp1.rightMargin = 2;
//        rlp1.leftMargin = 2;
//        rlp1.bottomMargin = 2;
//        // 偏移由这节课是星期几决定
//        rlp1.addRule(RelativeLayout.RIGHT_OF, courese_Detail.getToday());// 星期几
//        // 字体剧中
//        courseInfo2.setBackgroundColor(getResources().getColor(R.color.transparent));
//        courseInfo2.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL);
//        courseInfo2.setPadding(4, 2, 4, 2);
//        // 设置一种背景
//        courseInfo2.setTextSize(11);//上课老师
//        courseInfo2.setLayoutParams(rlp1);
//        courseInfo2.setTextColor(Color.WHITE);
//        // 设置不透明度
//        // btn2 位于 btn1 的下方、其左边和 btn1 的左边对齐
//        course_table_layout.addView(courseInfo2);

        course_table_layout.setBackgroundColor(getResources().getColor(R.color.transparent));
        courseInfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (acy_type != 10) {
                    Intent intent = new Intent(B_Side_Course_Acy.this, B_Side_Course_Detail.class);
                    Bundle mBundle = new Bundle();  
                    mBundle.putSerializable(INTERT_COURSE_DTAIL,courese_Detail);  
                    mBundle.putString("detail_id", mCurrentWeek.getDetail_id());
                    intent.putExtra("totle_weeks", totle_weeks);
                    intent.putExtra("semester_id", semester_id);
                    intent.putExtras(mBundle);
                    startActivity(intent);
                }
            }
        });
    }
    
    // 获取所有的周
    private void getAllWeek(String totle_weeks,String now_week) {
        array_Week = null;
        List list = new ArrayList();
        String[] array_Week_Temp = new String[Integer.parseInt(totle_weeks)];
        for (int i = 0; i < array_Week_Temp.length; i++) {
            array_Week_Temp[i] = String.valueOf(i + 1);
            if (now_week.equals(array_Week_Temp[i])) {
                if (temp_current_week_index < 0)
                    temp_current_week_index = i;
                INT_CURRENT_WEEK_POSTION = i;
                list.add(showCurrentWeek(now_week));
            }else{
                list.add("第" + array_Week_Temp[i] + "周");
            }
        }
        final int size = list.size();
        array_Week = (String[]) list.toArray(new String[size]);
    }
	 
    //根据第几周查找当前周信息
    private int searchCurrentWeekPosi(List<Cpk_Side_Course_Week> mList, String now_week) {
        for (int i = 0; i < mList.size(); i++) {
            if (now_week.equals(mList.get(i).getWeek())) {
                return i;
            }
        }
        return -1;
    }
    
    private String showCurrentWeek(String now_week) {
        return "第" + now_week + "周(本周)";
    }
    
    private boolean removeLeftNodeListener(String str, int num) {
        if (str == null)
            str = "";
        for (int i = 1; i <= num; i++) {
            if (str.equals(i + "")) {
                return true;
            }
        }
        return false;
    }
    
    //获取课程节数背景颜色
    private void sortGetBg(TextView courseInfo,int totle,int index) {
        if (index % totle == 0) {
            courseInfo.setBackgroundResource(background[0]);
        } else if (index % totle == 1) {
            courseInfo.setBackgroundResource(background[1]);
        } else if (index % totle == 2) {
            courseInfo.setBackgroundResource(background[2]);
        } else if (index % totle == 3) {
            courseInfo.setBackgroundResource(background[3]);
        } else if (index % totle == 4) {
            courseInfo.setBackgroundResource(background[4]);
        }
    }
    
    private void showDateAndWeekDay(Cpk_Side_Course_WeekDetail courese_Detail,String no_Week) {
        
        String today_course = courese_Detail.getWeekday();
        String tempMonth = courese_Detail.getMonth();
        String tempDate = courese_Detail.getDate();
        String oldDate, interval_Month = "", interval_Date = "";
        
        if (tempMonth != null && tempMonth.length() > 0 && tempDate != null && tempDate.length() > 0) {
            if (tempMonth.length() <= 2) {
                interval_Month = "0";
            } 
            if (tempDate.length() <= 2) {
                interval_Date = "0";
            } 
        }
        oldDate = interval_Month + tempMonth + interval_Date + tempDate;
        
        if ("1".equals(today_course)) {
            monColum.setText(courese_Detail.getDate() + "\n周一");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    monColum.setText(current_day_Str + "\n周一");
                    monColum.setTextColor(getResources().getColor(R.color.main_color));
                    monColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    monColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                monColum.setBackgroundColor(getResources().getColor(R.color.white));
                monColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                monColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        } else if ("2".equals(today_course)) {
            tueColum.setText(courese_Detail.getDate() + "\n周二");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    tueColum.setText(current_day_Str + "\n周二");
                    tueColum.setTextColor(getResources().getColor(R.color.main_color));
                    tueColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    tueColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                tueColum.setBackgroundColor(getResources().getColor(R.color.white));
                tueColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                tueColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
        if ("3".equals(today_course)) {
            wedColum.setText(courese_Detail.getDate() + "\n周三");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    wedColum.setText(current_day_Str + "\n周三");
                    wedColum.setTextColor(getResources().getColor(R.color.main_color));
                    wedColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    wedColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                wedColum.setBackgroundColor(getResources().getColor(R.color.white));
                wedColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                wedColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
        if ("4".equals(today_course)) {
            thrusColum.setText(courese_Detail.getDate() + "\n周四");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    thrusColum.setText(current_day_Str + "\n周四");
                    thrusColum.setTextColor(getResources().getColor(R.color.main_color));
                    thrusColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    thrusColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                thrusColum.setBackgroundColor(getResources().getColor(R.color.white));
                thrusColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                thrusColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
        if ("5".equals(today_course)) {
            friColum.setText(courese_Detail.getDate() + "\n周五");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    friColum.setText(current_day_Str + "\n周五");
                    friColum.setTextColor(getResources().getColor(R.color.main_color));
                    friColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    friColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                friColum.setBackgroundColor(getResources().getColor(R.color.white));
                friColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                friColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
        if ("6".equals(today_course)) {
            satColum.setText(courese_Detail.getDate() + "\n周六");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    satColum.setText(current_day_Str + "\n周六");
                    satColum.setTextColor(getResources().getColor(R.color.main_color));
                    satColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    satColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                satColum.setBackgroundColor(getResources().getColor(R.color.white));
                satColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                satColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
        if ("7".equals(today_course)) {
            sunColum.setText(courese_Detail.getDate() + "\n周日");
            if(no_Week.equals(STR_CURRENT_WEEK)){
                if(dataJudge(oldDate)){
                    sunColum.setText(current_day_Str + "\n周日");
                    sunColum.setTextColor(getResources().getColor(R.color.main_color));
                    sunColum.setBackgroundColor(getResources().getColor(R.color.main_color));
                    sunColum.getBackground().mutate().setAlpha(current_day_Alpha);
                }
            }else{
                sunColum.setBackgroundColor(getResources().getColor(R.color.white));
                sunColum.getBackground().mutate().setAlpha(no_current_day_Alpha);
                sunColum.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            }
        }
    }
    
    //组合所有的课程
    private List<Cpk_Side_Course_CourseDetail> collectAllCourse(List<Cpk_Side_Course_WeekDetail> mSingleWeek,String no_Week) {
        List<Cpk_Side_Course_CourseDetail> tempCourseList = new ArrayList<Cpk_Side_Course_CourseDetail>();
        for (int i = 0; i < mSingleWeek.size(); i++) {
            showDateAndWeekDay(mSingleWeek.get(i),no_Week);
            if(mSingleWeek.get(i).getCourse().size()>0){
                for (int j = 0; j < mSingleWeek.get(i).getCourse().size(); j++) {
                    tempCourseList.add(mSingleWeek.get(i).getCourse().get(j));
                }
            }
        }
        return  tempCourseList;
    }
    
    private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile,boolean noData) {
        
        if (wholeView)
            liner_side_course_whole.setVisibility(View.VISIBLE);
        else
            liner_side_course_whole.setVisibility(View.GONE);
        
        if (loadFaile)
            side_course_load_error.setVisibility(View.VISIBLE);
        else
            side_course_load_error.setVisibility(View.GONE);
        
        if (noData)
            side_course_no_content.setVisibility(View.VISIBLE);
        else
            side_course_no_content.setVisibility(View.GONE);
        if(loading){
        	drawable.start();
            side_course_loading.setVisibility(View.VISIBLE);
        }else{
        	if (drawable!=null) {
        		drawable.stop();
			}
            side_course_loading.setVisibility(View.GONE);
    }}
    
    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.side_course_load_error:
                    showLoadResult(true, false, false, false);
                    if (acy_type == 10) {// 分享课表进入
                        readData(select_week, course_id, course_user_uniqid, share_time,message_id);
                    } else if (acy_type == 11||acy_type == 1||acy_type == 12) {// 正常进入、推送进入
                        readData(select_week, "", "", "",message_id);
                    }
                    break;
                case R.id.liner_select_week:
                    if (STR_CURRENT_WEEK == null || STR_CURRENT_WEEK.equals("0")) {
                        PubMehods.showToastStr(B_Side_Course_Acy.this, "无课表信息");
                        return;
                    }
                    final Dialog dialog = new Dialog(B_Side_Course_Acy.this,
                            android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.setContentView(R.layout.warn_dialog);
                    WheelView wv = (WheelView) dialog
                            .findViewById(R.id.wheel_view_wv);
                    Button commit = (Button) dialog.findViewById(R.id.bt_commit);
                    Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);
                    TextView titleStr = (TextView) dialog.findViewById(R.id.tv_wheel_week_title);
                    
                    titleStr.setText("本周是第" + STR_CURRENT_WEEK + "周");
                    titleStr.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(STR_CURRENT_WEEK.equals("0")){
                                PubMehods.showToastStr(B_Side_Course_Acy.this, R.string.str_course_no_plan);
                            }else{
                                tv_titlebar_title.setText(showCurrentWeek(STR_CURRENT_WEEK));
                                temp_current_week_index = INT_CURRENT_WEEK_POSTION;
                            
                                select_week = String.valueOf(INT_CURRENT_WEEK_POSTION + 1);
                                readCache();
                                dialog.cancel();
                            }
                        }
                    });
                    
                    wv.setOffset(1);
                    wv.setItems(Arrays.asList(array_Week));
                    wv.setSeletion(temp_current_week_index);
                    wv.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
                        public void onSelected(int selectedIndex, String item) {
                              logD(selectedIndex +"--滚动---"+ item);
                              temp_dialog_selected = selectedIndex - 1;
                        }
                    });
                    dialog.show();
                    commit.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
//                            List<Cpk_Side_Course_WeekDetail> mSingleWeek = null;
//                            String now_week;
                            if (temp_dialog_selected >= 0) {// 用户做了滚动动作
                                temp_current_week_index = temp_dialog_selected;// 传递变量用，dialog滚动选择
//                                now_week = PubMehods.subStrNum(array_Week[temp_current_week_index]);
                                if (String.valueOf(temp_dialog_selected + 1).equals(STR_CURRENT_WEEK)) {
                                    tv_titlebar_title.setText(showCurrentWeek(STR_CURRENT_WEEK));
                                } else {
                                    tv_titlebar_title.setText("第" + (temp_dialog_selected + 1) + "周");
                                }

                            } else {// 用户没有滚动
                                temp_current_week_index = INT_CURRENT_WEEK_POSTION;
//                                now_week = PubMehods.subStrNum(array_Week[temp_current_week_index]);
                                tv_titlebar_title.setText(showCurrentWeek(STR_CURRENT_WEEK));
                            }
                            select_week = String.valueOf(temp_current_week_index + 1);
                            readCache();
                            dialog.cancel();
                        }
                    });
                    cancel.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View arg0) {
                            dialog.cancel();
                        }
                    });
                    
                    break;
                default:
                    break;
            }
        }
    };

    class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
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
            if (acy_type == 10) {// 分享课表进入
                readData(select_week, course_id, course_user_uniqid, share_time,message_id);
            } else if (acy_type == 11||acy_type == 1||acy_type == 12) {// 正常进入、推送进入
                readData(select_week, "", "", "",message_id);
            }
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

    @Override
    protected void onResume() {
        super.onResume();
        if (!firstLoad) {
            readCache();
            /*if (!sp.getString("select", "").equals(null) && !sp.getString("select", "").equals("")) {
                File file = new File(sp.getString("select", ""));
                if (file.exists()) {
                    Bitmap bm = BitmapFactory.decodeFile(sp.getString("select", ""));
                    // 将图片显示到ImageView中
                    Drawable drawable = new BitmapDrawable(bm);
                    rela_couse_bg.setBackgroundDrawable(drawable);
                }
            }*/
        } else {
            firstLoad = false;
        }
//        rela_couse_bg.setBackgroundResource(bg_course[A_0_App.course_bg_index]);
    }

    private void clearData(boolean setNull) {
        if (mCurrentWeek != null) {
            mCurrentWeek = null;
        }
    }

    private void clearWeekData(boolean setNull) {
        if (mSingleWeek != null) {
            mSingleWeek.clear();
            if (setNull)
                mSingleWeek = null;
        }
    }

    private void clearCourseData(boolean setNull) {
        if (mListCourse != null) {
            mListCourse.clear();
            if (setNull)
                mListCourse = null;
        }
    }
    
    private GeneralDialog upDateDialog;
    public void showUserTitleDialog(final String message,final String semester_id, final String semester_status) {
        if (upDateDialog != null) {
            upDateDialog.dismiss();
            upDateDialog = null;
        }
        upDateDialog = new GeneralDialog(B_Side_Course_Acy.this,
                R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent(message);
        upDateDialog.setCanceledOnTouchOutside(false);
        upDateDialog.showMiddleButton(R.string.str_course_user_tip, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
                saveUserTips(semester_id, semester_status, true);
            }
        });
        upDateDialog.show();
    }
    
    // 保存“我知道了”用户提示状态
    private void saveUserTips(String semester_id, String semester_status,boolean values) {
        SharedPreferences mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_APP_DATA, Activity.MODE_PRIVATE);
        Editor editor = mSharePre.edit();
        editor.putBoolean(A_0_App.USER_UNIQID + AppStrStatic.SHARE_COURSE_SEMESTER_STATUS + semester_id + semester_status, values);
        editor.commit();
    }
    
    public boolean getUserTips(String semester_id, String semester_status) {
        SharedPreferences mSharePre = this.getSharedPreferences(AppStrStatic.SHARE_APP_DATA, Activity.MODE_PRIVATE);
        boolean getValue = mSharePre.getBoolean(A_0_App.USER_UNIQID + AppStrStatic.SHARE_COURSE_SEMESTER_STATUS 
                + semester_id + semester_status, false);
        return getValue;
    }
    
    /**
     * 设置连接状态变化的监听器.
     */
    public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    public class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(B_Side_Acy_list_Scy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Course_Acy.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
    private void showCopyCourseDialog() {
        final Dialog dialog = new Dialog(B_Side_Course_Acy.this,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_copy_course);
        TextView tvContent = (TextView)dialog.findViewById(R.id.tv_dialog_course_content);
        Button commit = (Button) dialog.findViewById(R.id.bt_commit);
        Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        tvContent.setText(R.string.str_copy_course_title);
        
        dialog.show();
        commit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                copyCourse(A_0_App.USER_TOKEN, share_time, course_user_uniqid, semester_id,course_id);
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
    }
    
    private boolean dataJudge(String oldDate) {
        boolean result = false;
        long currentTime = Long.valueOf(PubMehods.subStrTime(System.currentTimeMillis()));
        String currenTime = PubMehods.getFormatDate(currentTime, "MM月dd日");
        if(oldDate.equals(currenTime)){
            result = true;
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        clearData(true);
        clearWeekData(true);
        clearCourseData(true);
        drawable.stop();
        drawable=null;
        super.onDestroy();
    }

    public static void logD(String msg) {
        LogUtils.logD("B_Side_Course_Acy", "B_Side_Course_Acy==>" + msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("B_Side_Course_Acy", "B_Side_Course_Acy==>" + msg);
    }
}
