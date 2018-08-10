package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Side_Course_CourseDetail;
import com.yuanding.schoolpass.bean.Cpk_User_Values;
import com.yuanding.schoolpass.service.Api.AppNotice_InviteInstallAppCallBack;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.MyListView;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 课程表详情
 */
public class B_Side_Course_Detail extends A_0_CpkBaseTitle_Navi {

    public static final int RESUTL_CODE = 100;
    private Cpk_Side_Course_CourseDetail courese_Detail;
    
    private MyCourseAdapter myCourseAdapter;
    private MyClassAdapter myClassAdapter;
    
    private List<Cpk_User_Values> mCourseDetailList;
    private List<String> mClassList;
    
    private MyListView lv_course_detail_detail,lv_course_detail_class;
    private Button btn_go_attdence;
    private RelativeLayout rel_course_detail,rel_course_attdent_class;
    private String totle_weeks,semester_id;
    private String detail_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_course_detail);
		setTitleText("课程详情");
		showTitleBt(ZUI_RIGHT_BUTTON, true);
		setZuiRightBtn(R.drawable.navigationbar_delete);
		
		Bundle bun = getIntent().getExtras();
		courese_Detail = (Cpk_Side_Course_CourseDetail)getIntent().getSerializableExtra(B_Side_Course_Acy.INTERT_COURSE_DTAIL);
		detail_id = bun.getString("detail_id", "");
        totle_weeks = getIntent().getExtras().getString("totle_weeks");
        semester_id = getIntent().getExtras().getString("semester_id");
        
		lv_course_detail_detail = (MyListView)findViewById(R.id.lv_course_detail_detail);
		lv_course_detail_class = (MyListView)findViewById(R.id.lv_course_detail_class);
		btn_go_attdence = (Button)findViewById(R.id.btn_go_attdence);
		rel_course_detail = (RelativeLayout)findViewById(R.id.rel_course_detail);
		rel_course_attdent_class = (RelativeLayout)findViewById(R.id.rel_course_attdent_class);
		
		myCourseAdapter = new MyCourseAdapter();
		myClassAdapter = new MyClassAdapter();
		mClassList = new ArrayList<String>();
		mCourseDetailList = new ArrayList<Cpk_User_Values>();

		mCourseDetailList = courese_Detail.getUser_values();
		mClassList  = courese_Detail.getOrgan_names();
		
		rel_course_detail.setOnClickListener(onClick);
		rel_course_attdent_class.setOnClickListener(onClick);
		btn_go_attdence.setOnClickListener(onClick);
		
		lv_course_detail_detail.setAdapter(myCourseAdapter);
		lv_course_detail_class.setAdapter(myClassAdapter);
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rel_course_detail:
                    Intent intentAdd = new Intent();
                    intentAdd.setClass(B_Side_Course_Detail.this, B_Side_Course_Add.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putSerializable(B_Side_Course_Acy.INTERT_COURSE_DTAIL, courese_Detail);
                    intentAdd.putExtras(mBundle);
                    intentAdd.putExtra("acy_enter", 2);
                    intentAdd.putExtra("totle_weeks", totle_weeks);
                    intentAdd.putExtra("semester_id", semester_id);
                    startActivityForResult(intentAdd,RESUTL_CODE);
                    break;
//                case R.id.rel_course_attdent_class://添加班级
//                    A_0_App.getInstance().class_ids = courese_Detail.getClass_ids();
//                    Intent intentClass=new Intent();
//                    intentClass.setClass(B_Side_Course_Detail.this,B_Side_Attence_Main_Add_Contacts.class);
//                    intentClass.putExtra("type", "0");
//                    intentClass.putExtra("acy_enter", "0");
//                    intentClass.putExtra("weekday_id", courese_Detail.getWeekday_id());
//                    startActivityForResult(intentClass,0);
//                    break;
//                case R.id.btn_go_attdence://开始考勤
//                    clearAddClassData();
//                    Intent intent=new Intent();
//                    intent.setClass(B_Side_Course_Detail.this,B_Side_Attence_Main_A1_New_Attence.class);
//                    intent.putExtra("type", "0");
//                    startActivity(intent);
//                    break;
                default:
                    break;
            }

        }
    };
    
    //清除之前选择的数据
