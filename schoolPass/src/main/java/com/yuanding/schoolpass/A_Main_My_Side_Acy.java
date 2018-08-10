package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.R.color;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_A0_Main;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle;
import com.yuanding.schoolpass.bean.Cpk_Banner_Mode;
import com.yuanding.schoolpass.bean.Cpk_Module_List;
import com.yuanding.schoolpass.bean.Cpk_Top_Info;
import com.yuanding.schoolpass.bean.Cpk_Top_List;
import com.yuanding.schoolpass.service.Api.InterGetInfo;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.DensityUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;
import com.yuanding.schoolpass.view.MyGridView;
import com.yuanding.schoolpass.view.banner.ADInfo;
import com.yuanding.schoolpass.view.banner.CycleViewPager;
import com.yuanding.schoolpass.view.banner.CycleViewPager.ImageCycleViewListener;
import com.yuanding.schoolpass.view.banner.ViewFactory;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月2日 下午8:29:25 类说明
 */
public class A_Main_My_Side_Acy extends A_0_CpkBaseTitle {
   
    private static final int DURING_Rung = 2000;
	private HashMap<Integer, View> lmap = new HashMap<Integer, View>();
	private View mLinerReadDataError,liner_acy_list_whole_view,side_acy_loading;
	private CycleViewPager cycleViewPager; 
	private List<ImageView> views = new ArrayList<ImageView>();
	private List<ADInfo> infos = new ArrayList<ADInfo>();
	private List<Cpk_Banner_Mode> list;
	private List<Cpk_Module_List> module_Lists;
	private List<Cpk_Top_Info> cpk_Top_Infos;
	private Cpk_Top_List cpk_Top_List;
	private ImageView iv_banner_defaule;
	private boolean firstLoad = false;//第一次进入
	private ACache maACache;
	private JSONObject jsonObject;
	
	private MyGridView gridview_side;
	private Mydapter adapter;
	protected ImageOptions bitmapUtils;
	private int screenWidth;
	private WindowManager wm;
	private int NumColumns=4;
	private RelativeLayout rela_side_info;
	private TextSwitcher ts_side_info_top_animal;
	private TextView tv_side_info_top_no_animal;
	private int curInfo=0;

    private static A_Main_My_Side_Acy instance;
    private Map<String, String> map_red=new HashMap<String, String>();
    public static A_Main_My_Side_Acy getInstance() {
        return instance;
    }
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setView(R.layout.activity_main_side);
		A_0_App.getInstance().addActivity(this);
        instance = this;
		setTitleText(R.string.str_my_side_title);
		rl_title_base.setBackgroundColor(getResources().getColor(color.white));
		titleText.setTextColor(getResources().getColor(color.black_dan));
		rela_side_info=(RelativeLayout)findViewById(R.id.rela_side_info);
		
		firstLoad = true;
		list = new ArrayList<Cpk_Banner_Mode>();
		module_Lists=new ArrayList<Cpk_Module_List>();
		cpk_Top_List=new Cpk_Top_List();
		cpk_Top_Infos=new ArrayList<Cpk_Top_Info>();
		cycleViewPager = (CycleViewPager) getFragmentManager().findFragmentById(R.id.fragment_cycle_viewpager_content);
		

		wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		screenWidth = wm.getDefaultDisplay().getWidth();
		gridview_side=(MyGridView) findViewById(R.id.gridview_side);
		bitmapUtils=A_0_App.getBitmapUtils(A_Main_My_Side_Acy.this,R.drawable.icon_side_nothing,R.drawable.icon_side_nothing,true);
		
		liner_acy_list_whole_view = findViewById(R.id.liner_side_whole_view);
		mLinerReadDataError = findViewById(R.id.side_info_load_error);
		side_acy_loading=findViewById(R.id.side_info_loading);
	
		home_load_loading = (LinearLayout) side_acy_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		//drawable.start(); 
		
