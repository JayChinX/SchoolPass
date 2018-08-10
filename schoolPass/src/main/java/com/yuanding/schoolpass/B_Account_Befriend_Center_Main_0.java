package com.yuanding.schoolpass;

import java.text.DecimalFormat;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.bangbang.B_Side_Befriend_A0_Main;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Befriend_Center_Bean;
import com.yuanding.schoolpass.service.Api.InterBefriend_CenterList;
import com.yuanding.schoolpass.service.Api.InterMessageSwitch;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.toggle.ToggleButton;
import com.yuanding.schoolpass.view.toggle.ToggleButton.OnToggleChanged;
 /**
 * 学生个人帮帮中心主界面
 */
public class B_Account_Befriend_Center_Main_0 extends A_0_CpkBaseTitle_Navi{
   private LinearLayout liner_unpay,liner_unget,liner_service,liner_unfinish,liner_un_gather,liner_finish_gather,liner_balance;
   private RelativeLayout rela_cash,rela_common_problem,rela_coupon;
   private ToggleButton tb_set_message_help;
   private TextView tv_unpay_count,tv_unget,tv_service,tv_unfinish_count,tv_un_gather,tv_finish_gather,tv_totalAmount,tv_total_coupon;
   private Befriend_Center_Bean center_Bean;
   private boolean firsetEnterLoader = false;
   private ACache maACache;
   private JSONObject jsonObject;
   private String type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.activity_acc_help_center_main_0);
        setTitleText("帮帮中心");
        showTitleBt(ZUI_RIGHT_TEXT, true);
        setZuiYouText("帮帮首页");
        
        if (getIntent().getStringExtra("type")!=null) {
        	type=getIntent().getStringExtra("type");
		}
        firsetEnterLoader = true;
        liner_unpay = (LinearLayout)findViewById(R.id.liner_unpay);
        liner_unget = (LinearLayout)findViewById(R.id.liner_unget);
        liner_service = (LinearLayout)findViewById(R.id.liner_service);
        liner_unfinish = (LinearLayout)findViewById(R.id.liner_unfinish);
        liner_un_gather = (LinearLayout)findViewById(R.id.liner_un_gather);
        liner_finish_gather = (LinearLayout)findViewById(R.id.liner_finish_gather);
        liner_balance=(LinearLayout)findViewById(R.id.liner_balance);
        
        rela_cash = (RelativeLayout)findViewById(R.id.rela_cash);
		rela_coupon=(RelativeLayout)findViewById(R.id.rela_coupon);
        rela_common_problem = (RelativeLayout)findViewById(R.id.rela_common_problem);
        
        tv_unpay_count = (TextView)findViewById(R.id.tv_unpay_count);
        tv_unget = (TextView)findViewById(R.id.tv_unget);
        tv_service = (TextView)findViewById(R.id.tv_service);
        tv_unfinish_count = (TextView)findViewById(R.id.tv_unfinish_count);
        tv_un_gather = (TextView)findViewById(R.id.tv_un_gather);
        tv_finish_gather = (TextView)findViewById(R.id.tv_finish_gather);
        tv_totalAmount = (TextView)findViewById(R.id.tv_totalAmount);
		tv_total_coupon = (TextView)findViewById(R.id.tv_total_coupon);
        tb_set_message_help = (ToggleButton)findViewById(R.id.tb_set_message_help);
        
        liner_unpay.setOnClickListener(onClick);
        liner_unget.setOnClickListener(onClick);
        liner_service.setOnClickListener(onClick);
        liner_unfinish.setOnClickListener(onClick);
        liner_un_gather.setOnClickListener(onClick);
        liner_finish_gather.setOnClickListener(onClick);
        rela_cash.setOnClickListener(onClick);
        rela_common_problem.setOnClickListener(onClick);
        liner_balance.setOnClickListener(onClick);
		rela_coupon.setOnClickListener(onClick);
        center_Bean=new Befriend_Center_Bean();
        		
        tb_set_message_help.setOnToggleChanged(new OnToggleChanged() {
			
			@Override
			public void onToggle(boolean on) {
				if (on==true) {
					SwitchMessage("1");
				} else if(on==false) {
					SwitchMessage("0");
				}
				
			}
		});
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    	readCache();
    }
    
	private void readCache() {
		maACache = ACache.get(this);
		jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_acc_befriend_center_main+A_0_App.USER_UNIQID);
		if (jsonObject!= null&& !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
		   showInfo(jsonObject);
		}else{
		   updateInfo();
		}
	}

	private void showInfo(JSONObject jsonObject) {
		if (isFinishing())
			return;
		JSONObject jsonObject2;
		try {
			jsonObject2 = new JSONObject(jsonObject+"");
			int state = jsonObject2.optInt("status");
			String message = jsonObject2.optString("msg");
			long servertime=jsonObject2.getLong("time");
			if (state == 1) {
				JSONObject dd = jsonObject2.getJSONObject("list");
				center_Bean=JSON.parseObject(dd+"", Befriend_Center_Bean.class);
			}
			loaddata();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	private void updateInfo() {
		MyAsyncTask updateLectureInfo = new MyAsyncTask(this);
		updateLectureInfo.execute();
	}

	class MyAsyncTask extends AsyncTask<Void, Integer, Integer> {
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

			readData();
			return null;
		}

		/**
		 * 运行在ui线程中，在doInBackground()执行完毕后执行
		 */
		@Override
		protected void onPostExecute(Integer integer) {
			// logD("上传融云数据完毕");
		}

		/**
		 * 在publishProgress()被调用以后执行，publishProgress()用于更新进度
		 */
		@Override
		protected void onProgressUpdate(Integer... values) {

		}
	}
    // 数据加载，及网络错误提示
    OnClickListener onClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	Intent intent;
        	
        	switch (v.getId()) {
			case R.id.liner_unpay:
                 //待付款
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=0;
				break;
			case R.id.liner_unget:
                 //待收取
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=0;
				break;
			case R.id.liner_service:
                //服务中
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=0;
				break;
			case R.id.liner_unfinish:
                //待完成
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=1;
				break;
			case R.id.liner_un_gather:
                //待收款
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=1;
				break;
			case R.id.liner_finish_gather:
                 //已收款
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Task.class) ;
				startActivity(intent);
				A_0_App.SIDE_NOTICE=1;
				break;
			case R.id.rela_cash:
                 //提现绑定
				
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_PresentBind.class) ;
				intent.putExtra("bindStatus", center_Bean.getBindStatus());
				intent.putExtra("account", center_Bean.getAccount());
				intent.putExtra("accountName", center_Bean.getAccountName());
				startActivity(intent);
				break;
			case R.id.rela_common_problem:
                //常见问题
				    intent=new Intent(B_Account_Befriend_Center_Main_0.this, Pub_WebView_Load_Acy.class);
	                intent.putExtra("title_text", "常见问题");
	                intent.putExtra("url_text", AppStrStatic.LINK_USER_BEFRIEND_HELP);
	                intent.putExtra("tag_skip", "1");
	                intent.putExtra("tag_show_refresh_btn", "2");
	                startActivity(intent);
	                
				break;
			case R.id.liner_balance:
                //账户余额
				intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Account_Befriend_Center_Main_1_Balance.class) ;
				intent.putExtra("money", center_Bean.getTotalAmount());
				intent.putExtra("bindStatus", center_Bean.getBindStatus());
				intent.putExtra("account", center_Bean.getAccount());
				intent.putExtra("accountName", center_Bean.getAccountName());
				intent.putExtra("week", center_Bean.getWithdraw());
				intent.putExtra("low", center_Bean.getWithdrawAmountLow());
				intent.putExtra("max", center_Bean.getWithdrawMax());
				intent.putExtra("now", center_Bean.getWithdrawTimes());
				intent.putExtra("push", "0");
				startActivity(intent);
				break;
				case R.id.rela_coupon:
					//我的邦豆

					if(A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_UNDER_REVIEW)){
						PubMehods.showToastStr(B_Account_Befriend_Center_Main_0.this, R.string.str_no_certified_open);
						return;
					}
					if (A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_NO_SUPPLEMENTARY_INFO)
							||A_0_App.USER_STATUS.equals(AppStrStatic.USER_ROLE_AUDIT_FAILURE)) {
						A_0_App.getInstance().enter_Perfect_information(B_Account_Befriend_Center_Main_0.this,true);
						return;
					}
					String url_c =  A_0_App.BANG_USER_COUPON_URL+"&type="+center_Bean.getCouponType();
					if(url_c == null || url_c.equals("")){
						PubMehods.showToastStr(B_Account_Befriend_Center_Main_0.this, "数据请求失败，请重试");
						return;
					}
					logD(url_c+"=邦豆"+center_Bean.getCouponType());
					intent=new Intent(B_Account_Befriend_Center_Main_0.this, Pub_WebView_Load_Coupon.class);
					intent.putExtra("title_text", "优惠券");
					intent.putExtra("url_text", url_c);
					intent.putExtra("tag_skip", "0");
					intent.putExtra("tag_show_refresh_btn", "1");
					intent.putExtra("enter", "2");//帮帮中心进入
					startActivity(intent);
					break;
			default:
				break;
			}
        }
    };
    
    private void readData() {
    	A_0_App.getApi().getBefriend_CenterList(B_Account_Befriend_Center_Main_0.this, A_0_App.USER_TOKEN,new InterBefriend_CenterList() {
			
			@Override
			public void onSuccess(Befriend_Center_Bean befriend_Center_Bean,
					long servertime) {
				if (isFinishing())
					return; 
				center_Bean=befriend_Center_Bean;
				loaddata();
			}
		}, new Inter_Call_Back() {
			
			@Override
			public void onFinished() {
				
			}
			
			@Override
			public void onFailure(String msg) {
				if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Account_Befriend_Center_Main_0.this, msg);
			}
			
			@Override
			public void onCancelled() {
				
			}
		});
    }
    private void SwitchMessage(String status) {
    	A_0_App.getApi().getMessageSwitch(B_Account_Befriend_Center_Main_0.this, A_0_App.USER_TOKEN,status,new InterMessageSwitch() {
			
			@Override
			public void onSuccess(String msg) {
				if (isFinishing())
					return; 
				//PubMehods.showToastStr(B_Account_Befriend_Center_Main_0.this, msg);
			}
		}, new Inter_Call_Back() {
			
			@Override
			public void onFinished() {
				
			}
			
			@Override
			public void onFailure(String msg) {
				if (isFinishing())
                    return;
                PubMehods.showToastStr(B_Account_Befriend_Center_Main_0.this, msg);
			}
			
			@Override
			public void onCancelled() {
				
			}
		});
    }
    private void loaddata(){
    	if (center_Bean.getWaitPayment().equals("0")) {
			tv_unpay_count.setVisibility(View.GONE);
		}else{
			tv_unpay_count.setVisibility(View.VISIBLE);
			tv_unpay_count.setText(center_Bean.getWaitPayment());
		}
		if (center_Bean.getWaitAcquire().equals("0")) {
			tv_unget.setVisibility(View.GONE);
		}else{
			tv_unget.setVisibility(View.VISIBLE);
			tv_unget.setText(center_Bean.getWaitAcquire());
		}
		if (center_Bean.getOnService().equals("0")) {
			tv_service.setVisibility(View.GONE);
		}else{
			tv_service.setVisibility(View.VISIBLE);
			tv_service.setText(center_Bean.getOnService());
		}
		if (center_Bean.getWaitComlpete().equals("0")) {
			tv_unfinish_count.setVisibility(View.GONE);
		}else{
			tv_unfinish_count.setVisibility(View.VISIBLE);
			tv_unfinish_count.setText(center_Bean.getWaitComlpete());
		}
		if (center_Bean.getPendingReceivables().equals("0")) {
			tv_un_gather.setVisibility(View.GONE);
		}else{
			tv_un_gather.setVisibility(View.VISIBLE);
			tv_un_gather.setText(center_Bean.getPendingReceivables());
		}
		if (center_Bean.getOnNotice().equals("0")) {
			tb_set_message_help.setToggleOff();
		}else{
			tb_set_message_help.setToggleOn();
		}
		if (center_Bean.getTotalAmount().equals("null")||center_Bean.getTotalAmount()==null||center_Bean.getTotalAmount().equals("")) {
			tv_totalAmount.setText("余额:--");
		}else{
			DecimalFormat df = new DecimalFormat("0.00");
			double money = Double.parseDouble(center_Bean.getTotalAmount())/100.00;
			tv_totalAmount.setText("余额:"+df.format(money));
			
		}
		if (center_Bean.getCouponCount().equals("0")) {
			tv_total_coupon.setVisibility(View.GONE);
		}else{
			tv_total_coupon.setVisibility(View.VISIBLE);
			tv_total_coupon.setText(center_Bean.getCouponCount());
		}
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
                        A_0_App.getInstance().showExitDialog(B_Account_Befriend_Center_Main_0.this,
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
    


    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
            	finish();
                break;
            case ZUI_RIGHT_TEXT:
            	if (type.equals("1")) {
            		Intent intent=new Intent(B_Account_Befriend_Center_Main_0.this, B_Side_Befriend_A0_Main.class);
	            	startActivity(intent);
				} else {
					finish();
				}
            	
                break;
            default:
                break;
        }
    }
    
    
    
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    break;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if(firsetEnterLoader){
            firsetEnterLoader = false;
        }else{//第一次加载
        	readData();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        A_0_App.SIDE_NOTICE=0;
    }
    
    
    public static void logD(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logD("B_Account_Help_Center_Main_0", "B_Account_Help_Center_Main_0==>" + msg);
    }

    public static void logE(String msg) {
        com.yuanding.schoolpass.utils.LogUtils.logE("B_Account_Help_Center_Main_0", "B_Account_Help_Center_Main_0==>" + msg);
    }
   
}
