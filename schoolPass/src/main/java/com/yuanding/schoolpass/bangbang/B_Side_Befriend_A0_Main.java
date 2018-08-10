package com.yuanding.schoolpass.bangbang;

import java.util.ArrayList;
import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.SpannableStringBuilder;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import com.yuanding.schoolpass.A_0_App;
import com.yuanding.schoolpass.A_Main_My_Message_Acy;
import com.yuanding.schoolpass.B_Account_Befriend_Center_Main_0;
import com.yuanding.schoolpass.Pub_WebView_Load_Acy;
import com.yuanding.schoolpass.R;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Befriend_Center_Bean;
import com.yuanding.schoolpass.bean.Befriend_Task_List;
import com.yuanding.schoolpass.service.Api;
import com.yuanding.schoolpass.service.Api.InterBefriend_CenterList;
import com.yuanding.schoolpass.service.Api.InterRollingList;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author qinxiaojie 
 * @version 创建时间：2017年1月13日 身边帮帮首页
 */
public class B_Side_Befriend_A0_Main extends A_0_CpkBaseTitle_Navi {

	private SlidingDrawer slidingDrawer;
	private GridView gridView;
	private RelativeLayout helpOpen, helpClose;
	private List<String> helps;
	private String token = A_0_App.USER_TOKEN;
	public static List<Befriend_Task_List> mTaskList;
	private LinearLayout helpTaskLayoutFragment;
	private TextSwitcher ts_side_info_top_animal;
	private int curInfo = 0;
	private static final int DURING_Rung = 2000;
	private List<String> list = new ArrayList<String>();

	boolean yishenhe = false;
	private String shenhetishi;
	private int[] guideResourceId = new int[3];// 引导页图片资源id
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO 自动生成的方法存根
		super.onCreate(savedInstanceState);
		A_0_App.getInstance().addActivity(this);
		setView(R.layout.activity_befriend_task_for_help);
		
		/*******引导开始*******/
		guideResourceId[0] = R.drawable.bfriend_yindao_01;
		guideResourceId[1] = R.drawable.bfriend_yindao_02;
		guideResourceId[2] = R.drawable.bfriend_yindao_03;
		if(GuidePageManager.hasNotShowed(this, B_Side_Befriend_A0_Main.class.getSimpleName())){
		    new GuidePage.Builder(this)
		            .setLayoutId(R.layout.view_home_guide_page)
		            .setKnowViewId(R.id.guide_image)
		            .setImageIdList(guideResourceId)
		            .setPageTag(B_Side_Befriend_A0_Main.class.getSimpleName())
		            .builder()
		            .apply();
		}
		/*******引导结束*******/
		if (A_0_App.USER_STATUS.equals("2")) {
			yishenhe = true;
		}else {
			switch (A_0_App.USER_STATUS) {
			case "0"://审核中
				shenhetishi = "您的认证资料正在审核中，无法使用该功能，请耐心等待。";
				break;
			case "5"://审核失败
				shenhetishi = "此功能需要您提交个人认证资料。";
				break;
			case AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO://未认证
				shenhetishi = "此功能需要您提交个人认证资料。";
				break;

			}
		}

		// setTitleText(R.string.str_help_task_home);
		setTitleText(getResources().getString(R.string.str_help_task_help));
		showTitleBt(ZUI_RIGHT_BUTTON, true);
		showTitleBt(PIAN_RIGHT_BUTTON, true);
		
		setZuiRightBtn(R.drawable.ic_defalut_person_center);
		setPianRightBtn(R.drawable.navigationbar_help);
		//动态
		ts_side_info_top_animal = (TextSwitcher) findViewById(R.id.ts_side_info_top_animal);
		ts_side_info_top_animal.setFactory(new ViewSwitcher.ViewFactory() {
			@Override
			public View makeView() {
				final TextView tv = new TextView(B_Side_Befriend_A0_Main.this);
				tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
				tv.setTextColor(getResources().getColor(R.color.title_no_focus_login));
				tv.setLineSpacing(18, 1.2f);
				return tv;
			}
		});
		
		initView();
		initData();
		getRolling();