		iv_banner_defaule = (ImageView)findViewById(R.id.iv_banner_defaule);
		ts_side_info_top_animal = (TextSwitcher) findViewById(R.id.ts_side_info_top_animal);
		tv_side_info_top_no_animal = (TextView) findViewById(R.id.tv_side_info_top_no_animal);
		adapter=new Mydapter();
		ts_side_info_top_animal.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                final TextView tv = new TextView(A_Main_My_Side_Acy.this);
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                tv.setTextColor(getResources().getColor(R.color.title_notice_title));
                tv.setLineSpacing(18,1.2f);
                return tv;
            }
        });
		readCache();
		
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
        rela_side_info.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(A_Main_My_Side_Acy.this, B_Side_Info_0_Main_Acy.class);
				intent.putExtra("cate_id",cpk_Top_List.getTop_cate_id() );
				startActivity(intent);
				overridePendingTransition(R.anim.animal_push_left_in_normal, R.anim.animal_push_left_out_normal);
			}
		});
        mLinerReadDataError.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				getGetInfoList(firstLoad);
			}
		});
        map_red=A_0_App.getInstance().readOAuth();
        display_red();

	}

	//接收长连接的广播
    public void refreshMainView(String broadcast) {
    	if (map_red!=null&&map_red.size()>0) {
    		 map_red.clear();
		}
    	
    	 map_red=A_0_App.getInstance().readOAuth();
    	 adapter.notifyDataSetChanged();
    	 display_red();
    }
    
	private void readCache() {//&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected() 暂时去掉会引起加载变白
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_mySide+A_0_App.USER_UNIQID);
       if (jsonObject!= null) {// 说明有缓存
        	showInfo(jsonObject);
			}
       updateInfo();
	}

	private void showInfo(JSONObject jsonObject) {
		
			if (isFinishing())
	            return;
		    int state = jsonObject.optInt("status");
		    List<Cpk_Banner_Mode> mlistContact = new ArrayList<Cpk_Banner_Mode>();
			List<Cpk_Module_List> moduleContact = new ArrayList<Cpk_Module_List>();
			Cpk_Top_List top_List=new Cpk_Top_List();
			if (state == 1) {
				JSONArray jsonArrayItem = jsonObject.optJSONArray("adList");
				JSONArray jsonArrayItem2 = jsonObject.optJSONArray("moduleList");
				JSONObject jsonArrayItem3 = jsonObject.optJSONObject("info");
				mlistContact=JSON.parseArray(jsonArrayItem+"", Cpk_Banner_Mode.class);
				moduleContact=JSON.parseArray(jsonArrayItem2+"", Cpk_Module_List.class);
				top_List=JSON.parseObject(jsonArrayItem3+"", Cpk_Top_List.class);
				
				if (mlistContact == null||moduleContact==null||top_List==null)
					return;
				
				if (mlistContact!=null&&mlistContact.size() > 0) {
		            iv_banner_defaule.setVisibility(View.GONE);
		            list.clear();
		            list.addAll(mlistContact);
		            inItBannerData(list);
		        }else{
		            iv_banner_defaule.setVisibility(View.VISIBLE);
		        }
				
			    module_Lists.clear();
				module_Lists=moduleContact;
				cpk_Top_Infos.clear();
			    cpk_Top_List=top_List;
			    cpk_Top_Infos=JSON.parseArray(cpk_Top_List.getTop_list(), Cpk_Top_Info.class);
			    showScrollLiner();
			   if (module_Lists.size()>8) {
				   NumColumns=5;
			}else{
				NumColumns=4;
			}
				gridview_side.setNumColumns(NumColumns);
			    gridview_side.setAdapter(adapter);
			  
			    display_red();
			}
			
			 
		
		
	}
	
