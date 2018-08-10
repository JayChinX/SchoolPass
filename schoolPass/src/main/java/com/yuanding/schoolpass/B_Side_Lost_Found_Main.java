package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Tab;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.DensityUtils;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月12日 下午3:18:18 失物招领————失物招领————失物招领
 */
public class B_Side_Lost_Found_Main extends A_0_CpkBaseTitle_Tab {

	public static final int FRAGMENT_ONE = 0;
	public static final int FRAGMENT_TWO = 1;
	public static final int FRAGMENT_THREE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (A_0_App.USER_STATUS.equals("2")) {
			showTitleBt(ZUI_RIGHT_BUTTON, true);
			setZuiRightBtn(R.drawable.navigationbar_add_button);
			showTitleBt(PIAN_RIGHT_BUTTON, true);
			setPianRightBtn(R.drawable.navigationbar_search_button);
		}else{
			showTitleBt(PIAN_RIGHT_BUTTON, false);
			showTitleBt(ZUI_RIGHT_BUTTON, false);
		}
		showTitleBt(BACK_BUTTON, true);
		setTitleText("失物招领");
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}

	@Override
	protected int supplyTabs(List<TabInfo> tabs) {
		tabs.add(new TabInfo(FRAGMENT_ONE, "招领",
				B_Side_Found_Acy.class));
		tabs.add(new TabInfo(FRAGMENT_TWO, "寻物",
				B_Side_Found_Found.class));
		tabs.add(new TabInfo(FRAGMENT_THREE,
				"我的",
				B_Side_Found_My.class));

		return FRAGMENT_ONE;
	}

	private PopupWindow statusPopup;
	private LinearLayout mLinerCollct, mLinerForward;

	private void showWindow(View parent) {
		if (statusPopup == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View view = layoutInflater.inflate(R.layout.item_lost_detail, null);
			mLinerCollct = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_collect);
			mLinerForward = (LinearLayout) view
					.findViewById(R.id.liner_lecture_detail_forward);
			statusPopup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
		}
		statusPopup.setFocusable(true);
		statusPopup.setOutsideTouchable(true);
		statusPopup.setBackgroundDrawable(new BitmapDrawable());
		int x = DensityUtils.dip2px(B_Side_Lost_Found_Main.this, 155);
		statusPopup.showAsDropDown(parent, -x, A_0_App.getInstance()
				.getShowViewHeight());// 第一参数负的向左，第二个参数正的向下

		mLinerForward.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				A_0_App.SIDE_NOTICE=0;
				Intent intent = new Intent(B_Side_Lost_Found_Main.this,
						B_Side_Found_Add_Found.class);
				intent.putExtra("type", "1");
				startActivity(intent);
			}
		});

		mLinerCollct.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (statusPopup != null) {
					statusPopup.dismiss();
				}
				A_0_App.SIDE_NOTICE=1;
				Intent intent = new Intent(B_Side_Lost_Found_Main.this,
						B_Side_Found_Add_Found.class);
				intent.putExtra("type", "2");
				startActivity(intent);
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
			if (A_0_App.USER_STATUS.equals("2")) {
				showWindow(v);
			}
			
			break;
		case PIAN_RIGHT_BUTTON:
			if (A_0_App.USER_STATUS.equals("2")) {
				Intent intent = new Intent(B_Side_Lost_Found_Main.this,
						B_Side_Found_Search.class);
				startActivity(intent);
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Lost_Found_Main.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Lost_Found_Main.this, AppStrStatic.kicked_offline());
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
		A_0_App.SIDE_NOTICE=-1;
	}
}
