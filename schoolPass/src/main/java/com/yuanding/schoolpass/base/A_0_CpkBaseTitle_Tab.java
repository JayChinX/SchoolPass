/*
 * @author http://blog.csdn.net/singwhatiwanna
 */
package com.yuanding.schoolpass.base;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle.MyConnectionStatusListener;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.view.frament.TitleIndicator;

/**
 * 
* @ClassName: IndicatorFragmentActivity
* @Description: TODO(带有返回按钮的选项卡效果)
* @author Jiaohaili 
* @date 2015年12月29日 下午3:12:11
*
 */
@SuppressWarnings("static-access")
public abstract class A_0_CpkBaseTitle_Tab extends FragmentActivity implements View.OnClickListener,OnPageChangeListener {
	
	public static final int BACK_BUTTON = 1;
	public static final int ZUI_RIGHT_BUTTON = 2;
	public static final int PIAN_RIGHT_BUTTON = 3;
    public static final int ZUI_RIGHT_TEXT = 5;
	
    private Button tv_title_tab_zuiyou_text;
	private TextView titleText,titleTextCenter;
	private LinearLayout mLinerBack,liner_titlebar_zui_right,liner_titlebar_pian_right;
	private ImageView iv_pian_right,iv_zui_right;
	
	protected abstract void handleTitleBarEvent(int resId,View v);
	
	private static final String TAG = "DxFragmentActivity";

	public static final String EXTRA_TAB = "tab";
	public static final String EXTRA_QUIT = "extra.quit";

	protected int mCurrentTab = 0;
	protected int mLastTab = -1;
	// 存放选项卡信息的列表
	protected ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

	// viewpager adapter
	protected MyAdapter myAdapter = null;

	// viewpager
	protected ViewPager mPager;

	// 选项卡控件
	protected TitleIndicator mIndicator;

	public FinishReceiver finishReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    A_0_App.getInstance().addActivity(this);
		setContentView(getMainViewResId());
		
		 finishReceiver = new FinishReceiver();
			IntentFilter intentFilter = new IntentFilter("notice");
		    registerReceiver(finishReceiver, intentFilter);
	   
        titleText = (TextView) findViewById(R.id.tv_titlebar_title_tab);
        titleTextCenter = (TextView) findViewById(R.id.tv_titlebar_title_center_tab);
        mLinerBack = (LinearLayout) findViewById(R.id.liner_titlebar_back_tab);
        liner_titlebar_zui_right = (LinearLayout) findViewById(R.id.liner_titlebar_zui_right_tab);
        liner_titlebar_pian_right = (LinearLayout) findViewById(R.id.liner_titlebar_pian_right_tab);
        tv_title_tab_zuiyou_text = (Button)findViewById(R.id.tv_title_tab_zuiyou_text);
        
        iv_pian_right = (ImageView) findViewById(R.id.iv_pian_right_tab);
        iv_zui_right = (ImageView) findViewById(R.id.iv_zui_right_tab);
        
        mLinerBack.setOnClickListener(this);
        liner_titlebar_zui_right.setOnClickListener(this);
        liner_titlebar_pian_right.setOnClickListener(this);
        tv_title_tab_zuiyou_text.setOnClickListener(this);
        
