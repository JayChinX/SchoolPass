package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_Notice_Detail;
import com.yuanding.schoolpass.service.Api.InterNoticeDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月23日 下午5:22:12
 * 通知详情
 */
public class B_Mess_Notice_Detail_Sys extends A_0_CpkBaseTitle_Navi {

    private View mess_notice_detail_load_error,liner_notice_detail_whole,mess_notice_detail_loading;
    private ImageView iv_notice_detail_banner;
    private TextView tv_message_detail_title,tv_message_detail_notice_time;
    private WebView webview_notice_detail;
    
    private String message_id;
    protected ImageLoader imageLoader;
    private DisplayImageOptions optionsBanner;
    //private BitmapUtils bitmapUtilsBanner;
    private int acy_type;//页面类型    推送1:校务助手消息推送,2：正常列表进入 ，3推送：正常的首页消息推送
    private TextView tv_mess_detail_from;
    
    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;
    private String school_notice;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail_sys);
      
        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setTitleText("通知详情");
        
        acy_type =  getIntent().getExtras().getInt("acy_type");
        //页面类型    1:转发,2：正常列表进入 ，3推送：正常的首页消息推送,推送4:校务助手消息推送
        if (acy_type == 1) {
            message_id = getIntent().getExtras().getString("message_id");
        } else if (acy_type == 2) {
            message_id = getIntent().getExtras().getString("message_id");
            school_notice = getIntent().getExtras().getString("school_notice");
        } else if (acy_type == 3) {
            message_id =PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        } else if (acy_type == 4) {
            message_id = getIntent().getExtras().getString("message_id");
            school_notice = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }
        if (school_notice == null) {
            school_notice = "";
        }
        mess_notice_detail_load_error = findViewById(R.id.mess_notice_detail_load_error);
        liner_notice_detail_whole = findViewById(R.id.liner_notice_detail_whole);
        mess_notice_detail_loading=findViewById(R.id.mess_notice_detail_loading);
        
        home_load_loading = (LinearLayout) mess_notice_detail_loading.findViewById(R.id.home_load_loading);
		home_load_loading.setBackgroundResource(R.drawable.load_progress);
		drawable = (AnimationDrawable) home_load_loading.getBackground();
		drawable.start(); 
        
        iv_notice_detail_banner = (ImageView)findViewById(R.id.iv_notice_detail_banner);
        tv_message_detail_title =(TextView)findViewById(R.id.tv_message_detail_title_sys);
        tv_message_detail_notice_time =(TextView)findViewById(R.id.tv_message_detail_notice_time_sys);
        tv_mess_detail_from=(TextView) findViewById(R.id.tv_message_detail_notice_from);
        webview_notice_detail = (WebView)findViewById(R.id.webview_notice_detail_sys);
        
        webview_notice_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webview_notice_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
        int size=A_0_App.getInstance().setWebviewSize();
        webview_notice_detail.getSettings().setDefaultFontSize(size);
        webview_notice_detail.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
            	//view.loadUrl(url); 
                return true; 
             }
        });
//      webview_notice_detail.getSettings().setUseWideViewPort(true); 
//    webview_notice_detail.getSettings().setLoadWithOverviewMode(true);
      
//     webview_notice_detail.getSettings().setJavaScriptEnabled(true);
//     webview_notice_detail.getSettings().setAllowFileAccess(true);
//     webview_notice_detail.getSettings().setPluginState(PluginState.ON);
      
//  第一种： 解决图文混排问题
//    settings.setUseWideViewPort(true); 
//    settings.setLoadWithOverviewMode(true); 
//    第二种： 
//    WebSetting settings = webView.getSettings(); 
//    settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
//    把所有内容放在webview等宽的一列中。（可能会出现页面中链接失效） 
      
