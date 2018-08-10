package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Version;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.DensityUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.utils.download.Update_App;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 下午2:53:58
 * 类说明
 */
public class B_Account_MySet_About_Acy extends A_0_CpkBaseTitle_Navi{
	
	private TextView tv_about_app_version;
	private LinearLayout liner_about_check_version;
	private TextView tv_about_check_version;
	private TextView tv_about_product_introduction;
	private TextView tv_about_normal_problems;
	private TextView tv_about_call_us;
	private TextView tv_about_app_arguement,tv_temp_about;
	private ImageView iv_app_logo;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Update_App.check_upDate_App(B_Account_MySet_About_Acy.this,false);
		setView(R.layout.activity_acc_about_app);
		
		setTitleText("关于"+A_0_App.APP_NAME);
		tv_temp_about=(TextView) findViewById(R.id.tv_temp_about);
		tv_about_app_version=(TextView) findViewById(R.id.tv_about_app_version);
		liner_about_check_version=(LinearLayout) findViewById(R.id.liner_check_version);
		tv_about_check_version=(TextView) findViewById(R.id.tv_about_check_version);
		tv_about_product_introduction=(TextView) findViewById(R.id.tv_about_product_introduction);
		tv_about_normal_problems=(TextView) findViewById(R.id.tv_about_normal_problems);
		tv_about_call_us=(TextView) findViewById(R.id.tv_about_call_us);
		tv_about_app_arguement=(TextView) findViewById(R.id.tv_about_app_arguement);
		iv_app_logo=(ImageView) findViewById(R.id.iv_app_logo);
		tv_about_app_arguement.setOnClickListener(onclick);
		liner_about_check_version.setOnClickListener(onclick);
		tv_about_product_introduction.setOnClickListener(onclick);
		tv_about_normal_problems.setOnClickListener(onclick);
		tv_about_call_us.setOnClickListener(onclick);
		tv_temp_about.setText(AppStrStatic.str_about_copyright_bottom());
		tv_about_app_arguement.setText(A_0_App.APP_NAME+"使用条款和协议");
		tv_about_app_version.setText("V" + PubMehods.getVerName(getApplicationContext()));
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
        int top=DensityUtils.dip2px(this, 48);
	    layoutParams.setMargins(0, top, 0, 0);// 4个参数按顺序分别是左上右下
	    iv_app_logo.setLayoutParams(layoutParams);
	    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
			ViewGroup.LayoutParams.WRAP_CONTENT,
			ViewGroup.LayoutParams.WRAP_CONTENT);
	    int top2=DensityUtils.dip2px(this, 34);
	    layoutParams2.setMargins(0, top2, 0, 0);// 4个参数按顺序分别是左上右下
	    tv_about_app_arguement.setLayoutParams(layoutParams2);
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
        new Handler().postDelayed(new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                dealUpdates(1);
            }
        }, 300);
	}
	
	/*
	 * 1是初始化，2是点击升级
	 */
    private void dealUpdates(int type) {
	    Cpk_Version cpk_Version = A_0_App.getInstance().getVersion();
        if (cpk_Version != null && cpk_Version.getVersionCode()!= null ) {
            if (Integer.valueOf(cpk_Version.getVersionCode()) > PubMehods.getVerCode(B_Account_MySet_About_Acy.this)) {
                if(type==1){
                   tv_about_check_version.setText("发现新版本");
                }else{
                    if(cpk_Version.getIs_require().equals("1")){//强制更新
                        Update_App.showUpdateDialog(B_Account_MySet_About_Acy.this,cpk_Version,false);
                    }else{
                        Update_App.showUpdateDialog(B_Account_MySet_About_Acy.this,cpk_Version,true);
                    }
                }
            } else {
                if(type==1)
                     tv_about_check_version.setText("已是最新版本");
                else
                    PubMehods.showToastStr(B_Account_MySet_About_Acy.this, "已是最新版本");
            }
        }else{
            if(type==1){
               tv_about_check_version.setText("当前版本：V" + PubMehods.getVerName(getApplicationContext()));
            }else{
                Update_App.check_upDate_App(B_Account_MySet_About_Acy.this,true);
            }
        }
    }
	
    OnClickListener onclick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent=new Intent();
			switch(v.getId())
			{
			case R.id.liner_check_version:
				//检查版本
			    dealUpdates(2);
				break;
			case R.id.tv_about_product_introduction:
				//产品介绍
				intent.setClass(B_Account_MySet_About_Acy.this,Pub_WebView_Load_Other_Acy.class);
				intent.putExtra("title_text", getResources().getString(R.string.str_product_description));
				 intent.putExtra("tag_skip", "1");
				intent.putExtra("url_text", AppStrStatic.LINK_USER_INTRODUCTION);
				startActivity(intent);
				break;
			case R.id.tv_about_normal_problems:
				//常见问题
				intent.setClass(B_Account_MySet_About_Acy.this,Pub_WebView_Load_Other_Acy.class);
				intent.putExtra("title_text", getResources().getString(R.string.str_common_problem));
				intent.putExtra("tag_skip", "1");
				intent.putExtra("url_text", AppStrStatic.LINK_USER_QUESTION);
				startActivity(intent);
				break;
			case R.id.tv_about_call_us:
				intent.setClass(B_Account_MySet_About_Acy.this, B_Account_MySet_About_Call_Acy.class);
				startActivity(intent);
				//联系我们
				break;
			case R.id.tv_about_app_arguement:
				//微校邦条款协议
				intent.setClass(B_Account_MySet_About_Acy.this,Pub_WebView_Load_Other_Acy.class);
				intent.putExtra("title_text", "用户协议");
				intent.putExtra("tag_skip", "1");
				intent.putExtra("url_text", AppStrStatic.LINK_USER_REGEDIT);
				startActivity(intent);
				break;
			}
		}
	};
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
                    //A_0_App.getInstance().showExitDialog(B_Account_Push_Set_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Account_MySet_About_Acy.this,AppStrStatic.kicked_offline());
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
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
			finish();
			break;

		default:
			break;
		}
		
	}

}
