
package com.yuanding.schoolpass;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.image.ImageOptions;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yuanding.schoolpass.base.A_0_CpkBaseTitle_Navi;
import com.yuanding.schoolpass.bean.Cpk_School_Assistant_detail;
import com.yuanding.schoolpass.service.Api.InterAcceptTheDelegate;
import com.yuanding.schoolpass.service.Api.InterNoticeSchoolAssistantDetail;
import com.yuanding.schoolpass.service.Api.Inter_Call_Back;
import com.yuanding.schoolpass.utils.ACache;
import com.yuanding.schoolpass.utils.AppStrStatic;
import com.yuanding.schoolpass.utils.PubMehods;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2016年10月24日 上午10:58:12 
 * 校务助手列表考勤详情
 */
public class B_Mess_School_Assistant_1_Detai_Acy extends A_0_CpkBaseTitle_Navi {

    private View mess_notice_detail_load_error, liner_notice_detail_whole,
            mess_notice_detail_loading;
    private TextView tv_message_detail_title, tv_message_detail_notice_time, tv_notice_detail;
    private Button btn_accept_the_delegate;
    private WebView webview_notice_detail;
    private String message_id;
    private ImageOptions bitmapUtils;
    private int acy_type;// 页面类型 推送1，正常列表进入2
    private TextView tv_mess_detail_from;
    private ACache maACache;
    private JSONObject jsonObject;
    private Cpk_School_Assistant_detail detail_Notice;

    private LinearLayout home_load_loading;
    private AnimationDrawable drawable;

    private String messge_id,post_id = "";
    private boolean havaSuccessLoadData = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setView(R.layout.activity_mess_notice_detail_assistant);

        showTitleBt(ZUI_RIGHT_BUTTON, true);
        setTitleText("详情");
        detail_Notice = new Cpk_School_Assistant_detail();
        acy_type = getIntent().getExtras().getInt("acy_type");
        if (acy_type == 2) {
            // 列表进入
            message_id = getIntent().getExtras().getString("message_id");
        } else {
            // 推送进入
            message_id = PubMehods.getSharePreferStr(this, "mCurrentClickNotificationMsgId");
        }

        mess_notice_detail_load_error = findViewById(R.id.mess_notice_detail_load_error);
        liner_notice_detail_whole = findViewById(R.id.liner_notice_detail_whole);
        mess_notice_detail_loading = findViewById(R.id.mess_notice_detail_loading);

        home_load_loading = (LinearLayout) mess_notice_detail_loading
                .findViewById(R.id.home_load_loading);
        home_load_loading.setBackgroundResource(R.drawable.load_progress);
        drawable = (AnimationDrawable) home_load_loading.getBackground();
        drawable.start();

