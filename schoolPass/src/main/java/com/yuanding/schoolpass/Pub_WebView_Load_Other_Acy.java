
package com.yuanding.schoolpass;


import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;
import com.yuanding.schoolpass.view.ProgressWebView;

/**
 * @author Jiaohaili 
 * @version 创建时间：2015年11月11日 上午9:13:39 
 * 加载webview url  不启用缓存模式
 * 我的常见问题，产品介绍，运行权限设置
 */

public class Pub_WebView_Load_Other_Acy extends A_0_CpkBaseTitle_Navi {

    private ProgressWebView webView;
    private String titleText, urlText = "";
    private String tag_skip;//1：不再过滤，支持跳转  2：页面主动过滤掉超链接点击不再跳转。
    private LinearLayout liner_webview;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        A_0_App.getInstance().addActivity(this);
        setView(R.layout.pub_webview_load);
        
        A_0_App.getInstance().addActivity(this);
        if (getIntent() != null) {
            titleText = getIntent().getExtras().getString("title_text");
            urlText = getIntent().getExtras().getString("url_text");
            tag_skip = getIntent().getExtras().getString("tag_skip");
        }
        if (urlText.length() <= 0) {
            PubMehods.showToastStr(Pub_WebView_Load_Other_Acy.this, "链接为空");
            return;
        }
        liner_webview = (LinearLayout) findViewById(R.id.liner_webview);
        setTitleText(titleText);
        webView = new ProgressWebView(getApplicationContext());
        webView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        liner_webview.addView(webView);
        WebSettings ws = webView.getSettings();
        webView.setHorizontalScrollBarEnabled(false);//水平不显示  
        webView.setVerticalScrollBarEnabled(false); //垂直不显示  
        ws.setBuiltInZoomControls(false);// 隐藏缩放按钮
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);// 排版适应屏幕
        ws.setUseWideViewPort(true);// 可任意比例缩放setUseWideViewPort方法设置webview推荐使用的窗口。
        ws.setLoadWithOverviewMode(true);//setLoadWithOverviewMode方法是设置webview加载的页面的模式。
        ws.setSavePassword(true);
        ws.setSaveFormData(true);// 保存表单数据
        ws.setJavaScriptEnabled(true);
        ws.setGeolocationEnabled(true);// 启用地理定位
        ws.setDomStorageEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setAppCacheEnabled(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);// 不加上白边
        webView.loadUrl(urlText);
        
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
            	if(tag_skip != null && "1".equals(tag_skip)){
                  view.loadUrl(url);
            	}
                return true;
            }
        });
        
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }
    }
    
    @Override
    protected void handleTitleBarEvent(int resId, View v) {
        switch (resId) {
            case BACK_BUTTON:
                finish();
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
                            A_0_App.getInstance().showExitDialog(Pub_WebView_Load_Other_Acy.this, AppStrStatic.kicked_offline());
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
    	super.onDestroy();
    	liner_webview.removeAllViews();
    	webView.removeAllViews();
        webView.destroy();
        webView = null;
    }
}
