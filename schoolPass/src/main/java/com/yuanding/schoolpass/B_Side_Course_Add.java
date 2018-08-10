package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_Add;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_CourseDetail;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_Week_Select;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.service.Api.Inter_Side_Course_Add;
import com.yuanding.schoolpass.service.Api.Inter_Side_Course_Add_Sure;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.GeneralDialog;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.MyListView;
import com.yuanding.schoolpass.view.WheelView;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2017年1月10日 下午5:02:18
 * 类说明
 */
public class B_Side_Course_Add  extends A_0_CpkBaseTitle_Navi {
    
    private LinearLayout iv_courses_add,iv_courses_teacher_add; 
    private EditText et_course_name,et_course_teacher_name;
    private MyListView mylistview_course;
    private Mydapter adapter;
    private GridAdapter gridAdapter;
    private List<Cpk_Side_Course_Add> mCourseList = null;
    private List<Cpk_Side_Course_Week_Select> mTotleWeekList,mTempWeekList = null;//固定的总周数
    private String totle_weeks,semester_id,cm_id = "";
    private int acy_enter;//1：表示添加，2表示编辑，3表示选中宫格添加
    private int week_select_position = 0;//
    private String userSureCourseName = "";//标示：选择的课程名称或者编辑传入的课程名称
    