//    private void clearAddClassData() {
//        A_0_App.attence_result_defined.clear();
//        A_0_App.attence_result_defined_summit.clear();
//        A_0_App.attence_result_Contacts.clear();
//        A_0_App.attence_result_Contacts_summit.clear();
//        A_0_App.attence_temp_Contacts.clear();
//    }
	
	public class MyCourseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCourseDetailList.size();
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
                converView = LayoutInflater.from(B_Side_Course_Detail.this)
                        .inflate(R.layout.item_side_course_detail, null);
            }

            TextView title = (TextView) converView.findViewById(R.id.tv_item_course_name);
            TextView content = (TextView) converView.findViewById(R.id.tv_item_course_values);
            content.setTextColor(getResources().getColor(R.color.title_no_focus_login));
            
            title.setText(mCourseDetailList.get(posi).getName());
            content.setText(mCourseDetailList.get(posi).getValue());
            return converView;
        }
    }
	
	public class MyClassAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mClassList.size();
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
                converView = LayoutInflater.from(B_Side_Course_Detail.this)
                        .inflate(R.layout.item_side_course_detail, null);
            }

            TextView title = (TextView) converView.findViewById(R.id.tv_item_course_name);
            TextView content = (TextView) converView.findViewById(R.id.tv_item_course_values);
            title.setVisibility(View.GONE);
            content.setTextColor(getResources().getColor(R.color.black_code));
            
            content.setText(mClassList.get(posi));
            return converView;
        }
    }
	
    private void to_Del_Action(String weekday_id,String detail_id) {
        A_0_App.getInstance().showProgreDialog(B_Side_Course_Detail.this, "", true);
        A_0_App.getApi().del_Course_Action(weekday_id, detail_id, A_0_App.USER_TOKEN, new AppNotice_InviteInstallAppCallBack() {
            @Override
            public void onSuccess(String message) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Detail.this);
                if (message != null && !("").equals(message))
                    PubMehods.showToastStr(B_Side_Course_Detail.this, message);
                finish();
            }
        },new Inter_Call_Back() {
            
            @Override
            public void onFinished() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void onFailure(String msg) {
                if (isFinishing())
                    return;
                A_0_App.getInstance().CancelProgreDialog(B_Side_Course_Detail.this);
                PubMehods.showToastStr(B_Side_Course_Detail.this, msg);
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
            case ZUI_RIGHT_BUTTON:
                if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT))
                    to_Del_Action(courese_Detail.getWeekday_id(), detail_id);
                break;
            default:
                break;
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
//            A_0_App.attence_result_defined_summit.addAll(A_0_App.attence_result_defined);
//            A_0_App.attence_result_Contacts_summit.addAll(A_0_App.attence_result_Contacts);
//            A_0_App.attence_result_Contacts_summit.addAll(A_0_App.attence_temp_Contacts);
//            String organ_name = data.getExtras().getString("organNames");
//            String class_ids = data.getExtras().getString("class_ids");
//            if (requestCode == 0) {
//                if (!"".equals(organ_name)) {
//                    if (organ_name != null && organ_name.length()>0) {
//                        String[] temp = organ_name.split(",");
//                        List<String> mList = new ArrayList<String>();
//                        for (int i = 0; i < temp.length; i++) {
//                            mList.add(temp[i]);
//                        }
//                        courese_Detail.setClass_ids(class_ids);
//                        mClassList = mList;
//                        myClassAdapter.notifyDataSetChanged();
//                    }
//                }
//            }else if (requestCode == RESUTL_CODE) {
                this.finish();
//            }
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
                          A_0_App.getInstance().showExitDialog(B_Side_Course_Detail.this,AppStrStatic.kicked_offline());
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
//        A_0_App.getInstance().class_ids = "";
        if (mClassList != null) {
            mClassList.clear();
            mClassList = null;
        }
        if (mCourseDetailList != null) {
            mCourseDetailList.clear();
            mCourseDetailList = null;
        }
        myCourseAdapter = null;
        myClassAdapter = null;
        courese_Detail = null;

//        clearAddClassData();
        super.onDestroy();
    }

}