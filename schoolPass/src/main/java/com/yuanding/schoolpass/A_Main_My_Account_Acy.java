package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_A0_Main;
import com.yuanding.schoolpass.bean.Befriend_Center_Bean;
import com.yuanding.schoolpass.bean.Cpk_Student_Info;
import com.yuanding.schoolpass.service.Api;
import com.yuanding.schoolpass.service.Api.InterBefriend_CenterList;
import com.yuanding.schoolpass.service.Api.InterStudentInfo;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.CircleImageView;


/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月2日 下午8:29:57 类说明
 */
public class A_Main_My_Account_Acy extends Activity {

	
	private RelativeLayout rel_myaccount_no;
	private LinearLayout liner_myaccount_class,liner_myaccount_collect,liner_myaccount_set,lienr_wanshan_message,for_help_center_to;
	private TextView tv_account_name,tv_account_no,account_renzheng_state;
	private TextView account_phone_no,account_my_banji;
	private CircleImageView iv_account_por;
	private static A_Main_My_Account_Acy instance;
	
	protected ImageLoader imageLoader;
	private DisplayImageOptions options;
	//private BitmapUtils bitmapUtils;
	private boolean firstLoad = false;//第一次进入
	private ACache maACache;
	private JSONObject jsonObject;
	private Cpk_Student_Info mstudent;
	private LinearLayout liner_myaccount_bangdou,ll_myaccount_renzheng,liner_myaccount_invite,for_help_center_coupon;
	private TextView tv_my_bangdou;
	private ImageView iv_rengzheng;
	private ImageView iv_sysnotice, iv_zui_right_red_dian,iv_zui_right_red_dian_y;
    private String bang_url="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_account);
		A_0_App.getInstance().addActivity(this);
//		setTitleText(R.string.str_my_account_title);
		instance = this;
		firstLoad = true;
		//rel_myaccount_rezheng = (RelativeLayout) findViewById(R.id.rel_myaccount_rezheng);
		iv_rengzheng=(ImageView) findViewById(R.id.iv_myaccount_rezheng);
		rel_myaccount_no = (RelativeLayout) findViewById(R.id.rel_myaccount_no);
		