//    webSettings.setJavaScriptEnabled(true);
//    webSettings.setAllowFileAccess(true);
//    webSettings.setPluginState(PluginState.ON);
//    webSettings.setUseWideViewPort(true);
//    
//    webView.loadUrl(loadStr);
//    
//    webSettings.setAppCacheEnabled(true);// 设置缓存
//    webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);// 设置缓存模式
//    webSettings.setBuiltInZoomControls(true);// 显示缩放控件
//    webSettings.setSupportZoom(true); // 支持缩放
//    webSettings.setDefaultFontSize(16);
     imageLoader = A_0_App.getInstance().getimageLoader();
     optionsBanner = A_0_App.getInstance().getOptions(R.drawable.ic_default_empty_bg, R.drawable.ic_default_empty_bg,
     R.drawable.ic_default_empty_bg);
      //bitmapUtilsBanner=A_0_App.getBitmapUtils(this, R.drawable.ic_default_acy_empty, R.drawable.ic_default_acy_empty);
        
      readData(message_id);
      if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
          startListtenerRongYun();// 监听融云网络变化
      }
      
      mess_notice_detail_load_error.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View arg0) {
              showLoadResult(true,false, false);
              readData(message_id);
          }
      });
      
    }

    
    private void showLoadResult(boolean loading,boolean whole,boolean loadFaile) {
        if (whole)
            liner_notice_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_notice_detail_whole.setVisibility(View.GONE);
        
        if (loadFaile)
            mess_notice_detail_load_error.setVisibility(View.VISIBLE);
        else
            mess_notice_detail_load_error.setVisibility(View.GONE);
        if(loading){
        	drawable.start();
            mess_notice_detail_loading.setVisibility(View.VISIBLE);
        }else{
        	if (drawable!=null) {
        		drawable.stop();
			}
            mess_notice_detail_loading.setVisibility(View.GONE);
    }}
    
    private void readData(String message_id) {
        A_0_App.getApi().getNoticeDetail(B_Mess_Notice_Detail_Sys.this,school_notice, A_0_App.USER_TOKEN, message_id,AppStrStatic.cache_key_mess_detail_sys+A_0_App.USER_UNIQID,"0", new InterNoticeDetail() {
            
            @Override
            public void onSuccess(Cpk_Notice_Detail notice,long time) {
                if(isFinishing())
                    return;
                
                if(notice.getBg_img() != null && notice.getBg_img().length()>0 && !notice.getBg_img().equals("")){
                    iv_notice_detail_banner.setVisibility(View.VISIBLE);
                    String uri = notice.getBg_img();
    				if(iv_notice_detail_banner.getTag() == null){
    				    PubMehods.loadServicePic(imageLoader,uri,iv_notice_detail_banner, optionsBanner);
    					iv_notice_detail_banner.setTag(uri);
    				}else{
    				    if(!iv_notice_detail_banner.getTag().equals(uri)){
    				        PubMehods.loadServicePic(imageLoader,uri,iv_notice_detail_banner, optionsBanner);
    				        iv_notice_detail_banner.setTag(uri);
    				    }
    				}
                    //bitmapUtilsBanner.display(iv_notice_detail_banner, notice.getBg_img());
                }else{
                    iv_notice_detail_banner.setVisibility(View.GONE);
                }
                if (notice.getTitle() != null && notice.getTitle().length() > 0)
                    tv_message_detail_title.setText(notice.getTitle());
                
                if (notice.getCreate_time() != null && notice.getCreate_time().length() > 0)
                    tv_message_detail_notice_time.setText(PubMehods.getFormatDate(
                            Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
                try {
                	 tv_mess_detail_from.setText(notice.getOrgan_str());
				} catch (Exception e) {
				}
               
                webview_notice_detail.loadDataWithBaseURL(null,notice.getContent(), "text/html","UTF-8", null);
                showLoadResult(false,true, false);
                
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
                PubMehods.showToastStr(B_Mess_Notice_Detail_Sys.this, msg);
                showLoadResult(false, false, true);
            }
            
            @Override
            public void onCancelled() {
                // TODO Auto-generated method stub
                
            }
        });

	}
	
	@Override
	protected void handleTitleBarEvent(int resId,View v) {
		switch (resId) {
		case BACK_BUTTON:
		    goAcy();
			break;
		default:
			break;
		}

	}
	
	private void goData() {
		Intent it = new Intent();
		it.putExtra("read_count","0");
		it.putExtra("repley_count", "0");
		setResult(1, it);
	}
	
    private void goAcy() {
        if (acy_type != 2) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_Notice_Detail_Sys.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,R.anim.animal_push_right_out_normal);
        }
    }
    
    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,R.anim.animal_push_left_out_normal);
        finish();
    }
	
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    goAcy();
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
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
                    //A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail_Sys.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(B_Mess_Notice_Detail_Sys.this,AppStrStatic.kicked_offline());
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
    	// TODO Auto-generated method stub
    	//bitmapUtilsBanner=null;
    	drawable.stop();
    	drawable=null;
    	super.onDestroy();
    }

}