    private String add_week,add_week_day,add_start_note,add_end_note;
    private Cpk_Side_Course_CourseDetail courese_Detail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_side_course_add);
        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setZuiRightBtn(R.drawable.navigationbar_save);

        if (getIntent().getExtras() != null) {
            acy_enter = getIntent().getIntExtra("acy_enter", 1);
            totle_weeks = getIntent().getExtras().getString("totle_weeks");
            semester_id = getIntent().getExtras().getString("semester_id");
            switch (acy_enter) {
                case 1://主页右上角进入
                    break;
                case 2://课程详情编辑
                    courese_Detail = (Cpk_Side_Course_CourseDetail)getIntent().getSerializableExtra(B_Side_Course_Acy.INTERT_COURSE_DTAIL);
                    userSureCourseName = courese_Detail.getCoursename();
                    cm_id = courese_Detail.getCm_id();
                    break;
                case 3://主页宫格进入
                    add_week = getIntent().getExtras().getString("add_week");
                    add_week_day = getIntent().getExtras().getString("add_week_day");
                    add_start_note = getIntent().getExtras().getString("add_start_note");
                    add_end_note = getIntent().getExtras().getString("add_end_note");
                    break;
                default:
                    break;
            }
        }
        
        iv_courses_add = (LinearLayout)findViewById(R.id.iv_courses_add);
        iv_courses_teacher_add = (LinearLayout)findViewById(R.id.iv_courses_teacher_add);
        et_course_name = (EditText)findViewById(R.id.et_course_name);
        et_course_teacher_name = (EditText)findViewById(R.id.et_course_teacher_name);
        mylistview_course = (MyListView)findViewById(R.id.mylistview_course);
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        
        mCourseList = new ArrayList<Cpk_Side_Course_Add>();
        
        if (acy_enter == 1) {
            setTitleText("添加课程");
            addNullCourse(mCourseList);
        } else if (acy_enter == 2) {
            setTitleText("编辑课程");
            Cpk_Side_Course_Add addOne = new Cpk_Side_Course_Add();
            addOne.setWeek(courese_Detail.getCourseweek());
            addOne.setWeekday(String.valueOf(courese_Detail.getToday()));
            addOne.setStartSection(String.valueOf(courese_Detail.getStartsection()));
            addOne.setEndSection(String.valueOf(courese_Detail.getEndsection()));
            addOne.setPlace(courese_Detail.getPlace());
            mCourseList.add(addOne);
            
            et_course_name.setText(courese_Detail.getCoursename());
            et_course_teacher_name.setText(courese_Detail.getTeachername());
        } else if (acy_enter == 3) {
            setTitleText("添加课程");
            Cpk_Side_Course_Add addOne = new Cpk_Side_Course_Add();
            addOne.setWeek(add_week);
            addOne.setWeekday(add_week_day);
            addOne.setStartSection(add_start_note);
            addOne.setEndSection(add_end_note);
            addOne.setPlace("");
            mCourseList.add(addOne);
        }
        
        mTotleWeekList = new ArrayList<Cpk_Side_Course_Week_Select>();
        mTempWeekList = new ArrayList<Cpk_Side_Course_Week_Select>();
        mTotleWeekList = getTotleWeek(totle_weeks);
        
        adapter = new Mydapter();
        gridAdapter = new GridAdapter();
        if(acy_enter != 2){
            addFootView(mylistview_course);
        }
        mylistview_course.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        iv_courses_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(B_Side_Course_Add.this, B_Side_Course_Search.class);
                startActivityForResult(intent, 2);
            }
        });
        
        iv_courses_teacher_add.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String courseName = et_course_name.getText().toString();
                if ( courseName== null || courseName.length() <= 0 || courseName.equals("")) {
                    PubMehods.showToastStr(B_Side_Course_Add.this, "请输入课程名称");
                    return;
                }
                String hava_course_name = et_course_name.getText().toString();
                if (hava_course_name == null || hava_course_name.length() <= 0) {
                    hava_course_name = "";
                }
                if (userSureCourseName != null && !userSureCourseName.equals("") && !courseName.equals(userSureCourseName)) {
                    cm_id = "";
//                    PubMehods.showToastStr(B_Side_Course_Add.this, "清除掉cm_id值");
                }
                Intent intent = new Intent();
                intent.setClass(B_Side_Course_Add.this, B_Side_Course_Search_Teacher.class);
                intent.putExtra("hava_course_name", hava_course_name);
                intent.putExtra("cm_id", cm_id);
                startActivityForResult(intent, 3);
            }
        });

        mylistview_course.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 当停止滚动时
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 滚动时
                        HideKeyBoard();
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 手指抬起，但是屏幕还在滚动状态
                        break;
                }
            }
            @Override
            public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
            }
        });
        
        et_course_name.setFilters(PubMehods.emojiFilters);
        et_course_teacher_name.setFilters(PubMehods.emojiFilters);
    }
    
    /**
     * 隐藏软键盘
     */
    private void HideKeyBoard() {
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
    }
    
    private void addFootView(MyListView mylistview_course) {
        View view = LayoutInflater.from(B_Side_Course_Add.this).inflate(R.layout.item_side_course_add_more, null);
        LinearLayout liner_course_add_more = (LinearLayout)view.findViewById(R.id.liner_course_add_more);
        mylistview_course.addFooterView(view);
        liner_course_add_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                addNullCourse(mCourseList);
                adapter.notifyDataSetChanged();
            }
        });
     }
    
    // 添加一个课标项
    private void addNullCourse(List<Cpk_Side_Course_Add> mCourseList) {
        Cpk_Side_Course_Add addOne = new Cpk_Side_Course_Add();
        addOne.setWeek("");
        addOne.setWeekday("");
        addOne.setStartSection("");
        addOne.setEndSection("");
        addOne.setPlace("");
        mCourseList.add(addOne);
    }
    
    //根据总周数填充list
    private List<Cpk_Side_Course_Week_Select> getTotleWeek(String totle_weeks) {
        List<Cpk_Side_Course_Week_Select> mList = new ArrayList<Cpk_Side_Course_Week_Select>();
        if (totle_weeks != null && !totle_weeks.equals("")) {
            int weeks = Integer.parseInt(totle_weeks);
            for (int i = 0; i < weeks; i++) {
                Cpk_Side_Course_Week_Select week = new Cpk_Side_Course_Week_Select();
                week.setWeekName(String.valueOf(i + 1));
                mList.add(week);
            }
        }
        return mList;

    }
    
    // 根据状态 1:双选，2：单选，3：全选，4：全不选，处理数据
    private List<Cpk_Side_Course_Week_Select> changeWeekData( List<Cpk_Side_Course_Week_Select> mWeek, String cate_name) {
        List<Cpk_Side_Course_Week_Select> mList = new ArrayList<Cpk_Side_Course_Week_Select>();
        for (int i = 0; i < mWeek.size(); i++) {
            Cpk_Side_Course_Week_Select week = new Cpk_Side_Course_Week_Select();
            week.setWeekName(String.valueOf(i + 1));
            if (cate_name.equals("1")) {
                if ((i + 1) % 2 == 1) {
                    week.setSelected(false);
                } else if ((i + 1) % 2 == 0) {
                    week.setSelected(true);
                }
            } else if (cate_name.equals("2")) {
                if ((i + 1) % 2 == 1) {
                    week.setSelected(true);
                } else if ((i + 1) % 2 == 0) {
                    week.setSelected(false);
                }
            } else if (cate_name.equals("3")) {
                week.setSelected(true);
            } else if (cate_name.equals("4")) {
                week.setSelected(false);
            }
            mList.add(week);
        }
        return mList;
    }
    
    /**
     * 上课周数
     */
    void course_Week_Dialog(final int posi) {
        final Dialog dialog = new Dialog(B_Side_Course_Add.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_course_select_week);
        Button commit = (Button) dialog.findViewById(R.id.bt_commit);
        Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        
        final Button btn_double_week = (Button) dialog.findViewById(R.id.btn_double_week);
        final Button btn_single_week = (Button) dialog.findViewById(R.id.btn_single_week);
        final Button btn_all_select = (Button) dialog.findViewById(R.id.btn_all_select);
        //加载3个按钮状态
        setWeekBtnBg(week_select_position,btn_double_week, btn_single_week, btn_all_select);
        
        btn_double_week.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {//双周
                mTempWeekList = changeWeekData(mTempWeekList, "1");
                gridAdapter.notifyDataSetChanged();
                week_select_position = 1;
                setWeekBtnBg(week_select_position,btn_double_week, btn_single_week, btn_all_select);
            }
        });
        
        btn_single_week.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {//单周
                mTempWeekList = changeWeekData(mTempWeekList, "2");
                gridAdapter.notifyDataSetChanged();
                week_select_position = 2;
                setWeekBtnBg(week_select_position,btn_double_week, btn_single_week, btn_all_select);
            }
        });
        
        btn_all_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {//全选
                mTempWeekList = changeWeekData(mTempWeekList, "3");
                gridAdapter.notifyDataSetChanged();
                week_select_position = 3;
                setWeekBtnBg(week_select_position,btn_double_week, btn_single_week, btn_all_select);
            }
        });
        
        MyGridView myGridView = (MyGridView) dialog.findViewById(R.id.gv_week_select);
        myGridView.setAdapter(gridAdapter);        
        dialog.show();
        
        commit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String str_Week = "";
                for (int i = 0; i < mTempWeekList.size(); i++) {
                    if(mTempWeekList.get(i).getSelected()){
                        String temp = mTempWeekList.get(i).getWeekName();
                        str_Week += temp+",";
                    }
                }
                if (str_Week != null && !str_Week.equals("")){
                    mCourseList.get(posi).setWeek(str_Week.substring(0, str_Week.length() - 1));
                    adapter.notifyDataSetChanged();
                }else{
                    mCourseList.get(posi).setWeek(str_Week);
                    adapter.notifyDataSetChanged();
                }
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
        
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
              
            }
        });
    }
    
    //0:不选，1:双选，2：单选，3：全选
    private void setWeekBtnBg(int week_select_position,Button btn_double_week,Button btn_single_week,Button btn_all_select) {
        switch (week_select_position) {
            case 0:
                btn_double_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_double_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_single_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_single_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_all_select.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_all_select.setTextColor(getResources().getColor(R.color.start_title_col));
                break;
            case 1:
                btn_double_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_select));
                btn_double_week.setTextColor(getResources().getColor(R.color.main_color));
                btn_single_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_single_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_all_select.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_all_select.setTextColor(getResources().getColor(R.color.start_title_col));
                break;
            case 2:
                btn_double_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_double_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_single_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_select));
                btn_single_week.setTextColor(getResources().getColor(R.color.main_color));
                btn_all_select.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_all_select.setTextColor(getResources().getColor(R.color.start_title_col));
                break;
            case 3:
                btn_double_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_double_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_single_week.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_normal));
                btn_single_week.setTextColor(getResources().getColor(R.color.start_title_col));
                btn_all_select.setBackgroundDrawable(getResources().getDrawable(R.drawable.icon_course_week_btn_select));
                btn_all_select.setTextColor(getResources().getColor(R.color.main_color));
                break;
            default:
                break;
        }
    }
    
    private String[] array_Week = {"周一","周二","周三","周四","周五","周六","周日"};
    private String[] array_Start = {"第1节","第2节","第3节","第4节","第5节","第6节","第7节","第8节","第9节","第10节","第11节","第12节"};
    private String[] array_End = {"到1节","到2节","到3节","到4节","到5节","到6节","到7节","到8节","到9节","到10节","到11节","到12节"};
    int temp_dialog_selected001, temp_dialog_selected002, temp_dialog_selected003 = 1;
    int user_cust_selected001, user_cust_selected002, user_cust_selected003 = 0;
    /**
     * 上课时间
     */
    void course_Time_Dialog(final int posi) {

        final Dialog dialog = new Dialog(B_Side_Course_Add.this,
                android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setContentView(R.layout.dialog_course_select_time);
        Button commit = (Button) dialog.findViewById(R.id.bt_commit);
        Button cancel = (Button) dialog.findViewById(R.id.bt_cancel);
        
        WheelView wvWeekDay = (WheelView) dialog.findViewById(R.id.wheel_view_wv01);
        wvWeekDay.setOffset(1);
        wvWeekDay.setItems(Arrays.asList(array_Week));
        wvWeekDay.setSeletion(temp_dialog_selected001);
        wvWeekDay.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            public void onSelected(int selectedIndex, String item) {
                user_cust_selected001 = selectedIndex;
            }
        });
        
        WheelView wvStart = (WheelView) dialog.findViewById(R.id.wheel_view_wv02);
        final WheelView wvEnd = (WheelView) dialog.findViewById(R.id.wheel_view_wv03);
        //user_cust_selected = temp_dialog_selected + 1;
        wvStart.setOffset(1);
        wvStart.setItems(Arrays.asList(array_Start));
        wvStart.setSeletion(temp_dialog_selected002);
        wvStart.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            public void onSelected(int selectedIndex, String item) {
                user_cust_selected002 = selectedIndex;
                if (user_cust_selected003 <= 0) {//没有滑动开始节数__下面已经处理
                    user_cust_selected003 = temp_dialog_selected003 + 1;
                    if (user_cust_selected003 < user_cust_selected002) {
                        wvEnd.setSeletion(user_cust_selected002);
                        user_cust_selected003 = user_cust_selected002 + 1;
                        if(user_cust_selected003 >= 13){
                            user_cust_selected003 = 12;
                        }
                    }
                }else{
                    if (user_cust_selected003 < user_cust_selected002) {
                        wvEnd.setSeletion(user_cust_selected002);
                        user_cust_selected003 = user_cust_selected002 + 1;
                        if(user_cust_selected003 >= 13){
                            user_cust_selected003 = 12;
                        }
                    }
                }
            }
        });
        
        wvEnd.setOffset(1);
        wvEnd.setItems(Arrays.asList(array_End));
        wvEnd.setSeletion(temp_dialog_selected003);
        wvEnd.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            public void onSelected(int selectedIndex, String item) {
                user_cust_selected003 = selectedIndex;
                if (user_cust_selected002 <= 0) {//没有滑动开始节数
                    user_cust_selected002 = temp_dialog_selected002 + 1;
                    if (user_cust_selected003 < user_cust_selected002) {
                        wvEnd.setSeletion(user_cust_selected002);
                        user_cust_selected003 = user_cust_selected002 + 1;
                        if(user_cust_selected003 >= 13){
                            user_cust_selected003 = 12;
                        }
                    }
                }else{
                    if (user_cust_selected003 < user_cust_selected002) {
                        wvEnd.setSeletion(user_cust_selected002);
                        user_cust_selected003 = user_cust_selected002 + 1;
                        if(user_cust_selected003 >= 13){
                            user_cust_selected003 = 12;
                        }
                    }
                }
            }
        });
        
        dialog.show();
        commit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                
                int submitWeek = 0,start_Note = 0,end_Note = 0;
                
                if (user_cust_selected001 == 0) {
                    submitWeek = temp_dialog_selected001 + 1;
                }else{
                    submitWeek = user_cust_selected001;
                }
                
                if (user_cust_selected002 == 0) {
                    start_Note = temp_dialog_selected002 + 1;
                }else{
                    start_Note = user_cust_selected002;
                }
                
                if (user_cust_selected003 == 0) {
                    end_Note = temp_dialog_selected003 + 1;
                }else{
                    end_Note = user_cust_selected003;
                }
                
                if (end_Note < start_Note) {
                    PubMehods.showToastStr(B_Side_Course_Add.this, "结束节数必须大于或等于开始节数");
                    return;
                }
                
                mCourseList.get(posi).setWeekday(String.valueOf(submitWeek));
                mCourseList.get(posi).setStartSection(String.valueOf(start_Note));
                mCourseList.get(posi).setEndSection(String.valueOf(end_Note));
                adapter.notifyDataSetChanged();
                dialog.cancel();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog.cancel();
            }
        });
        dialog.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface arg0) {
                temp_dialog_selected001 = 0;
                temp_dialog_selected002 = 0;
                temp_dialog_selected003 = 0;
            }
        });
    }
    
    public class Mydapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mCourseList != null)
                return mCourseList.size();
            return 0;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int posi, View converView, ViewGroup arg2) {

            final ViewHolder holder;
            if (converView == null) {
                converView = LayoutInflater.from(B_Side_Course_Add.this).inflate(R.layout.item_side_course_add, null);
                holder = new ViewHolder();

                holder.iv_course_add_delete = (ImageView) converView.findViewById(R.id.iv_course_add_delete);
                holder.rel_course_add_week = (RelativeLayout) converView.findViewById(R.id.rel_course_add_week);
                holder.rel_course_add_time = (RelativeLayout) converView.findViewById(R.id.rel_course_add_time);
                holder.et_course_add_place = (EditText) converView.findViewById(R.id.et_course_add_place);
                holder.tv_course_add_week = (TextView) converView.findViewById(R.id.tv_course_add_week);
                holder.tv_course_add_time = (TextView) converView.findViewById(R.id.tv_course_add_time);
                
                converView.setTag(holder);
            } else {
                holder = (ViewHolder) converView.getTag();
            }
            
            if (posi == 0) {
                holder.iv_course_add_delete.setVisibility(View.GONE);
            } else {
                holder.iv_course_add_delete.setVisibility(View.VISIBLE);
            }

            //周数
            String weekCounts = mCourseList.get(posi).getWeek();
            if (weekCounts != null && !weekCounts.equals("") && weekCounts.length() > 0){
                holder.tv_course_add_week.setText(weekCounts);
            }else{
                holder.tv_course_add_week.setText("");
            }
            
            //上课时间
            final String weekday =  mCourseList.get(posi).getWeekday();
            final String startSection =  mCourseList.get(posi).getStartSection();
            final String endSection =  mCourseList.get(posi).getEndSection();
            if (weekday != null && !weekday.equals("") && weekday.length() > 0) {
                int temp = Integer.parseInt(weekday) -1;
                if (temp < array_Week.length)
                    holder.tv_course_add_time.setText(array_Week[temp] + " " + startSection + "-" + endSection + "节");
            }else{
                holder.tv_course_add_time.setText("");
            }
            
            holder.iv_course_add_delete.setTag(R.id.iv_course_add_delete, posi);
            holder.iv_course_add_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int temp = (Integer)arg0.getTag(R.id.iv_course_add_delete);
                    mCourseList.remove(temp);
                    notifyDataSetChanged();
                }
            });
            
            holder.rel_course_add_week.setTag(R.id.rel_course_add_week,posi);
            holder.rel_course_add_week.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int temp_posi = (Integer)arg0.getTag(R.id.rel_course_add_week);
                    String oldWeekData = holder.tv_course_add_week.getText().toString();
                    
                    List<Cpk_Side_Course_Week_Select> mTempTotleWeekList = null;
                    mTotleWeekList = getTotleWeek(totle_weeks);
                    mTempTotleWeekList = mTotleWeekList;
                    mTempWeekList.clear();
                    
                    if (oldWeekData != null && oldWeekData.length() > 0) {
                        String[] temp = oldWeekData.split(",");
                        for (int i = 0; i < mTempTotleWeekList.size(); i++) {
                            Cpk_Side_Course_Week_Select week_Select = new Cpk_Side_Course_Week_Select();
                            week_Select.setWeekName(String.valueOf(i + 1));
                            boolean same_week = false;
                            for (int j = 0; j < temp.length; j++) {
                                if(mTempTotleWeekList.get(i).getWeekName().equals(temp[j])){
                                    same_week = true;
                                }
                            }
                            if(same_week){
                                week_Select.setSelected(true);
                            }else{
                                week_Select.setSelected(false);
                            }
                            mTempWeekList.add(week_Select);
                        }
                        
                        /********************************按钮状态判断*********************************************/
                        //0:不选，1:双选，2：单选，3：全选
                        List<Cpk_Side_Course_Week_Select> mTempDouble = changeWeekData(mTempTotleWeekList, "1");
                        List<Cpk_Side_Course_Week_Select> mTempSingle = changeWeekData(mTempTotleWeekList, "2");
                        List<Cpk_Side_Course_Week_Select> mTempAll = changeWeekData(mTempTotleWeekList, "3");
                        if(compare(mTempWeekList, mTempDouble)){
                            week_select_position = 1;
                        }else if(compare(mTempWeekList, mTempSingle)){
                            week_select_position = 2;
                        }else if(compare(mTempWeekList, mTempAll)){
                            week_select_position = 3;
                        }else{
                            week_select_position = 0;
                        }
                    }else{
                        week_select_position = 0;
                        mTempWeekList = mTempTotleWeekList;
                    }
                    course_Week_Dialog(temp_posi);
                }
            });
            
            holder.rel_course_add_time.setTag(R.id.rel_course_add_time,posi);
            holder.rel_course_add_time.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int true_week_day = 0, true_note_num_01, true_note_num_02 = 0;
                    String addTimeData = holder.tv_course_add_time.getText().toString();
                    int temp_posi = (Integer)arg0.getTag(R.id.rel_course_add_time);
                    if (addTimeData != null && addTimeData.length() > 0) {
                        for (int i = 0; i < array_Week.length; i++) {
                            String temp = addTimeData.substring(0, 2);
                            if(temp.equals(array_Week[i])){
                                true_week_day = i;
                            }
                        }
                        true_note_num_01 = Integer.parseInt( addTimeData.substring(2,
                                addTimeData.indexOf("-")).trim());
                        true_note_num_02 = Integer.parseInt(addTimeData.substring(
                                addTimeData.indexOf("-")+1, addTimeData.indexOf("节")).trim());
                        temp_dialog_selected001 = true_week_day;
                        temp_dialog_selected002 = true_note_num_01 -1;
                        temp_dialog_selected003 = true_note_num_02 -1;
                        
                    }else{
                        temp_dialog_selected001 = 0;
                        temp_dialog_selected002 = 0;
                        temp_dialog_selected003 = 0;
                    }
                    course_Time_Dialog(temp_posi);
                }
            });
            
            final Cpk_Side_Course_Add cpk_Side_Course_Add = mCourseList.get(posi);
            holder.et_course_add_place.setTag(cpk_Side_Course_Add);
            holder.et_course_add_place.clearFocus();
            
            holder.et_course_add_place.setFilters(PubMehods.emojiFilters);
            holder.et_course_add_place.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
