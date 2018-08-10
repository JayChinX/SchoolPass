
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.LogUtils;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.ProgressWebView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 上午9:13:39 
 * 加载webview url  不启用缓存模式
 * 我的邦豆、用户协议、我的邀请说明
 */

public class Pub_WebView_Load_Acy extends A_0_CpkBaseTitle_Navi {

    private ProgressWebView webView;
    private String titleText, loadStr = "";
    private JSObject_aa jsobject;
    private LinearLayout liner_webview;
    
    private Handler mHandler = new Handler();
    private String urlText = "";
    private String tag_show_refresh_btn = "";//1：显示刷新按钮。2：不显示
    private String tag_skip = "";//1：不再过滤，支持跳转  2：页面主动过滤掉超链接点击不再跳转。
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.pub_webview_load);
        A_0_App.getInstance().addActivity(this);
        logE("onCreate");
        if(getIntent() != null){
            titleText = getIntent().getExtras().getString("title_text");
            loadStr = getIntent().getExtras().getString("url_text");
            tag_show_refresh_btn=getIntent().getExtras().getString("tag_show_refresh_btn");
            tag_skip=getIntent().getExtras().getString("tag_skip");
        	if(tag_show_refresh_btn != null && tag_show_refresh_btn.equals("1")){
        		showTitleBt(ZUI_RIGHT_BUTTON, true);
                setZuiRightBtn(R.drawable.navigationbar_refresh);
            }else{
            	showTitleBt(ZUI_RIGHT_BUTTON, false);
            }
        }else{
        	showTitleBt(ZUI_RIGHT_BUTTON, false);
        }
        if (loadStr.length() <= 0) {
            PubMehods.showToastStr(Pub_WebView_Load_Acy.this, "链接为空");
            return;
        }
        liner_webview = (LinearLayout) findViewById(R.id.liner_webview);
        setTitleText(titleText);
        webView = (ProgressWebView) new ProgressWebView(getApplicationContext());
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        liner_webview.addView(webView);
        WebSettings ws = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);//水平不显示  
        webView.setVerticalScrollBarEnabled(false); //垂直不显示  
        ws.setBuiltInZoomControls(false);// 隐藏缩放按钮
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        ws.setUseWideViewPort(true);// 可任意比例缩放
        ws.setLoadWithOverviewMode(true);// setUseWideViewPort方法设置webview推荐使用的窗口。setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setJavaScriptEnabled(true);
        ws.setGeolocationEnabled(true);// 启用地理定位
        ws.setDomStorageEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setAppCacheEnabled(false);
        ws.setBlockNetworkImage(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 不加上白边
        jsobject = new JSObject_aa(Pub_WebView_Load_Acy.this);
        webView.addJavascriptInterface(jsobject, "JsTest");//<a  href="javascript:JsTest.JsCallAndroid(123)">123</a></td></tr> 
        webView.loadUrl(loadStr);
        webView.setWebViewClient(new WebViewClient() { 
            public boolean shouldOverrideUrlLoading(WebView view, String url) 
                                  
              { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边 
                if(tag_skip != null && "1".equals(tag_skip)){
            	   view.loadUrl(url); 
                }
                return true; 
                    
             }
          @Override
          public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {
          	// TODO Auto-generated method stub
          	 super.onReceivedError(view, errorCode, description, failingUrl);
//          	 isFailed=true;
          	 view.loadUrl("<html><head></head><body></body><html>");
          	 showErrorPage();
          }
          @Override
          public void onPageStarted(WebView view, String url, Bitmap favicon) {
          	// TODO Auto-generated method stub
          	super.onPageStarted(view, url, favicon);
          	if(mErrorView!=null){
              	mErrorView.setVisibility(View.GONE);
              	}
          	if(!url.equals("")||url.length()>0){
          	    urlText=url;
          	}
          }
          @Override
          public void onPageFinished(WebView view, String url) {
          	// TODO Auto-generated method stub
            webView.getSettings().setBlockNetworkImage(false);
          	super.onPageFinished(view, url);
          }
        }
          );
        webView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        if (webView.canGoBack()) {
                        	if(webView.getUrl().contains("blank"))
                        	{
                        		finishAcyGo(A_0_App.getInstance().WB_BangDou_Tag);
                        	}else
                        	{
                            webView.goBack(); // 后退
                            // webView.goForward();//前进
                            // webView.reload(); //刷新
                        	}
                        } else {
                            finishAcyGo(A_0_App.getInstance().WB_BangDou_Tag);
                        }
                        return true;
                    }
                }
                return false;
            }
        });
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }

    /** 
     * 显示自定义错误提示页面，用一个View覆盖在WebView 
     */  
    private View mErrorView; 
    protected void showErrorPage() {  
        LinearLayout webParentView = (LinearLayout)webView.getParent();  
          
        initErrorPage();  
       if (webParentView.getChildCount() > 1) {  
           webParentView.removeViewAt(0);  
        }  
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);  
        webParentView.addView(mErrorView, 0, lp);  
          
    }  
    /***
	 * 显示加载失败时自定义的网页
	 */
	protected void initErrorPage() {
		if (mErrorView == null) {
			mErrorView = View.inflate(this, R.layout.pub_read_faile, null);
			RelativeLayout button = (RelativeLayout)mErrorView.findViewById(R.id.home_loading_error);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if(!isNetworkConnected())
					{
						PubMehods.showToastStr(Pub_WebView_Load_Acy.this, "请检查您的网络设置");
						return;
					}
					webView.loadUrl(urlText);
					//hideErrorPage();
				}
			});
			mErrorView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					webView.loadUrl(urlText);
					PubMehods.showToastStr(Pub_WebView_Load_Acy.this, "请检查您的网络设置");
					//hideErrorPage();
				}
			});
		}
		mErrorView.setVisibility(View.VISIBLE);
	}
    protected void hideErrorPage() {  
        LinearLayout webParentView = (LinearLayout)webView.getParent();  
        if (webParentView.getChildCount() > 1) {  
            webParentView.removeViewAt(0);  
        }  
    }  
    /*
     * 绑定的object对象(内部类)
     */
    public class JSObject_aa {
        private Context context;

        public JSObject_aa(Context context) {
            this.context = context;
        }

        /*
         * JS调用android的方法
         * @JavascriptInterface仍然必不可少
         * 服务端可以传递参数过来
         */
        @JavascriptInterface
        public void JsCallAndroid() {
            exit_login();
        }
    }
    
    /*
     * android 调用 服务器method 也可以传递参数
     */
    private void goON() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:testLogin('顺便传个参数给JS!')");// 调用JS中的 函数，当然也可以不传参
            }
        });
    }
    
    // 退出登录
    private void exit_login() {
    	
        A_0_App.getInstance().WB_BangDou_Tag = 1;
        
        PubMehods.showToastStr(Pub_WebView_Load_Acy.this, R.string.str_token_timeout);
        startActivity(new Intent(Pub_WebView_Load_Acy.this, A_3_0_Login_Acy.class));
        A_0_App.getInstance().clearUserSpInfo(false);
        finish();
        overridePendingTransition(R.anim.animal_push_right_in_normal, R.anim.animal_push_right_out_normal);
    }
    
    //关闭页面设置
    private void finishAcyGo(int token_over) {
        if (token_over == 1) {
				 startAcy(getApplicationContext(), A_Main_Acy.class);
                 A_0_App.getInstance().WB_BangDou_Tag = 0;
                 finish();
           
        } else {
            finish();
        }
    }

    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
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
                    //A_0_App.getInstance().showExitDialog(Pub_WebView_Load_Acy.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(Pub_WebView_Load_Acy.this,AppStrStatic.kicked_offline());
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
                finishAcyGo(A_0_App.getInstance().WB_BangDou_Tag);
                break;
            case ZUI_RIGHT_BUTTON:
                if(!isNetworkConnected())
                {
                    PubMehods.showToastStr(Pub_WebView_Load_Acy.this, "请检查您的网络设置");
                    return;
                }
                if (!PubMehods.isFastClick(AppStrStatic.INTERVAL_LOGIN_TIME)) {
                    webView.reload();
                }
                break;
            default:
                break;
        }
    }
    
    /**
     * 检测网络是否可用
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }
    public static void logD(String msg) {
        LogUtils.logD("Pub_WebView_Load_Acy", "Pub_WebView_Load_Acy==>" + msg);
    }

    public static void logE(String msg) {
        LogUtils.logE("Pub_WebView_Load_Acy", "Pub_WebView_Load_Acy学生==>" + msg);
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	liner_webview.removeAllViews();
    	webView.removeAllViews();
        webView.destroy();
        webView = null;
    }
}
