package com.yuanding.schoolpass.base;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.MobclickAgent;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.utils.AppStrStatic;

/**
 * 
 * @ClassName: CpkBaseTitle
 * @Description: 通用标题栏：包含标题、搜素、地图按钮、返回按钮
 * @author Jiaohaili 
 *
 */
public abstract class A_0_CpkBaseTitle_Navi extends FragmentActivity implements View.OnClickListener{

	public static final int BACK_BUTTON = 1;
	public static final int ZUI_RIGHT_BUTTON = 2;
	public static final int PIAN_RIGHT_BUTTON = 3;
	public static final int ZUI_RIGHT_TEXT = 5;
	
	private TextView titleText;
	private LinearLayout mLinerBack,liner_titlebar_zui_right,liner_titlebar_pian_right;
	private ImageView iv_pian_right,iv_zui_right;
	private Button tv_zuiyou_text;
	
	protected abstract void handleTitleBarEvent(int resId,View v);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setContentView(R.layout.titlebar_base_navi);
		
        titleText = (TextView) findViewById(R.id.tv_titlebar_title);
        tv_zuiyou_text = (Button)findViewById(R.id.tv_title_zuiyou_text);
        mLinerBack = (LinearLayout) findViewById(R.id.liner_titlebar_back);
        liner_titlebar_zui_right = (LinearLayout) findViewById(R.id.liner_titlebar_zui_right);
        liner_titlebar_pian_right = (LinearLayout) findViewById(R.id.liner_titlebar_pian_right);
        
        iv_pian_right = (ImageView) findViewById(R.id.iv_pian_right);
        iv_zui_right = (ImageView) findViewById(R.id.iv_zui_right);
        mLinerBack.setOnClickListener(this);
        liner_titlebar_zui_right.setOnClickListener(this);
        liner_titlebar_pian_right.setOnClickListener(this);
        tv_zuiyou_text.setOnClickListener(this);
        
        startListtenerRongYun();
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.liner_titlebar_back:
			handleTitleBarEvent(BACK_BUTTON,v);
			break;
			
		case R.id.liner_titlebar_zui_right:
			handleTitleBarEvent(ZUI_RIGHT_BUTTON,v);
			break;
			
		case R.id.liner_titlebar_pian_right:
			handleTitleBarEvent(PIAN_RIGHT_BUTTON,v);
			break;
			
		case R.id.tv_title_zuiyou_text:
            handleTitleBarEvent(ZUI_RIGHT_TEXT,v);
            break;
            
        }
	}

	protected void setView(int layoutID)
	{
		LinearLayout lin=(LinearLayout)findViewById(R.id.base_content_base);
		LayoutInflater flater=LayoutInflater.from(this);
		View v=flater.inflate(layoutID, null);
		v.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
		lin.removeAllViews();
		lin.addView(v);
	}
	
	
	
	//标题
	public void setTitleText(int resid){
		titleText.setText(resid);
	}
	
	public void setTitleText(String resid){
		titleText.setText(resid);
	}

	//最右边Text
	public void setZuiYouText(String resid) {
		tv_zuiyou_text.setText(resid);
	}

	public void showToastFileStr(String str) {
		Toast.makeText(A_0_CpkBaseTitle_Navi.this, str, Toast.LENGTH_SHORT).show();
	}

	//设置最右图片
	@SuppressLint("NewApi")
	public void setZuiRightBtn(int resid){
		iv_zui_right.setBackgroundDrawable(getResources().getDrawable(resid));
	}
	
	//设置偏右图片
	@SuppressLint("NewApi")
	public void setPianRightBtn(int resid){
		iv_pian_right.setBackgroundDrawable(getResources().getDrawable(resid));
	}
	
	/**
	 * 
	* @Title: showTitleBt
	* @Description: TODO(设置标题栏按钮图片)
	* @param @param btTag
	* @param @param bShow 
	 */
	protected void showTitleBt(int btTag,boolean bShow)
	{
		   if (btTag == BACK_BUTTON) {
            if(bShow)
            {
                mLinerBack.setVisibility(View.VISIBLE);
            }else{
                mLinerBack.setVisibility(View.GONE);
            }
        }else if (btTag == ZUI_RIGHT_BUTTON) {
            if(bShow)
            {
            	liner_titlebar_zui_right.setVisibility(View.VISIBLE);
            }else{
            	liner_titlebar_zui_right.setVisibility(View.GONE);
            }
        }else if (btTag == PIAN_RIGHT_BUTTON) {
            if(bShow)
            {
            	liner_titlebar_pian_right.setVisibility(View.VISIBLE);
            }else{
            	liner_titlebar_pian_right.setVisibility(View.GONE);
            }
        }else if (btTag == ZUI_RIGHT_TEXT) {
            if(bShow)
            {
                tv_zuiyou_text.setVisibility(View.VISIBLE);
            }else{
                tv_zuiyou_text.setVisibility(View.GONE);
            }
        }
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
                            A_0_App.getInstance().showExitDialog(A_0_CpkBaseTitle_Navi.this, AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);}
	
	
}