//                    int index = holder.et_course_add_place.getSelectionStart() - 1;
//                    if (index > 0) {
//                        if (PubMehods.isEmojiCharacter(s.charAt(index))) {
//                            Editable edit = holder.et_course_add_place.getText();
//                            edit.delete(index, index + 1);
//                        }
//                    } else {
////                        s.clear();
//                    }

                    // 获得Edittext所在position里面的Bean，并设置数据
                    Cpk_Side_Course_Add bean = (Cpk_Side_Course_Add) holder.et_course_add_place.getTag();
                    bean.setPlace(s + "");
                }
            });
            
            // 上课地点
            if (posi < mCourseList.size()) {
                String place = mCourseList.get(posi).getPlace();
                if (place == null)
                    place = "";
                holder.et_course_add_place.setText(place);
            }
            
            return converView;
        }

    }
    
    /**
     * 队列比较
     * 
     * @param <T>
     * @param a
     * @param b
     * @return
     */
    public boolean compare(List<Cpk_Side_Course_Week_Select> a, List<Cpk_Side_Course_Week_Select> b) {
        for (int i = 0; i < a.size(); i++) {
            if (b.get(i).getSelected() != a.get(i).getSelected()) {
                return false;
            }
        }
        return true;
    }

    class ViewHolder {
        ImageView iv_course_add_delete;
        RelativeLayout rel_course_add_week,rel_course_add_time;
        EditText et_course_add_place;
        TextView tv_course_add_week,tv_course_add_time;
    }
    
    public class GridAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            if (mTempWeekList != null)
                return mTempWeekList.size();
            return 0;
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(final int posi, View converView, ViewGroup arg2) {
            if (converView == null) {
                converView = LayoutInflater.from(B_Side_Course_Add.this).inflate(R.layout.item_side_course_select_week, null);
            }
            Button btn_week_name = (Button) converView.findViewById(R.id.btn_week_name);
            if(mTempWeekList.get(posi).getSelected()){
                btn_week_name.setBackgroundResource(R.drawable.icon_course_week_cate_select);
                btn_week_name.setTextColor(getResources().getColor(R.color.white));
            }else{
                btn_week_name.setBackgroundResource(R.drawable.icon_course_week_cate_normal);
                btn_week_name.setTextColor(getResources().getColor(R.color.start_title_col));
            }
            btn_week_name.setText(mTempWeekList.get(posi).getWeekName());
            btn_week_name.setTag(R.id.btn_week_name, posi);
            btn_week_name.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    int position = (Integer)arg0.getTag(R.id.btn_week_name);
                    if (mTempWeekList.get(position).getSelected())
                        mTempWeekList.get(position).setSelected(false);
                    else
                        mTempWeekList.get(position).setSelected(true);
                    gridAdapter.notifyDataSetChanged();
                }
            });
            return converView;
        }

    }
    
    @Override
    protected void onActivityResult(final int requestCode, int resultCode, Intent data) {
        if (data != null && data.getExtras() != null
                && data.getExtras().getString("modify_content") != null) {
            final String result = data.getExtras().getString("modify_content");
            final String temp_cm_id = data.getExtras().getString("modify_id");
            final String course_teacher = data.getExtras().getString("course_teacher");
            B_Side_Course_Add.this.runOnUiThread(new Runnable() {    
                public void run() {
                	if (requestCode==2) {
                        userSureCourseName = result;
                        et_course_name.setText(result);
                        et_course_teacher_name.setText(course_teacher);
                        cm_id = temp_cm_id;
                    }else if (requestCode==3) {
                        et_course_teacher_name.setText(result);
                    }
                }
        });
            
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void to_Add_Course_Action(final String semester_id,final String jsonWeeks,final String course_name,
            final String cm_id,final String token,String is_sure,final String is_edit,final String weekday_id,final String teacher_name) {
        A_0_App.getInstance().showProgreDialog(B_Side_Course_Add.this, "", true);
        A_0_App.getApi().add_Course_Action(semester_id, jsonWeeks, course_name, cm_id, token,is_sure,is_edit,
                weekday_id,teacher_name,new Inter_Side_Course_Add() {
            @Override
            public void onSuccess(int state,String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Add.this);
                if(state ==2){
                    showUserConfirmDialog(message,semester_id, jsonWeeks, course_name, cm_id, token, is_edit,weekday_id,teacher_name);
                }else{
                    if (message != null && !("").equals(message))
                        PubMehods.showToastStr(B_Side_Course_Add.this, message);
                    if (acy_enter == 2) {
                        setResult(B_Side_Course_Detail.RESUTL_CODE);
                    }
                    finish();
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Add.this);
                PubMehods.showToastStr(B_Side_Course_Add.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    private void to_Add_Course_Action_Sure(final String semester_id,final String jsonWeeks,final String course_name,final String cm_id,
            final String token,String is_sure,final String is_edit,String weekday_id,String teacher_name) {
        A_0_App.getInstance().showProgreDialog(B_Side_Course_Add.this, "", true);
        A_0_App.getApi().add_Course_Action_Sure(semester_id, jsonWeeks, course_name, cm_id, token,is_sure,is_edit,
                weekday_id,teacher_name, new Inter_Side_Course_Add_Sure() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Add.this);
                if (message != null && !("").equals(message))
                    PubMehods.showToastStr(B_Side_Course_Add.this, message);
                if (acy_enter == 2) {
                    Intent intent = new Intent();
                    intent.putExtra("modify_success", true);
                    setResult(B_Side_Course_Detail.RESUTL_CODE, intent);
                }
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
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Add.this);
                PubMehods.showToastStr(B_Side_Course_Add.this, msg);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    public void showUserConfirmDialog(String titie,final String semester_id,String jsonWeeks,final String course_name,
            final String cm_id,String token,final String is_edit,final String weekday_id,final String teacher_name) {
        final GeneralDialog upDateDialog = new GeneralDialog(B_Side_Course_Add.this,
                R.style.Theme_GeneralDialog);
        upDateDialog.setTitle(R.string.pub_title);
        upDateDialog.setContent(titie);
        upDateDialog.showLeftButton(R.string.pub_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
            }
        });
        upDateDialog.showRightButton(R.string.pub_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upDateDialog.cancel();
                to_Add_Course_Action_Sure(semester_id, JSONArray.toJSON(mCourseList).toString(),course_name, 
                        cm_id, A_0_App.USER_TOKEN, "1", is_edit,weekday_id,teacher_name);
            }
        });
        upDateDialog.show();
    }

    @Override
    protected void handleTitleBarEvent(int resId,View v) {
        switch (resId) {
        case BACK_BUTTON:
            finish();
            break;
        case ZUI_RIGHT_BUTTON:
            if (A_0_App.USER_STATUS.equals("2")) {
                if (!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)) {
                    String course_name = et_course_name.getText().toString();
                    String teacher_name = et_course_teacher_name.getText().toString();
                    if(course_name==null ||course_name.equals("")){
                        PubMehods.showToastStr(B_Side_Course_Add.this, "请输入课程名称");
                        return;
                    }
                    if(teacher_name==null ||teacher_name.equals("")){
                        PubMehods.showToastStr(B_Side_Course_Add.this, "请输入任课老师名称");
                        return;
                    }
                    if(mCourseList==null ||mCourseList.size()<=0){
                        PubMehods.showToastStr(B_Side_Course_Add.this, "请添加上课周数、上课时间、上课地点");
                        return;
                    }
                    
                    for (int i = 0; i < mCourseList.size(); i++) {
                        if (mCourseList.get(i).getWeek() == null || mCourseList.get(i).getWeek().equals("")) {
                            PubMehods.showToastStr(B_Side_Course_Add.this, "请为第" + (i+1) + "项添加上课周数");
                            return;
                        }
                        if (mCourseList.get(i).getStartSection() == null || mCourseList.get(i).getStartSection().equals("")) {
                            PubMehods.showToastStr(B_Side_Course_Add.this, "请为第" + (i+1) + "项添加上课节数");
                            return;
                        }
                        if (mCourseList.get(i).getEndSection() == null || mCourseList.get(i).getEndSection().equals("")) {
                            PubMehods.showToastStr(B_Side_Course_Add.this, "请为第" + (i+1) + "项添加上课节数");
                            return;
                        }
                        if (mCourseList.get(i).getPlace() == null || mCourseList.get(i).getPlace().equals("")) {
                            PubMehods.showToastStr(B_Side_Course_Add.this, "请为第" + (i+1) + "项添加上课地点");
                            return;
                        }
                    }
                        if (userSureCourseName != null && !userSureCourseName.equals("") && !course_name.equals(userSureCourseName)) {
                            cm_id = "";
//                            PubMehods.showToastStr(B_Side_Course_Add.this, "清除掉cm_id值");
                        }
                    String is_edit;
                    switch (acy_enter) {
                        case 1://主页右上角进入
                            is_edit = "0";
                            to_Add_Course_Action(semester_id, JSONArray.toJSON(mCourseList).toString(),course_name, cm_id, A_0_App.USER_TOKEN, "0", is_edit,""
                                    ,teacher_name);
                            break;
                        case 2://课程详情编辑
                            is_edit = "1";
                            cm_id = courese_Detail.getCm_id();
                            String weekday_id = courese_Detail.getWeekday_id();
                            to_Add_Course_Action(semester_id, JSONArray.toJSON(mCourseList).toString(),course_name, cm_id, A_0_App.USER_TOKEN, "0", is_edit,weekday_id
                                    ,teacher_name);
                            break;
                        case 3://主页宫格进入
                            is_edit = "0";
                            totle_weeks = getIntent().getExtras().getString("totle_weeks");
                            semester_id = getIntent().getExtras().getString("semester_id");
                            to_Add_Course_Action(semester_id, JSONArray.toJSON(mCourseList).toString(),course_name, cm_id, A_0_App.USER_TOKEN, "0", is_edit,""
                                    ,teacher_name);
                            break;
                        default:
                            break;
                    }
                }
            }
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
                      //A_0_App.getInstance().showExitDialog(B_Side_Course_Detail.this,getResources().getString(R.string.token_timeout));
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
                              A_0_App.getInstance().showExitDialog(B_Side_Course_Add.this,AppStrStatic.kicked_offline());
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
        adapter = null;
        gridAdapter = null;
        if (mCourseList != null) {
            mCourseList.clear();
            mCourseList = null;
        }
        if (mTotleWeekList != null) {
            mTotleWeekList.clear();
            mTotleWeekList = null;
        }
        if (mTempWeekList != null) {
            mTempWeekList.clear();
            mTempWeekList = null;
        }
        super.onDestroy();
    }
      
}