        btn_accept_the_delegate = (Button)findViewById(R.id.btn_accept_the_delegate);
        tv_message_detail_title = (TextView) findViewById(R.id.tv_message_detail_title_sys);
        tv_message_detail_notice_time = (TextView) findViewById(R.id.tv_message_detail_notice_time_sys);
        tv_mess_detail_from = (TextView) findViewById(R.id.tv_message_detail_notice_from);
        webview_notice_detail = (WebView) findViewById(R.id.webview_notice_detail_sys);
        tv_notice_detail = (TextView) findViewById(R.id.tv_notice_detail);
        webview_notice_detail.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        webview_notice_detail.setBackgroundColor(getResources().getColor(R.color.col_account_bg));
        webview_notice_detail.getSettings().setJavaScriptEnabled(true);
        webview_notice_detail.getSettings().setDefaultTextEncodingName("utf-8"); // 设置文本编码
        int size = A_0_App.getInstance().setWebviewSize();
        webview_notice_detail.getSettings().setDefaultFontSize(size);
        webview_notice_detail.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url)
            { // 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
              // view.loadUrl(url);
                return true;
            }
        });

        bitmapUtils = A_0_App.getBitmapUtils(this, R.drawable.ic_default_empty_bg,
                R.drawable.ic_default_empty_bg,false);
        if (A_0_App.USER_QUTOKEN != null && !A_0_App.USER_QUTOKEN.equals("")) {
            startListtenerRongYun();// 监听融云网络变化
        }

        mess_notice_detail_load_error.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showLoadResult(true, false, false);
                readData(message_id);
            }
        });

        btn_accept_the_delegate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (message_id != null && !"".equals(message_id) && post_id != null && !"".equals(post_id)) {
                    if(!PubMehods.isFastClick(AppStrStatic.WORD_COMMENT_MIN_LIMIT)){
                        noticeAcceptTheDelegate(messge_id, post_id);
                    }
                } else {
                    finish();
                    PubMehods.showToastStr(B_Mess_School_Assistant_1_Detai_Acy.this,
                            R.string.error_title_analyze_error);
                }
            }
        });
        
        readCache(message_id);
    }

    private void readCache(String message_id) {
        // TODO Auto-generated method stub
        maACache = ACache.get(this);
        jsonObject = maACache.getAsJSONObject(AppStrStatic.cache_key_schoolassistant_detail_text
                + A_0_App.USER_UNIQID + message_id);
        if (jsonObject != null && !A_0_App.getInstance().getNetWorkManager().isNetWorkConnected()) {// 说明有缓存
            showInfo(jsonObject);
        }else{
            updateInfo();
        }
    }

    private void showInfo(JSONObject jsonObject) {
        Cpk_School_Assistant_detail notice;
        try {
            JSONObject dd = jsonObject.getJSONObject("info");
            notice = new Cpk_School_Assistant_detail();
            notice = JSON.parseObject(dd + "", Cpk_School_Assistant_detail.class);
            detail_Notice = notice;
            successResult(notice);
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    /**
     * 多张图片展示
     */

    private void showLoadResult(boolean loading, boolean whole, boolean loadFaile) {
        if (whole)
            liner_notice_detail_whole.setVisibility(View.VISIBLE);
        else
            liner_notice_detail_whole.setVisibility(View.GONE);

        if (loadFaile)
            mess_notice_detail_load_error.setVisibility(View.VISIBLE);
        else
            mess_notice_detail_load_error.setVisibility(View.GONE);
        if (loading) {
            drawable.start();
            mess_notice_detail_loading.setVisibility(View.VISIBLE);
        } else {
            if (drawable != null) {
                drawable.stop();
            }
            mess_notice_detail_loading.setVisibility(View.GONE);
        }
    }
    
    private void successResult(Cpk_School_Assistant_detail notice) {
        havaSuccessLoadData = true;   
        messge_id = notice.getMessage_id();
        post_id = notice.getPost_id();
        if(notice.getBtn_attend().equals("1")){
            btn_accept_the_delegate.setVisibility(View.VISIBLE);
        }else{
            btn_accept_the_delegate.setVisibility(View.GONE);
        }
        
        if (notice.getTitle() != null && notice.getTitle().length() > 0)
            tv_message_detail_title.setText(notice.getTitle());

        if (notice.getCreate_time() != null && notice.getCreate_time().length() > 0)
            tv_message_detail_notice_time.setText(PubMehods.getFormatDate(
                    Long.valueOf(notice.getCreate_time()), "MM/dd HH:mm"));
        tv_mess_detail_from.setText(R.string.str_school_assistant);
        webview_notice_detail.setVisibility(View.GONE);
        tv_notice_detail.setVisibility(View.VISIBLE);
        String content = notice.getContent();
        if (null != content && content.length() > 0) {
            SpannableStringBuilder builder = PubMehods.splitStrWhereStr(B_Mess_School_Assistant_1_Detai_Acy.this, R.color.main_color, content, "\\{#", "#\\}");
            if(builder != null){
                tv_notice_detail.setText(builder); 
            }
        }
//        /**
//         * 本地读取html
//         */
//        // webview_notice_detail.loadDataWithBaseURL("",notice.getContent(),
//        // "text/html","UTF-8", null);
//        PubMehods.saveHtml(notice.getContent(), "TEMP_HTML");
//        String html_path = "file://" + Environment.getExternalStorageDirectory()
//                + "/TEMP_HTML.html";
//
//        webview_notice_detail.loadUrl(html_path);
//        webview_notice_detail.addJavascriptInterface(new JavascriptInterface(
//                B_Mess_School_Assistant_1_Detai_Acy.this), "imagelistner");
//        webview_notice_detail.setWebViewClient(new MyWebViewClient());

        showLoadResult(false, true, false);
    }

    private void readData(String message_id) {
        A_0_App.getApi().getNoticeSchoolAssistantDetail(B_Mess_School_Assistant_1_Detai_Acy.this,
                A_0_App.USER_TOKEN, message_id, new InterNoticeSchoolAssistantDetail() {

                    @Override
                    public void onSuccess(Cpk_School_Assistant_detail notice) {
                        if (isFinishing())
                            return;
                        detail_Notice = notice;
                        successResult(detail_Notice);
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
                        if(!havaSuccessLoadData)
                            showLoadResult(false, false, true);
                        PubMehods.showToastStr(B_Mess_School_Assistant_1_Detai_Acy.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });

    }
    
    // 接受委派
    private void noticeAcceptTheDelegate(String message_id,String post_id) {
        A_0_App.getInstance().showProgreDialog(B_Mess_School_Assistant_1_Detai_Acy.this, "", true);
        A_0_App.getApi().acceptTheDelegate(message_id,post_id, A_0_App.USER_TOKEN,
                new InterAcceptTheDelegate() {
                    @Override
                    public void onSuccess(String msg) {
                        if (isFinishing())
                            return;
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_1_Detai_Acy.this);
                        btn_accept_the_delegate.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_login_disabled));
                        btn_accept_the_delegate.setEnabled(false);
                        btn_accept_the_delegate.setText(getResources().getString(R.string.str_attdence_accepted));
                        if (msg != null && !("").equals(msg))
                            PubMehods.showToastStr(B_Mess_School_Assistant_1_Detai_Acy.this, msg);
                        else
                            PubMehods.showToastStr(B_Mess_School_Assistant_1_Detai_Acy.this, "操作成功");

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
                        A_0_App.getInstance().CancelProgreDialog(B_Mess_School_Assistant_1_Detai_Acy.this);
                        PubMehods.showToastStr(B_Mess_School_Assistant_1_Detai_Acy.this, msg);
                    }
                    
                    @Override
                    public void onCancelled() {
                        // TODO Auto-generated method stub
                        
                    }
                });
    }

    @Override
    protected void handleTitleBarEvent(int resId, View v) {
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
        setResult(1, it);
    }

    private void goAcy() {
        if (acy_type != 2) {// 推送
            if (A_Main_Acy.getInstance() != null) {
                finish();
            } else {
                startAcy(B_Mess_School_Assistant_1_Detai_Acy.this, A_Main_Acy.class);
            }
        } else {// 正常进入
            goData();
            finish();
            overridePendingTransition(R.anim.animal_push_right_in_normal,
                    R.anim.animal_push_right_out_normal);
        }
    }

    private void startAcy(Context packageContext, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(packageContext, cls);
        startActivity(intent);
        overridePendingTransition(R.anim.animal_push_left_in_normal,
                R.anim.animal_push_left_out_normal);
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
                    A_Main_My_Message_Acy
                            .logE("教师——connectRoogIm()，融云连接~~~~~~~~~~~~~~~~~~~~~~~~~~~~~断开连接");
                    // A_0_App.getInstance().showExitDialog(B_Mess_Notice_Official_News_1.this,getResources().getString(R.string.token_timeout));
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
                            A_0_App.getInstance().showExitDialog(
                                    B_Mess_School_Assistant_1_Detai_Acy.this,
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

            readData(message_id);
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

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        // bitmapUtils=null;
        drawable.stop();
        drawable = null;
        super.onDestroy();
        System.gc();

    }

    // 监听
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.contains("//img")) {

                return true;
            } else {

                return super.shouldOverrideUrlLoading(view, url);
            }

        }

        @Override
        public void onPageFinished(WebView view, String url) {

            view.getSettings().setJavaScriptEnabled(true);

            super.onPageFinished(view, url);
            // html加载完成之后，添加监听图片的点击js函数
            addImageClickListner();
            getWebViewAllImgSrc();

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            view.getSettings().setJavaScriptEnabled(true);

            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description,
                String failingUrl) {

            super.onReceivedError(view, errorCode, description, failingUrl);

        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview_notice_detail.canGoBack()) {
            webview_notice_detail.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 注入js函数监听
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        webview_notice_detail.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{" +
                "objs[i].onclick=function()" +
                "{ " +
                "window.imagelistner.openImage(this.src);  " +
                "}" +
                "}" +
                "})()");
    }

    /**
     * 获取当前页面所有img地址为list
     * 
     * @author Administrator
     */
    private void getWebViewAllImgSrc() {
        webview_notice_detail.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "var imgScr = '';" +
                "for(var i=0;i<objs.length;i++){" +
                "imgScr =imgScr +  '\\n'+ objs[i].src;" +
                " };" +
                "window.imagelistner.getAllImg(imgScr);  " +
                // "return imgScr;"+
                "})()");

    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;
        final ArrayList<String> image_List = new ArrayList<String>();
        int temp = 0;

        public JavascriptInterface(Context context) {
            this.context = context;

        }

        public void openImage(String img) {

            for (int i = 0; i < image_List.size(); i++) {
                if (image_List.get(i).equals(img)) {
                    temp = i;
                }
            }

            Intent intent = new Intent();
            intent.putStringArrayListExtra("path", image_List);
            intent.putExtra("num", temp);
            intent.setClass(context, B_Side_Found_BigImage.class);
            context.startActivity(intent);

        }

        public void getAllImg(String img) {
            image_List.clear();
            String[] temp = null;
            temp = img.split("\n");
            // 以换行符为url分隔符 第一个元素的null
            int counts = temp.length - 1;
            for (int i = 0; i < temp.length; i++) {
                // 以换行符为url分隔符 第一个元素的null
                if (i == 0) {
                    continue;
                }
                image_List.add(temp[i]);
            }
        }
    }
}