//private void showLoadResult(boolean loading,boolean wholeView,boolean loadFaile) {
//		
//		if (wholeView)
//			liner_acy_list_whole_view.setVisibility(View.VISIBLE);
//		else
//			liner_acy_list_whole_view.setVisibility(View.GONE);
//		
//		if (loadFaile)
//			mLinerReadDataError.setVisibility(View.VISIBLE);
//		else
//			mLinerReadDataError.setVisibility(View.GONE);
//		if(loading)
//			side_acy_loading.setVisibility(View.VISIBLE);
//		else
//			side_acy_loading.setVisibility(View.GONE);
//	}
	
	private void startAcy(Context packageContext, Class<?> cls) {
		Intent intent = new Intent();
		intent.setClass(packageContext, cls);
		startActivity(intent);
		overridePendingTransition(R.anim.animal_push_left_in_normal, R.anim.animal_push_left_out_normal);
	}
	
	/********************************Banner****************************************************/
	
    /**
     * 设置连接状态变化的监听器.
     */
    @SuppressWarnings("static-access")
	public void startListtenerRongYun() {
        RongIM.getInstance().setConnectionStatusListener(new MyConnectionStatusListener());
    }

    private class MyConnectionStatusListener implements RongIMClient.ConnectionStatusListener {
        @Override
        public void onChanged(ConnectionStatus connectionStatus) {

            switch (connectionStatus) {
                case CONNECTED:// 连接成功。
                    A_Main_My_Message_Acy.logD("A_Main_My_Side_Acy模板监听=学生——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logD("A_Main_My_Side_Acy模板监听=学生——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(A_Main_My_Side_Acy.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logD("A_Main_My_Side_Acy模板监听=学生——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logD("A_Main_My_Side_Acy模板监听=学生——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logD("A_Main_My_Side_Acy模板监听=学生——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(A_Main_My_Side_Acy.this,AppStrStatic.kicked_offline());
                            Looper.loop();
                        }
                    }
                    LooperThread looper = new LooperThread();
                    looper.start();
                    break;
            }
        }
    }
    private void getGetInfoList(final boolean firstLoad) {
    	A_0_App.getApi().GetInfoList(A_0_App.USER_TOKEN, new InterGetInfo() {
			
			@Override
            public void onSuccess(List<Cpk_Banner_Mode> mList,
                    List<Cpk_Module_List> moduleContact, Cpk_Top_List top_List) {
                if (isFinishing())
                    return;
                if (mList != null) {
                    if (mList.size() > 0) {
                        iv_banner_defaule.setVisibility(View.GONE);
                        list.clear();
                        list.addAll(mList);
                        inItBannerData(list);
                    } else if (mList.size() == 0) {
                        list.clear();
                        iv_banner_defaule.setVisibility(View.VISIBLE);
                    }else{
                        iv_banner_defaule.setVisibility(View.VISIBLE);
                    }
                }else{
                    iv_banner_defaule.setVisibility(View.VISIBLE);
                }
                if (moduleContact != null) {
                    module_Lists.clear();
                    module_Lists = moduleContact;
                    cpk_Top_Infos.clear();
                }

                cpk_Top_List = top_List;
                if (cpk_Top_List.getTop_list() != null) {
                    cpk_Top_Infos = JSON.parseArray(cpk_Top_List.getTop_list(), Cpk_Top_Info.class);
                }
                showScrollLiner();
                if (module_Lists != null && module_Lists.size() > 8) {
                    NumColumns = 5;
                } else {
                    NumColumns = 4;
                }

                gridview_side.setNumColumns(NumColumns);
                gridview_side.setAdapter(adapter);

                display_red();
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
                if (firstLoad) {
                    PubMehods.showToastStr(A_Main_My_Side_Acy.this, msg);
                }
                
                if(jsonObject==null)
                {
                iv_banner_defaule.setVisibility(View.VISIBLE);
                }
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    private void showScrollLiner() {
        if (cpk_Top_Infos != null) {//总条数大于2开始滚动
            if (cpk_Top_Infos.size() > 2) {
                rela_side_info.setVisibility(View.VISIBLE);
                tv_side_info_top_no_animal.setVisibility(View.GONE);
                ts_side_info_top_animal.setVisibility(View.VISIBLE);
                initSideInfo();
            }else if (cpk_Top_Infos.size() > 1 && cpk_Top_Infos.size() <= 2) {
                rela_side_info.setVisibility(View.VISIBLE);
                tv_side_info_top_no_animal.setVisibility(View.VISIBLE);
                ts_side_info_top_animal.setVisibility(View.GONE);
                tv_side_info_top_no_animal.setText(getSubString(cpk_Top_Infos.get(0).getTitle())
                        + "\n" + getSubString(cpk_Top_Infos.get(1).getTitle()));
            }else if (cpk_Top_Infos.size() > 0 && cpk_Top_Infos.size() <= 1) {
                rela_side_info.setVisibility(View.VISIBLE);
                tv_side_info_top_no_animal.setVisibility(View.VISIBLE);
                ts_side_info_top_animal.setVisibility(View.GONE);
                tv_side_info_top_no_animal.setText(getSubString(cpk_Top_Infos.get(0).getTitle())+"\n");
            }else {
                rela_side_info.setVisibility(View.GONE);
                tv_side_info_top_no_animal.setVisibility(View.GONE);
                ts_side_info_top_animal.setVisibility(View.GONE);
            }
        }
    }
    
    private static final int SCREEN_ONE = 14;
    private static final int SCREEN_TWO = 17;
    private static final int SCREEN_THREE = 17;
    private static final int SCREEN_FOUR = 16;
    
    public String getSubString(String str) {
        if (str == null || "".equals(str))
            return "";
        try {
            String temp_Str = "";
            int strLenth = (int) PubMehods.getLength(str);//实际位数宽度
            if (screenWidth < 710) {
                if (strLenth >= SCREEN_ONE) {
                    temp_Str = dealWithStr(str,str.substring(0, SCREEN_ONE),SCREEN_ONE) + "...";
                    return temp_Str;
                }
            } else if (screenWidth >= 710 && screenWidth <= 900) {
                if (strLenth >= SCREEN_TWO) {
                    temp_Str = dealWithStr(str,str.substring(0, SCREEN_TWO),SCREEN_TWO) + "...";
                    return temp_Str;
                }
            } else if (screenWidth > 900 && screenWidth < 1000) {
                if (strLenth >= SCREEN_THREE) {
                    temp_Str = dealWithStr(str,str.substring(0, SCREEN_THREE),SCREEN_THREE) + "...";
                    return temp_Str;
                }
            } else if (screenWidth >= 1000) {
                if (strLenth >= SCREEN_FOUR) {
                    temp_Str = dealWithStr(str,str.substring(0, SCREEN_FOUR),SCREEN_FOUR) + "...";
                    return temp_Str;
                }
            }
        } catch (Exception e) {
            
        }
        return str;
    }
    
    /**
     * 
     * @Title: dealWithStr
     * @param @param noSubStr  未裁切的字符串
     * @param @param str  待裁切字符串
     * @param subCou       需要裁切字符串的长度
     * @param @return    设定文件
     * @return String    返回类型
     * @throws
     */
     
    private String dealWithStr(String noSubStr,String str,int subCou) {
        int count = 0;
        String tempStr = str;
        String chinese = "[\u4e00-\u9fa5]";
        try {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (!temp.matches(chinese)) {
                    count++;
                }
            }
            if (count > 1) {
                if (count % 2 == 1) {// 奇数
                    count = count - 1;
                }
                int subCount = count / 2;
                return noSubStr.substring(0, subCou + subCount);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return tempStr;
    }
    
    // 校园头条滚动
    private void initSideInfo()
    {
        if (cpk_Top_Infos == null || cpk_Top_Infos.size() <= 0)
            return;
        List<Cpk_Top_Info> temp_1 = new ArrayList<Cpk_Top_Info>();
        List<Cpk_Top_Info> temp_2 = new ArrayList<Cpk_Top_Info>();
        temp_1 = cpk_Top_Infos;
        for (int i = 0; i < temp_1.size(); i++) {
            temp_2.add(temp_1.get(i));
        }
        cpk_Top_Infos.addAll(temp_2);
        if (locationHand != null && thread != null) {
            locationHand.removeCallbacks(thread);
        }
        logD(cpk_Top_Infos.size()+"qq");
        locationHand.postDelayed(thread, 0);
    }
    
    Handler locationHand = new Handler();
    Runnable thread = new Runnable() {
        public void run() {
              if (curInfo >= cpk_Top_Infos.size())
              {
                  curInfo = 0;
              }
              if (curInfo < cpk_Top_Infos.size() - 1) {
                String text_str = getSubString(cpk_Top_Infos.get(curInfo).getTitle()) + "\n"
                        + getSubString(cpk_Top_Infos.get(curInfo + 1).getTitle());
                  ts_side_info_top_animal.setText(text_str);
              }
              locationHand.postDelayed(this, DURING_Rung);
              curInfo += 2;
              logD(cpk_Top_Infos.size()+"ff");
        }
    };

	// 加载Banner数据
	private void inItBannerData(List<Cpk_Banner_Mode> list) {
		infos.clear();
		for(int i = 0; i < list.size(); i ++){
			ADInfo info = new ADInfo();
			info.setUrl(list.get(i).getImage_url());
			info.setContent(list.get(i).getTitle());
			info.setId(list.get(i).getId());
			infos.add(info);
		}
        if (infos.size() <= 0)
            return;
		// 将最后一个ImageView添加进来
		views.clear();
		views.add(ViewFactory.getImageView(this, infos.get(infos.size() - 1).getUrl()));
		for (int i = 0; i < infos.size(); i++) {
			views.add(ViewFactory.getImageView(this, infos.get(i).getUrl()));
		}
		// 将第一个ImageView添加进来
		views.add(ViewFactory.getImageView(this, infos.get(0).getUrl()));
	
		cycleViewPager.setCycle(true);// 设置循环，在调用setData方法前调用
		cycleViewPager.setData(views, infos, mAdCycleViewListener);// 在加载数据前设置是否循环
		cycleViewPager.setWheel(true);//设置轮播
		cycleViewPager.setTime(DURING_Rung);// 设置轮播时
		cycleViewPager.setIndicatorCenter();//设置圆点指示图标组居中显示，默认靠右
		
	}
	
	// 点击事件
	private ImageCycleViewListener mAdCycleViewListener = new ImageCycleViewListener() {
		@Override
		public void onImageClick(ADInfo info, int position, View imageView) {
            if (list.size() == 0)
                return;
            if (position < 1 || position > list.size()) {
                return;
            }
			
			if (cycleViewPager.isCycle()) {
                Intent intent = new Intent();
                switch (Integer.parseInt(list.get(position - 1).getType())) {
                case 1:// 活动
//                    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                        intent.setClass(A_Main_My_Side_Acy.this,B_Side_Acy_list_Detail_Acy.class);
                        intent.putExtra("acy_type", 2);
                        intent.putExtra("acy_detail_id", list.get(position - 1).getId());
                        intent.putExtra("share_url_text",list.get(position - 1).getShare_url());
                        intent.putExtra("share_url_pic", list.get(position-1).getImage_url());//分享的图片
                        startActivity(intent);
//                    }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
//                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
//                    }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
//                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
//                    }else{
//                        PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
//                    }
                    break;
                case 2:// 讲座
                    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                        intent.setClass(A_Main_My_Side_Acy.this,B_Side_Lectures_Detail_Acy.class);
                        intent.putExtra("acy_type", 2);
                        intent.putExtra("lecture_id", list.get(position - 1).getId());
                        startActivity(intent);
                    }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                    }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                    }else{
                        PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                    }
                    break;
                 case 3://URL
                    intent.setClass(A_Main_My_Side_Acy.this,Pub_WebView_Banner_Acy.class);
                    intent.putExtra("url_text", list.get(position-1).getUrl());
                    intent.putExtra("title_text", list.get(position-1).getTitle());
                    intent.putExtra("tag_show_refresh_btn", "1");
                    intent.putExtra("tag_skip", "1");
                    startActivity(intent);
                    break;
                 case 4://图片
                     
                     break;
                default:
                    break;
                }
			}
		}
	};
	private void updateInfo(){
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
    }
	 class MyAsyncTask extends AsyncTask<Void,Integer,Integer>{
	        private Context context;
	        MyAsyncTask(Context context) {
	            this.context = context;
	        }
	        @Override
	        protected void onPreExecute() {
	        }
	        /**
	         * 后台运行的方法，可以运行非UI线程，可以执行耗时的方法
	         */
	        @Override
	        protected Integer doInBackground(Void... params) {
	        	
	        	//getBannerList();
	        	getGetInfoList(firstLoad);
	            return null;
	        }

	        /**
	         * 运行在ui线程中，在doInBackground()执行完毕后执行
	         */
	        @Override
	        protected void onPostExecute(Integer integer) {
//	            logD("上传融云数据完毕");
	        }

	        /**
	         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
	         */
	        @Override
	        protected void onProgressUpdate(Integer... values) {
	        	
	        }
	    }
	
/******************************************Banner********************************************************/
	@Override
	protected void handleTitleBarEvent(int resId) {}
	
	

	
	public class Mydapter extends BaseAdapter {

		@Override
		public int getCount() {
			if (module_Lists != null)
				return module_Lists.size();
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			return arg0;
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@SuppressLint("NewApi")
		@Override
		public View getView(final int posi, View converView, ViewGroup arg2) {

			final ViewHolder holder;
			if (lmap.get(posi) == null) {
				converView = LayoutInflater.from(A_Main_My_Side_Acy.this)
						.inflate(R.layout.item_side_main, null);
				holder = new ViewHolder();
				holder.iv_side_img = (ImageView) converView
						.findViewById(R.id.iv_side_circle_image);
				holder.tv_side_info_read_count = (ImageView) converView
						.findViewById(R.id.tv_side_info_read_count);
				holder.tv_side_name = (TextView) converView
						.findViewById(R.id.tv_side_name);
				lmap.put(posi, converView);
				converView.setTag(holder); 
			} else {
				converView = lmap.get(posi);
				holder = (ViewHolder) converView.getTag();
			}
			LinearLayout.LayoutParams lp_menpiao1 = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			lp_menpiao1.width = (int) ((screenWidth -20)/(NumColumns+2));
			lp_menpiao1.height = (int) ((screenWidth -20)/(NumColumns+2));
			holder.iv_side_img.setLayoutParams(lp_menpiao1);
			
			if (map_red.size()>0&&map_red.get(module_Lists.get(posi).getCode())!=null) {
				if (map_red.get(module_Lists.get(posi).getCode()).equals("1")) {
					holder.tv_side_info_read_count.setVisibility(View.VISIBLE);
				} else {
					holder.tv_side_info_read_count.setVisibility(View.INVISIBLE);
				}
			}
			
			holder.tv_side_info_read_count.setBackgroundResource(R.drawable.icon_message_noread);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int width = 0, top = 0, left = 0;
            if(NumColumns==4){
                width = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 13);
                top = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 2);
                left = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 55);
            }else{
                width = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 8);
                top = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 2);
                left = DensityUtils.dip2px(A_Main_My_Side_Acy.this, 50);
            }
            layoutParams.width = width;
            layoutParams.height = width;
            layoutParams.setMargins(left, top, 0, 0);// 4个参数按顺序分别是左上右下
            holder.tv_side_info_read_count.setLayoutParams(layoutParams);
			final String uri = module_Lists.get(posi).getIcon_url();
			if(holder.iv_side_img.getTag() == null){
			    PubMehods.loadBitmapUtilsPic(bitmapUtils, holder.iv_side_img, uri);
			    
			    holder.iv_side_img.setTag(uri);
			}else{
			    if(!holder.iv_side_img.getTag().equals(uri)){
			        PubMehods.loadBitmapUtilsPic(bitmapUtils,holder.iv_side_img, uri);
			        holder.iv_side_img.setTag(uri);
			    }
			}
			
			holder.tv_side_name.setText(module_Lists.get(posi).getName());
			converView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (module_Lists.get(posi).getApp_type().equals("1")) {
						if (map_red.get(module_Lists.get(posi).getCode())!=null&&map_red.get(module_Lists.get(posi).getCode()).equals("1")) {
							map_red.remove(module_Lists.get(posi).getCode());
							map_red.put(module_Lists.get(posi).getCode(), "0");
							holder.tv_side_info_read_count.setVisibility(View.INVISIBLE);
							ToSayServiceInfo(A_0_App.USER_TOKEN, module_Lists.get(posi).getCode());
						}
    					if (module_Lists.get(posi).getCode().equals("ACHIEVEMENT")) { 
    					    //成绩
                            if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Achievement_Acy.class));
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
    					}else if(module_Lists.get(posi).getCode().equals("COURSE")){
    						//课程表
    					    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
    					        Intent courseIntent = new Intent(A_Main_My_Side_Acy.this,B_Side_Course_Acy.class);
    					        courseIntent.putExtra("acy_type", 11);
    					        courseIntent.putExtra("img_course", uri);
                                startActivity(courseIntent);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
    					}else if(module_Lists.get(posi).getCode().equals("LOST")){
    						//失物招领
    					    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Lost_Found_Main.class));
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
    					}else if(module_Lists.get(posi).getCode().equals("ATTENDANCE")){
    						 //考勤
    					    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
    					        Intent intent = new Intent(A_Main_My_Side_Acy.this, B_Mess_Attdence_Main_0_Acy.class);//B_Mess_Attdence_Main_0_Acy xzkq
    	                        intent.putExtra("acy_type", 2);//正常列表进入
    	                        startActivity(intent);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
    					}else if(module_Lists.get(posi).getCode().equals("ATTENDANCEHELP")){
    					    //协助考勤
    					    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Attence_Main_A0.class));
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
    					}else if (module_Lists.get(posi).getCode().equals("ACTIVITY")) {
                            // 活动
                            startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Acy_list_Scy.class));
                        } else if (module_Lists.get(posi).getCode().equals("LECTURE")) {
                            // 讲座
                            if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Lectures_Acy.class));
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
                        }  else if (module_Lists.get(posi).getCode().equals("HEO")) {
                            // 帮帮
//                            if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Befriend_A0_Main.class));
//                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
//                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
//                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
//                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
//                            }else{
//                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
//                            }
                        }
                        else if (module_Lists.get(posi).getCode().equals("REPAIR")) {
                            // 报修
                            if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_HAVA_CERTIFIED)) {
                                startActivity(new Intent(A_Main_My_Side_Acy.this,B_Side_Repair_My.class));
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)){
                                A_0_App.getInstance().enter_Perfect_information(A_Main_My_Side_Acy.this,true);
                            }else{
                                PubMehods.showToastStr(A_Main_My_Side_Acy.this, R.string.str_no_certified_open);
                            }
                        }
                    } else {
                        if (map_red.get(module_Lists.get(posi).getCode()) != null && map_red.get(module_Lists.get(posi).getCode()).equals("1")) {
                            map_red.remove(module_Lists.get(posi).getCode());
                            map_red.put(module_Lists.get(posi).getCode(), "0");
                            holder.tv_side_info_read_count.setVisibility(View.INVISIBLE);
                            ToSayServiceInfo(A_0_App.USER_TOKEN, module_Lists.get(posi).getCode());
                        }
                        Intent intent = new Intent(A_Main_My_Side_Acy.this,Pub_WebView_Banner_Acy.class);
                        intent.putExtra("title_text", module_Lists.get(posi).getName());// 正常列表进入
                        intent.putExtra("url_text", module_Lists.get(posi).getLink_url());
                        intent.putExtra("tag_show_refresh_btn", "1");
                        intent.putExtra("tag_skip", "1");

        startActivityForResult(intent, 1);
    }
}
});
        return converView;
        }
	}

	class ViewHolder {
		
		ImageView iv_side_img;
		TextView tv_side_name;
		RelativeLayout rel_side_notice;
		ImageView tv_side_info_read_count;
	}
	
	 //告诉用户信息给服务器
    private void ToSayServiceInfo(String token,String type) {
//        JSONObject params = new JSONObject();
//        try {
//            params.put("token", token);
//            params.put("command", "MODULEUPDATE");
//            params.put("module", type);
//            String app_version = PubMehods.getVerName(A_Main_My_Side_Acy.this);
//            params.put("app_version", app_version);
//            params.put("type", "2");//1是教师，2是学生
//            
//            String sign = PubMehods.getMD5("app_version" + app_version + "command" + "MODULEUPDATE"
//                    + "module" + type + "token" + token +"type" + "2" + AppStrStatic.ZCODE
//                    + AppStrStatic.ZCODE_VALUE);
//            params.put(AppStrStatic.SIGN, sign);
//            
//            String msg = params.toString();
//            boolean isSend = A_Main_My_Message_Acy.iBackService.sendMessage(msg);
//            Log.e("BackService", isSend ? "发送成功" : "发送失败");
//            if(!isSend){
//                boolean isSend_Once = A_Main_My_Message_Acy.iBackService.sendMessage(msg);
//                Log.e("BackService", isSend_Once ? "再次发送成功" : "再次发送失败");
//            }else{
//            	A_0_App.getInstance().saveOAuth(map_red);
//            }
//            
////            PubMehods.showToastStr(A_Main_My_Message_Acy.this, isSend ? "发送成功" : "发送失败");
//        } catch (Exception e) {
//            Log.e("BackService", "ToSayServiceInfo Exception 发送失败");
////            PubMehods.showToastStr(A_Main_My_Message_Acy.this, "发送失败");
//        }
    }
    
    private void display_red(){
    	if (map_red==null||map_red.size()==0) 
			return;
		
    	 String temp="";
		  for(Entry<String,String> entry : map_red.entrySet()){  
               String strkey1 = entry.getKey();  
               String strval1 = entry.getValue();  
               temp=strval1+temp;
           }  
				
			if (temp.contains("1")) {
            A_Main_Acy.getInstance().showSideNoReadTag(true);
      }else{
          A_Main_Acy.getInstance().showSideNoReadTag(false);
      }
    }
    
	@Override
	protected void onResume() {
		if (!firstLoad) {
			// 第一次进入页面不刷新
			updateInfo();
			display_red();
		} else {
			firstLoad = false;
		}
		
        if (!A_0_App.getInstance().getSocket_login()) {
            if (A_Main_My_Message_Acy.getInstance() != null
                    && A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {
                A_Main_My_Message_Acy.getInstance().ToSayServiceLogin();
            }
        }
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {

	    if (locationHand != null && thread != null) {
            locationHand.removeCallbacks(thread);
        }
		if (views != null) {
			views.clear();
			views = null;
		}

		if (infos != null) {
			infos.clear();
			infos = null;
		}
		if (list != null) {
			list.clear();
			list = null;
		}
		super.onDestroy();
	}
	
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("A_Main_My_Side_Acy","A_Main_My_Side_Acy==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("A_Main_My_Side_Acy","A_Main_My_Side_Acy==>" + msg);
    }
	
}
