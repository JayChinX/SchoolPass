package com.yuanding.schoolpass;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Group_Info_item;
import com.yuanding.schoolpass.bean.Cpk_Side_Lectures_List;
import com.yuanding.schoolpass.service.Api.InteSideCourse;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.download.FileCourse;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12 
 * 课程表选择背景图片
 */
public class B_Side_Course_Select_Image_temp extends A_0_CpkBaseTitle_Navi {
    
	private GridView gridView;
	private GridAdapter adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_side_course_select_grid);
		setTitleText("选择背景");

		gridView = (GridView) findViewById(R.id.gv_side_course_bg);
		adapter = new GridAdapter();
		
		gridView.setAdapter(adapter);
		
		gridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int posi, long arg3) {
                A_0_App.course_bg_index = posi;
                A_0_App.getInstance().saveUserCourseBg(posi);
                finish();
            }
        });
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	public class GridAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return B_Side_Course_Acy.bg_course.length;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {
			ViewHolder holder = null;
			if (converView == null) {
				holder = new ViewHolder();
				converView = LayoutInflater.from(B_Side_Course_Select_Image_temp.this).inflate(
						R.layout.item_side_course_select_temp, null);
				holder.iv_thumb = (ImageView) converView.findViewById(R.id.iv_course_bg_thumb);
				holder.iv_select_tag = (ImageView) converView.findViewById(R.id.iv_course_bg_tag_change);
				converView.setTag(holder);
			} else {
				holder = (ViewHolder) converView.getTag();
			}
			holder.iv_thumb.setBackgroundResource(B_Side_Course_Acy.bg_course[posi]);
			if(posi == A_0_App.course_bg_index){
			    holder.iv_select_tag.setVisibility(View.VISIBLE);
			}else{
			    holder.iv_select_tag.setVisibility(View.GONE);
			}

			return converView;
		}
	}

	static class ViewHolder { // 自定义控件集合
		public ImageView iv_thumb,iv_select_tag;
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Course_Select_Image_temp.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Course_Select_Image_temp.this,AppStrStatic.kicked_offline());
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
        super.onDestroy();
        adapter = null;
    }
}
