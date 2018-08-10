package com.yuanding.schoolpass;

import java.util.ArrayList;
import java.util.List;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.ConnectionStatusListener.ConnectionStatus;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月21日 下午3:14:59
 * 失物招领
 */
public class B_Side_Lost_Found_Acy extends A_0_CpkBaseTitle_Navi{
	
	
	private ListView lv_meeting_going,lv_meeting_no_start,lv_meeting_end;
	private View side_one_load_error,side_one_network_error,side_one_no_content;
	private View side_two_load_error,side_two_network_error,side_two_no_content;
	private View side_three_load_error,side_three_network_error,side_three_no_content;
	
	private RelativeLayout rel_tab_meet_going,rel_tab_meet_no_start,rel_tab_meet_over;
	private TextView tv_tab_meet_title1,tv_tab_meet_title2,tv_tab_meet_title3;
	
	private View vw_Going,vwNoStart,vmHavaEnd;
	private ViewPager mPager;// 页卡内容
	private List<View> listViews; // Tab页面列表
	private ImageView cursor;// 动画图片
	private TextView t1, t2, t3;// 页卡头标
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_side_lost_mian);
		setTitleText("失物招领");
		
		tv_tab_meet_title1 = (TextView)findViewById(R.id.tv_tab_meet_title1);
		tv_tab_meet_title2 = (TextView)findViewById(R.id.tv_tab_meet_title2);
		tv_tab_meet_title3 = (TextView)findViewById(R.id.tv_tab_meet_title3);
		
		InitImageView();
		InitTextView();
		InitViewPager();
		
		if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
		
	}
	
	/********************************选项卡*****************************************************/
	/**
	 * 初始化头标
	 */
	private void InitTextView() {
		rel_tab_meet_going = (RelativeLayout)findViewById(R.id.rel_tab_meet_going);
		rel_tab_meet_no_start = (RelativeLayout)findViewById(R.id.rel_tab_meet_no_start);
		rel_tab_meet_over = (RelativeLayout)findViewById(R.id.rel_tab_meet_over);

		rel_tab_meet_going.setOnClickListener(new MyOnClickListener(0));
		rel_tab_meet_no_start.setOnClickListener(new MyOnClickListener(1));
		rel_tab_meet_over.setOnClickListener(new MyOnClickListener(2));
	}

	/**
	 * 初始化ViewPager
	 */
	private void InitViewPager() {
		mPager = (ViewPager) findViewById(R.id.vp_meeting_tab);
		listViews = new ArrayList<View>();
		LayoutInflater mInflater = getLayoutInflater();
		vw_Going = mInflater.inflate(R.layout.activity_side_lost_zhaoling,null);
		vwNoStart = mInflater.inflate(R.layout.activity_side_lost_found,null);
		vmHavaEnd = mInflater.inflate(R.layout.activity_side_lost_mine,null);
		
		lv_meeting_going = (ListView)vw_Going.findViewById(R.id.lv_meeting_going);
		lv_meeting_no_start = (ListView)vwNoStart.findViewById(R.id.lv_meeting_no_start);
		lv_meeting_end = (ListView)vmHavaEnd.findViewById(R.id.lv_meeting_end);
		
		side_one_load_error = vw_Going.findViewById(R.id.side_one_load_error);
		side_one_network_error = vw_Going.findViewById(R.id.side_one_network_error);
		side_one_no_content = vw_Going.findViewById(R.id.side_one_no_content);
		ImageView iv_blank_por = (ImageView)side_one_no_content.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name = (TextView)side_one_no_content.findViewById(R.id.tv_blank_name);
		iv_blank_por.setBackgroundResource(R.drawable.no_zhaoling);
		tv_blank_name.setText("丢东西先去发寻物，暂时没人捡到遗失物品~");
		
		side_one_load_error.setOnClickListener(onClickOne);
		side_one_network_error.setOnClickListener(onClickOne);
		
		side_two_load_error = vwNoStart.findViewById(R.id.side_two_load_error);
		side_two_network_error = vwNoStart.findViewById(R.id.side_two_network_error);
		side_two_no_content = vwNoStart.findViewById(R.id.side_two_no_content);
		ImageView iv_blank_por_ = (ImageView)side_two_no_content.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name_ = (TextView)side_two_no_content.findViewById(R.id.tv_blank_name);
		iv_blank_por_.setBackgroundResource(R.drawable.no_zhaoling);
		tv_blank_name_.setText("小伙伴们都好好哒，暂时没人丢东西~");
		
		side_two_load_error.setOnClickListener(onClickTwo);
		side_two_network_error.setOnClickListener(onClickTwo);
		
		side_three_load_error = vmHavaEnd.findViewById(R.id.side_three_load_error);
		side_three_network_error = vmHavaEnd.findViewById(R.id.side_three_network_error);
		side_three_no_content = vmHavaEnd.findViewById(R.id.side_three_no_content);
		ImageView iv_blank_por_a = (ImageView)side_three_no_content.findViewById(R.id.iv_blank_por);
		TextView tv_blank_name_a = (TextView)side_three_no_content.findViewById(R.id.tv_blank_name);
		iv_blank_por_a.setBackgroundResource(R.drawable.no_zhaoling);
		tv_blank_name_a.setText("暂时没有与我相关的招领寻物信息~");
		
		side_three_load_error.setOnClickListener(onClickThree);
		side_three_network_error.setOnClickListener(onClickThree);
		
		listViews.add(vw_Going);
		listViews.add(vwNoStart);
		listViews.add(vmHavaEnd);
		mPager.setAdapter(new MyPagerAdapter(listViews));
		mPager.setCurrentItem(0);
		mPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		showLoadResultOne(false, false, false, true);
		showLoadResultTwo(false, false, false, true);
		showLoadResultThree(false, false, false, true);
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.test_line)
				.getWidth();// 获取图片宽度
		bmpW +=20;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
		}

		@Override
		public void finishUpdate(View arg0) {
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {

			if (arg1 < 3) {
				((ViewPager) arg0).addView(mListViews.get(arg1 % 3), 0);
			}
			return mListViews.get(arg1 % 3);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			mPager.setCurrentItem(index);
		}
	};

	/**
	 * 页卡切换监听
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2 + 20;// 页卡1 -> 页卡3 偏移量

		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
				}
				break;
			case 1:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, one, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, one, 0, 0);
				}
				break;
			case 2:
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, two, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
			switchTitleColor(arg0);
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	private void switchTitleColor(int arg0) {
		switch (arg0) {
		case 0:
			tv_tab_meet_title1.setTextColor(getResources().getColor(R.color.blue_code));
			tv_tab_meet_title2.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			tv_tab_meet_title3.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			break;
		case 1:
			tv_tab_meet_title1.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			tv_tab_meet_title2.setTextColor(getResources().getColor(R.color.blue_code));
			tv_tab_meet_title3.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			break;
		case 2:
			tv_tab_meet_title1.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			tv_tab_meet_title2.setTextColor(getResources().getColor(R.color.title_no_focus_login));
			tv_tab_meet_title3.setTextColor(getResources().getColor(R.color.blue_code));
			break;
		default:
			break;
		}
	}
	/********************************招领*****************************************************/
	private void showLoadResultOne(boolean list, boolean noNet,boolean loadFaile,boolean noData) {
		if (list)
			lv_meeting_going.setVisibility(View.VISIBLE);
		else
			lv_meeting_going.setVisibility(View.GONE);
		if (noNet)
			side_one_network_error.setVisibility(View.VISIBLE);
		else
			side_one_network_error.setVisibility(View.GONE);
		
		if (loadFaile)
			side_one_load_error.setVisibility(View.VISIBLE);
		else
			side_one_load_error.setVisibility(View.GONE);
		
		if (noData)
			side_one_no_content.setVisibility(View.VISIBLE);
		else
			side_one_no_content.setVisibility(View.GONE);
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClickOne = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.side_acy_network_error:
//				showLoadResultOne(false, false, false,false);
//				clearBusinessList();
//				readData();
//				break;
			case R.id.side_acy_load_error:
				showLoadResultOne(false, false, false,false);
				
				break;
			default:
				break;
			}
		}
	};
	/********************************寻物*****************************************************/
	private void showLoadResultTwo(boolean list, boolean noNet,boolean loadFaile,boolean noData) {
		if (list)
			lv_meeting_no_start.setVisibility(View.VISIBLE);
		else
			lv_meeting_no_start.setVisibility(View.GONE);
		if (noNet)
			side_two_network_error.setVisibility(View.VISIBLE);
		else
			side_two_network_error.setVisibility(View.GONE);
		
		if (loadFaile)
			side_two_load_error.setVisibility(View.VISIBLE);
		else
			side_two_load_error.setVisibility(View.GONE);
		
		if (noData)
			side_two_no_content.setVisibility(View.VISIBLE);
		else
			side_two_no_content.setVisibility(View.GONE);
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClickTwo = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.side_acy_network_error:
//				showLoadResultTwo(false, false, false,false);
//				clearBusinessList();
//				readData();
//				break;
			case R.id.side_acy_load_error:
				showLoadResultTwo(false, false, false,false);
				
				break;
			default:
				break;
			}
		}
	};	/********************************我的*****************************************************/
	private void showLoadResultThree(boolean list, boolean noNet,boolean loadFaile,boolean noData) {
		if (list)
			lv_meeting_end.setVisibility(View.VISIBLE);
		else
			lv_meeting_end.setVisibility(View.GONE);
		if (noNet)
			side_three_network_error.setVisibility(View.VISIBLE);
		else
			side_three_network_error.setVisibility(View.GONE);
		
		if (loadFaile)
			side_three_load_error.setVisibility(View.VISIBLE);
		else
			side_three_load_error.setVisibility(View.GONE);
		
		if (noData)
			side_three_no_content.setVisibility(View.VISIBLE);
		else
			side_three_no_content.setVisibility(View.GONE);
	}
	
	// 数据加载，及网络错误提示
	OnClickListener onClickThree = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
//			case R.id.side_acy_network_error:
//				showLoadResultThree(false, false, false,false);
//				clearBusinessList();
//				readData();
//				break;
			case R.id.side_acy_load_error:
				showLoadResultThree(false, false, false,false);
				
				break;
			default:
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
                    //A_0_App.getInstance().showExitDialog(B_Side_Lost_Found_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Side_Lost_Found_Acy.this, AppStrStatic.kicked_offline());
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