		initViews();
		startListtenerRongYun();
		/*
		 * //设置viewpager内部页面之间的间距
		 * mPager.setPageMargin(getResources().getDimensionPixelSize
		 * (R.dimen.dp_5_xh10)); //设置viewpager内部页面间距的drawable
		 * mPager.setPageMarginDrawable(R.color.black_code);
		 */
	}
	
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.liner_titlebar_back_tab:
			handleTitleBarEvent(BACK_BUTTON,v);
			break;
			
		case R.id.liner_titlebar_zui_right_tab:
			handleTitleBarEvent(ZUI_RIGHT_BUTTON,v);
			break;
			
		case R.id.liner_titlebar_pian_right_tab:
			handleTitleBarEvent(PIAN_RIGHT_BUTTON,v);
			break;
			
        case R.id.tv_title_tab_zuiyou_text:
            handleTitleBarEvent(ZUI_RIGHT_TEXT,v);
            break;
			
		}
	}

	//左边标题
	public void setTitleText(int resid){
		titleText.setVisibility(View.VISIBLE);
		titleText.setText(resid);
	}
	
	public void setTitleText(String resid){
		titleText.setVisibility(View.VISIBLE);
		titleText.setText(resid);
	}
	
	
	//居中标题
	public void setTitleTextCenter(int resid){
		titleTextCenter.setVisibility(View.VISIBLE);
		titleTextCenter.setText(resid);
	}
	
	public void setTitleTextCenter(String resid){
		titleTextCenter.setVisibility(View.VISIBLE);
		titleTextCenter.setText(resid);
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
	
    //最右边Text
    
   public void setZuiYouText(String resid){
       tv_title_tab_zuiyou_text.setText(resid);
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
                tv_title_tab_zuiyou_text.setVisibility(View.VISIBLE);
            }else{
                tv_title_tab_zuiyou_text.setVisibility(View.GONE);
            }
        }
	}
	
	public TitleIndicator getIndicator() {
		return mIndicator;
	}
    
	public class MyAdapter extends FragmentPagerAdapter {
		ArrayList<TabInfo> tabs = null;
		Context context = null;

		public MyAdapter(Context context, FragmentManager fm,
				ArrayList<TabInfo> tabs) {
			super(fm);
			this.tabs = tabs;
			this.context = context;
		}

		@Override
		public Fragment getItem(int pos) {
			Fragment fragment = null;
			if (tabs != null && pos < tabs.size()) {
				TabInfo tab = tabs.get(pos);
				if (tab == null)
					return null;
				fragment = tab.createFragment();
			}
			return fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public int getCount() {
			if (tabs != null && tabs.size() > 0)
				return tabs.size();
			return 0;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabInfo tab = tabs.get(position);
			Fragment fragment = (Fragment) super.instantiateItem(container,
					position);
			tab.fragment = fragment;
			return fragment;
		}
	}

	private final void initViews() {
		// 这里初始化界面
		mCurrentTab = supplyTabs(mTabs);
		Intent intent = getIntent();
		if (intent != null) {
			mCurrentTab = intent.getIntExtra(EXTRA_TAB, mCurrentTab);
		}
		Log.d(TAG, "mTabs.size() == " + mTabs.size() + ", cur: " + mCurrentTab);
		myAdapter = new MyAdapter(this, getSupportFragmentManager(), mTabs);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPager.setAdapter(myAdapter);
		mPager.setOnPageChangeListener(this);
		mPager.setOffscreenPageLimit(mTabs.size());

		mIndicator = (TitleIndicator) findViewById(R.id.pagerindicator);
        mIndicator.init(mCurrentTab, mTabs, mPager);

		mPager.setCurrentItem(mCurrentTab);
		mLastTab = mCurrentTab;
		
	}

	/**
	 * 添加一个选项卡
	 * 
	 * @param tab
	 */
	public void addTabInfo(TabInfo tab) {
		mTabs.add(tab);
		myAdapter.notifyDataSetChanged();
	}

	/**
	 * 从列表添加选项卡
	 * 
	 * @param tabs
	 */
	public void addTabInfos(ArrayList<TabInfo> tabs) {
		mTabs.addAll(tabs);
		myAdapter.notifyDataSetChanged();
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		mIndicator.onScrolled((mPager.getWidth() + mPager.getPageMargin())
				* position + positionOffsetPixels);
	}

	@Override
	public void onPageSelected(int position) {
		mIndicator.onSwitched(position);
		mCurrentTab = position;
		Intent intent1 = new Intent("sidebar");
		sendBroadcast(intent1);
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		if (state == ViewPager.SCROLL_STATE_IDLE) {
			mLastTab = mCurrentTab;
		}
		Intent intent1 = new Intent("sidebar");
		sendBroadcast(intent1);
	}

	protected TabInfo getFragmentById(int tabId) {
		if (mTabs == null)
			return null;
		for (int index = 0, count = mTabs.size(); index < count; index++) {
			TabInfo tab = mTabs.get(index);
			if (tab.getId() == tabId) {
				return tab;
			}
		}
		return null;
	}

	/**
	 * 跳转到任意选项卡
	 * 
	 * @param tabId
	 *            选项卡下标
	 */
	public void navigate(int tabId) {
		for (int index = 0, count = mTabs.size(); index < count; index++) {
			if (mTabs.get(index).getId() == tabId) {
				mPager.setCurrentItem(index);
			}
		}
	}
	
	public void updateChildTips(int postion, boolean showTips) {
	    mIndicator.updateChildTips(postion, showTips);
    }

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 * 返回layout id
	 * 
	 * @return layout id
	 */
	protected int getMainViewResId() {
		return R.layout.titled_fragment_tab_activity;
	}

	/**
	 * 在这里提供要显示的选项卡数据
	 */
	protected abstract int supplyTabs(List<TabInfo> tabs);

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// for fix a known issue in support library
		// https://code.google.com/p/android/issues/detail?id=19917
		outState.putString("WORKAROUND_FOR_BUG_19917_KEY",
				"WORKAROUND_FOR_BUG_19917_VALUE");
		super.onSaveInstanceState(outState);
	}

	/**
	 * 单个选项卡类，每个选项卡包含名字，图标以及提示（可选，默认不显示）
	 */
	public static class TabInfo implements Parcelable {

		private int id;
		private int icon;
		private String name = null;
		public boolean hasTips = false;
		public Fragment fragment = null;
		public boolean notifyChange = false;
		@SuppressWarnings("rawtypes")
		public Class fragmentClass = null;

		@SuppressWarnings("rawtypes")
		public TabInfo(int id, String name, Class clazz) {
			this(id, name, 0, clazz);
		}

		@SuppressWarnings("rawtypes")
		public TabInfo(int id, String name, boolean hasTips, Class clazz) {
			this(id, name, 0, clazz);
			this.hasTips = hasTips;
		}

		@SuppressWarnings("rawtypes")
		public TabInfo(int id, String name, int iconid, Class clazz) {
			super();

			this.name = name;
			this.id = id;
			icon = iconid;
			fragmentClass = clazz;
		}

		public TabInfo(Parcel p) {
			this.id = p.readInt();
			this.name = p.readString();
			this.icon = p.readInt();
			this.notifyChange = p.readInt() == 1;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setIcon(int iconid) {
			icon = iconid;
		}

		public int getIcon() {
			return icon;
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public Fragment createFragment() {
			if (fragment == null) {
				Constructor constructor;
				try {
					constructor = fragmentClass.getConstructor(new Class[0]);
					fragment = (Fragment) constructor
							.newInstance(new Object[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return fragment;
		}

		public static final Parcelable.Creator<TabInfo> CREATOR = new Parcelable.Creator<TabInfo>() {
			public TabInfo createFromParcel(Parcel p) {
				return new TabInfo(p);
			}

			public TabInfo[] newArray(int size) {
				return new TabInfo[size];
			}
		};

		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel p, int flags) {
			p.writeInt(id);
			p.writeString(name);
			p.writeInt(icon);
			p.writeInt(notifyChange ? 1 : 0);
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
                            A_0_App.getInstance().showExitDialog(A_0_CpkBaseTitle_Tab.this, AppStrStatic.kicked_offline());
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
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onDestroy() {
		mTabs.clear();
		mTabs = null;
		myAdapter.notifyDataSetChanged();
		myAdapter = null;
		mPager.setAdapter(null);
		mPager = null;
		mIndicator = null;
		if (finishReceiver!=null) {
			unregisterReceiver(finishReceiver);
		}
        
		super.onDestroy();
	}
	public class FinishReceiver extends BroadcastReceiver {@Override
		public void onReceive(Context arg0, Intent arg1) {
			navigate(A_0_App.SIDE_NOTICE);
		}}
}