		for_help_center_to = (LinearLayout) findViewById(R.id.for_help_center_to);
		lienr_wanshan_message = (LinearLayout) findViewById(R.id.lienr_wanshan_message);
		liner_myaccount_class = (LinearLayout) findViewById(R.id.liner_myaccount_class);
		liner_myaccount_collect = (LinearLayout) findViewById(R.id.liner_myaccount_collect);
		liner_myaccount_set = (LinearLayout) findViewById(R.id.liner_myaccount_set);
	    liner_myaccount_bangdou=(LinearLayout) findViewById(R.id.liner_myaccount_bangdou);
	    ll_myaccount_renzheng=(LinearLayout) findViewById(R.id.ll_myaccount_rezheng);
        for_help_center_coupon=(LinearLayout) findViewById(R.id.for_help_center_coupon);
	    liner_myaccount_invite=(LinearLayout) findViewById(R.id.liner_myaccount_invite);
	    iv_account_por = (CircleImageView)findViewById(R.id.iv_account_por_tag);
		tv_account_name = (TextView)findViewById(R.id.tv_account_name);
		tv_account_no = (TextView)findViewById(R.id.tv_account_no);
		account_renzheng_state = (TextView)findViewById(R.id.account_renzheng_state);
		account_phone_no = (TextView)findViewById(R.id.account_phone_no);
		account_my_banji = (TextView)findViewById(R.id.account_my_banji);
	    tv_my_bangdou = (TextView)findViewById(R.id.account_my_bangdou);
	    iv_sysnotice = (ImageView)findViewById(R.id.iv_sysnotice);
	    iv_zui_right_red_dian = (ImageView) findViewById(R.id.iv_zui_right_red_dian);
        iv_zui_right_red_dian_y= (ImageView) findViewById(R.id.iv_zui_right_red_dian_y);
	    iv_zui_right_red_dian.setVisibility(View.GONE);
	    for_help_center_to.setOnClickListener(onclick);
		rel_myaccount_no.setOnClickListener(onclick);
		liner_myaccount_class.setOnClickListener(onclick);
		liner_myaccount_collect.setOnClickListener(onclick);
		liner_myaccount_set.setOnClickListener(onclick);
        for_help_center_coupon.setOnClickListener(onclick);
		//lienr_wanshan_message.setOnClickListener(onclick);
	    liner_myaccount_bangdou.setOnClickListener(onclick);
		ll_myaccount_renzheng.setOnClickListener(onclick);
		iv_account_por.setOnClickListener(onclick);
		liner_myaccount_invite.setOnClickListener(onclick);
		iv_sysnotice.setOnClickListener(onclick);
		imageLoader = ImageLoader.getInstance();
	    options = A_0_App.getInstance().getOptions(R.drawable.i_default_por_120,
	                R.drawable.i_default_por_120,
	                R.drawable.i_default_por_120);
//		bitmapUtils=A_0_App.getBitmapUtils(this, R.drawable.i_person_img, R.drawable.i_person_img);
//		imageLoader.displayImage("http://ydgxt.oss-cn-beijing.aliyuncs.com/photos/566ab39fd727e.jpg",iv_account_por,options);
	    mstudent = new Cpk_Student_Info();
		readCache();

        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
	}
	
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_my_account+A_0_App.USER_UNIQID);
		if (jsonObject!= null) {//说明有缓存
            mstudent = JSON.parseObject(jsonObject + "", Cpk_Student_Info.class);
			showInfo(mstudent);
		}
		updateInfoToRongYun();
	}

    OnClickListener onclick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			switch (v.getId()) {
			case R.id.for_help_center_to:
                A_0_App.BANG_USER_COUPON_URL=mstudent.getCoupon_url();
                if(A_0_App.USER_STATUS.equals("2")){
                    if (bang_url!=""&&bang_url!=null){
                    intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Befriend.class);//B_Account_Help_Center_Main_0..A_3_5_Change_No_Ac
                    intent.putExtra("title_text", "帮帮中心");//1 表示从个人中心进入
                    intent.putExtra("url", bang_url);//1 表示从个人中心进入
                    startActivity(intent);}
                    else{
                        getCouponUrl("1");
                    }
                }else if(A_0_App.USER_STATUS.equals("0")){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                }else{
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_need_to_improve_the_information);
                }
				break;
			case R.id.iv_account_por_tag:
				//完善信息
				if (A_0_App.getInstance().getStudentInfo() == null) {
					PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.error_title_net_error);
					return;
				}
				A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,false);
				break;
			case R.id.ll_myaccount_rezheng:
				//认证
				intent.setClass(A_Main_My_Account_Acy.this,B_Account_Certivication_Temp_Acy.class);
				startActivity(intent);
				break;
			case R.id.rel_myaccount_no:
				//手机号
				intent.setClass(A_Main_My_Account_Acy.this, A_3_5_Change_No_Acy.class);//B_Account_Help_Center_Main_0..A_3_5_Change_No_Acy
				startActivity(intent);
				break;
			case R.id.liner_myaccount_class:
				//我的班级
			    if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                    return;
                }
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
                
                if (A_0_App.USER_STATUS.equals("2")) {
                    if (A_0_App.USER_QUNIQID != null && A_0_App.USER_QUNIQID.length() > 0) {
                        intent.setClass(A_Main_My_Account_Acy.this, B_Mess_Group_Info.class);
                        intent.putExtra("uniqid", A_0_App.USER_QUNIQID);
                        startActivity(intent);
                    }else{
                        PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.str_title_no_group);
                    }
                }
				break;
			case R.id.liner_myaccount_collect:
				//我的收藏
				intent.setClass(A_Main_My_Account_Acy.this, B_Pub_Side_NoDo_Action.class);
				intent.putExtra("title", "我的收藏");
				intent.putExtra("side_enter_type",3);
				startActivity(intent);
				break;
			case R.id.liner_myaccount_set:
				//我的设置
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_My_Set_Acy.class);
				startActivity(intent);
				break;
            case R.id.liner_myaccount_bangdou:
                //我的邦豆
                if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                    return;
                }
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
                String url = A_0_App.USER_BANGDOU_URL;
                if(url == null || url.equals("")){
                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, "数据请求失败，请重试");
                    return;
                }
                logD(url+"=邦豆");
                intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Acy.class);
                intent.putExtra("title_text", "我的邦豆");
                intent.putExtra("url_text", url);
                intent.putExtra("tag_skip", "1");
                intent.putExtra("tag_show_refresh_btn", "1");
                startActivity(intent);
                
                break;
            case R.id.liner_myaccount_invite:
				//邀请人
                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                    A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                    return;
                }
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_Invite_Main.class);
				startActivity(intent);
				break;
                case R.id.for_help_center_coupon:

                    //优惠券
                    if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
                        PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
                        return;
                    }
                    if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
                            ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
                        A_0_App.getInstance().enter_Perfect_information(A_Main_My_Account_Acy.this,true);
                        return;
                    }
                    String url_c = mstudent.getCoupon_url();
                           // AppStrStatic.USER_COUPON_URL;
                    if(url_c == null || url_c.equals("")){
                        PubMehods.showToastStr(A_Main_My_Account_Acy.this, "数据请求失败，请重试");
                        return;
                    }
                    logD(url_c+"=邦豆");
                    intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Coupon.class);
                    intent.putExtra("title_text", "优惠券");
                    intent.putExtra("url_text", url_c);
                    intent.putExtra("tag_skip", "0");
                    intent.putExtra("tag_show_refresh_btn", "1");
                    intent.putExtra("enter", "2");//身边进入
                    startActivity(intent);
                    break;
            case R.id.iv_sysnotice:
//                if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
//                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)
//                        ||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
//                    PubMehods.showToastStr(A_Main_My_Account_Acy.this, R.string.str_no_certified_open);
//                    return;
//                }
				intent.setClass(A_Main_My_Account_Acy.this, B_Account_SystemNotice_Main.class);
				startActivity(intent);
				break;
			default:
				break;
			}

		}
	};
	
    private void showInfo(Cpk_Student_Info student) {
        mstudent = student;
        if (A_0_App.USER_UNIQID.equals(student.getUniqid())) {
            A_0_App.getInstance().updateUserLoginInfo(student.getStudent_status(),student.getUniqid(),student.getPhone(),
                    student.getName(), student.getPhoto_url());
        }
        A_0_App.getInstance().setStudentInfo(student);
        
        String uri = student.getPhoto_url();
        if(iv_account_por.getTag() == null){
            PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
            iv_account_por.setTag(uri);
        }else{
            if(!iv_account_por.getTag().equals(uri)){
                PubMehods.loadServicePic(imageLoader,uri,iv_account_por, options);
                iv_account_por.setTag(uri);
            }
        }
      //bitmapUtils.display(iv_account_por, student.getPhoto_url());
        tv_account_name.setText(student.getName());
        tv_account_no.setText("学号：" +  student.getStudent_number());
        // 0：审核中  2：已认证 
        String auther = student.getStudent_status();
        if (auther.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)) {
            tv_account_name.setText("访客");
            tv_account_no.setText("");
        } else {
            tv_account_name.setText(student.getName());
            tv_account_no.setText("学号：" + student.getStudent_number());
        }
        if (auther.equals("0")){
            account_renzheng_state.setText("审核中");
            iv_rengzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }
        else if (auther.equals("2")){
            account_renzheng_state.setText("已认证");
            iv_rengzheng.setBackgroundResource(R.drawable.icon_account_renzheng);
        }
        else if (auther.equals("5")){
            account_renzheng_state.setText("审核失败");
            iv_rengzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }else if (auther.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)){
            account_renzheng_state.setText("未认证");
            iv_rengzheng.setBackgroundResource(R.drawable.icon_account_no_renzheng);
        }

        account_phone_no.setText(student.getPhone());
        account_my_banji.setText(student.getClass_name());
        tv_my_bangdou.setText(student.getBang_total());
        ll_myaccount_renzheng.setVisibility(View.VISIBLE);
        if (mstudent.getHeo_display()!=null) {
			if (mstudent.getHeo_display().equals("1")) {
				for_help_center_to.setVisibility(View.VISIBLE);
			}else{
				for_help_center_to.setVisibility(View.GONE);
			}
		}
        if (mstudent.getCoupon_dot().equals("0")) {
           iv_zui_right_red_dian_y.setVisibility(View.GONE);
       }else if(mstudent.getCoupon_dot().equals("1")){
           iv_zui_right_red_dian_y.setVisibility(View.VISIBLE);
        }
    }
	
     private void readUserInfo(final boolean firstLoad) {
		A_0_App.getApi().getStudentInfo(A_Main_My_Account_Acy.this, A_0_App.USER_UNIQID,
				A_0_App.USER_TOKEN, new InterStudentInfo() {
					@Override
					public void onSuccess(Cpk_Student_Info student) {
		                if (isFinishing())
		                    return;
		                showInfo(student);
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
//                        A_0_App.getInstance().CancelProgreDialog(A_Main_My_Account_Acy.this);
                        if (msg.equals(AppStrStatic.TAG_USER_IS_DELETE)) {
                            A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this,getResources().getString(R.string.str_user_is_delete));
                            PubMehods.showToastStr(A_Main_My_Account_Acy.this, "该用户已删除");
                        } else {
                            if (jsonObject == null) {
                                ll_myaccount_renzheng.setVisibility(View.GONE);
                            } else {
                                ll_myaccount_renzheng.setVisibility(View.VISIBLE);
                            }
                            if (firstLoad) {
                                 PubMehods.showToastStr(A_Main_My_Account_Acy.this,R.string.error_title_net_error);
                            }
                           
                        }
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
	}
	
	public static A_Main_My_Account_Acy getInstace(){
    	 return instance;
     }
     
	private void updateInfoToRongYun(){
	    UpdatePersionInfo updateTextTask = new UpdatePersionInfo(this);
        updateTextTask.execute();
    }

    class UpdatePersionInfo extends AsyncTask<Void,Integer,Integer>{
        private Context context;
        UpdatePersionInfo(Context context) {
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
        	readUserInfo(firstLoad);
            readData();
            getCouponUrl("0");
            return null;
        }

        /**
         * 运行在ui线程中，在doInBackground()执行完毕后执行
         */
        @Override
        protected void onPostExecute(Integer integer) {

        }

        /**
         * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
        	
        }
    }
   
    private void readData() {
    	A_0_App.getApi().getBefriend_CenterList(A_Main_My_Account_Acy.this, A_0_App.USER_TOKEN,new InterBefriend_CenterList() {
			
			@Override
			public void onSuccess(Befriend_Center_Bean befriend_Center_Bean,
					long servertime) {
				if (isFinishing())
					return; 
				if (befriend_Center_Bean.getTotalUnreadMessages().equals("0")) {
					iv_zui_right_red_dian.setVisibility(View.GONE);
				}else {
					 iv_zui_right_red_dian.setVisibility(View.VISIBLE);
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
//                PubMehods.showToastStr(A_Main_My_Account_Acy.this, msg);
			}
			
			@Override
			public void onCancelled() {
				
			}
		});
    }
    private void getCouponUrl(final String type) {
        A_0_App.getApi().getBefriend_Coupon(A_Main_My_Account_Acy.this, A_0_App.USER_TOKEN, "2", new Api.InterBefriend_Coupon() {
            @Override
            public void onSuccess(String coupon_use_url, String coupon_url) {
                bang_url = coupon_use_url;
             if (type.equals("1")) {
                 Intent intent=new Intent();
                 intent.setClass(A_Main_My_Account_Acy.this, Pub_WebView_Load_Befriend.class);//B_Account_Help_Center_Main_0..A_3_5_Change_No_Ac
                 intent.putExtra("title_text", "帮帮中心");//1 表示从个人中心进入
                 intent.putExtra("url", bang_url);//1 表示从个人中心进入
                 startActivity(intent);
             }
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
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=学生——connectRoogIm()，融云连接成功");
                    break;
                case DISCONNECTED:// 断开连接。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=学生——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    //A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this,getResources().getString(R.string.token_timeout));
                    break;
                case CONNECTING:// 连接中。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=学生——connectRoogIm()，融云连接中");
                    break;
                case NETWORK_UNAVAILABLE:// 网络不可用。
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=学生——connectRoogIm()，融云连接网络不可用");
                    break;
                case KICKED_OFFLINE_BY_OTHER_CLIENT:// 用户账户在其他设备登录，本机会被踢掉线
                    A_Main_My_Message_Acy.logD("A_Main_My_Account_Acy监听=学生——connectRoogIm()，用户账户在其他设备登录，本机会被踢掉线");
                    class LooperThread extends Thread {
                        public void run() {
                            Looper.prepare();
                            A_0_App.getInstance().showExitDialog(A_Main_My_Account_Acy.this,AppStrStatic.kicked_offline());
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
    protected void onResume() {
        super.onResume();
        if (!firstLoad) {
           readCache();
        } else {
            firstLoad = false;
        }
        
    }
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    }
    
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("A_Main_My_Account_Acy", "A_Main_My_Account_Acy=学生=>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("A_Main_My_Account_Acy", "A_Main_My_Account_Acy=学生=>" + msg);
    }
}
