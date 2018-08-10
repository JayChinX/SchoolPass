package com.yuanding.schoolpass.base;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi.MyConnectionStatusListener;
import com.yuanding.schoolpass.utils.AppStrStatic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * @ClassName: CpkBaseTitle
 * @Description: 通用标题栏：包含标题、返回键、最右按钮，修改标题栏颜色
 * @author Jiaohaili 
 */
public abstract class A_0_CpkBaseTitle extends FragmentActivity implements View.OnClickListener{

	public static final int BACK_BUTTON = 1;
	public static final int SEARCH_BUTTON = 3;
	
	public TextView titleText;
	private LinearLayout mLinerBack;
	private LinearLayout mLinerSearch;
	public  RelativeLayout rl_title_base;//设置标题栏颜色
	private ImageView iv_zui_right;
	
	protected abstract void handleTitleBarEvent(int resId);
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setContentView(R.layout.titlebar_base);
		
        titleText = (TextView) findViewById(R.id.titlebar_base_text);
        rl_title_base = (RelativeLayout) findViewById(R.id.rl_title_base_color);
        mLinerBack = (LinearLayout) findViewById(R.id.titlebar_liner_back);
        mLinerSearch = (LinearLayout) findViewById(R.id.liner_titlebar_base_zui_right);
        iv_zui_right = (ImageView) findViewById(R.id.iv_title_base_zui_right);
        
        mLinerBack.setOnClickListener(this);
		mLinerSearch.setOnClickListener(this);
		
		startListtenerRongYun();
	}
	
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.titlebar_liner_back:
                handleTitleBarEvent(BACK_BUTTON);
                break;
            case R.id.liner_titlebar_base_zui_right:
                handleTitleBarEvent(SEARCH_BUTTON);
                break;
        }
    }

	protected void setView(int layoutID)
	{
		LinearLayout lin=(LinearLayout)findViewById(R.id.title_base_content_base);
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
	
	   //设置最右图片
    @SuppressLint("NewApi")
    public void setZuiRightBtn(int resid){
        iv_zui_right.setBackgroundDrawable(getResources().getDrawable(resid));
    }
    
	/**
	* @Title: showTitleBt
	* @Description: TODO(设置标题栏按钮图片)
	* @param @param btTag
	 */
	protected void showTitleBt(int btTag,boolean bShow)
	{
		   if (btTag == SEARCH_BUTTON) {
			if(bShow)
			{
				mLinerSearch.setVisibility(View.VISIBLE);
			}else{
				mLinerSearch.setVisibility(View.INVISIBLE);
			}
		}else if (btTag == BACK_BUTTON) {
            if(bShow)
            {
                mLinerBack.setVisibility(View.VISIBLE);
            }else{
                mLinerBack.setVisibility(View.GONE);
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
                            A_0_App.getInstance().showExitDialog(A_0_CpkBaseTitle.this, AppStrStatic.kicked_offline());
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