		getCouponUrl();
	}
	

	// 校园头条滚动
	private void initSideInfo() {
		if (list == null || list.size() <= 0)
			return;
		if (locationHand != null && thread != null) {
			locationHand.removeCallbacks(thread);
		}
		locationHand.postDelayed(thread, 0);
	}

	Handler locationHand = new Handler();
	Runnable thread = new Runnable() {
		public void run() {
			if (curInfo >= list.size()) {
				curInfo = 0;
			}
			if (curInfo < list.size() - 1) {
				String text_str = list.get(curInfo);

				if (null != text_str && text_str.length() > 0) {
					SpannableStringBuilder builder = PubMehods.splitStrWhereStr(B_Side_Befriend_A0_Main.this,
							R.color.main_color, text_str, "\\{#", "#\\}");
					if (builder != null) {
						ts_side_info_top_animal.setText(builder);
					}
				}
			}
			locationHand.postDelayed(this, DURING_Rung);
			curInfo += 1;
		}
	};

	private void initView() {
		// TODO 自动生成的方法存根
		slidingDrawer = (SlidingDrawer) findViewById(R.id.for_help_sd);

		slidingDrawer.animateOpen();
		slidingDrawer.animateClose();
		gridView = (GridView) findViewById(R.id.for_help_type);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		helpOpen = (RelativeLayout) findViewById(R.id.for_help_open);
		helpClose = (RelativeLayout) findViewById(R.id.for_help_close);

		helpTaskLayoutFragment = (LinearLayout) findViewById(R.id.for_help_task_list_layout);

		B_Side_Befriend_A0_List_Fragment fragment = new B_Side_Befriend_A0_List_Fragment();
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.for_help_task_list_fragment, fragment);
		transaction.commit();
		
	}

	private void initData() {
		// TODO 自动生成的方法存根
		// 任务列表菜单弹出控件 开 关
		slidingDrawer.setOnDrawerOpenListener(new SlidingDrawer.OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				// TODO 自动生成的方法存根
				helpOpen.setVisibility(View.GONE);
				helpClose.setVisibility(View.VISIBLE);

			}
		});
		slidingDrawer.setOnDrawerCloseListener(new SlidingDrawer.OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				// TODO 自动生成的方法存根
				helpOpen.setVisibility(View.VISIBLE);
				helpClose.setVisibility(View.GONE);
			}
		});
		helps = new ArrayList<>();
		helps.add("取物品");
		helps.add("送物品");
		helps.add("教技能");
		helps.add("修物品");
		helps.add("帮其他");
		// 创建任务菜单
		StartCreateTask();
		
		 if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
	            startListtenerRongYun();// 监听融云网络变化
	        }
	}

	// 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) { // 继承了Activity的onTouchEvent方法，直接监听点击事件
		if (event.getAction() == MotionEvent.ACTION_DOWN) { // 当手指按下的时候
			x1 = event.getX();
			y1 = event.getY();
		}
		if (event.getAction() == MotionEvent.ACTION_UP) { // 当手指离开的时候
			x2 = event.getX();
			y2 = event.getY();
			if (y1 - y2 > 50) {
				// Toast.makeText(MainActivity.this, "向上滑",
				// Toast.LENGTH_SHORT).show();
			} else if (y2 - y1 > 50) {
				slidingDrawer.close();
			} else if (x1 - x2 > 50) {
				// Toast.makeText(MainActivity.this, "向左滑",
				// Toast.LENGTH_SHORT).show();
			} else if (x2 - x1 > 50) {
				// Toast.makeText(MainActivity.this, "向右滑",
				// Toast.LENGTH_SHORT).show();
			}
		}
		return super.onTouchEvent(event);

	}

	/*****
	 * 创建任务菜单
	 */
	private void StartCreateTask() {
		// TODO Auto-generated method stub
		// 任务创建开始菜单列表
		gridView.setAdapter(new HelpsAdapter(this));
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO 自动生成的方法存根
				// slidingDrawer.toggle();
				
				if (yishenhe) {
					Intent intent = null;
					switch (helps.get(arg2)) {
					case "取物品":
						intent = new Intent(B_Side_Befriend_A0_Main.this, B_Side_Befriend_A1_Take_Or_Send.class);
						intent.putExtra("type", 1);
						break;
					case "送物品":
						intent = new Intent(B_Side_Befriend_A0_Main.this, B_Side_Befriend_A1_Take_Or_Send.class);
						intent.putExtra("type", 2);
						break;
					case "教技能":
						intent = new Intent(B_Side_Befriend_A0_Main.this, B_Side_Befriend_A1_Teach_Repair_Other.class);
						intent.putExtra("type", 3);
						break;
					case "修物品":
						intent = new Intent(B_Side_Befriend_A0_Main.this, B_Side_Befriend_A1_Teach_Repair_Other.class);
						intent.putExtra("type", 4);
						break;
					case "帮其他":
						intent = new Intent(B_Side_Befriend_A0_Main.this, B_Side_Befriend_A1_Teach_Repair_Other.class);
						intent.putExtra("type", 99);
						break;
					}
					if (intent != null) {
						startActivity(intent);

					}
				}else {
					PubMehods.showToastStr(B_Side_Befriend_A0_Main.this, shenhetishi);
				}
				
			}
		});
	}

	@Override
	protected void handleTitleBarEvent(int resId, View v) {
		// TODO Auto-generated method stub
		switch (resId) {
		case BACK_BUTTON:
			finish();
			
//            doStartApplicationWithPackageName("com.yuanding.schoolteacher");  
			break;

		case ZUI_RIGHT_BUTTON:
			if (yishenhe) {
				Intent intent1 = new Intent(B_Side_Befriend_A0_Main.this, B_Account_Befriend_Center_Main_0.class);
				intent1.putExtra("type", "2");
				startActivity(intent1);
			}else {
				PubMehods.showToastStr(B_Side_Befriend_A0_Main.this, shenhetishi);
			}
			
			break;
		case PIAN_RIGHT_BUTTON:
			if (yishenhe) {
				Intent intent = new Intent();
				intent.setClass(B_Side_Befriend_A0_Main.this, Pub_WebView_Load_Acy.class);
				intent.putExtra("title_text", "常见问题");
				intent.putExtra("url_text", AppStrStatic.LINK_USER_BEFRIEND_HELP);
				intent.putExtra("tag_skip", "1");
				 intent.putExtra("tag_show_refresh_btn", "2");
				startActivity(intent);
			}else {
				PubMehods.showToastStr(B_Side_Befriend_A0_Main.this, shenhetishi);
			}
			
			break;
		}

	}

	private void getCouponUrl(){
		A_0_App.getApi().getBefriend_Coupon(B_Side_Befriend_A0_Main.this, A_0_App.USER_TOKEN,"1", new Api.InterBefriend_Coupon() {
			@Override
			public void onSuccess(String coupon_use_url, String coupon_url) {
             A_0_App.USER_COUPON_USE_URL=coupon_use_url;
				A_0_App.BANG_USER_COUPON_URL=coupon_url;
			}
		}, new Inter_Call_Back() {
			@Override
			public void onCancelled() {

			}

			@Override
			public void onFinished() {

			}

			@Override
			public void onFailure(String msg) {

			}
		});
	}

	private void getRolling() {
		A_0_App.getApi().getRollingList(B_Side_Befriend_A0_Main.this, token, new InterRollingList() {

			@Override
			public void onSuccess(String string) {

				if (string != null && string.length() > 3) {
					String[] temp = ((string.replace("[", "")).replace("]", "")).replace("\"", "").split(",");
					for (int i = 0; i < temp.length; i++) {
						list.add(temp[i]);
					}
					initSideInfo();
				}
			}
		}, new Inter_Call_Back() {

			@Override
			public void onFinished() {

			}

			@Override
			public void onFailure(String msg) {

			}

			@Override
			public void onCancelled() {

			}
		});
	}

	/**
	 * 任务创建开始列表适配
	 */
	public class HelpsAdapter extends BaseAdapter {
		Context context;

		public HelpsAdapter(Context context) {
			// TODO 自动生成的构造函数存根
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO 自动生成的方法存根
			return helps != null ? helps.size() : 0;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO 自动生成的方法存根
			return helps.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO 自动生成的方法存根
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			// TODO 自动生成的方法存根
			arg1 = LayoutInflater.from(context).inflate(R.layout.item_help_task_for_help_helps, null);
			TextView control = (TextView) arg1.findViewById(R.id.control_name);
			ImageView controlImage = (ImageView) arg1.findViewById(R.id.control_image);
			control.setText(helps.get(arg0));
			switch (helps.get(arg0)) {
			case "取物品":
				controlImage.setImageResource(R.drawable.bangbang_ico_q);
				control.setText("取物品");
				break;
			case "送物品":
				controlImage.setImageResource(R.drawable.bangbang_ico_s);
				control.setText("送物品");
				break;
			case "教技能":
				controlImage.setImageResource(R.drawable.bangbang_ico_j);
				control.setText("教技能");
				break;
			case "修物品":
				controlImage.setImageResource(R.drawable.bangbang_ico_x);
				control.setText("修物品");
				break;
			case "帮其他":
				controlImage.setImageResource(R.drawable.bangbang_ico_t);
				control.setText("帮其他");
				break;
			}

			return arg1;
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		readData();
		if(getIntent().getExtras() != null){
			int type = getIntent().getIntExtra("type", 99);
			switch (type){
				case 5:
					B_Side_Befriend_A0_List_Fragment fragment = new B_Side_Befriend_A0_List_Fragment();
					FragmentManager fragmentManager = getSupportFragmentManager();
					FragmentTransaction transaction = fragmentManager.beginTransaction();
					transaction.replace(R.id.for_help_task_list_fragment, fragment);
					transaction.commit();
					break;
			default:
				break;
			}
		}

	}
	 private void readData() {
	    	A_0_App.getApi().getBefriend_CenterList(B_Side_Befriend_A0_Main.this, A_0_App.USER_TOKEN,new InterBefriend_CenterList() {
				
				@Override
				public void onSuccess(Befriend_Center_Bean befriend_Center_Bean,
						long servertime) {
					if (isFinishing())
						return; 
					if (befriend_Center_Bean.getTotalUnreadMessages().equals("0")) {
						setZuiRightBtn(R.drawable.ic_defalut_person_center);
					}else {
						setZuiRightBtn(R.drawable.ic_defalut_person_center_noread);
					}
					
					
				}
			}, new Inter_Call_Back() {
				
				@Override
				public void onFinished() {
					
				}
				
				@Override
				public void onFailure(String msg) {
					if (isFinishing())
	                    return;
	                PubMehods.showToastStr(B_Side_Befriend_A0_Main.this, msg);
				}
				
				@Override
				public void onCancelled() {
					
				}
			});
	    }
	 
	 /**
	     * 设置连接状态变化的监听器.
	     */
	    public void startListtenerRongYun() {
	        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
	    }

	    private class MyConnectionStatusListener implements
	            RongIMClient.ConnectionStatusListener {
	        @Override
	        public void onChanged(ConnectionStatus connectionStatus) {
	            switch (connectionStatus) {
	            case CONNECTED:// 连接成功。
	                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接成功");
	                break;
	            case DISCONNECTED:// 断开连接。
	                A_Main_My_Message_Acy
	                        .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
	                //A_0_App.getInstance().showExitDialog(B_Mess_Attdence_Main_0_Acy.this,getResources().getString(R.string.token_timeout));
	                break;
	            case CONNECTING:// 连接中。
	                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接中");
	                break;
	            case NETWORK_UNAVAILABLE:// 网络不可用。
	                A_Main_My_Message_Acy.logE("教师——connectRoogIm()，融云连接网络不可用");
	                break;
	            case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
	                A_Main_My_Message_Acy
	                        .logE("教师——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
	                class LooperThread extends Thread {
	                    public void run() {
	                        Looper.prepare();
	                        A_0_App.getInstance().showExitDialog(B_Side_Befriend_A0_Main.this,
									AppStrStatic.kicked_offline());
	                        Looper.loop();
	                    }
	                }
	                LooperThread looper = new LooperThread();
	                looper.start();
	                break;
	            }
	        }
	    }
	    private void doStartApplicationWithPackageName(String packagename) {  
	    	  
	        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等  
	        PackageInfo packageinfo = null;  
	        try {  
	            packageinfo = getPackageManager().getPackageInfo(packagename, 0);  
	        } catch (NameNotFoundException e) {  
	            e.printStackTrace();  
	        }  
	        if (packageinfo == null) {  
	            return;  
	        }  
	      
	        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent  
	        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);  
	        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);  
	        resolveIntent.setPackage(packageinfo.packageName);  
	      
	        // 通过getPackageManager()的queryIntentActivities方法遍历  
	        List<ResolveInfo> resolveinfoList = getPackageManager()  
	                .queryIntentActivities(resolveIntent, 0);  
	      
	        ResolveInfo resolveinfo = resolveinfoList.iterator().next();  
	        if (resolveinfo != null) {  
	            // packagename = 参数packname  
	            String packageName = resolveinfo.activityInfo.packageName;  
	            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]  
	            String className = resolveinfo.activityInfo.name;  
	            // LAUNCHER Intent  
	            Intent intent = new Intent(Intent.ACTION_MAIN);  
	            intent.addCategory(Intent.CATEGORY_LAUNCHER);  
	      
	            // 设置ComponentName参数1:packagename参数2:MainActivity路径  
	            ComponentName cn = new ComponentName(packageName, className);  
	      
	            intent.setComponent(cn);  
	            startActivity(intent);  
	        }  
	    }  
}
